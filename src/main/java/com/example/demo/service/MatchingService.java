package com.example.demo.service;

import com.example.demo.model.Candidature;
import com.example.demo.model.Offre;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${matching.api.url}")
    private String matchingApiUrl;

    @Value("${matching.api.key:#{null}}")  // Rend cette propriété optionnelle
    private String matchingApiKey;

    @Value("${application.upload.dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) throws Exception {
        // Créer le répertoire s'il n'existe pas
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Créer un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Sauvegarder le fichier
        Files.write(filePath, file.getBytes());

        return fileName;
    }

    public double calculateMatchingScore(Candidature candidature) {
        try {
            Offre offre = candidature.getOffre();
            String cvPath = Paths.get(uploadDir, candidature.getCv()).toString();

            // Préparer les données pour l'API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("job_description", offre.getDescription());
            requestBody.put("cv_path", cvPath);
            requestBody.put("cv_content", new String(Files.readAllBytes(Paths.get(cvPath))));

            // Configurer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Ajouter la clé API uniquement si elle est configurée
            if (matchingApiKey != null && !matchingApiKey.isEmpty() && !matchingApiKey.equals("votre_api_key_ici")) {
                headers.set("X-API-KEY", matchingApiKey);
            }

            // Faire la requête à l'API externe
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    matchingApiUrl,
                    request,
                    String.class
            );

            // Analyser la réponse
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.get("matching_score").asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, retourner un score par défaut
            return 0.5;
        }
    }
}