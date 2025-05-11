package com.example.demo.Controllers;

import com.example.demo.model.Offre;
import com.example.demo.service.OffreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/offres")
public class PublicOffreController {

    private final OffreService offreService;

    @GetMapping("/{id}")
    public String afficherDetailOffre(@PathVariable Long id, Model model) {
        Offre offre = offreService.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));
        model.addAttribute("offre", offre);
        return "candidat/offre-detail";
    }
}
