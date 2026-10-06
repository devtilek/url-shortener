package com.example.shortener.controller;

import com.example.shortener.dto.response.StatsResponse;
import com.example.shortener.entity.Link;
import com.example.shortener.entity.User;
import com.example.shortener.exception.NotFoundException;
import com.example.shortener.repository.LinkRepository;
import com.example.shortener.security.SecurityUtils;
import com.example.shortener.service.ClickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "Click statistics")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final LinkRepository linkRepository;
    private final ClickService clickService;
    private final SecurityUtils securityUtils;

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get click statistics for my link")
    public StatsResponse stats(@PathVariable Long id) {
        User user = securityUtils.currentUser();
        Link link = linkRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Link not found: " + id));
        return clickService.getStats(link);
    }
}