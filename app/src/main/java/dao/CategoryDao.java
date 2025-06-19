package com.crowdfundpro.android.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.crowdfundpro.android.data.models.Category;
import java.util.List;

/**
 * DAO (Data Access Object) pour les opérations sur les catégories
 */
@Dao
public interface CategoryDao {
    
    /**
     * Insertion d'une catégorie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);
    
    /**
     * Insertion de plusieurs catégories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> categories);
    
    /**
     * Mise à jour d'une catégorie
     */
    @Update
    void updateCategory(Category category);
    
    /**
     * Suppression d'une catégorie
     */
    @Delete
    void deleteCategory(Category category);
    
    /**
     * Récupération de toutes les catégories
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategories();
    
    /**
     * Récupération d'une catégorie par ID
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(int categoryId);
    
    /**
     * Récupération d'une catégorie par nom
     */
    @Query("SELECT * FROM categories WHERE name = :name")
    Category getCategoryByName(String name);
    
    /**
     * Suppression de toutes les catégories
     */
    @Query("DELETE FROM categories")
    void deleteAllCategories();
    
    /**
     * Comptage des catégories
     */
    @Query("SELECT COUNT(*) FROM categories")
    int getCategoryCount();
}

