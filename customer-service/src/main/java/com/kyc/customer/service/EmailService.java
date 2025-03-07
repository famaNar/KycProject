package com.kyc.customer.service;

import com.kyc.customer.entity.Client;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${server.port}")
    private String serverPort;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(Client client) {
        String subject = "Bienvenue sur notre plateforme KYC";
        String kycFormUrl = "http://localhost:" + serverPort + "/kyc/form?clientId=" + client.getId();
        
        String content = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Bienvenue " + client.getPrenom() + " " + client.getNom() + " !</h2>"
                + "<p>Merci de vous être inscrit sur notre plateforme.</p>"
                + "<p>Pour compléter votre processus KYC (Know Your Customer), veuillez cliquer sur le lien ci-dessous :</p>"
                + "<p><a href='" + kycFormUrl + "' style='display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;'>Compléter mon formulaire KYC</a></p>"
                + "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>"
                + "<p>Cordialement,<br>L'équipe KYC</p>"
                + "</div>";
        
        sendHtmlEmail(client.getEmail(), subject, content);
    }

    public void sendKycApprovedEmail(Client client) {
        String subject = "Votre KYC a été approuvé";
        
        String content = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Félicitations " + client.getPrenom() + " " + client.getNom() + " !</h2>"
                + "<p>Nous sommes heureux de vous informer que votre processus KYC a été approuvé avec succès.</p>"
                + "<p>Vous pouvez maintenant accéder à tous nos services financiers.</p>"
                + "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>"
                + "<p>Cordialement,<br>L'équipe KYC</p>"
                + "</div>";
        
        sendHtmlEmail(client.getEmail(), subject, content);
    }

    public void sendKycRejectedEmail(Client client, String reason) {
        String subject = "Votre KYC a été rejeté";
        
        String content = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Cher " + client.getPrenom() + " " + client.getNom() + ",</h2>"
                + "<p>Nous sommes désolés de vous informer que votre processus KYC a été rejeté.</p>"
                + "<p><strong>Raison du rejet :</strong> " + reason + "</p>"
                + "<p>Vous pouvez soumettre à nouveau votre demande en corrigeant les informations nécessaires.</p>"
                + "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>"
                + "<p>Cordialement,<br>L'équipe KYC</p>"
                + "</div>";
        
        sendHtmlEmail(client.getEmail(), subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Email envoyé avec succès à : {}", to);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à {} : {}", to, e.getMessage(), e);
        }
    }
}
