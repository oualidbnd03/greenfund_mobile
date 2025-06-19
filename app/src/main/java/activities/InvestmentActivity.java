package com.crowdfundpro.android.ui.investments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.ui.projects.ProjectViewModel;
import com.crowdfundpro.android.utils.ValidationUtils;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Activité pour effectuer un investissement dans un projet
 */
public class InvestmentActivity extends AppCompatActivity {
    
    private TextView tvProjectTitle;
    private TextView tvProjectTarget;
    private TextView tvProjectCurrent;
    private EditText etInvestmentAmount;
    private TextView tvMinAmount;
    private TextView tvMaxAmount;
    private Button btnConfirmInvestment;
    private ProgressBar progressBar;
    
    private ProjectViewModel projectViewModel;
    private InvestmentViewModel investmentViewModel;
    private PaymentSheet paymentSheet;
    
    private int projectId;
    private Project currentProject;
    private NumberFormat currencyFormat;
    
    // Limites d'investissement
    private static final double MIN_INVESTMENT = 10.0;
    private static final double MAX_INVESTMENT = 10000.0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment);
        
        // Récupérer l'ID du projet
        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId == -1) {
            Toast.makeText(this, "Erreur: ID de projet manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        initViewModels();
        initStripe();
        setupClickListeners();
        observeViewModels();
        
        // Charger les détails du projet
        projectViewModel.loadProject(projectId);
    }
    
    private void initViews() {
        tvProjectTitle = findViewById(R.id.tv_project_title);
        tvProjectTarget = findViewById(R.id.tv_project_target);
        tvProjectCurrent = findViewById(R.id.tv_project_current);
        etInvestmentAmount = findViewById(R.id.et_investment_amount);
        tvMinAmount = findViewById(R.id.tv_min_amount);
        tvMaxAmount = findViewById(R.id.tv_max_amount);
        btnConfirmInvestment = findViewById(R.id.btn_confirm_investment);
        progressBar = findViewById(R.id.progress_bar);
        
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
        // Afficher les limites d'investissement
        tvMinAmount.setText(String.format("Minimum : %s", currencyFormat.format(MIN_INVESTMENT)));
        tvMaxAmount.setText(String.format("Maximum : %s", currencyFormat.format(MAX_INVESTMENT)));
    }
    
    private void initViewModels() {
        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);
        investmentViewModel = new ViewModelProvider(this).get(InvestmentViewModel.class);
    }
    
    private void initStripe() {
        // TODO: Remplacer par votre clé publique Stripe
        PaymentConfiguration.init(getApplicationContext(), "pk_test_your_stripe_publishable_key");
        
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
    }
    
    private void setupClickListeners() {
        btnConfirmInvestment.setOnClickListener(v -> processInvestment());
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void observeViewModels() {
        // Observer le projet sélectionné
        projectViewModel.getSelectedProject().observe(this, project -> {
            if (project != null) {
                currentProject = project;
                displayProjectInfo(project);
            }
        });
        
        // Observer les erreurs du projet
        projectViewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });
        
        // Observer l'état de l'investissement
        investmentViewModel.getInvestmentState().observe(this, state -> {
            switch (state.getStatus()) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    showSuccess("Investissement réussi !");
                    finish();
                    break;
                case ERROR:
                    showLoading(false);
                    showError(state.getMessage());
                    break;
                case PAYMENT_REQUIRED:
                    showLoading(false);
                    presentPaymentSheet(state.getPaymentIntentClientSecret());
                    break;
            }
        });
    }
    
    private void displayProjectInfo(Project project) {
        tvProjectTitle.setText(project.getTitle());
        tvProjectTarget.setText(String.format("Objectif : %s", 
            currencyFormat.format(project.getTargetAmount())));
        tvProjectCurrent.setText(String.format("Collecté : %s (%.1f%%)", 
            currencyFormat.format(project.getCurrentAmount()),
            project.getProgressPercentage()));
    }
    
    private void processInvestment() {
        String amountStr = etInvestmentAmount.getText().toString().trim();
        
        // Validation du montant
        ValidationUtils.ValidationResult validation = 
            ValidationUtils.validateInvestmentAmount(amountStr, MIN_INVESTMENT, MAX_INVESTMENT);
        
        if (!validation.isValid()) {
            etInvestmentAmount.setError(validation.getErrorMessage());
            etInvestmentAmount.requestFocus();
            return;
        }
        
        double amount = Double.parseDouble(amountStr);
        
        // Vérifier que le projet est toujours actif
        if (currentProject == null || !currentProject.isActive()) {
            showError("Ce projet n'est plus disponible pour les investissements");
            return;
        }
        
        // Créer l'investissement
        investmentViewModel.createInvestment(projectId, amount, "STRIPE");
    }
    
    private void presentPaymentSheet(String clientSecret) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("CrowdfundPro")
                .build();
        
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }
    
    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Paiement réussi
            investmentViewModel.confirmPayment();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            // Paiement annulé
            showError("Paiement annulé");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // Paiement échoué
            PaymentSheetResult.Failed failed = (PaymentSheetResult.Failed) paymentSheetResult;
            showError("Erreur de paiement : " + failed.getError().getLocalizedMessage());
        }
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnConfirmInvestment.setEnabled(!isLoading);
        etInvestmentAmount.setEnabled(!isLoading);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

