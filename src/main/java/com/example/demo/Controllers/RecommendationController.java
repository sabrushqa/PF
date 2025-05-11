package com.example.demo.Controllers;

import com.example.demo.model.Offre;
import com.example.demo.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class RecommendationController {

    private final RecommendationService recommendationService;

    // Injection de la dépendance RecommendationService via le constructeur
    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Méthode pour afficher les recommandations d'offres pour un candidat donné.
     * @param candidatId L'ID du candidat pour lequel les offres doivent être récupérées.
     * @param model Le modèle qui est envoyé à la vue.
     * @return La vue contenant les offres recommandées ou un message d'erreur.
     */
    @GetMapping("/candidat/recommandation")
    public String showRecommendations(@RequestParam("candidatId") Long candidatId, Model model) {
        try {
            List<Offre> offres = recommendationService.recommendOffersForCandidate(candidatId);

            // Log pour vérifier que les offres sont bien envoyées à la vue
            System.out.println("Offres à envoyer à la vue : " + offres.size());
            offres.forEach(offer -> {
                System.out.println(offer.getTitre() + " (Score: " + offer.getMatchScore() + ")");
            });

            model.addAttribute("offres", offres);
            model.addAttribute("candidatId", candidatId);

            // Vérification si aucune offre n'est disponible
            if (offres.isEmpty()) {
                model.addAttribute("message", "Aucune offre recommandée pour le moment.");
                System.out.println("Aucune offre à afficher pour le candidat " + candidatId);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue lors de la récupération des recommandations: " + e.getMessage());
            System.out.println("Erreur dans le contrôleur: " + e.getMessage());
            e.printStackTrace();
        }

        return "candidat/recommandation";  // Vue à rendre
    }
}