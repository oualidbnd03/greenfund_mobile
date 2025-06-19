package com.crowdfundpro.android.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.crowdfundpro.android.data.ProjectRepository;
import com.crowdfundpro.android.data.InvestmentRepository;
import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.data.models.Investment;
import com.crowdfundpro.android.utils.TokenManager;
import java.util.List;
import java.util.ArrayList;

/**
 * ViewModel pour le tableau de bord
 */
public class DashboardViewModel extends ViewModel {
    
    private ProjectRepository projectRepository;
    private InvestmentRepository investmentRepository;
    private TokenManager tokenManager;
    
    private MutableLiveData<DashboardData> dashboardData = new MutableLiveData<>();
    private MutableLiveData<List<RecentActivity>> recentActivity = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    
    public DashboardViewModel() {
        // TODO: Injection de dépendances à implémenter
        // projectRepository = DependencyInjection.getProjectRepository();
        // investmentRepository = DependencyInjection.getInvestmentRepository();
        // tokenManager = DependencyInjection.getTokenManager();
        loading.setValue(false);
    }
    
    public LiveData<DashboardData> getDashboardData() {
        return dashboardData;
    }
    
    public LiveData<List<RecentActivity>> getRecentActivity() {
        return recentActivity;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    /**
     * Chargement des données du tableau de bord
     */
    public void loadDashboardData() {
        loading.setValue(true);
        error.setValue(null);
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            loading.setValue(false);
            return;
        }
        
        // Charger les investissements de l'utilisateur
        investmentRepository.getUserInvestments(token, new InvestmentRepository.InvestmentListCallback() {
            @Override
            public void onSuccess(List<Investment> investments) {
                calculateDashboardStats(investments);
                generateRecentActivity(investments);
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
     * Calcul des statistiques du tableau de bord
     */
    private void calculateDashboardStats(List<Investment> investments) {
        double totalInvested = 0;
        int activeInvestments = 0;
        int completedInvestments = 0;
        int failedInvestments = 0;
        
        for (Investment investment : investments) {
            if ("COMPLETED".equals(investment.getStatus())) {
                totalInvested += investment.getAmount();
                completedInvestments++;
            } else if ("PENDING".equals(investment.getStatus())) {
                activeInvestments++;
            } else if ("FAILED".equals(investment.getStatus())) {
                failedInvestments++;
            }
        }
        
        // TODO: Récupérer le nombre total de projets depuis l'API
        int totalProjects = 0;
        
        DashboardData data = new DashboardData(
            totalInvested,
            activeInvestments,
            completedInvestments,
            failedInvestments,
            totalProjects
        );
        
        dashboardData.setValue(data);
    }
    
    /**
     * Génération de l'activité récente
     */
    private void generateRecentActivity(List<Investment> investments) {
        List<RecentActivity> activities = new ArrayList<>();
        
        // Prendre les 5 derniers investissements
        int count = Math.min(investments.size(), 5);
        for (int i = 0; i < count; i++) {
            Investment investment = investments.get(i);
            
            String description = String.format("Investissement de %.2f€ dans le projet #%d", 
                investment.getAmount(), investment.getProjectId());
            
            RecentActivity activity = new RecentActivity(
                description,
                investment.getCreatedAt(),
                getActivityType(investment.getStatus())
            );
            
            activities.add(activity);
        }
        
        recentActivity.setValue(activities);
    }
    
    private RecentActivity.Type getActivityType(String status) {
        switch (status) {
            case "COMPLETED":
                return RecentActivity.Type.INVESTMENT_SUCCESS;
            case "FAILED":
                return RecentActivity.Type.INVESTMENT_FAILED;
            case "PENDING":
                return RecentActivity.Type.INVESTMENT_PENDING;
            default:
                return RecentActivity.Type.OTHER;
        }
    }
    
    /**
     * Actualisation des données
     */
    public void refresh() {
        loadDashboardData();
    }
    
    /**
     * Classe pour représenter les données du tableau de bord
     */
    public static class DashboardData {
        private double totalInvested;
        private int activeInvestments;
        private int completedInvestments;
        private int failedInvestments;
        private int totalProjects;
        
        public DashboardData(double totalInvested, int activeInvestments, 
                           int completedInvestments, int failedInvestments, int totalProjects) {
            this.totalInvested = totalInvested;
            this.activeInvestments = activeInvestments;
            this.completedInvestments = completedInvestments;
            this.failedInvestments = failedInvestments;
            this.totalProjects = totalProjects;
        }
        
        // Getters
        public double getTotalInvested() { return totalInvested; }
        public int getActiveInvestments() { return activeInvestments; }
        public int getCompletedInvestments() { return completedInvestments; }
        public int getFailedInvestments() { return failedInvestments; }
        public int getTotalProjects() { return totalProjects; }
    }
    
    /**
     * Classe pour représenter une activité récente
     */
    public static class RecentActivity {
        public enum Type {
            INVESTMENT_SUCCESS, INVESTMENT_FAILED, INVESTMENT_PENDING, PROJECT_CREATED, OTHER
        }
        
        private String description;
        private long timestamp;
        private Type type;
        
        public RecentActivity(String description, long timestamp, Type type) {
            this.description = description;
            this.timestamp = timestamp;
            this.type = type;
        }
        
        // Getters
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
        public Type getType() { return type; }
    }
}

