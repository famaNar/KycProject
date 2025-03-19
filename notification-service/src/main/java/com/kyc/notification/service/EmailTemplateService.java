package com.kyc.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public String generateAccountCreatedEmailContent(String fullName, String numeroCompte, String typeCompte, String devise) {
        return String.format("""
            <div style='font-family: Arial, sans-serif; padding: 20px;'>
                <h2>Nouveau Compte Créé - %s</h2>
                <p>Cher(e) %s,</p>
                <p>Votre nouveau compte a été créé avec succès :</p>
                <div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>
                    <p><strong>Numéro de compte :</strong> %s</p>
                    <p><strong>Type de compte :</strong> %s</p>
                    <p><strong>Devise :</strong> %s</p>
                </div>
                <p>Vous pouvez dès maintenant effectuer des opérations sur ce compte.</p>
                <p>Cordialement,<br>L'équipe KYC</p>
            </div>
            """, typeCompte, fullName, numeroCompte, typeCompte, devise);
    }

    public String generateTransactionEmailContent(String fullName, String numeroCompte, BigDecimal montant, 
                                                BigDecimal nouveauSolde, String typeOperation, String devise) {
        String operationText = typeOperation.equals("CREDIT") ? "Dépôt effectué" : "Retrait effectué";
        String signeMontant = typeOperation.equals("CREDIT") ? "+" : "-";
        
        return String.format("""
            <div style='font-family: Arial, sans-serif; padding: 20px;'>
                <h2>%s</h2>
                <p>Cher(e) %s,</p>
                <p>Une opération a été effectuée sur votre compte :</p>
                <div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>
                    <p><strong>Compte :</strong> %s</p>
                    <p><strong>Montant :</strong> <span style='color: %s;'>%s%s %s</span></p>
                    <p><strong>Nouveau solde :</strong> %s %s</p>
                </div>
                <p style='font-size: 0.9em; color: #6c757d;'>
                    Si vous n'êtes pas à l'origine de cette opération, contactez-nous immédiatement.
                </p>
                <p>Cordialement,<br>L'équipe KYC</p>
            </div>
            """, 
            operationText,
            fullName, 
            numeroCompte,
            typeOperation.equals("CREDIT") ? "#28a745" : "#dc3545",
            signeMontant,
            montant,
            devise,
            nouveauSolde,
            devise);
    }

    public String generateAccountStatusEmailContent(String fullName, String numeroCompte, String nouveauStatut) {
        String statusMessage = switch(nouveauStatut) {
            case "BLOQUE" -> "Votre compte a été temporairement bloqué. Veuillez nous contacter pour plus d'informations.";
            case "FERME" -> "Votre compte a été fermé. Si vous pensez qu'il s'agit d'une erreur, contactez-nous.";
            case "ACTIF" -> "Votre compte est maintenant actif et peut être utilisé normalement.";
            default -> "Le statut de votre compte a été modifié.";
        };

        return String.format("""
            <div style='font-family: Arial, sans-serif; padding: 20px;'>
                <h2>Modification du Statut de Compte</h2>
                <p>Cher(e) %s,</p>
                <div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>
                    <p><strong>Compte :</strong> %s</p>
                    <p><strong>Nouveau statut :</strong> %s</p>
                </div>
                <p>%s</p>
                <p>Cordialement,<br>L'équipe KYC</p>
            </div>
            """, 
            fullName,
            numeroCompte,
            nouveauStatut,
            statusMessage);
    }
}
