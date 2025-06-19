package com.crowdfundpro.android.data.api;

import com.crowdfundpro.android.data.models.Investment;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

/**
 * Interface Retrofit pour les appels API de gestion des investissements
 */
public interface InvestmentApiService {
    
    /**
     * Création d'un nouvel investissement
     */
    @POST("api/investments/")
    Call<Investment> createInvestment(@Header("Authorization") String token, @Body InvestmentRequest request);
    
    /**
     * Récupération de l'historique des investissements de l'utilisateur
     */
    @GET("api/investments/")
    Call<List<Investment>> getUserInvestments(@Header("Authorization") String token);
    
    /**
     * Récupération des détails d'un investissement
     */
    @GET("api/investments/{id}/")
    Call<Investment> getInvestment(@Header("Authorization") String token, @Path("id") int investmentId);
    
    /**
     * Récupération des investissements pour un projet spécifique
     */
    @GET("api/projects/{projectId}/investments/")
    Call<List<Investment>> getProjectInvestments(@Path("projectId") int projectId);
    
    /**
     * Confirmation de paiement Stripe
     */
    @POST("api/investments/{id}/confirm-payment/")
    Call<PaymentConfirmationResponse> confirmPayment(
        @Header("Authorization") String token,
        @Path("id") int investmentId,
        @Body PaymentConfirmationRequest request
    );
    
    /**
     * Annulation d'un investissement (si autorisé)
     */
    @POST("api/investments/{id}/cancel/")
    Call<Void> cancelInvestment(@Header("Authorization") String token, @Path("id") int investmentId);
    
    // Classes de requête et réponse
    class InvestmentRequest {
        private int project_id;
        private double amount;
        private String payment_method;
        
        public InvestmentRequest(int project_id, double amount, String payment_method) {
            this.project_id = project_id;
            this.amount = amount;
            this.payment_method = payment_method;
        }
        
        // Getters et setters
        public int getProjectId() { return project_id; }
        public void setProjectId(int project_id) { this.project_id = project_id; }
        
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        
        public String getPaymentMethod() { return payment_method; }
        public void setPaymentMethod(String payment_method) { this.payment_method = payment_method; }
    }
    
    class PaymentConfirmationRequest {
        private String stripe_payment_intent_id;
        private String stripe_payment_method_id;
        
        public PaymentConfirmationRequest(String stripe_payment_intent_id, String stripe_payment_method_id) {
            this.stripe_payment_intent_id = stripe_payment_intent_id;
            this.stripe_payment_method_id = stripe_payment_method_id;
        }
        
        // Getters et setters
        public String getStripePaymentIntentId() { return stripe_payment_intent_id; }
        public void setStripePaymentIntentId(String stripe_payment_intent_id) { this.stripe_payment_intent_id = stripe_payment_intent_id; }
        
        public String getStripePaymentMethodId() { return stripe_payment_method_id; }
        public void setStripePaymentMethodId(String stripe_payment_method_id) { this.stripe_payment_method_id = stripe_payment_method_id; }
    }
    
    class PaymentConfirmationResponse {
        private boolean success;
        private String message;
        private Investment investment;
        
        // Getters et setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Investment getInvestment() { return investment; }
        public void setInvestment(Investment investment) { this.investment = investment; }
    }
}

