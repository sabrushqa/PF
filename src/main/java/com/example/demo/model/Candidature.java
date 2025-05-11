package com.example.demo.model;

import com.example.demo.model.Candidat;
import com.example.demo.model.Entretien;
import com.example.demo.model.Offre;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "candidatures")
@Data
public class Candidature {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "candidature",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<Entretien> entretiens;

    @ManyToOne(optional = false)
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCandidature = LocalDateTime.now();

    @Column(nullable = false)
    private String statut;

    private LocalDateTime dateEntretien; // Date du prochain/dernier entretien
    private String lienZoom;

    @Column(length = 5000)
    private String lettreMotivation;

    @Column(columnDefinition = "text")
    private String cv;

    @Column(name = "cv_url", columnDefinition = "text")
    private String cvUrl;

    private Double matchingScore;

    @Column(nullable = false)
    private Boolean vueParRecruteur = false;
}