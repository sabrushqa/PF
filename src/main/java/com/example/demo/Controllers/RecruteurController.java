package com.example.demo.Controllers;

import com.example.demo.model.*;
import com.example.demo.Repository.*;
import com.example.demo.service.CandidatureService;
import com.example.demo.service.EntretienService;
import com.example.demo.service.RecruteurService;
import com.example.demo.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recruteur")
public class RecruteurController {

    private final RecruteurService recruteurService;
    private final CandidatureService candidatureService;
    private final EntretienService entretienService;
    private final OffreRepository offreRepository;
    private final CandidatureRepository candidatureRepository;
    private final EntretienRepository entretienRepository;

    public RecruteurController(RecruteurService recruteurService,
                               CandidatureService candidatureService,
                               EntretienService entretienService,
                               OffreRepository offreRepository,
                               CandidatureRepository candidatureRepository,
                               EntretienRepository entretienRepository) {
        this.recruteurService = recruteurService;
        this.candidatureService = candidatureService;
        this.entretienService = entretienService;
        this.offreRepository = offreRepository;
        this.candidatureRepository = candidatureRepository;
        this.entretienRepository = entretienRepository;
    }

    @GetMapping("/home")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Recruteur recruteur = recruteurService.getRecruteurByEmail(email);
        model.addAttribute("recruteur", recruteur);

        List<Offre> offres = offreRepository.findByRecruteur(recruteur);
        List<Candidature> candidatures = candidatureRepository.findByOffre_Recruteur(recruteur);
        List<Entretien> entretiens = entretienRepository.findByCandidature_Offre_Recruteur(recruteur);

        long totalOffres = offres.size();
        long totalCandidatures = candidatures.size();
        long totalAcceptees = countByStatut(candidatures, "Acceptée");
        long totalRefusees = countByStatut(candidatures, "Refusée");
        long totalValidees = countByStatut(candidatures, "Valider");
        long totalAttente = countByStatut(candidatures, "EN ATTENTE");

        // Calcul des pourcentages pour le pie chart
        double tauxValidees = totalCandidatures > 0 ? (totalValidees * 100.0 / totalCandidatures) : 0;
        double tauxRefusees = totalCandidatures > 0 ? (totalRefusees * 100.0 / totalCandidatures) : 0;
        double tauxAutres = totalCandidatures > 0 ? (100 - tauxValidees - tauxRefusees) : 0;

        // Création de la Map pour les données du graphique
        Map<String, Object> pieChartData = new HashMap<>();
        pieChartData.put("validees", totalValidees);
        pieChartData.put("refusees", totalRefusees);
        pieChartData.put("autres", totalCandidatures - totalValidees - totalRefusees);
        pieChartData.put("tauxValidees", Math.round(tauxValidees));
        pieChartData.put("tauxRefusees", Math.round(tauxRefusees));
        pieChartData.put("tauxAutres", Math.round(tauxAutres));

        model.addAttribute("pieChartData", pieChartData);

        model.addAttribute("totalOffres", totalOffres);
        model.addAttribute("totalCandidatures", totalCandidatures);
        model.addAttribute("totalAcceptées", totalAcceptees);
        model.addAttribute("totalRefusées", totalRefusees);
        model.addAttribute("totalValidées", totalValidees);
        model.addAttribute("totalAttente", totalAttente);

        model.addAttribute("dernieresCandidatures", getLastItems(candidatures, 5));
        model.addAttribute("dernieresOffres", getLastItems(offres, 5));
        model.addAttribute("derniersEntretiens", getUpcomingInterviews(entretiens, 5));


        // Candidatures non vues
        List<Candidature> nouvellesCandidatures = candidatureRepository.findByOffre_RecruteurAndVueParRecruteurFalse(recruteur);

        // Entretiens à venir
        List<Entretien> entretiensAVenir = entretienRepository.findTop5ByCandidature_Offre_RecruteurOrderByDateEntretienDesc(recruteur);

