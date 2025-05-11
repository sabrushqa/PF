package com.example.demo.service;

import com.example.demo.Repository.CandidatRepository;
import com.example.demo.Repository.RecruteurRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.Candidat;
import com.example.demo.model.Recruteur;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Méthode d'enregistrement pour le candidat
    public void registerCandidat(Candidat candidat) {
        // Validation des mots de passe
        validatePasswords(candidat.getPassword(), candidat.getConfirmPassword());

        // Validation de l'email
        String email = candidat.getUser().getEmail();
        checkEmailAvailability(email);

        // Si email est null ou vide
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        // Création de l'utilisateur
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(candidat.getPassword()));
        user.setRole(Role.CANDIDAT);

        // Sauvegarde de l'utilisateur
        userRepository.save(user);

        // Associer l'utilisateur au candidat
        candidat.setUser(user);
        candidatRepository.save(candidat);
    }

    // Méthode d'enregistrement pour le recruteur
    public void registerRecruteur(Recruteur recruteur) {
        // Validation des mots de passe
        validatePasswords(recruteur.getPassword(), recruteur.getConfirmPassword());

        // Validation de l'email
        String email = recruteur.getUser().getEmail();
        checkEmailAvailability(email);

        // Si email est null ou vide
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        // Création de l'utilisateur
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(recruteur.getPassword()));
        user.setRole(Role.RECRUTEUR);

        // Sauvegarde de l'utilisateur
        userRepository.save(user);

        // Associer l'utilisateur au recruteur
        recruteur.setUser(user);
        recruteurRepository.save(recruteur);
    }

    // Vérification de la correspondance des mots de passe
    private void validatePasswords(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }
    }

    // Vérification de la disponibilité de l'email
    private void checkEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email.");
        }
    }
}
