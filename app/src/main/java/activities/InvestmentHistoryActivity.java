package com.crowdfundpro.android.ui.investments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Investment;
import java.util.List;

/**
 * Activité pour afficher l'historique des investissements de l'utilisateur
 */
public class InvestmentHistoryActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewInvestments;
    private InvestmentAdapter investmentAdapter;
    private ProgressBar progressBar;
    
    private InvestmentViewModel investmentViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_history);
        
        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        // Charger l'historique des investissements
        investmentViewModel.loadUserInvestments();
    }
    
    private void initViews() {
        recyclerViewInvestments = findViewById(R.id.recycler_view_investments);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initViewModel() {
        investmentViewModel = new ViewModelProvider(this).get(InvestmentViewModel.class);
    }
    
    private void setupRecyclerView() {
        investmentAdapter = new InvestmentAdapter();
        recyclerViewInvestments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInvestments.setAdapter(investmentAdapter);
    }
    
    private void setupClickListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void observeViewModel() {
        investmentViewModel.getInvestments().observe(this, investments -> {
            if (investments != null) {
                investmentAdapter.setInvestments(investments);
                showLoading(false);
                
                // Afficher un message si la liste est vide
                if (investments.isEmpty()) {
                    findViewById(R.id.layout_empty).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.layout_empty).setVisibility(View.GONE);
                }
            }
        });
        
        investmentViewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
                showLoading(false);
            }
        });
        
        investmentViewModel.getLoading().observe(this, isLoading -> {
            showLoading(isLoading);
        });
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerViewInvestments.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

