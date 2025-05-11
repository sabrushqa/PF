package com.example.demo.model;

import java.util.List;

public class RecommandationRequest {
    private Profil profil;
    private List<Offre> offres;

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    public List<Offre> getOffres() {
        return offres;
    }

    public void setOffres(List<Offre> offres) {
        this.offres = offres;
    }

    // Getters & Setters
}

