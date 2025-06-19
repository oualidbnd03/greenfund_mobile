package com.crowdfundpro.android.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Classe utilitaire pour la validation des formulaires
 */
public class ValidationUtils {
    
    /**
     * Validation du nom d'utilisateur
     */
    public static ValidationResult validateUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return new ValidationResult(false, "Le nom d'utilisateur est obligatoire");
        }
        
        if (username.length() < 3) {
            return new ValidationResult(false, "Le nom d'utilisateur doit contenir au moins 3 caractères");
        }
        
        if (username.length() > 30) {
            return new ValidationResult(false, "Le nom d'utilisateur ne peut pas dépasser 30 caractères");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return new ValidationResult(false, "Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Validation de l'email
     */
    public static ValidationResult validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return new ValidationResult(false, "L'email est obligatoire");
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return new ValidationResult(false, "Format d'email invalide");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Validation du mot de passe
     */
    public static ValidationResult validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return new ValidationResult(false, "Le mot de passe est obligatoire");
        }
        
        if (password.length() < 6) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins 6 caractères");
        }
        
        if (password.length() > 128) {
            return new ValidationResult(false, "Le mot de passe ne peut pas dépasser 128 caractères");
        }
        
        // Vérifier la complexité du mot de passe
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (!hasLetter || !hasDigit) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins une lettre et un chiffre");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Validation de la confirmation du mot de passe
     */
    public static ValidationResult validatePasswordConfirmation(String password, String confirmPassword) {
        if (TextUtils.isEmpty(confirmPassword)) {
            return new ValidationResult(false, "La confirmation du mot de passe est obligatoire");
        }
        
        if (!password.equals(confirmPassword)) {
            return new ValidationResult(false, "Les mots de passe ne correspondent pas");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Validation du montant d'investissement
     */
    public static ValidationResult validateInvestmentAmount(String amountStr, double minAmount, double maxAmount) {
        if (TextUtils.isEmpty(amountStr)) {
            return new ValidationResult(false, "Le montant est obligatoire");
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Montant invalide");
        }
        
        if (amount <= 0) {
            return new ValidationResult(false, "Le montant doit être positif");
        }
        
        if (amount < minAmount) {
            return new ValidationResult(false, String.format("Le montant minimum est de %.2f €", minAmount));
        }
        
        if (amount > maxAmount) {
            return new ValidationResult(false, String.format("Le montant maximum est de %.2f €", maxAmount));
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Validation du titre de projet
     */
    public static ValidationResult validateProjectTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return new ValidationResult(false, "Le titre est obligatoire");
        }
        
        if (title.length() < 5) {
            return new ValidationResult(false, "Le titre doit contenir au moins 5 caractères");
        }
        
        if (title.length() > 100) {
            return new ValidationResult(false, "Le titre ne peut pas dépasser 100 caractères");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Validation de la description de projet
     */
    public static ValidationResult validateProjectDescription(String description) {
        if (TextUtils.isEmpty(description)) {
            return new ValidationResult(false, "La description est obligatoire");
        }
        
        if (description.length() < 50) {
            return new ValidationResult(false, "La description doit contenir au moins 50 caractères");
        }
        
        if (description.length() > 5000) {
            return new ValidationResult(false, "La description ne peut pas dépasser 5000 caractères");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Classe pour représenter le résultat d'une validation
     */
    public static class ValidationResult {
        private boolean isValid;
        private String errorMessage;
        
        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

