package com.example.demo.Controllers;

import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.model.Offre;
import com.example.demo.model.User;
import com.example.demo.service.CandidatService;
import com.example.demo.service.CandidatureService;
import com.example.demo.service.OffreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CandidatController {

    private final CandidatService candidatService;
    private final OffreService offreService;

    // Page d'accueil du candidat
    @GetMapping("/candidat/home")
    public String home(Authentication authentication, Model model) {
        String email = authentication.getName();
        Candidat candidat = candidatService.getCandidatByEmail(email);
        model.addAttribute("candidat", candidat);
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

        return "candidat/toutes-offres"; // Utilise la même page pour afficher les résultats
    }

    // Méthode utilitaire pour nettoyer les paramètres
    private String clean(String value) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }

    private final CandidatureService candidatureService; // Injection automatique du service





    // Retourner la vue pour afficher les candidatures

}
