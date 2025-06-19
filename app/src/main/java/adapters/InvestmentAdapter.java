package com.crowdfundpro.android.ui.investments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Investment;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter pour l'affichage des investissements dans une RecyclerView
 */
public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.InvestmentViewHolder> {
    
    private List<Investment> investments = new ArrayList<>();
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    public InvestmentAdapter() {
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
    }
    
    @NonNull
    @Override
    public InvestmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_investment, parent, false);
        return new InvestmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull InvestmentViewHolder holder, int position) {
        Investment investment = investments.get(position);
        holder.bind(investment);
    }
    
    @Override
    public int getItemCount() {
        return investments.size();
    }
    
    public void setInvestments(List<Investment> investments) {
        this.investments = investments != null ? investments : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    class InvestmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAmount;
        private TextView tvProjectTitle;
        private TextView tvDate;
        private TextView tvStatus;
        private TextView tvTransactionId;
        
        public InvestmentViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvProjectTitle = itemView.findViewById(R.id.tv_project_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
        }
        
        public void bind(Investment investment) {
            // Montant
            tvAmount.setText(currencyFormat.format(investment.getAmount()));
            
            // Titre du projet (TODO: récupérer depuis la base de données)
            tvProjectTitle.setText("Projet #" + investment.getProjectId());
            
            // Date
            tvDate.setText(dateFormat.format(new Date(investment.getCreatedAt())));
            
            // Statut
            tvStatus.setText(getStatusText(investment.getStatus()));
            tvStatus.setTextColor(getStatusColor(investment.getStatus()));
            
            // ID de transaction
            if (investment.getTransactionId() != null && !investment.getTransactionId().isEmpty()) {
                tvTransactionId.setText("ID: " + investment.getTransactionId());
                tvTransactionId.setVisibility(View.VISIBLE);
            } else {
                tvTransactionId.setVisibility(View.GONE);
            }
        }
        
        private String getStatusText(String status) {
            switch (status) {
                case "PENDING":
                    return "En attente";
                case "COMPLETED":
                    return "Terminé";
                case "FAILED":
                    return "Échec";
                case "REFUNDED":
                    return "Remboursé";
                case "CANCELLED":
                    return "Annulé";
                default:
                    return status;
            }
        }
        
        private int getStatusColor(String status) {
            switch (status) {
                case "COMPLETED":
                    return itemView.getContext().getColor(R.color.success);
                case "FAILED":
                case "CANCELLED":
                    return itemView.getContext().getColor(R.color.error);
                case "PENDING":
                    return itemView.getContext().getColor(R.color.warning);
                case "REFUNDED":
                    return itemView.getContext().getColor(R.color.info);
                default:
                    return itemView.getContext().getColor(R.color.text_secondary_dark);
            }
        }
    }
}

