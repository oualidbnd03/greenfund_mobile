package com.crowdfundpro.android.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.crowdfundpro.android.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter pour l'affichage de l'activité récente
 */
public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder> {
    
    private List<DashboardViewModel.RecentActivity> activities = new ArrayList<>();
    private SimpleDateFormat dateFormat;
    
    public RecentActivityAdapter() {
        this.dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE);
    }
    
    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ActivityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        DashboardViewModel.RecentActivity activity = activities.get(position);
        holder.bind(activity);
    }
    
    @Override
    public int getItemCount() {
        return activities.size();
    }
    
    public void setActivities(List<DashboardViewModel.RecentActivity> activities) {
        this.activities = activities != null ? activities : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    class ActivityViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvDescription;
        private TextView tvTimestamp;
        
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
        
        public void bind(DashboardViewModel.RecentActivity activity) {
            tvDescription.setText(activity.getDescription());
            tvTimestamp.setText(dateFormat.format(new Date(activity.getTimestamp())));
            
            // Définir l'icône et la couleur selon le type d'activité
            switch (activity.getType()) {
                case INVESTMENT_SUCCESS:
                    ivIcon.setImageResource(R.drawable.ic_check_circle);
                    ivIcon.setColorFilter(itemView.getContext().getColor(R.color.success));
                    break;
                case INVESTMENT_FAILED:
                    ivIcon.setImageResource(R.drawable.ic_error);
                    ivIcon.setColorFilter(itemView.getContext().getColor(R.color.error));
                    break;
                case INVESTMENT_PENDING:
                    ivIcon.setImageResource(R.drawable.ic_schedule);
                    ivIcon.setColorFilter(itemView.getContext().getColor(R.color.warning));
                    break;
                case PROJECT_CREATED:
                    ivIcon.setImageResource(R.drawable.ic_add_circle);
                    ivIcon.setColorFilter(itemView.getContext().getColor(R.color.primary_blue));
                    break;
                default:
                    ivIcon.setImageResource(R.drawable.ic_info);
                    ivIcon.setColorFilter(itemView.getContext().getColor(R.color.text_secondary_dark));
                    break;
            }
        }
    }
}

