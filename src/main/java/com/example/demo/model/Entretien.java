package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "entretiens")
@Data
@ToString(exclude = "candidature")

public class Entretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Relation avec la candidature
    @ManyToOne(optional = false)
    @JoinColumn(name = "candidature_id")
    private Candidature candidature;

    // 📅 Date et heure de l'entretien
    @Column(nullable = false)
    private LocalDateTime dateEntretien;


    // ⏳ Durée de l'entretien en minutes
    @Column(nullable = false)
    private int dureeMinutes;

    // 📎 Lien Zoom
    private String lienZoom;
    private boolean vuParRecruteur = false;


}
