package com.crowdfundpro.android.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.ui.projects.ProjectListActivity;
import com.crowdfundpro.android.ui.investments.InvestmentHistoryActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activité principale du tableau de bord
 */
public class DashboardActivity extends AppCompatActivity {
    
    private TextView tvTotalInvested;
    private TextView tvActiveInvestments;
    private TextView tvCompletedInvestments;
    private TextView tvTotalProjects;
    private PieChart pieChartInvestments;
    private LineChart lineChartProgress;
    private RecyclerView recyclerViewRecentActivity;
    private ProgressBar progressBar;
    
    private DashboardViewModel dashboardViewModel;
    private RecentActivityAdapter recentActivityAdapter;
    private NumberFormat currencyFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        setupCharts();
        
        // Charger les données du tableau de bord
        dashboardViewModel.loadDashboardData();
    }
    
    private void initViews() {
        tvTotalInvested = findViewById(R.id.tv_total_invested);
        tvActiveInvestments = findViewById(R.id.tv_active_investments);
        tvCompletedInvestments = findViewById(R.id.tv_completed_investments);
        tvTotalProjects = findViewById(R.id.tv_total_projects);
        pieChartInvestments = findViewById(R.id.pie_chart_investments);
        lineChartProgress = findViewById(R.id.line_chart_progress);
        recyclerViewRecentActivity = findViewById(R.id.recycler_view_recent_activity);
        progressBar = findViewById(R.id.progress_bar);
        
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    }
    
    private void initViewModel() {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }
    
    private void setupRecyclerView() {
        recentActivityAdapter = new RecentActivityAdapter();
        recyclerViewRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecentActivity.setAdapter(recentActivityAdapter);
    }
    
    private void setupClickListeners() {
        findViewById(R.id.card_total_invested).setOnClickListener(v -> {
            Intent intent = new Intent(this, InvestmentHistoryActivity.class);
            startActivity(intent);
        });
        
        findViewById(R.id.card_projects).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProjectListActivity.class);
            startActivity(intent);
        });
        
        findViewById(R.id.btn_view_all_projects).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProjectListActivity.class);
            startActivity(intent);
        });
        
        findViewById(R.id.btn_view_all_investments).setOnClickListener(v -> {
            Intent intent = new Intent(this, InvestmentHistoryActivity.class);
            startActivity(intent);
        });
    }
    
    private void observeViewModel() {
        dashboardViewModel.getDashboardData().observe(this, data -> {
            if (data != null) {
                updateDashboardStats(data);
                updateCharts(data);
                showLoading(false);
            }
        });
        
        dashboardViewModel.getRecentActivity().observe(this, activities -> {
            if (activities != null) {
                recentActivityAdapter.setActivities(activities);
            }
        });
        
        dashboardViewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
                showLoading(false);
            }
        });
        
        dashboardViewModel.getLoading().observe(this, isLoading -> {
            showLoading(isLoading);
        });
    }
    
    private void updateDashboardStats(DashboardViewModel.DashboardData data) {
        tvTotalInvested.setText(currencyFormat.format(data.getTotalInvested()));
        tvActiveInvestments.setText(String.valueOf(data.getActiveInvestments()));
        tvCompletedInvestments.setText(String.valueOf(data.getCompletedInvestments()));
        tvTotalProjects.setText(String.valueOf(data.getTotalProjects()));
    }
    
    private void setupCharts() {
        // Configuration du graphique en secteurs
        pieChartInvestments.setUsePercentValues(true);
        pieChartInvestments.getDescription().setEnabled(false);
        pieChartInvestments.setDrawHoleEnabled(true);
        pieChartInvestments.setHoleColor(getColor(R.color.background_dark));
        pieChartInvestments.setTransparentCircleColor(getColor(R.color.background_dark));
        pieChartInvestments.setHoleRadius(58f);
        pieChartInvestments.setTransparentCircleRadius(61f);
        pieChartInvestments.setDrawCenterText(true);
        pieChartInvestments.setCenterText("Répartition\\ndes investissements");
        pieChartInvestments.setCenterTextColor(getColor(R.color.text_primary_dark));
        pieChartInvestments.getLegend().setTextColor(getColor(R.color.text_secondary_dark));
        
        // Configuration du graphique linéaire
        lineChartProgress.getDescription().setEnabled(false);
        lineChartProgress.setTouchEnabled(true);
        lineChartProgress.setDragEnabled(true);
        lineChartProgress.setScaleEnabled(true);
        lineChartProgress.setDrawGridBackground(false);
        lineChartProgress.getXAxis().setTextColor(getColor(R.color.text_secondary_dark));
        lineChartProgress.getAxisLeft().setTextColor(getColor(R.color.text_secondary_dark));
        lineChartProgress.getAxisRight().setEnabled(false);
        lineChartProgress.getLegend().setTextColor(getColor(R.color.text_secondary_dark));
    }
    
    private void updateCharts(DashboardViewModel.DashboardData data) {
        updatePieChart(data);
        updateLineChart(data);
    }
    
    private void updatePieChart(DashboardViewModel.DashboardData data) {
        List<PieEntry> entries = new ArrayList<>();
        
        if (data.getActiveInvestments() > 0) {
            entries.add(new PieEntry(data.getActiveInvestments(), "Actifs"));
        }
        if (data.getCompletedInvestments() > 0) {
            entries.add(new PieEntry(data.getCompletedInvestments(), "Terminés"));
        }
        if (data.getFailedInvestments() > 0) {
            entries.add(new PieEntry(data.getFailedInvestments(), "Échecs"));
        }
        
        if (!entries.isEmpty()) {
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            
            // Couleurs du thème futuriste
            List<Integer> colors = new ArrayList<>();
            colors.add(getColor(R.color.primary_blue));
            colors.add(getColor(R.color.accent_green));
            colors.add(getColor(R.color.error));
            dataSet.setColors(colors);
            
            PieData pieData = new PieData(dataSet);
            pieData.setValueTextSize(11f);
            pieData.setValueTextColor(getColor(R.color.text_primary_dark));
            
            pieChartInvestments.setData(pieData);
            pieChartInvestments.invalidate();
        }
    }
    
    private void updateLineChart(DashboardViewModel.DashboardData data) {
        List<Entry> entries = new ArrayList<>();
        
        // Données simulées pour l'évolution des investissements
        // TODO: Remplacer par de vraies données depuis l'API
        for (int i = 0; i < 12; i++) {
            entries.add(new Entry(i, (float) (Math.random() * 1000 + 500)));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "Évolution des investissements");
        dataSet.setColor(getColor(R.color.primary_blue));
        dataSet.setCircleColor(getColor(R.color.primary_blue));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setValueTextColor(getColor(R.color.text_secondary_dark));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getColor(R.color.primary_blue));
        dataSet.setFillAlpha(50);
        
        LineData lineData = new LineData(dataSet);
        lineChartProgress.setData(lineData);
        lineChartProgress.invalidate();
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

