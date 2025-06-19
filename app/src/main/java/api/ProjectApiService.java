package com.crowdfundpro.android.data.api;

import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.data.models.Category;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

/**
 * Interface Retrofit pour les appels API de gestion des projets
 */
public interface ProjectApiService {
    
    /**
     * Récupération de la liste des projets avec pagination
     */
    @GET("api/projects/")
    Call<ProjectListResponse> getProjects(
        @Query("page") int page,
        @Query("page_size") int pageSize,
        @Query("category") Integer categoryId,
        @Query("search") String searchQuery,
        @Query("status") String status
    );
    
    /**
     * Récupération des détails d'un projet
     */
    @GET("api/projects/{id}/")
    Call<Project> getProject(@Path("id") int projectId);
    
    /**
     * Création d'un nouveau projet
     */
    @POST("api/projects/")
    Call<Project> createProject(@Header("Authorization") String token, @Body Project project);
    
    /**
     * Mise à jour d'un projet
     */
    @PUT("api/projects/{id}/")
    Call<Project> updateProject(@Header("Authorization") String token, @Path("id") int projectId, @Body Project project);
    
    /**
     * Suppression d'un projet
     */
    @DELETE("api/projects/{id}/")
    Call<Void> deleteProject(@Header("Authorization") String token, @Path("id") int projectId);
    
    /**
     * Récupération des catégories
     */
    @GET("api/categories/")
    Call<List<Category>> getCategories();
    
    /**
     * Récupération des projets favoris de l'utilisateur
     */
    @GET("api/projects/favorites/")
    Call<List<Project>> getFavoriteProjects(@Header("Authorization") String token);
    
    /**
     * Ajout d'un projet aux favoris
     */
    @POST("api/projects/{id}/favorite/")
    Call<Void> addToFavorites(@Header("Authorization") String token, @Path("id") int projectId);
    
    /**
     * Suppression d'un projet des favoris
     */
    @DELETE("api/projects/{id}/favorite/")
    Call<Void> removeFromFavorites(@Header("Authorization") String token, @Path("id") int projectId);
    
    // Classes de réponse
    class ProjectListResponse {
        private int count;
        private String next;
        private String previous;
        private List<Project> results;
        
        // Getters et setters
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        
        public String getNext() { return next; }
        public void setNext(String next) { this.next = next; }
        
        public String getPrevious() { return previous; }
        public void setPrevious(String previous) { this.previous = previous; }
        
        public List<Project> getResults() { return results; }
        public void setResults(List<Project> results) { this.results = results; }
    }
}

