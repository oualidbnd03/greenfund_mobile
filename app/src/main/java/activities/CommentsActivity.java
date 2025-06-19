package com.crowdfundpro.android.ui.social;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Comment;

/**
 * Activité pour afficher et gérer les commentaires d'un projet
 */
public class CommentsActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewComments;
    private EditText etComment;
    private Button btnPostComment;
    private ProgressBar progressBar;
    
    private CommentsAdapter commentsAdapter;
    private SocialViewModel socialViewModel;
    
    private int projectId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        
        // Récupérer l'ID du projet
        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId == -1) {
            Toast.makeText(this, "Erreur: ID de projet manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        // Charger les commentaires
        socialViewModel.loadComments(projectId);
    }
    
    private void initViews() {
        recyclerViewComments = findViewById(R.id.recycler_view_comments);
        etComment = findViewById(R.id.et_comment);
        btnPostComment = findViewById(R.id.btn_post_comment);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initViewModel() {
        socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);
    }
    
    private void setupRecyclerView() {
        commentsAdapter = new CommentsAdapter();
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentsAdapter);
    }
    
    private void setupClickListeners() {
        btnPostComment.setOnClickListener(v -> postComment());
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void observeViewModel() {
        socialViewModel.getComments().observe(this, comments -> {
            if (comments != null) {
                commentsAdapter.setComments(comments);
                showLoading(false);
                
                // Faire défiler vers le bas pour voir le nouveau commentaire
                if (!comments.isEmpty()) {
                    recyclerViewComments.scrollToPosition(comments.size() - 1);
                }
            }
        });
        
        socialViewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
                showLoading(false);
            }
        });
        
        socialViewModel.getLoading().observe(this, isLoading -> {
            showLoading(isLoading);
        });
        
        socialViewModel.getCommentPosted().observe(this, posted -> {
            if (posted != null && posted) {
                etComment.setText("");
                Toast.makeText(this, "Commentaire publié avec succès", Toast.LENGTH_SHORT).show();
                // Recharger les commentaires
                socialViewModel.loadComments(projectId);
            }
        });
    }
    
    private void postComment() {
        String commentText = etComment.getText().toString().trim();
        
        if (TextUtils.isEmpty(commentText)) {
            etComment.setError("Le commentaire ne peut pas être vide");
            etComment.requestFocus();
            return;
        }
        
        if (commentText.length() < 5) {
            etComment.setError("Le commentaire doit contenir au moins 5 caractères");
            etComment.requestFocus();
            return;
        }
        
        if (commentText.length() > 500) {
            etComment.setError("Le commentaire ne peut pas dépasser 500 caractères");
            etComment.requestFocus();
            return;
        }
        
        socialViewModel.postComment(projectId, commentText);
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnPostComment.setEnabled(!isLoading);
        etComment.setEnabled(!isLoading);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

