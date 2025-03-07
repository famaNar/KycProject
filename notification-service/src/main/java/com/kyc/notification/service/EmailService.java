package com.kyc.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${server.port}")
    private String serverPort;

    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            variables.forEach(context::setVariable);
            
            String htmlContent = templateEngine.process(template, context);
            
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

    public void sendWelcomeEmail(String to, String fullName, Long clientId) {
        String subject = "Bienvenue sur notre plateforme KYC";
        String kycFormUrl = "http://localhost:8081/kyc/form?clientId=" + clientId;
        
        Map<String, Object> variables = Map.of(
                "fullName", fullName,
                "kycFormUrl", kycFormUrl
        );
        
        sendEmail(to, subject, "welcome", variables);
    }

    public void sendKycSubmittedEmail(String to, String fullName) {
        String subject = "Votre formulaire KYC a été soumis avec succès";
        
        Map<String, Object> variables = Map.of(
                "fullName", fullName
        );
        
        sendEmail(to, subject, "kyc-submitted", variables);
    }

    public void sendKycApprovedEmail(String to, String fullName) {
        String subject = "Votre KYC a été approuvé";
        
        Map<String, Object> variables = Map.of(
                "fullName", fullName
        );
        
        sendEmail(to, subject, "kyc-approved", variables);
    }

    public void sendKycRejectedEmail(String to, String fullName, String reason) {
        String subject = "Votre KYC a été rejeté";
        
        Map<String, Object> variables = Map.of(
                "fullName", fullName,
                "reason", reason
        );
        
        sendEmail(to, subject, "kyc-rejected", variables);
    }
}
