package com.example.demo.service;

import com.example.demo.model.SimilarityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AISimilarityService {

    private static final Logger logger = LoggerFactory.getLogger(AISimilarityService.class);

    private final RestTemplate restTemplate;
    private int apiCallCount = 0;
    private int apiSuccessCount = 0;
    private int apiFailureCount = 0;

    // L'URL de l'API IA configurée dans application.properties
    @Value("${ai.similarity.api.url:http://localhost:5000/calculate-similarity}")
    private String aiApiUrl;

    // Initialisation de RestTemplate via constructeur
    public AISimilarityService() {
        this.restTemplate = new RestTemplate();
        logger.info("AISimilarityService initialisé avec l'URL par défaut (sera remplacée par la configuration)");
    }

    /**
     * Calcule la similarité entre deux textes en utilisant uniquement l'API externe.
     *
     * @param text1 Premier texte à comparer
     * @param text2 Second texte à comparer
     * @return Score de similarité entre 0 et 1
     * @throws RuntimeException si l'API n'est pas disponible ou renvoie une erreur
     */
    public double getSimilarity(String text1, String text2) {
        apiCallCount++;
        String callId = String.format("CALL-%04d", apiCallCount);

        logger.info("[{}] Appel à l'API de similarité: URL={}", callId, aiApiUrl);

        // Vérifier si les textes sont nuls ou vides
        if (text1 == null || text2 == null || text1.trim().isEmpty() || text2.trim().isEmpty()) {
            logger.warn("[{}] Textes nuls ou vides fournis à l'API", callId);
            return 0.0;
        }

        // Log des premiers caractères des textes pour vérification
        logger.info("[{}] Début du texte1: {}", callId, text1.substring(0, Math.min(30, text1.length())));
        logger.info("[{}] Début du texte2: {}", callId, text2.substring(0, Math.min(30, text2.length())));

        // Créer les en-têtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Échapper correctement les guillemets dans les textes JSON
        String escapedText1 = escapeJsonString(text1);
        String escapedText2 = escapeJsonString(text2);

        // Créer le payload JSON
        String jsonPayload = String.format("{\"text1\": \"%s\", \"text2\": \"%s\"}", escapedText1, escapedText2);
        logger.debug("[{}] Payload JSON préparé (longueur: {})", callId, jsonPayload.length());

        // Créer l'entité HTTP avec le payload et les en-têtes
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        long startTime = System.currentTimeMillis();

        try {
            // Effectuer l'appel POST à l'API Flask
            logger.info("[{}] Envoi de la requête à l'API...", callId);
            ResponseEntity<SimilarityResponse> response = restTemplate.exchange(
                    aiApiUrl, HttpMethod.POST, entity, SimilarityResponse.class);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[{}] Réponse reçue de l'API en {}ms: status={}", callId, duration, response.getStatusCode());

            // Vérifier la réponse et renvoyer le score de similarité
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                double similarity = response.getBody().getSimilarity();
                logger.info("[{}] Score de similarité calculé: {}", callId, similarity);
                apiSuccessCount++;
                return similarity;
            } else {
                apiFailureCount++;
                logger.error("[{}] L'API a renvoyé une réponse invalide: {}", callId, response.getStatusCode());
                throw new RuntimeException("L'API de similarité a renvoyé une réponse invalide: " + response.getStatusCode());
            }
        } catch (Exception e) {
            apiFailureCount++;
            long duration = System.currentTimeMillis() - startTime;
            // Log détaillé de l'erreur
            logger.error("[{}] Erreur après {}ms lors de l'appel à l'API: {}", callId, duration, e.getMessage(), e);
            throw new RuntimeException("Impossible de calculer la similarité: " + e.getMessage(), e);
        }
    }

    /**
     * Fournit des statistiques sur l'utilisation de l'API
     */
    public String getApiStats() {
        return String.format("API appels: %d, succès: %d, échecs: %d",
                apiCallCount, apiSuccessCount, apiFailureCount);
    }

    /**
     * Échapper les caractères spéciaux dans une chaîne JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}