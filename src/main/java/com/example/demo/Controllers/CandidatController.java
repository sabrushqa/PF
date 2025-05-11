package com.example.demo.Controllers;

import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.model.Entretien;
import com.example.demo.model.Offre;
import com.example.demo.service.CandidatService;
import com.example.demo.service.CandidatureService;
import com.example.demo.service.EntretienService;
import com.example.demo.service.OffreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CandidatController {

    private final CandidatService candidatService;
    private final OffreService offreService;
    private final CandidatureService candidatureService;
    private final EntretienService entretienService;

    // Page d'accueil du candidat
    @GetMapping("/candidat/home")
    public String home(Authentication authentication, Model model) {
        String email = authentication.getName();
        Candidat candidat = candidatService.getCandidatByEmail(email);

        // Récupérer les statistiques des candidatures
        long totalCandidatures = candidatureService.countByCandidat(candidat);
        long enAttente = candidatureService.countByCandidatAndStatut(candidat, "EN_ATTENTE");
        long acceptees = candidatureService.countByCandidatAndStatut(candidat, "ACCEPTEE");
        long refusees = candidatureService.countByCandidatAndStatut(candidat, "REFUSEE");
        long entretiensPrevus = entretienService.countByCandidatAndDateAfter(candidat, LocalDateTime.now());

        // Récupérer les entretiens à venir
        List<Entretien> entretiensAVenir = entretienService.findByCandidatAndDateAfter(candidat, LocalDateTime.now());



        // Préparation des données pour le graphique
        Map<String, Long> pieChartData = new HashMap<>();
        pieChartData.put("enAttente", enAttente);
        pieChartData.put("acceptees", acceptees);
        pieChartData.put("refusees", refusees);

        model.addAttribute("candidat", candidat);
        model.addAttribute("totalCandidatures", totalCandidatures);
        model.addAttribute("candidaturesEnAttente", enAttente);
        model.addAttribute("candidatureAcceptees", acceptees);
        model.addAttribute("candidaturesRefusees", refusees);
        model.addAttribute("entretiensPrevus", entretiensPrevus);
        model.addAttribute("pieChartData", pieChartData);

        return "candidat/home";
    }

    // Affichage de toutes les offres
    @GetMapping("/candidat/offres")
    public String afficherToutesLesOffres(Model model) {
        List<Offre> offres = offreService.getToutesLesOffres();
        model.addAttribute("offres", offres);
        return "candidat/toutes-offres";
    }

    // Recherche d'offres avec filtres
    @GetMapping("/candidat/offres/recherche")
    public String rechercherOffres(@RequestParam(required = false) String secteur,
                                   @RequestParam(required = false) String typeContrat,
                                   @RequestParam(required = false) String lieu,
                                   @RequestParam(required = false) String entreprise,
                                   Model model) {

        secteur = clean(secteur);
        typeContrat = clean(typeContrat);
        lieu = clean(lieu);
        entreprise = clean(entreprise);

        List<Offre> offres = offreService.rechercherOffres(secteur, typeContrat, lieu, entreprise);
        model.addAttribute("offres", offres);

        return "candidat/toutes-offres";
    }

    // Méthode utilitaire pour nettoyer les paramètres
    private String clean(String value) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }
}