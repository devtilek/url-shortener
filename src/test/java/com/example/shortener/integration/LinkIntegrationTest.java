package com.example.shortener.integration;

import com.example.shortener.dto.request.CreateLinkRequest;
import com.example.shortener.dto.request.RegisterRequest;
import com.example.shortener.dto.response.AuthResponse;
import com.example.shortener.dto.response.LinkResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LinkIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String registerAndGetToken(String username) throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("secret123")
                .build();

        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(body, AuthResponse.class).getAccessToken();
    }

    @Test
    void createLink_thenRedirect_thenStats() throws Exception {
        String token = registerAndGetToken("bob");

        // 1. создаём ссылку
        CreateLinkRequest req = CreateLinkRequest.builder()
                .originalUrl("https://example.com")
                .build();

        String linkJson = mockMvc.perform(post("/api/links")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        LinkResponse link = objectMapper.readValue(linkJson, LinkResponse.class);

        // 2. редирект
        mockMvc.perform(get("/" + link.getCode()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));

        // 3. статистика (нужно чуть подождать асинхронной записи клика)
        Thread.sleep(500);

        mockMvc.perform(get("/api/links/" + link.getId() + "/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(1))
                .andExpect(jsonPath("$.code").value(link.getCode()));
    }

    @Test
    void createLink_requiresAuth() throws Exception {
        CreateLinkRequest req = CreateLinkRequest.builder()
                .originalUrl("https://example.com")
                .build();

        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}