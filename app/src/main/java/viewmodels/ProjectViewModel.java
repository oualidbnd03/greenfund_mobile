package com.crowdfundpro.android.ui.projects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.crowdfundpro.android.data.ProjectRepository;
import com.crowdfundpro.android.data.api.ProjectApiService;
import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.data.models.Category;
import com.crowdfundpro.android.utils.TokenManager;
import java.util.List;

/**
 * ViewModel pour la gestion des projets
 */
public class ProjectViewModel extends ViewModel {
    
    private ProjectRepository projectRepository;
    private TokenManager tokenManager;
    
    private MutableLiveData<List<Project>> projects = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private MutableLiveData<Project> selectedProject = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    
    public ProjectViewModel() {
        // TODO: Injection de dépendances à implémenter
        // projectRepository = DependencyInjection.getProjectRepository();
        // tokenManager = DependencyInjection.getTokenManager();
        loading.setValue(false);
    }
    
    public LiveData<List<Project>> getProjects() {
        return projects;
    }
    
    public LiveData<List<Category>> getCategories() {
        return categories;
    }
    
    public LiveData<Project> getSelectedProject() {
        return selectedProject;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    /**
     * Chargement des projets avec filtres
     */
    public void loadProjects(int page, int pageSize, Integer categoryId, String searchQuery, String status) {
        loading.setValue(true);
        error.setValue(null);
        
        projectRepository.getProjects(page, pageSize, categoryId, searchQuery, status, 
            new ProjectRepository.ProjectListCallback() {
                @Override
                public void onSuccess(ProjectApiService.ProjectListResponse response) {
                    projects.setValue(response.getResults());
                    loading.setValue(false);
                }
                
                @Override
                public void onError(String errorMessage) {
                    error.setValue(errorMessage);
                    loading.setValue(false);
                }
            });
    }
    
    /**
     * Chargement des détails d'un projet
     */
    public void loadProject(int projectId) {
        loading.setValue(true);
        error.setValue(null);
        
        projectRepository.getProject(projectId, new ProjectRepository.ProjectCallback() {
            @Override
            public void onSuccess(Project project) {
                selectedProject.setValue(project);
                loading.setValue(false);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Chargement des catégories
     */
    public void loadCategories() {
        projectRepository.getCategories(new ProjectRepository.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                categories.setValue(categoryList);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
    
    /**
     * Création d'un nouveau projet
     */
    public void createProject(Project project) {
        loading.setValue(true);
        error.setValue(null);
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            loading.setValue(false);
            return;
        }
        
        projectRepository.createProject(token, project, new ProjectRepository.ProjectCallback() {
            @Override
            public void onSuccess(Project createdProject) {
                selectedProject.setValue(createdProject);
                loading.setValue(false);
                // Recharger la liste des projets
                loadProjects(1, 20, null, "", "ACTIVE");
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Basculer le statut favori d'un projet
     */
    public void toggleFavorite(Project project) {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            return;
        }
        
        // TODO: Implémenter la logique de favoris
        // Cette fonctionnalité nécessiterait des endpoints API supplémentaires
        // et une gestion de l'état des favoris dans le modèle Project
    }
    
    /**
     * Recherche de projets
     */
    public void searchProjects(String query) {
        loadProjects(1, 20, null, query, "ACTIVE");
    }
    
    /**
     * Filtrage par catégorie
     */
    public void filterByCategory(Integer categoryId) {
        loadProjects(1, 20, categoryId, "", "ACTIVE");
    }
    
    /**
     * Récupération des projets depuis le cache local
     */
    public void loadLocalProjects() {
        List<Project> localProjects = projectRepository.getLocalProjects();
        if (localProjects != null && !localProjects.isEmpty()) {
            projects.setValue(localProjects);
        }
    }
    
    /**
     * Actualisation des données
     */
    public void refresh() {
        loadProjects(1, 20, null, "", "ACTIVE");
        loadCategories();
    }
}

