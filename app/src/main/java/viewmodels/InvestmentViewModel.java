package com.crowdfundpro.android.ui.investments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.crowdfundpro.android.data.InvestmentRepository;
import com.crowdfundpro.android.data.api.InvestmentApiService;
import com.crowdfundpro.android.data.models.Investment;
import com.crowdfundpro.android.utils.TokenManager;
import java.util.List;

/**
 * ViewModel pour la gestion des investissements
 */
public class InvestmentViewModel extends ViewModel {
    
    private InvestmentRepository investmentRepository;
    private TokenManager tokenManager;
    
    private MutableLiveData<List<Investment>> investments = new MutableLiveData<>();
    private MutableLiveData<InvestmentState> investmentState = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    
    private Investment currentInvestment;
    
    public InvestmentViewModel() {
        // TODO: Injection de dépendances à implémenter
        // investmentRepository = DependencyInjection.getInvestmentRepository();
        // tokenManager = DependencyInjection.getTokenManager();
        investmentState.setValue(new InvestmentState(InvestmentState.Status.IDLE, null, null));
        loading.setValue(false);
    }
    
    public LiveData<List<Investment>> getInvestments() {
        return investments;
    }
    
    public LiveData<InvestmentState> getInvestmentState() {
        return investmentState;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    /**
     * Création d'un nouvel investissement
     */
    public void createInvestment(int projectId, double amount, String paymentMethod) {
        investmentState.setValue(new InvestmentState(InvestmentState.Status.LOADING, null, null));
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            investmentState.setValue(new InvestmentState(InvestmentState.Status.ERROR, 
                null, "Token d'authentification manquant"));
            return;
        }
        
        InvestmentApiService.InvestmentRequest request = 
            new InvestmentApiService.InvestmentRequest(projectId, amount, paymentMethod);
        
        investmentRepository.createInvestment(token, request, new InvestmentRepository.InvestmentCallback() {
            @Override
            public void onSuccess(Investment investment) {
                currentInvestment = investment;
                
                if ("PENDING".equals(investment.getStatus())) {
                    // L'investissement nécessite un paiement
                    investmentState.setValue(new InvestmentState(InvestmentState.Status.PAYMENT_REQUIRED, 
                        investment, "Paiement requis"));
                } else if ("COMPLETED".equals(investment.getStatus())) {
                    // L'investissement est déjà complété
                    investmentState.setValue(new InvestmentState(InvestmentState.Status.SUCCESS, 
                        investment, "Investissement réussi"));
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                investmentState.setValue(new InvestmentState(InvestmentState.Status.ERROR, 
                    null, errorMessage));
            }
        });
    }
    
    /**
     * Confirmation du paiement
     */
    public void confirmPayment() {
        if (currentInvestment == null) {
            investmentState.setValue(new InvestmentState(InvestmentState.Status.ERROR, 
                null, "Aucun investissement en cours"));
            return;
        }
        
        investmentState.setValue(new InvestmentState(InvestmentState.Status.LOADING, null, null));
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            investmentState.setValue(new InvestmentState(InvestmentState.Status.ERROR, 
                null, "Token d'authentification manquant"));
            return;
        }
        
        // TODO: Implémenter la confirmation de paiement avec Stripe
        // Cette méthode devrait appeler l'API pour confirmer le paiement
        // avec les détails de la transaction Stripe
        
        // Pour l'instant, simuler une confirmation réussie
        investmentState.setValue(new InvestmentState(InvestmentState.Status.SUCCESS, 
            currentInvestment, "Paiement confirmé"));
    }
    
    /**
     * Chargement de l'historique des investissements de l'utilisateur
     */
    public void loadUserInvestments() {
        loading.setValue(true);
        error.setValue(null);
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            loading.setValue(false);
            return;
        }
        
        investmentRepository.getUserInvestments(token, new InvestmentRepository.InvestmentListCallback() {
            @Override
            public void onSuccess(List<Investment> investmentList) {
                investments.setValue(investmentList);
                loading.setValue(false);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Chargement des investissements pour un projet spécifique
     */
    public void loadProjectInvestments(int projectId) {
        loading.setValue(true);
        error.setValue(null);
        
        investmentRepository.getProjectInvestments(projectId, new InvestmentRepository.InvestmentListCallback() {
            @Override
            public void onSuccess(List<Investment> investmentList) {
                investments.setValue(investmentList);
                loading.setValue(false);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Annulation d'un investissement (si autorisé)
     */
    public void cancelInvestment(int investmentId) {
        loading.setValue(true);
        error.setValue(null);
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            loading.setValue(false);
            return;
        }
        
        investmentRepository.cancelInvestment(token, investmentId, new InvestmentRepository.InvestmentCallback() {
            @Override
            public void onSuccess(Investment investment) {
                loading.setValue(false);
                // Recharger la liste des investissements
                loadUserInvestments();
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Classe pour représenter l'état de l'investissement
     */
    public static class InvestmentState {
        public enum Status {
            IDLE, LOADING, SUCCESS, ERROR, PAYMENT_REQUIRED
        }
        
        private Status status;
        private Investment investment;
        private String message;
        private String paymentIntentClientSecret;
        
        public InvestmentState(Status status, Investment investment, String message) {
            this.status = status;
            this.investment = investment;
            this.message = message;
        }
        
        public InvestmentState(Status status, Investment investment, String message, String clientSecret) {
            this.status = status;
            this.investment = investment;
            this.message = message;
            this.paymentIntentClientSecret = clientSecret;
        }
        
        public Status getStatus() { return status; }
        public Investment getInvestment() { return investment; }
        public String getMessage() { return message; }
        public String getPaymentIntentClientSecret() { return paymentIntentClientSecret; }
    }
}

