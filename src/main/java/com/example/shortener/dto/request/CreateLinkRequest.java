package com.example.shortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLinkRequest {

    @NotBlank(message = "originalUrl is required")
    @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
    @Size(max = 2048, message = "URL too long")
    private String originalUrl;

    /** опционально — свой код. Если null — сгенерим рандомный */
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,20}$",
            message = "code must be 4-20 chars, letters/digits/_/-")
    private String customCode;

    /** опционально — срок жизни */
    private Instant expiresAt;
}