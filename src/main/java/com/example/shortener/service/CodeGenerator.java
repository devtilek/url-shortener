package com.example.shortener.service;

import com.example.shortener.exception.CodeAlreadyExistsException;
import com.example.shortener.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final LinkRepository linkRepository;

    @Value("${app.code.length:7}")
    private int codeLength;

    @Value("${app.code.max-retries:5}")
    private int maxRetries;

    /** Сгенерировать уникальный код */
    public String generateUniqueCode() {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            String code = randomCode(codeLength);
            if (!linkRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new CodeAlreadyExistsException(
                "Could not generate unique code after " + maxRetries + " attempts");
    }

    /** Валидировать и проверить кастомный код */
    public void ensureCustomCodeAvailable(String code) {
        if (linkRepository.existsByCode(code)) {
            throw new CodeAlreadyExistsException("Code already taken: " + code);
        }
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}