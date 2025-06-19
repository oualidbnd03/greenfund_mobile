package com.crowdfundpro.android.data;

import com.crowdfundpro.android.data.api.ProjectApiService;
import com.crowdfundpro.android.data.db.ProjectDao;
import com.crowdfundpro.android.data.db.CategoryDao;
import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.data.models.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

/**
 * Repository pour la gestion des projets
 * Centralise l'accès aux données depuis l'API et la base de données locale
 */
public class ProjectRepository {
    
    private ProjectApiService projectApiService;
    private ProjectDao projectDao;
    private CategoryDao categoryDao;
    
    public ProjectRepository(ProjectApiService projectApiService, ProjectDao projectDao, CategoryDao categoryDao) {
        this.projectApiService = projectApiService;
        this.projectDao = projectDao;
        this.categoryDao = categoryDao;
    }
    
    /**
     * Interface pour les callbacks de liste de projets
     */
    public interface ProjectListCallback {
        void onSuccess(ProjectApiService.ProjectListResponse response);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de projet unique
     */
    public interface ProjectCallback {
        void onSuccess(Project project);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de liste de catégories
     */
    public interface CategoryListCallback {
        void onSuccess(List<Category> categories);
        void onError(String error);
    }
    
    /**
     * Récupération des projets avec pagination
     */
    public void getProjects(int page, int pageSize, Integer categoryId, String searchQuery, String status, ProjectListCallback callback) {
        projectApiService.getProjects(page, pageSize, categoryId, searchQuery, status).enqueue(new Callback<ProjectApiService.ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectApiService.ProjectListResponse> call, Response<ProjectApiService.ProjectListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProjectApiService.ProjectListResponse projectListResponse = response.body();
                    
                    // Sauvegarder les projets en local
                    if (projectListResponse.getResults() != null && !projectListResponse.getResults().isEmpty()) {
                        new Thread(() -> projectDao.insertProjects(projectListResponse.getResults())).start();
                    }
                    
                    callback.onSuccess(projectListResponse);
                } else {
                    callback.onError("Erreur lors de la récupération des projets: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ProjectApiService.ProjectListResponse> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    List<Project> localProjects = projectDao.getAllProjects();
                    if (!localProjects.isEmpty()) {
                        ProjectApiService.ProjectListResponse localResponse = new ProjectApiService.ProjectListResponse();
                        localResponse.setResults(localProjects);
                        localResponse.setCount(localProjects.size());
                        callback.onSuccess(localResponse);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Récupération des détails d'un projet
     */
    public void getProject(int projectId, ProjectCallback callback) {
        projectApiService.getProject(projectId).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Project project = response.body();
                    
                    // Sauvegarder le projet en local
                    new Thread(() -> projectDao.insertProject(project)).start();
                    
                    callback.onSuccess(project);
                } else {
                    callback.onError("Erreur lors de la récupération du projet: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    Project localProject = projectDao.getProjectById(projectId);
                    if (localProject != null) {
                        callback.onSuccess(localProject);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Création d'un nouveau projet
     */
    public void createProject(String token, Project project, ProjectCallback callback) {
        projectApiService.createProject("Bearer " + token, project).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Project createdProject = response.body();
                    
                    // Sauvegarder le projet en local
                    new Thread(() -> projectDao.insertProject(createdProject)).start();
                    
                    callback.onSuccess(createdProject);
                } else {
                    callback.onError("Erreur lors de la création du projet: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Récupération des catégories
     */
    public void getCategories(CategoryListCallback callback) {
        projectApiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    
                    // Sauvegarder les catégories en local
                    new Thread(() -> categoryDao.insertCategories(categories)).start();
                    
                    callback.onSuccess(categories);
                } else {
                    callback.onError("Erreur lors de la récupération des catégories: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    List<Category> localCategories = categoryDao.getAllCategories();
                    if (!localCategories.isEmpty()) {
                        callback.onSuccess(localCategories);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Récupération des projets depuis la base de données locale
     */
    public List<Project> getLocalProjects() {
        return projectDao.getAllProjects();
    }
    
    /**
     * Récupération d'un projet depuis la base de données locale
     */
    public Project getLocalProject(int projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    /**
     * Recherche de projets localement
     */
    public List<Project> searchLocalProjects(String searchQuery) {
        return projectDao.searchProjectsByTitle(searchQuery);
    }
}

