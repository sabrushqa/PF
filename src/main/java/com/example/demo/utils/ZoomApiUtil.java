package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZoomApiUtil {

    @Value("${zoom.api.base.url}")
    private String zoomApiUrl;

    @Value("${zoom.client.id}")
    private String zoomClientId;

    @Value("${zoom.client.secret}")
    private String zoomClientSecret;

    @Value("${zoom.token.url}")
    private String zoomTokenUrl;

    private String accessToken;
    private long tokenExpiryTime;

    // Méthode pour obtenir un token OAuth
    private String getAccessToken() {
        // Vérifier si le token actuel est toujours valide
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return accessToken;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Basic auth avec Client ID et Secret
            String auth = zoomClientId + ":" + zoomClientSecret;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);

            // Corps de la requête pour le grant_type
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "account_credentials");
            map.add("account_id", "yReiivkeR3ujRTEQoHzqpQ");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            // Appel pour obtenir le token
            ResponseEntity<Map> response = restTemplate.exchange(
                    zoomTokenUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse != null && tokenResponse.containsKey("access_token")) {
                accessToken = (String) tokenResponse.get("access_token");
                // Définir l'expiration (habituellement 1 heure)
                Integer expiresIn = (Integer) tokenResponse.get("expires_in");
                tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000);
                return accessToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Méthode pour créer une réunion Zoom de manière dynamique avec la date et l'heure
    public String createZoomMeeting(ZonedDateTime startDateTime) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Obtenir un token valide
            String token = getAccessToken();
            if (token == null) {
                return null;
            }

            headers.set("Authorization", "Bearer " + token);

            // Format Zoom ISO 8601 avec fuseau horaire
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String formattedDate = startDateTime
                    .withZoneSameInstant(ZoneId.of("UTC"))
                    .format(formatter);

            // Créer le corps JSON
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("topic", "Entretien de recrutement");
            requestBody.put("type", 2);  // Type 2 = Réunion planifiée
            requestBody.put("start_time", formattedDate);
            requestBody.put("duration", 30);  // durée en minutes
            requestBody.put("timezone", "Europe/Paris");
            requestBody.put("agenda", "Entretien pour le poste");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Construire l'URL complète pour créer une réunion
            String createMeetingUrl = UriComponentsBuilder
                    .fromHttpUrl(zoomApiUrl)
                    .path("/users/me/meetings")
                    .build()
                    .toUriString();

            // Appel POST vers l'API Zoom
            ResponseEntity<Map> response = restTemplate.exchange(
                    createMeetingUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> meetingDetails = response.getBody();
            if (meetingDetails != null && meetingDetails.containsKey("join_url")) {
                System.out.println("Meeting URL: "+meetingDetails.get("join_url"));
                return (String) meetingDetails.get("join_url");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}