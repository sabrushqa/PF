package com.example.demo.service;

import com.example.demo.Repository.UserRepository;
import com.example.demo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Vérifie si un token de réinitialisation est valide.
     */
    public boolean isResetTokenValid(String token) {
        return userRepository.findByResetToken(token).isPresent();
    }
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    /**
     * Génère un jeton de réinitialisation de mot de passe et l'associe à l'utilisateur.
     */
    public String generatePasswordResetToken(String email) {
        String token = UUID.randomUUID().toString(); // Génère un jeton unique

        // Cherche l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec cet e-mail"));

        user.setResetToken(token); // Associe le token à l'utilisateur
        userRepository.save(user); // Sauvegarde l'utilisateur avec le token

        return token;
    }

    /**
     * Réinitialise le mot de passe de l'utilisateur via le token.
     */
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Jeton de réinitialisation invalide"));

        // Encode le nouveau mot de passe avant de le sauvegarder
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Supprime le token après usage
        userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec cet e-mail"));
    }

}
