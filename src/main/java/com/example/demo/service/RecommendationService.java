package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.Repository.CandidatRepository;
import com.example.demo.Repository.OffreRepository;
import com.example.demo.Repository.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final OffreRepository offreRepository;
    private final CandidatRepository candidatRepository;
    private final ProfilRepository profilRepository;
    private final AISimilarityService aiSimilarityService;

    @Autowired
    public RecommendationService(OffreRepository offreRepository, CandidatRepository candidatRepository,
                                 ProfilRepository profilRepository, AISimilarityService aiSimilarityService) {
        this.offreRepository = offreRepository;
        this.candidatRepository = candidatRepository;
        this.profilRepository = profilRepository;
        this.aiSimilarityService = aiSimilarityService;
    }

    public List<Offre> recommendOffersForCandidate(Long candidatId) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

        // Récupération du profil du candidat
        Profil profil = profilRepository.findByCandidatId(candidatId)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé pour le candidat"));

        // Récupérer toutes les offres non expirées
        List<Offre> offres = offreRepository.findByDateExpirationAfter(LocalDate.now());

        // Log pour vérifier que les offres ont bien été récupérées
        System.out.println("Offres récupérées avant filtrage : " + offres.size());

        // Calculer les scores pour toutes les offres
        offres = offres.stream()
                .map(offer -> {
                    // Calcul du score de base (critères exacts)
                    double score = calculateBaseMatchScore(profil.getCandidat(), profil, offer);

                    // Ajout du score de similarité sémantique via l'API
                    if (profil.getBio() != null && !profil.getBio().isEmpty() && offer.getDescription() != null) {
                        try {
                            // Utiliser l'API de similarité pour comparer la description de l'offre avec le profil
                            double similarityScore = aiSimilarityService.getSimilarity(
                                    profil.getBio(),
                                    offer.getDescription()
                            );
                            // Le poids de la similarité sémantique est augmenté puisque c'est notre focus principal
                            score += similarityScore * 0.5;
                        } catch (Exception e) {
                            System.out.println("Erreur lors du calcul de similarité: " + e.getMessage());
                            // Dans cette version, nous considérons que l'API est critique
                            // Si elle échoue, l'offre reçoit un score plus bas
                            score *= 0.7; // Réduction du score car la composante principale (API) est absente
                        }
                    }

                    System.out.println("Score pour l'offre \"" + offer.getTitre() + "\" : " + score);
                    offer.setMatchScore(score);
                    return offer;
                })
                .sorted(Comparator.comparingDouble(Offre::getMatchScore).reversed())
                .collect(Collectors.toList());

        // Prendre les 5 meilleures offres
        List<Offre> bestOffers = offres.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Si aucune offre n'a été trouvée, prendre au moins les 3 premières sans filtre
        if (bestOffers.isEmpty() && !offres.isEmpty()) {
            bestOffers = offres.stream()
                    .limit(3)
                    .collect(Collectors.toList());
        }

        // Log pour vérifier la taille après filtrage
        System.out.println("Offres après filtrage : " + bestOffers.size());

        return bestOffers;
    }

    // Méthode pour calculer le score de base sans l'API de similarité
    private double calculateBaseMatchScore(Candidat candidat, Profil profil, Offre offre) {
        double score = 0.0;
        double maxScore = 0.5; // Score maximum possible réduit car nous priorisons l'API

        // Initialiser avec un score de base
        score = 0.05;

        // Vérification de la correspondance du secteur
        if (candidat.getFormation() != null && offre.getSecteur() != null &&
                containsIgnoreCase(offre.getSecteur(), candidat.getFormation())) {
            score += 0.1;
        } else if (candidat.getSpecialite() != null && offre.getSecteur() != null &&
                containsIgnoreCase(offre.getSecteur(), candidat.getSpecialite())) {
            score += 0.075;
        }

        // Vérification du niveau d'expérience
        if (candidat.getDiplome() != null && offre.getNiveauExperience() != null &&
                (containsIgnoreCase(offre.getNiveauExperience(), candidat.getDiplome()) ||
                        containsIgnoreCase(candidat.getDiplome(), offre.getNiveauExperience()))) {
            score += 0.1;
        }

        // Comparer les expériences du profil du candidat avec la description de l'offre
        if (profil.getExperiences() != null && offre.getDescription() != null) {
            for (Experience experience : profil.getExperiences()) {
                boolean matchFound = false;

                if (experience.getPoste() != null &&
                        containsIgnoreCase(offre.getDescription(), experience.getPoste())) {
                    score += 0.075;
                    matchFound = true;
                }

                if (experience.getEntreprise() != null &&
                        containsIgnoreCase(offre.getDescription(), experience.getEntreprise())) {
                    score += 0.05;
                    matchFound = true;
                }

                // Vérifier si le titre de l'offre correspond au poste de l'expérience
                if (experience.getPoste() != null && offre.getTitre() != null &&
                        containsIgnoreCase(offre.getTitre(), experience.getPoste())) {
                    score += 0.075;
                    matchFound = true;
                }

                if (matchFound) {
                    break;
                }
            }
        }

        // Comparer la formation du candidat avec les exigences de l'offre
        if (profil.getFormations() != null && offre.getDescription() != null) {
            for (Formation formation : profil.getFormations()) {
                if (formation.getDiplome() != null &&
                        containsIgnoreCase(offre.getDescription(), formation.getDiplome())) {
                    score += 0.075;
                    break;
                }
            }
        }

        // S'assurer que le score ne dépasse pas le maximum
        return Math.min(score, maxScore);
    }

    /**
     * Méthode utilitaire pour vérifier si une chaîne contient une autre, en ignorant la casse
     */
    private boolean containsIgnoreCase(String text, String searchTerm) {
        if (text == null || searchTerm == null) {
            return false;
        }

        // Normaliser les chaînes pour gérer les problèmes d'encodage
        String normalizedText = normalizeString(text);
        String normalizedSearchTerm = normalizeString(searchTerm);

        return normalizedText.toLowerCase().contains(normalizedSearchTerm.toLowerCase());
    }

    /**
     * Normalise une chaîne en remplaçant les caractères problématiques d'encodage
     */
    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }

        // Remplacer les caractères d'encodage problématiques
        return input.replace("‚", "é")
                .replace("Š", "è")
                .replace("Ž", "à");
    }
}