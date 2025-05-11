package com.example.demo.service;

import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.model.Offre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);
    private final CandidatureService candidatureService;

    /**
     * Calcule le score de matching entre un CV de candidat et une offre d'emploi
     * @param candidature La candidature contenant la référence au candidat et à l'offre
     * @return Le score de matching entre 0 et 100
     */
    public double calculateMatchingScore(Candidature candidature) {
        Candidat candidat = candidature.getCandidat();
        Offre offre = candidature.getOffre();

        // Extraction des mots-clés du CV
        Set<String> cvKeywords = extractKeywords(
                candidat.getFormation(),
                candidat.getDiplome(),
                candidat.getSpecialite(),
                candidat.getDescription(),
                candidature.getCv()
        );
        System.out.println(cvKeywords);

        // Extraction des mots-clés de l'offre
        Set<String> offreKeywords = extractKeywords(
                offre.getTitre(),
                offre.getDescription(),
                offre.getNiveauExperience(),
                offre.getSecteur()
        );
        System.out.println(offreKeywords);

        // Calcul du score basé sur différents critères
        double keywordMatchScore = calculateKeywordMatchScore(cvKeywords, offreKeywords);
        double sectorMatchScore = calculateSectorMatch(candidat.getSpecialite(), offre.getSecteur());
        double experienceMatchScore = calculateExperienceMatch(candidat.getDescription(), offre.getNiveauExperience());

        // Nouvelle pondération
        double finalScore = (keywordMatchScore * 0.5) + (sectorMatchScore * 0.3) + (experienceMatchScore * 0.2);

// Amplification douce ajustée pour donner plus de poids aux correspondances
        finalScore = Math.pow(finalScore / 100, 0.6) * 100;  // Augmentation plus marquée mais contrôlée

// Arrondir à une décimale
        finalScore = (Math.round(finalScore * 10) / 10.0)*1.5;

        // Journaliser les détails du matching pour le débogage
        logger.info("Matching score details for candidature {}: keywords={}, sector={}, experience={}, final={}",
                candidature.getId(), keywordMatchScore, sectorMatchScore, experienceMatchScore, finalScore);

// Mettre à jour le score dans la candidature
        candidature.setMatchingScore(finalScore);
        candidatureService.save(candidature);


        return finalScore;
    }

    /**
     * Extrait les mots-clés significatifs d'un ou plusieurs textes
     */
    private Set<String> extractKeywords(String... texts) {
        Set<String> keywords = new HashSet<>();

        // Liste de mots vides (à ignorer)
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "le", "la", "les", "un", "une", "des", "et", "ou", "de", "du", "en", "à", "au", "aux",
                "avec", "ce", "cette", "ces", "mon", "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses",
                "notre", "nos", "votre", "vos", "leur", "leurs", "pour", "par", "sur", "dans", "chez",
                "qui", "que", "quoi", "dont", "où", "comment", "pourquoi", "quand"
        ));

        for (String text : texts) {
            if (text == null) continue;

            // Convertir en minuscules et supprimer les caractères spéciaux
            String cleanText = text.toLowerCase().replaceAll("[^a-zàáâäçèéêëìíîïñòóôöùúûü0-9\\s]", " ");

            // Découper le texte en mots
            String[] words = cleanText.split("\\s+");

            for (String word : words) {
                // Ignorer les mots vides et les mots trop courts
                if (!stopWords.contains(word) && word.length() > 2) {
                    keywords.add(word);
                }
            }
        }

        return keywords;
    }

    /**
     * Calcule le score de correspondance entre les mots-clés du CV et de l'offre
     */
    private double calculateKeywordMatchScore(Set<String> cvKeywords, Set<String> offreKeywords) {
        if (offreKeywords.isEmpty()) return 0;

        int matches = 0;

        // Compter les correspondances
        for (String keyword : cvKeywords) {
            if (offreKeywords.contains(keyword)) {
                matches++;
            }
        }

        // Calculer le pourcentage de correspondance (maximum 100%)
        return Math.min(100, (matches * 100.0) / offreKeywords.size());
    }

    /**
     * Calcule le score de correspondance entre la spécialité du candidat et le secteur de l'offre
     */
    private double calculateSectorMatch(String specialite, String secteur) {
        if (specialite == null || secteur == null) return 0;

        // Convertir en minuscules pour la comparaison
        specialite = specialite.toLowerCase();
        secteur = secteur.toLowerCase();

        // Correspondance exacte
        if (specialite.equals(secteur)) {
            return 100;
        }

        // Correspondance partielle (si le secteur contient la spécialité ou vice versa)
        if (specialite.contains(secteur) || secteur.contains(specialite)) {
            return 75;
        }

        // Correspondance de mots-clés
        Set<String> specialiteKeywords = extractKeywords(specialite);
        Set<String> secteurKeywords = extractKeywords(secteur);

        return calculateKeywordMatchScore(specialiteKeywords, secteurKeywords);
    }

    /**
     * Évalue la correspondance entre l'expérience du candidat et le niveau requis dans l'offre
     */
    private double calculateExperienceMatch(String cvDescription, String niveauExperience) {
        if (cvDescription == null || niveauExperience == null) return 0;

        cvDescription = cvDescription.toLowerCase();
        niveauExperience = niveauExperience.toLowerCase();

        // Recherche d'années d'expérience dans le CV
        int yearsOfExperience = extractYearsOfExperience(cvDescription);

        // Score basé sur le niveau requis et l'expérience du candidat
        if (niveauExperience.contains("junior") || niveauExperience.contains("débutant")) {
            return yearsOfExperience <= 2 ? 100 : 75;
        } else if (niveauExperience.contains("confirmé") || niveauExperience.contains("intermédiaire")) {
            return (yearsOfExperience >= 2 && yearsOfExperience <= 5) ? 100 :
                    (yearsOfExperience > 5) ? 90 : 50;
        } else if (niveauExperience.contains("senior") || niveauExperience.contains("expert")) {
            return yearsOfExperience >= 5 ? 100 :
                    (yearsOfExperience >= 3) ? 70 : 30;
        }

        return 50; // Score par défaut si le niveau n'est pas clairement défini
    }

    /**
     * Extrait le nombre d'années d'expérience mentionné dans un texte
     */
    private int extractYearsOfExperience(String text) {
        // Pattern pour trouver les mentions d'années d'expérience
        Pattern pattern = Pattern.compile("(\\d+)\\s*an(s)?\\s*(d['']expérience)?");
        Matcher matcher = pattern.matcher(text);

        int maxYears = 0;

        while (matcher.find()) {
            try {
                int years = Integer.parseInt(matcher.group(1));
                maxYears = Math.max(maxYears, years);
            } catch (NumberFormatException e) {
                // Ignorer si la conversion en nombre échoue
            }
        }

        return maxYears;
    }
}