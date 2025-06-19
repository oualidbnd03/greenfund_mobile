package com.crowdfundpro.android.data;

import com.crowdfundpro.android.data.api.InvestmentApiService;
import com.crowdfundpro.android.data.db.InvestmentDao;
import com.crowdfundpro.android.data.models.Investment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

/**
 * Repository pour la gestion des investissements
 * Centralise l'accès aux données depuis l'API et la base de données locale
 */
public class InvestmentRepository {
    
    private InvestmentApiService investmentApiService;
    private InvestmentDao investmentDao;
    
    public InvestmentRepository(InvestmentApiService investmentApiService, InvestmentDao investmentDao) {
        this.investmentApiService = investmentApiService;
        this.investmentDao = investmentDao;
    }
    
    /**
     * Interface pour les callbacks d'investissement unique
     */
    public interface InvestmentCallback {
        void onSuccess(Investment investment);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de liste d'investissements
     */
    public interface InvestmentListCallback {
        void onSuccess(List<Investment> investments);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de confirmation de paiement
     */
    public interface PaymentConfirmationCallback {
        void onSuccess(InvestmentApiService.PaymentConfirmationResponse response);
        void onError(String error);
    }
    
    /**
     * Création d'un nouvel investissement
     */
    public void createInvestment(String token, InvestmentApiService.InvestmentRequest request, InvestmentCallback callback) {
        investmentApiService.createInvestment("Bearer " + token, request).enqueue(new Callback<Investment>() {
            @Override
            public void onResponse(Call<Investment> call, Response<Investment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Investment investment = response.body();
                    
                    // Sauvegarder l'investissement en local
                    new Thread(() -> investmentDao.insertInvestment(investment)).start();
                    
                    callback.onSuccess(investment);
                } else {
                    callback.onError("Erreur lors de la création de l'investissement: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Investment> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Récupération des investissements de l'utilisateur
     */
    public void getUserInvestments(String token, InvestmentListCallback callback) {
        investmentApiService.getUserInvestments("Bearer " + token).enqueue(new Callback<List<Investment>>() {
            @Override
            public void onResponse(Call<List<Investment>> call, Response<List<Investment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Investment> investments = response.body();
                    
                    // Sauvegarder les investissements en local
                    new Thread(() -> investmentDao.insertInvestments(investments)).start();
                    
                    callback.onSuccess(investments);
                } else {
                    callback.onError("Erreur lors de la récupération des investissements: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<List<Investment>> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    // TODO: Récupérer l'ID utilisateur depuis le token
                    int userId = 1; // Placeholder
                    List<Investment> localInvestments = investmentDao.getInvestmentsByUser(userId);
                    if (!localInvestments.isEmpty()) {
                        callback.onSuccess(localInvestments);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Récupération des détails d'un investissement
     */
    public void getInvestment(String token, int investmentId, InvestmentCallback callback) {
        investmentApiService.getInvestment("Bearer " + token, investmentId).enqueue(new Callback<Investment>() {
            @Override
            public void onResponse(Call<Investment> call, Response<Investment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Investment investment = response.body();
                    
                    // Mettre à jour l'investissement en local
                    new Thread(() -> investmentDao.updateInvestment(investment)).start();
                    
                    callback.onSuccess(investment);
                } else {
                    callback.onError("Erreur lors de la récupération de l'investissement: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Investment> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    Investment localInvestment = investmentDao.getInvestmentById(investmentId);
                    if (localInvestment != null) {
                        callback.onSuccess(localInvestment);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Récupération des investissements pour un projet
     */
    public void getProjectInvestments(int projectId, InvestmentListCallback callback) {
        investmentApiService.getProjectInvestments(projectId).enqueue(new Callback<List<Investment>>() {
            @Override
            public void onResponse(Call<List<Investment>> call, Response<List<Investment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Investment> investments = response.body();
                    
                    // Sauvegarder les investissements en local
                    new Thread(() -> investmentDao.insertInvestments(investments)).start();
                    
                    callback.onSuccess(investments);
                } else {
                    callback.onError("Erreur lors de la récupération des investissements du projet: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<List<Investment>> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    List<Investment> localInvestments = investmentDao.getInvestmentsByProject(projectId);
                    if (!localInvestments.isEmpty()) {
                        callback.onSuccess(localInvestments);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Confirmation de paiement Stripe
     */
    public void confirmPayment(String token, int investmentId, 
                              InvestmentApiService.PaymentConfirmationRequest request, 
                              PaymentConfirmationCallback callback) {
        investmentApiService.confirmPayment("Bearer " + token, investmentId, request)
                .enqueue(new Callback<InvestmentApiService.PaymentConfirmationResponse>() {
            @Override
            public void onResponse(Call<InvestmentApiService.PaymentConfirmationResponse> call, 
                                 Response<InvestmentApiService.PaymentConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InvestmentApiService.PaymentConfirmationResponse confirmationResponse = response.body();
                    
                    // Mettre à jour l'investissement en local si fourni
                    if (confirmationResponse.getInvestment() != null) {
                        new Thread(() -> investmentDao.updateInvestment(confirmationResponse.getInvestment())).start();
                    }
                    
                    callback.onSuccess(confirmationResponse);
                } else {
                    callback.onError("Erreur lors de la confirmation du paiement: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<InvestmentApiService.PaymentConfirmationResponse> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Annulation d'un investissement
     */
    public void cancelInvestment(String token, int investmentId, InvestmentCallback callback) {
        investmentApiService.cancelInvestment("Bearer " + token, investmentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Mettre à jour le statut en local
                    new Thread(() -> {
                        Investment investment = investmentDao.getInvestmentById(investmentId);
                        if (investment != null) {
                            investment.setStatus("CANCELLED");
                            investmentDao.updateInvestment(investment);
                            callback.onSuccess(investment);
                        }
                    }).start();
                } else {
                    callback.onError("Erreur lors de l'annulation de l'investissement: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Récupération des investissements depuis la base de données locale
     */
    public List<Investment> getLocalInvestments(int userId) {
        return investmentDao.getInvestmentsByUser(userId);
    }
    
    /**
     * Calcul du montant total investi par un utilisateur
     */
    public double getTotalInvestedByUser(int userId) {
        return investmentDao.getTotalInvestedByUser(userId);
    }
}

