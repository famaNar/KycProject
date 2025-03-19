package com.kyc.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public String generateWelcomeEmailContent(String fullName, Long clientId) {
        String kycFormUrl = String.format("%s/kyc-form/%d", frontendUrl, clientId);
        
        return String.format("""
            <div style='font-family: Arial, sans-serif; padding: 20px;'>
                <h2>Bienvenue %s !</h2>
                <p>Merci de vous être inscrit sur notre plateforme KYC.</p>
                <p>Pour compléter votre inscription, veuillez suivre ces étapes :</p>
                <ol>
                    <li>Cliquez sur ce lien pour remplir votre formulaire KYC : 
                        <a href='%s' style='color: #007bff; text-decoration: none;'>Remplir le formulaire KYC</a>
                    </li>
                    <li>Téléchargez les documents requis</li>
                    <li>Effectuez la vérification faciale</li>
                </ol>
                <p style='background-color: #f8f9fa; padding: 10px; border-radius: 5px;'>
                    <strong>Important :</strong> Le lien ci-dessus est personnel. Ne le partagez avec personne.
                </p>
                <p>Notre équipe examinera votre dossier dès que possible.</p>
                <p>Cordialement,<br>L'équipe KYC</p>
            </div>
            """, fullName, kycFormUrl);
    }

    public String generateKycApprovalEmailContent(String fullName) {
        return String.format("""
            <div style='font-family: Arial, sans-serif; padding: 20px;'>
                <h2>Félicitations %s !</h2>
                <p>Votre KYC a été approuvé avec succès.</p>
                <p>Vous pouvez maintenant accéder à tous les services de notre plateforme.</p>
                <p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>
                <p>Cordialement,<br>L'équipe KYC</p>
            </div>
            """, fullName);
    }

    public String generateKycRejectionEmailContent(String fullName, String reason) {
        return String.format("""
            <div style='font-family: Arial, sans-serif; padding: 20px;'>
                <h2>Cher(e) %s,</h2>
                <p>Nous avons examiné votre dossier KYC et nous regrettons de vous informer que nous ne pouvons pas l'approuver pour le moment.</p>
                <p>Raison : %s</p>
                <p>Vous pouvez soumettre à nouveau votre dossier en tenant compte des points mentionnés ci-dessus.</p>
                <p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>
                <p>Cordialement,<br>L'équipe KYC</p>
            </div>
            """, fullName, reason);
    }
}
