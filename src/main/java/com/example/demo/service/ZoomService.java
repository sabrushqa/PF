package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ZoomService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${zoom.client.id}")
    private String clientId;

    @Value("${zoom.client.secret}")
    private String clientSecret;

    @Value("${zoom.token.url}")
    private String tokenUrl;

    @Value("${zoom.api.base.url}")
    private String apiBaseUrl;

    // Token stocké en mémoire (dans une application réelle, utilisez un système de persistance)
    private String accessToken;
    private LocalDateTime tokenExpiry;

    private String getAccessToken() {
        if (accessToken == null || LocalDateTime.now().isAfter(tokenExpiry)) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Encodage Base64 de client_id:client_secret
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("scope", "meeting:write");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            accessToken = root.get("access_token").asText();
            int expiresIn = root.get("expires_in").asInt();

            // Calcul de l'expiration (avec une marge de sécurité)
            tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn - 300);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'obtention du token Zoom", e);
        }
    }

    public Map<String, String> createMeeting(String topic, LocalDateTime startTime, int durationMinutes) {
        try {
            String apiUrl = apiBaseUrl + "/users/me/meetings";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getAccessToken());

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("topic", topic);
            requestBody.put("type", 2); // Type 2 = réunion planifiée
            requestBody.put("start_time", startTime.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            requestBody.put("duration", durationMinutes);
            requestBody.put("timezone", "Europe/Paris");
            requestBody.put("agenda", "Entretien d'embauche");

            // Paramètres de la réunion
            ObjectNode settings = requestBody.putObject("settings");
            settings.put("host_video", true);
            settings.put("participant_video", true);
            settings.put("join_before_host", true);
            settings.put("waiting_room", false);
            settings.put("auto_recording", "none");

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            Map<String, String> meetingInfo = new HashMap<>();
            meetingInfo.put("id", root.get("id").asText());
            meetingInfo.put("join_url", root.get("join_url").asText());
            meetingInfo.put("start_url", root.get("start_url").asText());

            return meetingInfo;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la réunion Zoom", e);
        }
    }
}