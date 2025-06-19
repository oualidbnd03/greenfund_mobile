package com.crowdfundpro.android.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.crowdfundpro.android.data.models.Investment;
import java.util.List;

/**
 * DAO (Data Access Object) pour les opérations sur les investissements
 */
@Dao
public interface InvestmentDao {
    
    /**
     * Insertion d'un investissement
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvestment(Investment investment);
    
    /**
     * Insertion de plusieurs investissements
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvestments(List<Investment> investments);
    
    /**
     * Mise à jour d'un investissement
     */
    @Update
    void updateInvestment(Investment investment);
    
    /**
     * Suppression d'un investissement
     */
    @Delete
    void deleteInvestment(Investment investment);
    
    /**
     * Récupération de tous les investissements
     */
    @Query("SELECT * FROM investments ORDER BY created_at DESC")
    List<Investment> getAllInvestments();
    
    /**
     * Récupération d'un investissement par ID
     */
    @Query("SELECT * FROM investments WHERE id = :investmentId")
    Investment getInvestmentById(int investmentId);
    
    /**
     * Récupération des investissements d'un utilisateur
     */
    @Query("SELECT * FROM investments WHERE user_id = :userId ORDER BY created_at DESC")
    List<Investment> getInvestmentsByUser(int userId);
    
    /**
     * Récupération des investissements pour un projet
     */
    @Query("SELECT * FROM investments WHERE project_id = :projectId ORDER BY created_at DESC")
    List<Investment> getInvestmentsByProject(int projectId);
    
    /**
     * Récupération des investissements par statut
     */
    @Query("SELECT * FROM investments WHERE status = :status ORDER BY created_at DESC")
    List<Investment> getInvestmentsByStatus(String status);
    
    /**
     * Récupération des investissements d'un utilisateur pour un projet spécifique
     */
    @Query("SELECT * FROM investments WHERE user_id = :userId AND project_id = :projectId ORDER BY created_at DESC")
    List<Investment> getUserInvestmentsForProject(int userId, int projectId);
    
    /**
     * Calcul du montant total investi par un utilisateur
     */
    @Query("SELECT SUM(amount) FROM investments WHERE user_id = :userId AND status = 'COMPLETED'")
    double getTotalInvestedByUser(int userId);
    
    /**
     * Calcul du montant total collecté pour un projet
     */
    @Query("SELECT SUM(amount) FROM investments WHERE project_id = :projectId AND status = 'COMPLETED'")
    double getTotalCollectedForProject(int projectId);
    
    /**
     * Comptage des investissements d'un utilisateur
     */
    @Query("SELECT COUNT(*) FROM investments WHERE user_id = :userId")
    int countInvestmentsByUser(int userId);
    
    /**
     * Récupération des investissements récents d'un utilisateur
     */
    @Query("SELECT * FROM investments WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    List<Investment> getRecentInvestmentsByUser(int userId, int limit);
    
    /**
     * Suppression de tous les investissements
     */
    @Query("DELETE FROM investments")
    void deleteAllInvestments();
}

