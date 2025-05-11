    package com.example.demo.Controllers;

    import com.example.demo.model.Candidature;
    import com.example.demo.model.Candidat;
    import com.example.demo.model.Offre;
    import com.example.demo.service.CandidatureService;
    import com.example.demo.service.CandidatService;
    import com.example.demo.service.OffreService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.time.LocalDateTime;
    import java.util.UUID;

    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/candidatures")
    public class CandidatureController {

        private final CandidatureService candidatureService;
        private final OffreService offreService;
        private final CandidatService candidatService;

        // Afficher le formulaire de candidature pour une offre
        @GetMapping("/postuler/{offreId}")
        public String afficherFormulairePostulation(@PathVariable Long offreId, Model model) {
            Offre offre = offreService.findById(offreId)
                    .orElseThrow(() -> new RuntimeException("Offre introuvable avec l'ID : " + offreId));

            model.addAttribute("offre", offre);
            return "candidat/postuler";  // Vue Thymeleaf à créer
        }

        // Traiter le formulaire de candidature et enregistrer la candidature dans la base de données
        @PostMapping("/postuler")
        public String postuler(@RequestParam Long offreId,
                               @RequestParam String lettreMotivation,
                               @RequestParam MultipartFile cv,
                               @AuthenticationPrincipal UserDetails userDetails) throws IOException {

            // Récupérer l'offre en fonction de son ID
            Offre offre = offreService.findById(offreId)
                    .orElseThrow(() -> new RuntimeException("Offre introuvable avec l'ID : " + offreId));

            // Récupérer le candidat en fonction de son email
            String email = userDetails.getUsername();
            Candidat candidat = candidatService.getCandidatByEmail(email);

            // Créer une nouvelle candidature
            Candidature candidature = new Candidature();
            candidature.setOffre(offre);
            candidature.setCandidat(candidat);
            candidature.setLettreMotivation(lettreMotivation);
            candidature.setCvUrl(saveCv(cv));  // Sauvegarde du CV
            candidature.setStatut("EN ATTENTE");  // Définir un statut par défaut
            candidature.setMatchingScore(null);  // Laisser vide pour l'instant

            // Sauvegarder la candidature dans la base de données
            candidatureService.save(candidature);

            // Rediriger vers la page "mes-candidatures" du candidat après soumission
            return "redirect:/candidatures/mes_candidatures";
        }


        // Méthode pour sauvegarder le CV sur le serveur
        private String saveCv(MultipartFile cv) throws IOException {
            if (cv.isEmpty()) {
                throw new IOException("Le fichier CV est vide");
            }

            String contentType = cv.getContentType();
            if (!(contentType.equals("application/pdf") ||
                    contentType.equals("application/msword") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                throw new IOException("Type de fichier non supporté. Seuls les fichiers PDF et Word sont autorisés.");
            }

            // Nom unique pour le fichier
            String originalFilename = cv.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            // Chemin où le fichier sera enregistré
            Path path = Paths.get("src/main/resources/static/cvs/" + uniqueFileName);

            // Créer le dossier s'il n'existe pas
            File directory = new File("src/main/resources/static/cvs/");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Sauvegarder le fichier sur le serveur
            cv.transferTo(path);

            // Retourner l'URL relative du fichier pour l'enregistrer dans la base de données
            return "/cvs/" + uniqueFileName;
        }

        // Afficher toutes les candidatures du candidat connecté
        @GetMapping("/mes_candidatures")
        public String voirMesCandidatures(Model model,
                                          @AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails.getUsername();
            Candidat candidat = candidatService.getCandidatByEmail(email);

            // Récupérer toutes les candidatures du candidat
            var candidatures = candidatureService.findByCandidat(candidat);

            model.addAttribute("candidatures", candidatures);
            return "candidat/mescandidatures";  // Vue Thymeleaf à créer
        }


    }
