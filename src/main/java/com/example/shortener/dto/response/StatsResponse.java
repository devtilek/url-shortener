package com.example.shortener.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponse {
    private String code;
    private String originalUrl;
    private long totalClicks;
    private Instant createdAt;
    private Instant lastClickAt;
    private Map<String, Long> clicksByReferer;   // top referers
    private List<DailyClicks> clicksByDay;       // последние 7 дней
    private long uniqueIps;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyClicks {
        private String date;
        private long count;
    }
}