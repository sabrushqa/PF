package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "candidats")
@Data
public class Candidat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom;
    private String telephone;
    private String adresse;

    private String formation;
    private String diplome;
    private String specialite;

    @Lob
    @Basic(fetch = FetchType.EAGER) // 🔥 important !
    private String description;

    @Transient
    private String confirmPassword;

    @OneToOne(fetch = FetchType.LAZY) // ✅ Important pour éviter le chargement automatique du User
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "candidat", cascade = CascadeType.ALL)
    private Profil profil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getDiplome() {
        return diplome;
    }


    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    public void setPassword(String password) {
        if (user != null) {
            user.setPassword(password);
        }
    }
}
