package com.crowdfundpro.android.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.crowdfundpro.android.data.AuthRepository;
import com.crowdfundpro.android.data.api.AuthApiService;
import com.crowdfundpro.android.data.models.User;
import com.crowdfundpro.android.utils.TokenManager;

/**
 * ViewModel pour la gestion de l'authentification
 */
public class AuthViewModel extends ViewModel {
    
    private AuthRepository authRepository;
    private TokenManager tokenManager;
    private MutableLiveData<AuthState> authState = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    
    public AuthViewModel() {
        // TODO: Injection de dépendances à implémenter
        // authRepository = DependencyInjection.getAuthRepository();
        // tokenManager = DependencyInjection.getTokenManager();
        authState.setValue(new AuthState(AuthState.Status.IDLE, null, null));
    }
    
    public LiveData<AuthState> getAuthState() {
        return authState;
    }
    
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Connexion utilisateur
     */
    public void login(String username, String password) {
        authState.setValue(new AuthState(AuthState.Status.LOADING, null, null));
        
        authRepository.login(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthApiService.AuthResponse response) {
                // Sauvegarder les tokens
                tokenManager.saveAccessToken(response.getAccessToken());
                tokenManager.saveRefreshToken(response.getRefreshToken());
                
                // Mettre à jour l'état
                currentUser.setValue(response.getUser());
                authState.setValue(new AuthState(AuthState.Status.SUCCESS, response, "Connexion réussie"));
            }
            
            @Override
            public void onError(String error) {
                authState.setValue(new AuthState(AuthState.Status.ERROR, null, error));
            }
        });
    }
    
    /**
     * Inscription utilisateur
     */
    public void register(String username, String email, String password) {
        authState.setValue(new AuthState(AuthState.Status.LOADING, null, null));
        
        authRepository.register(username, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthApiService.AuthResponse response) {
                // Sauvegarder les tokens
                tokenManager.saveAccessToken(response.getAccessToken());
                tokenManager.saveRefreshToken(response.getRefreshToken());
                
                // Mettre à jour l'état
                currentUser.setValue(response.getUser());
                authState.setValue(new AuthState(AuthState.Status.SUCCESS, response, "Inscription réussie"));
            }
            
            @Override
            public void onError(String error) {
                authState.setValue(new AuthState(AuthState.Status.ERROR, null, error));
            }
        });
    }
    
    /**
     * Déconnexion utilisateur
     */
    public void logout() {
        String token = tokenManager.getAccessToken();
        if (token != null) {
            authRepository.logout(token);
        }
        
        // Nettoyer les tokens et l'état
        tokenManager.clearTokens();
        currentUser.setValue(null);
        authState.setValue(new AuthState(AuthState.Status.IDLE, null, null));
    }
    
    /**
     * Vérification de l'état de connexion
     */
    public boolean isLoggedIn() {
        return tokenManager.getAccessToken() != null && !tokenManager.isTokenExpired();
    }
    
    /**
     * Récupération du profil utilisateur
     */
    public void getUserProfile() {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            authState.setValue(new AuthState(AuthState.Status.ERROR, null, "Token non disponible"));
            return;
        }
        
        authRepository.getUserProfile(token, new AuthRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setValue(user);
            }
            
            @Override
            public void onError(String error) {
                // En cas d'erreur, peut-être que le token a expiré
                if (error.contains("401") || error.contains("Unauthorized")) {
                    logout();
                }
            }
        });
    }
    
    /**
     * Classe pour représenter l'état de l'authentification
     */
    public static class AuthState {
        public enum Status {
            IDLE, LOADING, SUCCESS, ERROR
        }
        
        private Status status;
        private AuthApiService.AuthResponse data;
        private String message;
        
        public AuthState(Status status, AuthApiService.AuthResponse data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }
        
        public Status getStatus() { return status; }
        public AuthApiService.AuthResponse getData() { return data; }
        public String getMessage() { return message; }
    }
}