        model.addAttribute("nouvellesCandidatures", nouvellesCandidatures);
        model.addAttribute("entretiensAVenir", entretiensAVenir);
        return "recruteur/home";
    }

    @PostMapping("/notifications/lues")
    @ResponseBody
    public ResponseEntity<?> marquerNotificationsCommeVues(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Recruteur recruteur = recruteurService.getRecruteurByEmail(email);

        List<Entretien> entretiensAVenir = entretienRepository
                .findByCandidature_Offre_RecruteurAndDateEntretienAfterAndVuParRecruteurFalse(recruteur, LocalDateTime.now());

        for (Entretien e : entretiensAVenir) {
            e.setVuParRecruteur(true);
        }
        entretienRepository.saveAll(entretiensAVenir);

        List<Candidature> nouvellesCandidatures = candidatureRepository.findByOffre_RecruteurAndVueParRecruteurFalse(recruteur);
        for (Candidature candidature : nouvellesCandidatures) {
            candidature.setVueParRecruteur(true);
        }
        candidatureRepository.saveAll(nouvellesCandidatures);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/candidatures")
    public String voirCandidaturesDeMesOffres(Model model,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Recruteur recruteur = recruteurService.getRecruteurByEmail(email);

        List<Candidature> candidatures = candidatureService.getCandidaturesByRecruteur(recruteur);
        model.addAttribute("candidatures", candidatures);

        return "recruteur/candidatures_offres";
    }


    @PostMapping("/candidatures/modifier/{id}")
    public String modifierCandidature(@PathVariable Long id,
                                      @RequestParam("statut") String statut,
                                      @RequestParam(value = "matchingScore", required = false) Double matchingScore) throws MessagingException {

        Candidature candidature = candidatureService.getById(id); // ✅ Variable bien définie ici
        if (candidature != null) {
            candidature.setStatut(statut);
            candidature.setMatchingScore(matchingScore);

            if ("Acceptée".equalsIgnoreCase(statut)) {
                // ✅ Récupération de l'email depuis le User lié au candidat
                String email = candidature.getCandidat().getUser().getEmail();

                // ✅ Appel correct avec les deux arguments requis
                entretienService.envoyerLienZoomPourEntretien(candidature, email);
            }
        }

        return "redirect:/recruteur/candidatures";
    }


    @GetMapping("/stats/candidatures-par-mois")
    @ResponseBody
    public Map<String, Object> getCandidaturesParMois(@RequestParam(name = "year") int annee,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Recruteur recruteur = recruteurService.getRecruteurByEmail(email);
        List<Candidature> candidatures = candidatureRepository.findByOffre_Recruteur(recruteur);

        // Initialisation des mois avec tous les statuts à 0
        Map<String, Map<String, Long>> stats = new LinkedHashMap<>();
        for (int mois = 1; mois <= 12; mois++) {
            String moisStr = String.format("%02d", mois);
            Map<String, Long> statuts = new HashMap<>();
            statuts.put("Valider", 0L);
            statuts.put("Refusée", 0L);
            statuts.put("En Attente", 0L);
            statuts.put("Acceptée", 0L);
            stats.put(moisStr, statuts);
        }

        // Remplissage des statistiques
        for (Candidature c : candidatures) {
            if (c.getDateCandidature() != null && c.getStatut() != null
                    && c.getDateCandidature().getYear() == annee) {
                String moisStr = String.format("%02d", c.getDateCandidature().getMonthValue());
                String statut = c.getStatut();

                // Vérifie si le statut est bien prévu dans la carte (évite les erreurs inattendues)
                if (stats.get(moisStr).containsKey(statut)) {
                    stats.get(moisStr).merge(statut, 1L, Long::sum);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        return response;
    }

    private long countByStatut(List<Candidature> candidatures, String statut) {
        return candidatures.stream()
                .filter(c -> statut.equalsIgnoreCase(c.getStatut()))
                .count();
    }

    private <T> List<T> getLastItems(List<T> items, int count) {
        return items.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private List<Entretien> getUpcomingInterviews(List<Entretien> entretiens, int count) {
        return entretiens.stream()
                .sorted(Comparator.comparing(Entretien::getDateEntretien))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Autowired
    private UserService userService;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @GetMapping("/profil")
    public String afficherProfilEtChangerMotdepasse(Model model, Principal principal) {
        // Récupère l'email du recruteur actuellement connecté
        String email = principal.getName();

        // Recherche le recruteur dans la base de données
        Recruteur recruteur = recruteurRepository.findByUserEmail(email).orElse(null);

        // Ajoute le recruteur au modèle pour l'afficher dans la vue
        model.addAttribute("recruteur", recruteur);
        model.addAttribute("user", userService.findByEmail(email));

        return "recruteur/profil";  // Affiche la vue profil
    }

    @PostMapping("/profil")
    public String modifierProfilEtChangerMotdepasse(@RequestParam(required = false) String ancienPassword,
                                                    @RequestParam(required = false) String nouveauPassword,
                                                    @ModelAttribute Recruteur recruteurForm,
                                                    Principal principal,
                                                    RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Recruteur recruteur = recruteurRepository.findByUserEmail(email).orElse(null);

        // Modification des informations du profil
        if (recruteur != null) {
            recruteur.setEntreprise(recruteurForm.getEntreprise());
            recruteur.setPoste(recruteurForm.getPoste());
            recruteur.setTelephone(recruteurForm.getTelephone());
            recruteur.setSiteWeb(recruteurForm.getSiteWeb());
            recruteur.setSecteur(recruteurForm.getSecteur());
            recruteurRepository.save(recruteur);
        }

        // Changement de mot de passe (si l'ancien mot de passe et le nouveau sont fournis)
        if (ancienPassword != null && nouveauPassword != null) {
            User user = userService.findByEmail(email);
            if (userService.checkPassword(user, ancienPassword)) {
                userService.changePassword(user, nouveauPassword);
                redirectAttributes.addFlashAttribute("success", "Mot de passe changé avec succès.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Ancien mot de passe incorrect.");
            }
        }

        redirectAttributes.addFlashAttribute("success", "Profil modifié avec succès.");
        return "redirect:/recruteur/profil";
    }
}
