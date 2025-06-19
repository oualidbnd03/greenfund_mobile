package com.crowdfundpro.android.ui.projects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.data.models.Project;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter pour l'affichage des projets dans une RecyclerView
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    
    private List<Project> projects = new ArrayList<>();
    private OnProjectClickListener listener;
    private NumberFormat currencyFormat;
    
    public interface OnProjectClickListener {
        void onProjectClick(Project project);
        void onFavoriteClick(Project project);
        void onInvestClick(Project project);
    }
    
    public ProjectAdapter(OnProjectClickListener listener) {
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    }
    
    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.bind(project);
    }
    
    @Override
    public int getItemCount() {
        return projects.size();
    }
    
    public void setProjects(List<Project> projects) {
        this.projects = projects != null ? projects : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    class ProjectViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProjectImage;
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvTargetAmount;
        private TextView tvCurrentAmount;
        private TextView tvProgress;
        private ProgressBar progressBar;
        private TextView tvDaysLeft;
        private ImageView ivFavorite;
        private TextView btnInvest;
        
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivProjectImage = itemView.findViewById(R.id.iv_project_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTargetAmount = itemView.findViewById(R.id.tv_target_amount);
            tvCurrentAmount = itemView.findViewById(R.id.tv_current_amount);
            tvProgress = itemView.findViewById(R.id.tv_progress);
            progressBar = itemView.findViewById(R.id.progress_bar);
            tvDaysLeft = itemView.findViewById(R.id.tv_days_left);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            btnInvest = itemView.findViewById(R.id.btn_invest);
            
            // Configuration des listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProjectClick(projects.get(position));
                }
            });
            
            ivFavorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFavoriteClick(projects.get(position));
                }
            });
            
            btnInvest.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onInvestClick(projects.get(position));
                }
            });
        }
        
        public void bind(Project project) {
            // Titre et description
            tvTitle.setText(project.getTitle());
            tvDescription.setText(project.getDescription());
            
            // Montants
            tvTargetAmount.setText(String.format("Objectif : %s", 
                currencyFormat.format(project.getTargetAmount())));
            tvCurrentAmount.setText(String.format("Collecté : %s", 
                currencyFormat.format(project.getCurrentAmount())));
            
            // Progression
            double progressPercentage = project.getProgressPercentage();
            tvProgress.setText(String.format("%.0f%% atteint", progressPercentage));
            progressBar.setProgress((int) progressPercentage);
            
            // Jours restants
            long daysLeft = calculateDaysLeft(project.getEndDate());
            if (daysLeft > 0) {
                tvDaysLeft.setText(String.format("%d jours restants", daysLeft));
                tvDaysLeft.setTextColor(itemView.getContext().getColor(R.color.text_primary_dark));
            } else {
                tvDaysLeft.setText("Terminé");
                tvDaysLeft.setTextColor(itemView.getContext().getColor(R.color.error));
            }
            
            // Image du projet
            if (project.getImageUrl() != null && !project.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(project.getImageUrl())
                        .placeholder(R.drawable.placeholder_project)
                        .error(R.drawable.placeholder_project)
                        .centerCrop()
                        .into(ivProjectImage);
            } else {
                ivProjectImage.setImageResource(R.drawable.placeholder_project);
            }
            
            // État du bouton d'investissement
            if (project.isActive() && daysLeft > 0) {
                btnInvest.setEnabled(true);
                btnInvest.setText("Investir");
                btnInvest.setBackgroundResource(R.drawable.button_gradient_primary);
            } else {
                btnInvest.setEnabled(false);
                btnInvest.setText("Terminé");
                btnInvest.setBackgroundResource(R.drawable.button_disabled);
            }
            
            // TODO: Gérer l'état des favoris
            // ivFavorite.setImageResource(project.isFavorite() ? 
            //     R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
        }
        
        private long calculateDaysLeft(long endDate) {
            long currentTime = System.currentTimeMillis();
            long timeDiff = endDate - currentTime;
            return timeDiff > 0 ? timeDiff / (1000 * 60 * 60 * 24) : 0;
        }
    }
}

