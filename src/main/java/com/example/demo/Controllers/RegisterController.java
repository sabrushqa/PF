package com.example.demo.Controllers;

import com.example.demo.model.Candidat;
import com.example.demo.model.Recruteur;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private AuthService authService;

    // ========== CANDIDAT ==========

    @GetMapping("/candidat")
    public String showRegisterCandidatForm(Model model) {
        Candidat candidat = new Candidat();
        candidat.setUser(new User()); // Important pour éviter NullPointer
        model.addAttribute("candidat", candidat);
        return "Register/register_candidat";
    }

    @PostMapping("/candidat")
    public String registerCandidat(@ModelAttribute("candidat") Candidat candidat, Model model) {
        try {
            authService.registerCandidat(candidat);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "Register/register_candidat";
        }
    }

    // ========== RECRUTEUR ==========

    @GetMapping("/recruteur")
    public String showRegisterRecruteurForm(Model model) {
        Recruteur recruteur = new Recruteur();
        recruteur.setUser(new User()); // Important aussi ici
        model.addAttribute("recruteur", recruteur);
        return "Register/register_recruteur";
    }

    @PostMapping("/recruteur")
    public String registerRecruteur(@ModelAttribute("recruteur") Recruteur recruteur, Model model) {
        try {
            authService.registerRecruteur(recruteur);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "Register/register_recruteur";
        }
    }
}
