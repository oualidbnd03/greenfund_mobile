package com.crowdfundpro.android.data.api;

import com.crowdfundpro.android.data.models.User;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interface Retrofit pour les appels API d'authentification
 */
public interface AuthApiService {
    
    /**
     * Connexion utilisateur
     */
    @POST("api/auth/login/")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    /**
     * Inscription utilisateur
     */
    @POST("api/auth/register/")
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    /**
     * Rafraîchissement du token JWT
     */
    @POST("api/auth/refresh/")
    Call<TokenResponse> refreshToken(@Body RefreshTokenRequest request);
    
    /**
     * Déconnexion utilisateur
     */
    @POST("api/auth/logout/")
    Call<Void> logout(@Header("Authorization") String token);
    
    /**
     * Récupération du profil utilisateur
     */
    @GET("api/users/profile/")
    Call<User> getUserProfile(@Header("Authorization") String token);
    
    /**
     * Mise à jour du profil utilisateur
     */
    @PUT("api/users/profile/")
    Call<User> updateUserProfile(@Header("Authorization") String token, @Body User user);
    
    // Classes de requête et réponse
    class LoginRequest {
        private String username;
        private String password;
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        // Getters et setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    class RegisterRequest {
        private String username;
        private String email;
        private String password;
        
        public RegisterRequest(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
        
        // Getters et setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    class AuthResponse {
        private String access_token;
        private String refresh_token;
        private User user;
        
        // Getters et setters
        public String getAccessToken() { return access_token; }
        public void setAccessToken(String access_token) { this.access_token = access_token; }
        
        public String getRefreshToken() { return refresh_token; }
        public void setRefreshToken(String refresh_token) { this.refresh_token = refresh_token; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
    
    class TokenResponse {
        private String access_token;
        
        public String getAccessToken() { return access_token; }
        public void setAccessToken(String access_token) { this.access_token = access_token; }
    }
    
    class RefreshTokenRequest {
        private String refresh_token;
        
        public RefreshTokenRequest(String refresh_token) {
            this.refresh_token = refresh_token;
        }
        
        public String getRefreshToken() { return refresh_token; }
        public void setRefreshToken(String refresh_token) { this.refresh_token = refresh_token; }
    }
}

