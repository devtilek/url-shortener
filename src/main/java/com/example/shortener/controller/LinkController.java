package com.example.shortener.controller;

import com.example.shortener.dto.request.CreateLinkRequest;
import com.example.shortener.dto.response.LinkResponse;
import com.example.shortener.dto.response.PageResponse;
import com.example.shortener.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
@Tag(name = "Links", description = "Manage short links")
@SecurityRequirement(name = "bearerAuth")
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    @Operation(summary = "Create a short link")
    public ResponseEntity<LinkResponse> create(@Valid @RequestBody CreateLinkRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(linkService.create(req));
    }

    @GetMapping
    @Operation(summary = "List my links (paginated)")
    public PageResponse<LinkResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return linkService.listMine(page, Math.min(size, 100));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get my link by id")
    public LinkResponse get(@PathVariable Long id) {
        return linkService.getMine(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete my link")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        linkService.delete(id);
        return ResponseEntity.noContent().build();
    }
}