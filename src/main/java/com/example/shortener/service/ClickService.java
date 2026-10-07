package com.example.shortener.service;

import com.example.shortener.dto.response.StatsResponse;
import com.example.shortener.entity.Click;
import com.example.shortener.entity.Link;
import com.example.shortener.repository.ClickRepository;
import com.example.shortener.repository.LinkRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickService {

    private final ClickRepository clickRepository;
    private final LinkRepository linkRepository;

    /** Асинхронно — чтобы не тормозить редирект */
    @Async
    @Transactional
    public void recordClick(Long linkId, HttpServletRequest request) {
        try {
            Click click = Click.builder()
                    .link(linkRepository.getReferenceById(linkId))
                    .ipAddress(clientIp(request))
                    .userAgent(truncate(request.getHeader("User-Agent"), 500))
                    .referer(truncate(request.getHeader("Referer"), 500))
                    .build();
            clickRepository.save(click);
            linkRepository.incrementClicks(linkId);
        } catch (Exception e) {
            log.warn("Failed to record click for link {}: {}", linkId, e.getMessage());
        }
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return comma > 0 ? xff.substring(0, comma).trim() : xff.trim();
        }
        return req.getRemoteAddr();
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    /** Статистика по ссылке — собираем агрегаты */
    @Transactional(readOnly = true)
    public StatsResponse getStats(Link link) {
        Long linkId = link.getId();
        long total = clickRepository.countByLinkId(linkId);
        Instant lastClickAt = clickRepository.findLastClickAt(linkId);
        long uniqueIps = clickRepository.countUniqueIps(linkId);

        Map<String, Long> byReferer = new LinkedHashMap<>();
        clickRepository.topReferers(linkId, PageRequest.of(0, 5))
                .forEach(row -> byReferer.put((String) row[0], ((Number) row[1]).longValue()));

        Instant since = Instant.now().minus(7, ChronoUnit.DAYS);
        List<StatsResponse.DailyClicks> byDay = new ArrayList<>();
        for (Object[] row : clickRepository.clicksByDay(linkId, since)) {
            byDay.add(StatsResponse.DailyClicks.builder()
                    .date(row[0].toString())
                    .count(((Number) row[1]).longValue())
                    .build());
        }

        return StatsResponse.builder()
                .code(link.getCode())
                .originalUrl(link.getOriginalUrl())
                .totalClicks(total)
                .createdAt(link.getCreatedAt())
                .lastClickAt(lastClickAt)
                .uniqueIps(uniqueIps)
                .clicksByReferer(byReferer)
                .clicksByDay(byDay)
                .build();
    }
}