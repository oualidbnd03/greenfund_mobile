package com.crowdfundpro.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import org.json.JSONObject;
import java.security.GeneralSecurityException;
import java.io.IOException;

/**
 * Gestionnaire de tokens JWT avec stockage sécurisé
 */
public class TokenManager {
    
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    
    private SharedPreferences sharedPreferences;
    private Context context;
    
    public TokenManager(Context context) {
        this.context = context;
        initializeEncryptedPreferences();
    }
    
    private void initializeEncryptedPreferences() {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            
            sharedPreferences = EncryptedSharedPreferences.create(
                PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback vers SharedPreferences normal en cas d'erreur
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }
    
    /**
     * Sauvegarde du token d'accès
     */
    public void saveAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        
        // Extraire et sauvegarder la date d'expiration du token
        long expiryTime = extractTokenExpiry(token);
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime);
        
        editor.apply();
    }
    
    /**
     * Sauvegarde du token de rafraîchissement
     */
    public void saveRefreshToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_REFRESH_TOKEN, token);
        editor.apply();
    }
    
    /**
     * Récupération du token d'accès
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
    
    /**
     * Récupération du token de rafraîchissement
     */
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }
    
    /**
     * Vérification de l'expiration du token
     */
    public boolean isTokenExpired() {
        long expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0);
        long currentTime = System.currentTimeMillis() / 1000; // Convertir en secondes
        
        return currentTime >= expiryTime;
    }
    
    /**
     * Suppression de tous les tokens
     */
    public void clearTokens() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_TOKEN_EXPIRY);
        editor.apply();
    }
    
    /**
     * Extraction de la date d'expiration depuis le token JWT
     */
    private long extractTokenExpiry(String token) {
        try {
            // Diviser le token JWT en ses parties
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return 0;
            }
            
            // Décoder la partie payload (index 1)
            String payload = parts[1];
            
            // Ajouter le padding si nécessaire
            while (payload.length() % 4 != 0) {
                payload += "=";
            }
            
            // Décoder en Base64
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes);
            
            // Parser le JSON pour extraire 'exp'
            JSONObject jsonObject = new JSONObject(decodedPayload);
            return jsonObject.optLong("exp", 0);
            
        } catch (Exception e) {
            // En cas d'erreur, retourner 0 (token considéré comme expiré)
            return 0;
        }
    }
    
    /**
     * Vérification de la validité du token
     */
    public boolean isTokenValid() {
        String token = getAccessToken();
        return token != null && !isTokenExpired();
    }
    
    /**
     * Obtention du token avec le préfixe Bearer
     */
    public String getBearerToken() {
        String token = getAccessToken();
        return token != null ? "Bearer " + token : null;
    }
    
    /**
     * Extraction de l'ID utilisateur depuis le token
     */
    public int getUserIdFromToken() {
        try {
            String token = getAccessToken();
            if (token == null) return -1;
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) return -1;
            
            String payload = parts[1];
            while (payload.length() % 4 != 0) {
                payload += "=";
            }
            
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes);
            
            JSONObject jsonObject = new JSONObject(decodedPayload);
            return jsonObject.optInt("user_id", -1);
            
        } catch (Exception e) {
            return -1;
        }
    }
}

