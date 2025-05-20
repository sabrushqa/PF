package com.example.demo.service;

import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.Repository.CandidatureRepository;
import com.example.demo.model.Recruteur;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;

    // Récupérer une candidature par son ID
    public Candidature getById(Long id) {
        return candidatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable avec l'ID : " + id));
    }

    // Sauvegarder une candidature (renvoie la candidature sauvegardée)
    public Candidature save(Candidature candidature) {
        if (candidature.getDateCandidature() == null) {
            candidature.setDateCandidature(LocalDate.now().atStartOfDay());  // Définir une date si elle est nulle
        }
        return candidatureRepository.save(candidature);  // Retourner la candidature après sauvegarde
    }

    // Trouver les candidatures d'un candidat
    public List<Candidature> findByCandidat(Candidat candidat) {
        return candidatureRepository.findByCandidat(candidat);
    }

    // Trouver les candidatures d'un recruteur
    public List<Candidature> getCandidaturesByRecruteur(Recruteur recruteur) {
        return candidatureRepository.findByOffre_Recruteur(recruteur);
    }

    // Accepter une candidature (mettre à jour son statut et ajouter des détails)
    public void accepterCandidature(Long candidatureId, LocalDateTime dateEntretien, String lienZoom) {
        Candidature candidature = getById(candidatureId);
        candidature.setStatut("ACCEPTEE");
        candidature.setDateEntretien(dateEntretien);
        candidature.setLienZoom(lienZoom);  // Si vous avez un lien Zoom
        candidatureRepository.save(candidature);
    }

    // Refuser une candidature
    public void refuserCandidature(Long candidatureId) {
        Candidature candidature = getById(candidatureId);
        candidature.setStatut("REFUSEE");
        candidatureRepository.save(candidature);
    }

    // Modifier le statut et le score de la candidature
    public void modifierCandidature(Long id, String statut, double score) {
        Candidature candidature = getById(id);
        candidature.setStatut(statut);
       // candidature.setMatchingScore(score);  // Ajout d'un score pour la candidature
        candidatureRepository.save(candidature);
    }
    public List<Candidature> findAll() {
        return candidatureRepository.findAll();
    }

    public List<Candidature> findByStatut(String statut) {
        return candidatureRepository.findByStatut(statut);
    }

    public long countByCandidat(Candidat candidat) {
        return candidatureRepository.countByCandidat(candidat);
    }

    public long countByCandidatAndStatut(Candidat candidat, String statut) {
        return candidatureRepository.countByCandidatAndStatut(candidat, statut);
    }

    public Map<String, Long> getCandidatureStats(Candidat candidat) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", countByCandidat(candidat));
        stats.put("acceptees", countByCandidatAndStatut(candidat, "ACCEPTEE"));
        stats.put("refusees", countByCandidatAndStatut(candidat, "REFUSEE"));
        stats.put("en_attente", countByCandidatAndStatut(candidat, "EN_ATTENTE"));
        return stats;
    }
}
