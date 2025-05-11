package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "candidatures")
@Data
@ToString(exclude = "entretiens")
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entretien> entretiens;

    // 🔗 Relation vers l'offre concernée
    @ManyToOne(optional = false)
    @JoinColumn(name = "offre_id")
    private Offre offre;

    // 🔗 Relation vers le candidat ayant postulé
    @ManyToOne(optional = false)
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    // 📅 Date et heure de la candidature
    @Column(nullable = false)
    @NotNull
    private LocalDateTime dateCandidature;

    // 📌 Statut de la candidature : En attente, Acceptée, Refusée...
    @Column(nullable = false)
    private String statut;

    // 📝 Lettre de motivation
    @Column(length = 5000)
    private String lettreMotivation;

    @Column(columnDefinition = "text")
    private String cv;

    @Column(name = "cv_url", columnDefinition = "text")
    private String cvUrl;
    // 🤖 Score IA de correspondance entre candidat et offre (calcul automatique)
    private Double matchingScore;


    // 📅 Date et heure de l'entretien
    private LocalDateTime dateEntretien;

    // 📎 Lien Zoom pour l'entretien
    private String lienZoom;

    public Candidature() {
        this.dateCandidature = LocalDateTime.now();  // Définit la date actuelle
    }
    // par défaut non vue
    @Column(name = "vue_par_recruteur", nullable = false)
    private Boolean vueParRecruteur = false;

}
