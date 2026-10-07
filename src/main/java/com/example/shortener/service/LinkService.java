package com.example.shortener.service;

import com.example.shortener.dto.request.CreateLinkRequest;
import com.example.shortener.dto.response.LinkResponse;
import com.example.shortener.dto.response.PageResponse;
import com.example.shortener.entity.Link;
import com.example.shortener.entity.User;
import com.example.shortener.exception.BadRequestException;
import com.example.shortener.exception.NotFoundException;
import com.example.shortener.repository.LinkRepository;
import com.example.shortener.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final CodeGenerator codeGenerator;
    private final SecurityUtils securityUtils;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public LinkResponse create(CreateLinkRequest req) {
        User user = securityUtils.currentUser();

        String code;
        if (req.getCustomCode() != null && !req.getCustomCode().isBlank()) {
            codeGenerator.ensureCustomCodeAvailable(req.getCustomCode());
            code = req.getCustomCode();
        } else {
            code = codeGenerator.generateUniqueCode();
        }

        if (req.getExpiresAt() != null && req.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("expiresAt must be in the future");
        }

        Link link = Link.builder()
                .code(code)
                .originalUrl(req.getOriginalUrl())
                .user(user)
                .clicksCount(0L)
                .expiresAt(req.getExpiresAt())
                .createdAt(Instant.now())
                .build();

        linkRepository.save(link);
        return toResponse(link);
    }

    @Transactional(readOnly = true)
    public PageResponse<LinkResponse> listMine(int page, int size) {
        User user = securityUtils.currentUser();
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Link> result = linkRepository.findAllByUserId(user.getId(), pr);
        return PageResponse.<LinkResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public LinkResponse getMine(Long id) {
        User user = securityUtils.currentUser();
        Link link = linkRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Link not found: " + id));
        return toResponse(link);
    }

    @Transactional
    public void delete(Long id) {
        User user = securityUtils.currentUser();
        Link link = linkRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Link not found: " + id));
        linkRepository.delete(link);
    }

    /** Для редиректа — публичный метод, без auth */
    @Transactional(readOnly = true)
    public Link resolveByCode(String code) {
        Link link = linkRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Short link not found: " + code));
        if (link.isExpired()) {
            throw new NotFoundException("Short link expired: " + code);
        }
        return link;
    }

    public LinkResponse toResponse(Link link) {
        return LinkResponse.builder()
                .id(link.getId())
                .code(link.getCode())
                .shortUrl(baseUrl + "/" + link.getCode())
                .originalUrl(link.getOriginalUrl())
                .clicksCount(link.getClicksCount())
                .createdAt(link.getCreatedAt())
                .expiresAt(link.getExpiresAt())
                .expired(link.isExpired())
                .build();
    }
}