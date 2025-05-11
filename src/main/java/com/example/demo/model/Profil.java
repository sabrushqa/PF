package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profils")
@Data
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;
    @Column(name = "photo")
    private String photo;  // Peut contenir le nom du fichier ou un chemin relatif


    @OneToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @OneToMany(mappedBy = "profil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "profil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Formation> formations = new ArrayList<>();

    @OneToMany(mappedBy = "profil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Langue> langues = new ArrayList<>();
}
