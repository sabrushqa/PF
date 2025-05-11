package com.example.demo.service;

import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.model.Entretien;
import com.example.demo.Repository.CandidatRepository;
import com.example.demo.Repository.EntretienRepository;
import com.example.demo.utils.ZoomApiUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntretienService {

    private final EntretienRepository entretienRepository;
    private final EmailService emailService;
    private final ZoomApiUtil zoomApiUtil;
    private final CandidatRepository candidatRepository;

    public void envoyerLienZoomPourEntretien(Candidature candidature, String userEmail) throws MessagingException {
        // Récupérer le candidat à partir de son email utilisateur
        Optional<Candidat> candidatOpt = candidatRepository.findByUserEmail(userEmail);

        if (candidatOpt.isEmpty()) {
            System.out.println("Aucun candidat trouvé avec l'email : " + userEmail);
            return;
        }

        Candidat candidat = candidatOpt.get();
        String email = candidat.getUser().getEmail();  // l'email est dans l'objet User lié

        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email manquant pour le candidat ID : " + candidat.getId());
            return;
        }

        // Créer l'entretien
        Entretien entretien = new Entretien();
        entretien.setCandidature(candidature);

        // Définir la date d'entretien (par défaut dans 7 jours)
        LocalDateTime dateEntretien = LocalDateTime.now().plusDays(7);
        entretien.setDateEntretien(dateEntretien);

        // Convertir la date en ZonedDateTime
        ZonedDateTime zonedDateTime = dateEntretien.atZone(ZoneId.systemDefault());

        // Créer le lien Zoom
        String zoomLink = zoomApiUtil.createZoomMeeting(zonedDateTime);
        entretien.setLienZoom(zoomLink);

        // Sauvegarder l'entretien
        entretienRepository.save(entretien);

        // Formatage date/heure pour l'email
        String formattedDate = dateEntretien.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String formattedTime = dateEntretien.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        // Envoyer l'email
        emailService.sendInterviewConfirmation(
                email,
                candidat.getNom() + " " + candidat.getPrenom(),
                "ATOS Recrutement",
                candidature.getOffre().getTitre(),
                formattedDate,
                formattedTime,
                zoomLink
        );
    }
}
