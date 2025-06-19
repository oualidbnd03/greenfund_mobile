package com.crowdfundpro.android.data;

import com.crowdfundpro.android.data.api.AuthApiService;
import com.crowdfundpro.android.data.db.UserDao;
import com.crowdfundpro.android.data.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository pour la gestion de l'authentification et des utilisateurs
 * Centralise l'accès aux données depuis l'API et la base de données locale
 */
public class AuthRepository {
    
    private AuthApiService authApiService;
    private UserDao userDao;
    
    public AuthRepository(AuthApiService authApiService, UserDao userDao) {
        this.authApiService = authApiService;
        this.userDao = userDao;
    }
    
    /**
     * Interface pour les callbacks d'authentification
     */
    public interface AuthCallback {
        void onSuccess(AuthApiService.AuthResponse response);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de profil utilisateur
     */
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    /**
     * Connexion utilisateur
     */
    public void login(String username, String password, AuthCallback callback) {
        AuthApiService.LoginRequest request = new AuthApiService.LoginRequest(username, password);
        
        authApiService.login(request).enqueue(new Callback<AuthApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApiService.AuthResponse> call, Response<AuthApiService.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApiService.AuthResponse authResponse = response.body();
                    
                    // Sauvegarder l'utilisateur en local
                    if (authResponse.getUser() != null) {
                        new Thread(() -> userDao.insertUser(authResponse.getUser())).start();
                    }
                    
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("Erreur de connexion: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<AuthApiService.AuthResponse> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Inscription utilisateur
     */
    public void register(String username, String email, String password, AuthCallback callback) {
        AuthApiService.RegisterRequest request = new AuthApiService.RegisterRequest(username, email, password);
        
        authApiService.register(request).enqueue(new Callback<AuthApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApiService.AuthResponse> call, Response<AuthApiService.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApiService.AuthResponse authResponse = response.body();
                    
                    // Sauvegarder l'utilisateur en local
                    if (authResponse.getUser() != null) {
                        new Thread(() -> userDao.insertUser(authResponse.getUser())).start();
                    }
                    
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("Erreur d'inscription: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<AuthApiService.AuthResponse> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Récupération du profil utilisateur
     */
    public void getUserProfile(String token, UserCallback callback) {
        authApiService.getUserProfile("Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    
                    // Mettre à jour l'utilisateur en local
                    new Thread(() -> userDao.updateUser(user)).start();
                    
                    callback.onSuccess(user);
                } else {
                    callback.onError("Erreur lors de la récupération du profil: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Mise à jour du profil utilisateur
     */
    public void updateUserProfile(String token, User user, UserCallback callback) {
        authApiService.updateUserProfile("Bearer " + token, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    
                    // Mettre à jour l'utilisateur en local
                    new Thread(() -> userDao.updateUser(updatedUser)).start();
                    
                    callback.onSuccess(updatedUser);
                } else {
                    callback.onError("Erreur lors de la mise à jour du profil: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Déconnexion utilisateur
     */
    public void logout(String token) {
        // Appel API pour déconnexion côté serveur
        authApiService.logout("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Nettoyage des données locales
                new Thread(() -> userDao.deleteAllUsers()).start();
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Même en cas d'échec de l'API, nettoyer les données locales
                new Thread(() -> userDao.deleteAllUsers()).start();
            }
        });
    }
    
    /**
     * Récupération de l'utilisateur depuis la base de données locale
     */
    public User getLocalUser(int userId) {
        return userDao.getUserById(userId);
    }
}

