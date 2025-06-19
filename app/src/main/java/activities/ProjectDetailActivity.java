package com.crowdfundpro.android.ui.projects;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Project;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activité pour afficher les détails d'un projet
 */
public class ProjectDetailActivity extends AppCompatActivity {
    
    private ImageView ivProjectImage;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvTargetAmount;
    private TextView tvCurrentAmount;
    private TextView tvProgress;
    private ProgressBar progressBar;
    private TextView tvCreatedDate;
    private TextView tvEndDate;
    private TextView tvCreatorName;
    private MaterialButton btnInvest;
    private MaterialButton btnShare;
    private ImageView ivFavorite;
    private ProgressBar progressBarLoading;
    
    private ProjectViewModel projectViewModel;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    private int projectId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        
        // Récupérer l'ID du projet depuis l'intent
        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId == -1) {
            Toast.makeText(this, "Erreur: ID de projet manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
        
        // Charger les détails du projet
        projectViewModel.loadProject(projectId);
    }
    
    private void initViews() {
        ivProjectImage = findViewById(R.id.iv_project_image);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_description);
        tvTargetAmount = findViewById(R.id.tv_target_amount);
        tvCurrentAmount = findViewById(R.id.tv_current_amount);
        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progress_bar);
        tvCreatedDate = findViewById(R.id.tv_created_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        tvCreatorName = findViewById(R.id.tv_creator_name);
        btnInvest = findViewById(R.id.btn_invest);
        btnShare = findViewById(R.id.btn_share);
        ivFavorite = findViewById(R.id.iv_favorite);
        progressBarLoading = findViewById(R.id.progress_bar_loading);
        
        // Initialiser les formatters
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    }
    
    private void initViewModel() {
        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);
    }
    
    private void setupClickListeners() {
        btnInvest.setOnClickListener(v -> {
            // TODO: Naviguer vers l'activité d'investissement
            Toast.makeText(this, "Fonctionnalité d'investissement à venir", Toast.LENGTH_SHORT).show();
        });
        
        btnShare.setOnClickListener(v -> {
            shareProject();
        });
        
        ivFavorite.setOnClickListener(v -> {
            // TODO: Basculer le statut favori
            Toast.makeText(this, "Fonctionnalité favoris à venir", Toast.LENGTH_SHORT).show();
        });
        
        // Bouton retour
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void observeViewModel() {
        projectViewModel.getSelectedProject().observe(this, project -> {
            if (project != null) {
                displayProjectDetails(project);
                showLoading(false);
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
    
    private void displayProjectDetails(Project project) {
        // Image du projet
        if (project.getImageUrl() != null && !project.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(project.getImageUrl())
                    .placeholder(R.drawable.placeholder_project)
                    .error(R.drawable.placeholder_project)
                    .centerCrop()
                    .into(ivProjectImage);
        } else {
            ivProjectImage.setImageResource(R.drawable.placeholder_project);
        }
        
        // Informations de base
        tvTitle.setText(project.getTitle());
        tvDescription.setText(project.getDescription());
        
        // Montants
        tvTargetAmount.setText(String.format("Objectif : %s", 
            currencyFormat.format(project.getTargetAmount())));
        tvCurrentAmount.setText(String.format("Collecté : %s", 
            currencyFormat.format(project.getCurrentAmount())));
        
        // Progression
        double progressPercentage = project.getProgressPercentage();
        tvProgress.setText(String.format("%.1f%% atteint", progressPercentage));
        progressBar.setProgress((int) progressPercentage);
        
        // Dates
        tvCreatedDate.setText(String.format("Créé le : %s", 
            dateFormat.format(new Date(project.getCreatedAt()))));
        tvEndDate.setText(String.format("Fin le : %s", 
            dateFormat.format(new Date(project.getEndDate()))));
        
        // Créateur (TODO: récupérer les informations du créateur)
        tvCreatorName.setText("Créateur : Utilisateur #" + project.getCreatorId());
        
        // État du bouton d'investissement
        long daysLeft = calculateDaysLeft(project.getEndDate());
        if (project.isActive() && daysLeft > 0) {
            btnInvest.setEnabled(true);
            btnInvest.setText("Investir dans ce projet");
        } else {
            btnInvest.setEnabled(false);
            btnInvest.setText("Projet terminé");
        }
    }
    
    private void shareProject() {
        Project project = projectViewModel.getSelectedProject().getValue();
        if (project != null) {
            String shareText = String.format(
                "Découvrez ce projet sur CrowdfundPro :\n\n%s\n\n%s\n\nObjectif : %s",
                project.getTitle(),
                project.getDescription().substring(0, Math.min(project.getDescription().length(), 100)) + "...",
                currencyFormat.format(project.getTargetAmount())
            );
            
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            startActivity(android.content.Intent.createChooser(shareIntent, "Partager le projet"));
        }
    }
    
    private long calculateDaysLeft(long endDate) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = endDate - currentTime;
        return timeDiff > 0 ? timeDiff / (1000 * 60 * 60 * 24) : 0;
    }
    
    private void showLoading(boolean isLoading) {
        progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

