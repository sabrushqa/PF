package com.example.demo.Controllers;

import com.example.demo.Repository.CandidatRepository;
import com.example.demo.Repository.EntretienRepository;
import com.example.demo.Repository.RecruteurRepository;
import com.example.demo.model.Candidat;
import com.example.demo.model.Entretien;
import com.example.demo.model.Recruteur;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EntretienController {

    private final CandidatRepository candidatRepository;
    private final EntretienRepository entretienRepository;

    @GetMapping("/candidat/entretiens")
    public String afficherEntretiensCandidat(Principal principal, Model model) {
        String email = principal.getName();
        Optional<Candidat> optCandidat = candidatRepository.findByUserEmail(email);

        if (optCandidat.isPresent()) {
            Candidat candidat = optCandidat.get();
            List<Entretien> entretiens = entretienRepository.findByCandidature_Candidat(candidat);
            model.addAttribute("entretiens", entretiens);
        } else {
            model.addAttribute("message", "Aucun entretien trouvé pour ce candidat.");
        }

        return "candidat/entretiens";
    }
    private final RecruteurRepository recruteurRepository;

    @GetMapping("/recruteur/entretiens")
    public String afficherEntretiensRecruteur(Principal principal, Model model) {
        String email = principal.getName();
        Optional<Recruteur> optRecruteur = recruteurRepository.findByUserEmail(email);

        if (optRecruteur.isPresent()) {
            Recruteur recruteur = optRecruteur.get();
            List<Entretien> entretiens = entretienRepository.findByCandidature_Offre_Recruteur(recruteur);
            model.addAttribute("entretiens", entretiens);
        }

        return "recruteur/entretiens";
    }

}
