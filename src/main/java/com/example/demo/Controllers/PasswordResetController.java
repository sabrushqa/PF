package com.example.demo.Controllers;

import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final UserService userService;
    private final EmailService emailService;

    /**
     * Affiche le formulaire où l'utilisateur entre son email pour réinitialiser le mot de passe.
     */
    @GetMapping("/password-reset")
    public String showPasswordResetRequestForm() {
        return "password-reset-request";
    }

    /**
     * Traite la demande de réinitialisation de mot de passe.
     */
    @PostMapping("/password-reset")
    public String handlePasswordResetRequest(@RequestParam("email") String email, Model model) {
        try {
            // Génère le token
            String token = userService.generatePasswordResetToken(email);

            // Crée le lien de réinitialisation
            String resetLink = "http://localhost:8080/password-reset-form?token=" + token;

            // Envoie l'email
            emailService.sendEmail(
                    email,
                    "🔐 Réinitialisation de votre mot de passe",
                    "<p>Bonjour,</p>" +
                            "<p>Pour réinitialiser votre mot de passe, cliquez sur le lien ci-dessous :</p>" +
                            "<p><a href=\"" + resetLink + "\">Réinitialiser le mot de passe</a></p>" +
                            "<p>Ce lien est valable temporairement.</p>"
            );

            model.addAttribute("message", "Un lien de réinitialisation a été envoyé à votre adresse email.");
            return "password-reset-link-sent";
        } catch (RuntimeException | MessagingException e) {
            model.addAttribute("error", e.getMessage());
            return "password-reset-request";
        }
    }

    /**
     * Affiche le formulaire de saisie du nouveau mot de passe.
     */
    @GetMapping("/password-reset-form")
    public String showPasswordResetForm(@RequestParam("token") String token, Model model) {
        if (!userService.isResetTokenValid(token)) {
            model.addAttribute("error", "Lien invalide ou expiré.");
            return "password-reset-request";
        }
        model.addAttribute("token", token);
        return "password-reset-form";
    }

    /**
     * Traite le formulaire de nouveau mot de passe.
     */
    @PostMapping("/password-reset-form")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("newPassword") String newPassword,
                                      Model model) {
        try {
            userService.resetPassword(token, newPassword);
            return "password-reset-success";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Jeton invalide ou expiré.");
            return "password-reset-form";
        }
    }
}
