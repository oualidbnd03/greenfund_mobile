package com.crowdfundpro.android.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.crowdfundpro.android.data.models.Project;
import java.util.List;

/**
 * DAO (Data Access Object) pour les opérations sur les projets
 */
@Dao
public interface ProjectDao {
    
    /**
     * Insertion d'un projet
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProject(Project project);
    
    /**
     * Insertion de plusieurs projets
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProjects(List<Project> projects);
    
    /**
     * Mise à jour d'un projet
     */
    @Update
    void updateProject(Project project);
    
    /**
     * Suppression d'un projet
     */
    @Delete
    void deleteProject(Project project);
    
    /**
     * Récupération de tous les projets
     */
    @Query("SELECT * FROM projects ORDER BY created_at DESC")
    List<Project> getAllProjects();
    
    /**
     * Récupération d'un projet par ID
     */
    @Query("SELECT * FROM projects WHERE id = :projectId")
    Project getProjectById(int projectId);
    
    /**
     * Récupération des projets par catégorie
     */
    @Query("SELECT * FROM projects WHERE category_id = :categoryId ORDER BY created_at DESC")
    List<Project> getProjectsByCategory(int categoryId);
    
    /**
     * Récupération des projets par statut
     */
    @Query("SELECT * FROM projects WHERE status = :status ORDER BY created_at DESC")
    List<Project> getProjectsByStatus(String status);
    
    /**
     * Recherche de projets par titre
     */
    @Query("SELECT * FROM projects WHERE title LIKE '%' || :searchQuery || '%' ORDER BY created_at DESC")
    List<Project> searchProjectsByTitle(String searchQuery);
    
    /**
     * Récupération des projets créés par un utilisateur
     */
    @Query("SELECT * FROM projects WHERE creator_id = :creatorId ORDER BY created_at DESC")
    List<Project> getProjectsByCreator(int creatorId);
    
    /**
     * Récupération des projets actifs
     */
    @Query("SELECT * FROM projects WHERE status = 'ACTIVE' AND end_date > :currentTime ORDER BY created_at DESC")
    List<Project> getActiveProjects(long currentTime);
    
    /**
     * Récupération des projets les plus populaires (par montant collecté)
     */
    @Query("SELECT * FROM projects ORDER BY current_amount DESC LIMIT :limit")
    List<Project> getPopularProjects(int limit);
    
    /**
     * Suppression de tous les projets
     */
    @Query("DELETE FROM projects")
    void deleteAllProjects();
    
    /**
     * Comptage des projets par statut
     */
    @Query("SELECT COUNT(*) FROM projects WHERE status = :status")
    int countProjectsByStatus(String status);
}

