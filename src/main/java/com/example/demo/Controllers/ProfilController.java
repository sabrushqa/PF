package com.example.demo.Controllers;

import com.example.demo.Repository.CandidatRepository;
import com.example.demo.model.*;
import com.example.demo.service.ProfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
@RequestMapping("/candidat/profil")
public class ProfilController {

    @Autowired
    private ProfilService profilService;

    @Autowired
    private CandidatRepository candidatRepository;

    // --- AFFICHER LE PROFIL ---
    @GetMapping
    public String afficherProfil(Model model, Principal principal) {
        Candidat candidat = candidatRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));
        Profil profil = candidat.getProfil();

        if (profil == null) {
            profil = new Profil();
            profil.setCandidat(candidat);
            candidat.setProfil(profil);
            profilService.sauvegarderProfil(profil);
        }

        model.addAttribute("profil", profil);
        model.addAttribute("nouvelleExperience", new Experience());
        model.addAttribute("nouvelleFormation", new Formation());
        model.addAttribute("nouvelleLangue", new Langue());
        return "candidat/profil";
    }

    // --- UPLOAD PHOTO ---
    @PostMapping("/photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file, Principal principal, RedirectAttributes redirect) {
        if (file.isEmpty()) {
            redirect.addFlashAttribute("error", "Aucune photo sélectionnée.");
            return "redirect:/candidat/profil";
        }

        try {
            String uploadDir = "src/main/resources/static/uploads/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Profil profil = profilService.getProfilByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
            profil.setPhoto(fileName);
            profilService.sauvegarderProfil(profil);

            redirect.addFlashAttribute("success", "Photo mise à jour !");
        } catch (IOException e) {
            redirect.addFlashAttribute("error", "Erreur lors de l'envoi de la photo.");
        }

        return "redirect:/candidat/profil";
    }

    // ====================== EXPÉRIENCE ======================

    @PostMapping("/experience/ajouter")
    public String ajouterExperience(@ModelAttribute Experience experience, Principal principal, RedirectAttributes redirect) {
        profilService.ajouterExperience(experience, principal.getName());
        redirect.addFlashAttribute("success", "Expérience ajoutée.");
        return "redirect:/candidat/profil";
    }

    @GetMapping("/experience/modifier/{id}")
    public String formulaireModifierExperience(@PathVariable Long id, Model model) {
        Experience experience = profilService.getExperienceById(id)
                .orElseThrow(() -> new RuntimeException("Expérience non trouvée"));
        model.addAttribute("experience", experience);
        return "candidat/edit-experience";
    }

    @PostMapping("/experience/modifier/{id}")
    public String modifierExperience(@PathVariable Long id, @ModelAttribute Experience experience, RedirectAttributes redirect) {
        profilService.modifierExperience(id, experience);
        redirect.addFlashAttribute("success", "Expérience modifiée.");
        return "redirect:/candidat/profil";
    }

    @PostMapping("/experience/supprimer/{id}")
    public String supprimerExperience(@PathVariable Long id, RedirectAttributes redirect) {
        profilService.supprimerExperience(id);
        redirect.addFlashAttribute("success", "Expérience supprimée.");
        return "redirect:/candidat/profil";
    }

    // ====================== FORMATION ======================

    @PostMapping("/formation/ajouter")
    public String ajouterFormation(@ModelAttribute Formation formation, Principal principal, RedirectAttributes redirect) {
        profilService.ajouterFormation(formation, principal.getName());
        redirect.addFlashAttribute("success", "Formation ajoutée.");
        return "redirect:/candidat/profil";
    }

    @GetMapping("/formation/modifier/{id}")
    public String formulaireModifierFormation(@PathVariable Long id, Model model) {
        Formation formation = profilService.getFormationById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));
        model.addAttribute("formation", formation);
        return "candidat/edit-formation";
    }

    @PostMapping("/formation/modifier/{id}")
    public String modifierFormation(@PathVariable Long id, @ModelAttribute Formation formation, RedirectAttributes redirect) {
        profilService.modifierFormation(id, formation);
        redirect.addFlashAttribute("success", "Formation modifiée.");
        return "redirect:/candidat/profil";
    }

    @PostMapping("/formation/supprimer/{id}")
    public String supprimerFormation(@PathVariable Long id, RedirectAttributes redirect) {
        profilService.supprimerFormation(id);
        redirect.addFlashAttribute("success", "Formation supprimée.");
        return "redirect:/candidat/profil";
    }

    // ====================== LANGUE ======================

    @PostMapping("/langue/ajouter")
    public String ajouterLangue(@ModelAttribute Langue langue, Principal principal, RedirectAttributes redirect) {
        profilService.ajouterLangue(langue, principal.getName());
        redirect.addFlashAttribute("success", "Langue ajoutée.");
        return "redirect:/candidat/profil";
    }

    @GetMapping("/langue/modifier/{id}")
    public String formulaireModifierLangue(@PathVariable Long id, Model model) {
        Langue langue = profilService.getLangueById(id)
                .orElseThrow(() -> new RuntimeException("Langue non trouvée"));
        model.addAttribute("langue", langue);
        return "candidat/edit-langue";
    }

    @PostMapping("/langue/modifier/{id}")
    public String modifierLangue(@PathVariable Long id, @ModelAttribute Langue langue, RedirectAttributes redirect) {
        profilService.modifierLangue(id, langue);
        redirect.addFlashAttribute("success", "Langue modifiée.");
        return "redirect:/candidat/profil";
    }

    @PostMapping("/langue/supprimer/{id}")
    public String supprimerLangue(@PathVariable Long id, RedirectAttributes redirect) {
        profilService.supprimerLangue(id);
        redirect.addFlashAttribute("success", "Langue supprimée.");
        return "redirect:/candidat/profil";
    }
    // ====================== BIO ======================

    @PostMapping("/bio/modifier")
    public String modifierBio(@RequestParam("bio") String bio, Principal principal, RedirectAttributes redirect) {
        profilService.modifierBio(principal.getName(), bio);
        redirect.addFlashAttribute("success", "Bio mise à jour.");
        return "redirect:/candidat/profil";
    }

}
