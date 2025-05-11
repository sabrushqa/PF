package com.example.demo.Controllers;

import com.example.demo.Repository.OffreRepository;
import com.example.demo.model.Offre;
import com.example.demo.model.User;
import com.example.demo.service.FavoriService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/candidat/favoris")
@RequiredArgsConstructor
public class FavoriController {

    private final FavoriService favoriService;
    private final OffreRepository offreRepository;
    private final UserService userService;

    /**
     * Ajouter un favori
     */
    @PostMapping("/ajouter/{offreId}")
    public String ajouterFavori(@PathVariable Long offreId, Principal principal) {
        User candidat = userService.findByEmail(principal.getName());
        Offre offre = offreRepository.findById(offreId).orElseThrow();
        favoriService.ajouterFavori(candidat, offre);
        return "redirect:/candidat/offres"; // Redirection vers la page des offres
    }

    /**
     * Supprimer un favori
     */
    @PostMapping("/supprimer/{offreId}")
    public String supprimerFavori(@PathVariable Long offreId, Principal principal) {
        User candidat = userService.findByEmail(principal.getName());
        Offre offre = offreRepository.findById(offreId).orElseThrow();
        favoriService.supprimerFavori(candidat, offre);
        return "redirect:/candidat/favoris"; // Redirection vers la page des favoris
    }

    /**
     * Voir les favoris
     */
    @GetMapping
    public String voirFavoris(Model model, Principal principal) {
        User candidat = userService.findByEmail(principal.getName());
        model.addAttribute("favoris", favoriService.getFavoris(candidat));
        return "candidat/favoris"; // Page des favoris
    }

    /**
     * Voir les détails d'une offre à partir des favoris
     */
    @GetMapping("/detail/{offreId}")
    public String voirDetailsOffre(@PathVariable Long offreId, Model model) {
        Offre offre = offreRepository.findById(offreId).orElseThrow();
        model.addAttribute("offre", offre);
        return "candidat/offre-detail"; // Redirige vers la page de détails de l'offre
    }
}
