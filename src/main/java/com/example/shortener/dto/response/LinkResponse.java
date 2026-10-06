package com.example.shortener.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkResponse {
    private Long id;
    private String code;
    private String shortUrl;
    private String originalUrl;
    private Long clicksCount;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean expired;
}