package com.example.demo.Controllers;



import com.example.demo.service.AISimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour vérifier le bon fonctionnement de l'API de similarité
 */
@RestController
@RequestMapping("/api/verification")
public class ApiVerificationController {

    private final AISimilarityService aiSimilarityService;

    @Autowired
    public ApiVerificationController(AISimilarityService aiSimilarityService) {
        this.aiSimilarityService = aiSimilarityService;
    }

    /**
     * Endpoint pour tester directement l'API de similarité
     */
    @PostMapping("/test-similarity")
    public ResponseEntity<Map<String, Object>> testSimilarity(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");

        if (text1 == null || text2 == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Les deux textes sont requis")
            );
        }

        try {
            double similarityScore = aiSimilarityService.getSimilarity(text1, text2);

            Map<String, Object> response = new HashMap<>();
            response.put("text1_length", text1.length());
            response.put("text2_length", text2.length());
            response.put("similarity_score", similarityScore);
            response.put("api_stats", aiSimilarityService.getApiStats());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors du calcul de similarité");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("api_stats", aiSimilarityService.getApiStats());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Endpoint pour vérifier les statistiques de l'API
     */
    @GetMapping("/api-stats")
    public ResponseEntity<Map<String, Object>> getApiStats() {
        return ResponseEntity.ok(Map.of(
                "api_stats", aiSimilarityService.getApiStats()
        ));
    }
}
