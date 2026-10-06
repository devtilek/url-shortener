package com.example.shortener.controller;

import com.example.shortener.entity.Link;
import com.example.shortener.service.ClickService;
import com.example.shortener.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;
    private final ClickService clickService;

    @GetMapping("/{code:[a-zA-Z0-9]{4,20}}")
    public ResponseEntity<Void> redirect(@PathVariable String code,
                                         HttpServletRequest request) {
        Link link = linkService.resolveByCode(code);
        clickService.recordClick(link.getId(), request);

        return ResponseEntity.status(HttpStatus.FOUND)   // 302
                .location(URI.create(link.getOriginalUrl()))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .build();
    }
}