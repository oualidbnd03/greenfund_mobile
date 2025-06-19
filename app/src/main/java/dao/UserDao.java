package com.crowdfundpro.android.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.crowdfundpro.android.data.models.User;

/**
 * DAO (Data Access Object) pour les opérations sur les utilisateurs
 */
@Dao
public interface UserDao {
    
    /**
     * Insertion d'un utilisateur
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
    
    /**
     * Mise à jour d'un utilisateur
     */
    @Update
    void updateUser(User user);
    
    /**
     * Suppression d'un utilisateur
     */
    @Delete
    void deleteUser(User user);
    
    /**
     * Récupération d'un utilisateur par ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);
    
    /**
     * Récupération d'un utilisateur par nom d'utilisateur
     */
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    /**
     * Récupération d'un utilisateur par email
     */
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    /**
     * Vérification de l'existence d'un utilisateur
     */
    @Query("SELECT COUNT(*) FROM users WHERE id = :userId")
    int userExists(int userId);
    
    /**
     * Suppression de tous les utilisateurs (pour la déconnexion)
     */
    @Query("DELETE FROM users")
    void deleteAllUsers();
}

