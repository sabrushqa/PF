package com.example.demo.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String resetToken; // Champ pour stocker le jeton de réinitialisation

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean fromGoogle = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Candidat candidat;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recruteur recruteur;

    // === Implémentation de UserDetails ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + role.name());
    }

    @Override public String getUsername() { return email; }

    @Override public String getPassword() { return password; }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override public boolean isAccountNonLocked() { return true; }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override public boolean isEnabled() { return true; }

    // === Getters et Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isFromGoogle() {
        return fromGoogle;
    }

    public void setFromGoogle(boolean fromGoogle) {
        this.fromGoogle = fromGoogle;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Recruteur getRecruteur() {
        return recruteur;
    }

    public void setRecruteur(Recruteur recruteur) {
        this.recruteur = recruteur;
    }

    // === Ajout des méthodes pour gérer le resetToken ===

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)

    private List<Favori> favoris = new ArrayList<>();

}
