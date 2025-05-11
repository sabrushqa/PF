package com.example.demo.Controllers;

import com.example.demo.Repository.CandidatRepository;
import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.model.Entretien;
import com.example.demo.service.CandidatureService;
import com.example.demo.service.EmailService;
import com.example.demo.utils.ZoomApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/zoom")
@RequiredArgsConstructor
public class ZoomController {

    private final EmailService emailService;
    private final CandidatureService candidatureService;
    private final CandidatRepository candidatRepository;

    private final ZoomApiUtil zoomApiUtil;
    private final com.example.demo.Repository.EntretienRepository entretienRepository;

    @PostMapping("/sendZoomLink/{candidatureId}")
    public String sendZoomLink(@PathVariable Long candidatureId, Principal principal) {
        String emailConnecte = principal.getName();

        Optional<Candidat> optionalCandidat = candidatRepository.findByUserEmail(emailConnecte);
        if (optionalCandidat.isEmpty()) {
            return "Candidat non trouvé pour l'utilisateur connecté.";
        }

        Candidat candidat = optionalCandidat.get();
        Candidature candidature = candidatureService.getById(candidatureId);

        if (candidature != null && "Acceptée".equalsIgnoreCase(candidature.getStatut())) {
            Optional<Entretien> optionalEntretien = entretienRepository.findByCandidature(candidature);
            if (optionalEntretien.isEmpty()) {
                return "Aucun entretien trouvé pour cette candidature.";
            }

            Entretien entretien = optionalEntretien.get();
            LocalDateTime entretienDateTime = entretien.getDateEntretien();

            String candidateName = candidat.getNom();
            String candidateEmail = candidat.getUser().getEmail();  // ✅ Email récupéré depuis le user
            String recruiterName = "ATOS";
            String position = candidat.getSpecialite();

            String zoomLink = zoomApiUtil.createZoomMeeting(ZonedDateTime.from(entretienDateTime));
            if (zoomLink == null) {
                return "Erreur lors de la création de la réunion Zoom.";
            }

            String formattedDate = entretienDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String formattedTime = entretienDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            try {
                emailService.sendInterviewConfirmation(
                        candidateEmail, candidateName, recruiterName, position,
                        formattedDate, formattedTime, zoomLink
                );

                entretien.setLienZoom(zoomLink);
                entretienRepository.save(entretien);

                return "Email envoyé avec le lien Zoom.";
            } catch (Exception e) {
                return "Erreur lors de l'envoi de l'email : " + e.getMessage();
            }
        }

        return "Candidature non trouvée ou statut non 'Acceptée'.";
    }
    @GetMapping("/callback")
    public String handleZoomCallback(@RequestParam("code") String code) {
        return "Authentification Zoom réussie avec le code : " + code;
    }



}