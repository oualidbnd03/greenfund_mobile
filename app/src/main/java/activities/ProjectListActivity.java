package com.crowdfundpro.android.ui.projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.data.models.Category;
import java.util.List;

/**
 * Activité principale pour l'affichage et la gestion des projets
 */
public class ProjectListActivity extends AppCompatActivity implements ProjectAdapter.OnProjectClickListener {
    
    private RecyclerView recyclerViewProjects;
    private ProjectAdapter projectAdapter;
    private ProgressBar progressBar;
    private SearchView searchView;
    private ChipGroup chipGroupCategories;
    private FloatingActionButton fabCreateProject;
    
    private ProjectViewModel projectViewModel;
    
    private String currentSearchQuery = "";
    private Integer selectedCategoryId = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        
        initViews();
        initViewModel();
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
        observeViewModel();
        
        // Charger les données initiales
        loadProjects();
        loadCategories();
    }
    
    private void initViews() {
        recyclerViewProjects = findViewById(R.id.recycler_view_projects);
        progressBar = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);
        chipGroupCategories = findViewById(R.id.chip_group_categories);
        fabCreateProject = findViewById(R.id.fab_create_project);
    }
    
    private void initViewModel() {
        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);
    }
    
    private void setupRecyclerView() {
        projectAdapter = new ProjectAdapter(this);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProjects.setAdapter(projectAdapter);
    }
    
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                loadProjects();
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    currentSearchQuery = "";
                    loadProjects();
                }
                return true;
            }
        });
    }
    
    private void setupClickListeners() {
        fabCreateProject.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProjectCreateActivity.class);
            startActivity(intent);
        });
    }
    
    private void observeViewModel() {
        projectViewModel.getProjects().observe(this, projects -> {
            if (projects != null) {
                projectAdapter.setProjects(projects);
                showLoading(false);
            }
        });
        
        projectViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                setupCategoryChips(categories);
            }
        });
        
        projectViewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
                showLoading(false);
            }
        });
        
        projectViewModel.getLoading().observe(this, isLoading -> {
            showLoading(isLoading);
        });
    }
    
    private void setupCategoryChips(List<Category> categories) {
        chipGroupCategories.removeAllViews();
        
        // Ajouter un chip "Tous"
        Chip chipAll = new Chip(this);
        chipAll.setText("Tous");
        chipAll.setCheckable(true);
        chipAll.setChecked(selectedCategoryId == null);
        chipAll.setOnClickListener(v -> {
            selectedCategoryId = null;
            loadProjects();
        });
        chipGroupCategories.addView(chipAll);
        
        // Ajouter les chips pour chaque catégorie
        for (Category category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setChecked(selectedCategoryId != null && selectedCategoryId.equals(category.getId()));
            chip.setOnClickListener(v -> {
                selectedCategoryId = category.getId();
                loadProjects();
            });
            chipGroupCategories.addView(chip);
        }
    }
    
    private void loadProjects() {
        projectViewModel.loadProjects(1, 20, selectedCategoryId, currentSearchQuery, "ACTIVE");
    }
    
    private void loadCategories() {
        projectViewModel.loadCategories();
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerViewProjects.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onProjectClick(Project project) {
        Intent intent = new Intent(this, ProjectDetailActivity.class);
        intent.putExtra("project_id", project.getId());
        startActivity(intent);
    }
    
    @Override
    public void onFavoriteClick(Project project) {
        projectViewModel.toggleFavorite(project);
    }
    
    @Override
    public void onInvestClick(Project project) {
        Intent intent = new Intent(this, InvestmentActivity.class);
        intent.putExtra("project_id", project.getId());
        startActivity(intent);
    }
}

