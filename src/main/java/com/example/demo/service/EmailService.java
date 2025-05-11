package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        logger.info("Tentative d'envoi d'email à: {}, sujet: {}", to, subject);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            // Ajouter un expéditeur par défaut si ce n'est pas déjà configuré
            try {
                helper.setFrom("noreply@votredomaine.com");
            } catch (Exception e) {
                logger.warn("Impossible de définir l'expéditeur par défaut: {}", e.getMessage());
                // On continue quand même, au cas où l'expéditeur est déjà configuré par défaut
            }

            logger.info("Email préparé, tentative d'envoi...");
            mailSender.send(message);
            logger.info("Email envoyé avec succès à {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email: {}", e.getMessage(), e);
            throw e; // Propager l'exception pour que l'appelant sache qu'il y a eu un problème
        }
    }

    public void sendInterviewConfirmation(String to, String candidateName, String recruiterName,
                                          String position, String date, String time, String zoomLink) throws MessagingException {
        logger.info("Préparation de l'email de confirmation d'entretien pour: {}", to);
        String subject = "Confirmation d'entretien pour le poste de " + position;

        String htmlBody =
                "<html>" +
                        "<body>" +
                        "<h2>Confirmation d'entretien</h2>" +
                        "<p>Bonjour " + candidateName + ",</p>" +
                        "<p>Nous sommes heureux de vous informer que votre candidature pour le poste de <strong>" + position + "</strong> a été retenue.</p>" +
                        "<p>" + recruiterName + " souhaite vous rencontrer pour un entretien le <strong>" + date + "</strong> à <strong>" + time + "</strong>.</p>" +
                        "<p>Veuillez utiliser le lien Zoom suivant pour rejoindre la réunion:</p>" +
                        "<p><a href='" + zoomLink + "'>" + zoomLink + "</a></p>" +
                        "<p>Si vous avez des questions ou si vous devez modifier l'heure de l'entretien, n'hésitez pas à nous contacter.</p>" +
                        "<p>Cordialement,<br>L'équipe de recrutement</p>" +
                        "</body>" +
                        "</html>";

        sendEmail(to, subject, htmlBody);
    }
    public void sendPasswordResetEmail(String to, String resetToken) throws MessagingException {
        logger.info("Envoi de l'email de réinitialisation du mot de passe à: {}", to);
        String subject = "Réinitialisation de votre mot de passe";

        // ✅ URL corrigée pour correspondre à celle du contrôleur
        String resetLink = "http://localhost:8080/password-reset-form?token=" + resetToken;

        String htmlBody =
                "<html>" +
                        "<body>" +
                        "<h2>Réinitialisation de mot de passe</h2>" +
                        "<p>Bonjour,</p>" +
                        "<p>Vous avez demandé à réinitialiser votre mot de passe. Cliquez sur le lien ci-dessous pour définir un nouveau mot de passe :</p>" +
                        "<p><a href='" + resetLink + "'>Réinitialiser mon mot de passe</a></p>" +
                        "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet e-mail.</p>" +
                        "<p>Cordialement,<br>L'équipe de support</p>" +
                        "</body>" +
                        "</html>";

        sendEmail(to, subject, htmlBody);
    }


}