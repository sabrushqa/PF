package com.example.demo.Repository;

import com.example.demo.model.Offre;
import com.example.demo.model.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> {

    // Trouver les offres par ID de recruteur
    List<Offre> findByRecruteurId(Long recruteurId);

    // Trouver les offres par instance de recruteur
    List<Offre> findByRecruteur(Recruteur recruteur);

    // Récupérer les 5 dernières offres publiées par un recruteur donné
    List<Offre> findTop5ByRecruteurOrderByDatePublicationDesc(Recruteur recruteur);

    // Requête personnalisée : recherche dynamique avec filtres facultatifs
    @Query("""
        SELECT o FROM Offre o
        WHERE (:secteur IS NULL OR o.secteur = :secteur)
        AND (:typeContrat IS NULL OR o.typeContrat = :typeContrat)
        AND (:lieu IS NULL OR o.lieu = :lieu)
        AND (:entreprise IS NULL OR o.recruteur.entreprise = :entreprise)
        ORDER BY o.datePublication DESC
    """)
    List<Offre> rechercher(
            @Param("secteur") String secteur,
            @Param("typeContrat") String typeContrat,
            @Param("lieu") String lieu,
            @Param("entreprise") String entreprise
    );
    List<Offre> findByDateExpirationAfter(LocalDate date);

    // Requête simple dérivée (tous les paramètres doivent être non nuls)
    List<Offre> findByTitreAndLieuAndTypeContratAndRecruteur_Entreprise(
            String titre, String lieu, String typeContrat, String entreprise
    );
}
