package com.crowdfundpro.android.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.crowdfundpro.android.R;
import com.crowdfundpro.android.ui.dashboard.DashboardActivity;
import com.crowdfundpro.android.ui.projects.ProjectDetailActivity;

/**
 * Gestionnaire des notifications push
 */
public class NotificationManager {
    
    private static final String CHANNEL_ID_GENERAL = "crowdfundpro_general";
    private static final String CHANNEL_ID_INVESTMENTS = "crowdfundpro_investments";
    private static final String CHANNEL_ID_PROJECTS = "crowdfundpro_projects";
    
    private Context context;
    private NotificationManagerCompat notificationManager;
    
    public NotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }
    
    /**
     * Création des canaux de notification
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal général
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Notifications générales",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("Notifications générales de l'application");
            
            // Canal investissements
            NotificationChannel investmentChannel = new NotificationChannel(
                CHANNEL_ID_INVESTMENTS,
                "Investissements",
                android.app.NotificationManager.IMPORTANCE_HIGH
            );
            investmentChannel.setDescription("Notifications liées aux investissements");
            
            // Canal projets
            NotificationChannel projectChannel = new NotificationChannel(
                CHANNEL_ID_PROJECTS,
                "Projets",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            projectChannel.setDescription("Notifications liées aux projets");
            
            android.app.NotificationManager manager = 
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(generalChannel);
            manager.createNotificationChannel(investmentChannel);
            manager.createNotificationChannel(projectChannel);
        }
    }
    
    /**
     * Notification de succès d'investissement
     */
    public void showInvestmentSuccessNotification(String projectTitle, double amount) {
        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_INVESTMENTS)
                .setSmallIcon(R.drawable.ic_check_circle)
                .setContentTitle("Investissement réussi !")
                .setContentText(String.format("Votre investissement de %.2f€ dans \"%s\" a été confirmé", amount, projectTitle))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getColor(R.color.success));
        
        notificationManager.notify(generateNotificationId(), builder.build());
    }
    
    /**
     * Notification d'échec d'investissement
     */
    public void showInvestmentFailureNotification(String projectTitle, String reason) {
        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_INVESTMENTS)
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle("Échec de l'investissement")
                .setContentText(String.format("Votre investissement dans \"%s\" a échoué: %s", projectTitle, reason))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getColor(R.color.error));
        
        notificationManager.notify(generateNotificationId(), builder.build());
    }
    
    /**
     * Notification de fin de projet
     */
    public void showProjectEndNotification(int projectId, String projectTitle, boolean successful) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("project_id", projectId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        String title = successful ? "Projet financé avec succès !" : "Projet non financé";
        String message = successful ? 
            String.format("Le projet \"%s\" a atteint son objectif de financement", projectTitle) :
            String.format("Le projet \"%s\" n'a pas atteint son objectif", projectTitle);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_PROJECTS)
                .setSmallIcon(successful ? R.drawable.ic_check_circle : R.drawable.ic_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getColor(successful ? R.color.success : R.color.warning));
        
        notificationManager.notify(generateNotificationId(), builder.build());
    }
    
    /**
     * Notification de nouveau projet dans une catégorie suivie
     */
    public void showNewProjectNotification(int projectId, String projectTitle, String categoryName) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("project_id", projectId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_PROJECTS)
                .setSmallIcon(R.drawable.ic_add_circle)
                .setContentTitle("Nouveau projet disponible")
                .setContentText(String.format("Découvrez \"%s\" dans la catégorie %s", projectTitle, categoryName))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getColor(R.color.primary_blue));
        
        notificationManager.notify(generateNotificationId(), builder.build());
    }
    
    /**
     * Notification de commentaire sur un projet suivi
     */
    public void showNewCommentNotification(int projectId, String projectTitle, String commenterName) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("project_id", projectId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
                .setSmallIcon(R.drawable.ic_comment)
                .setContentTitle("Nouveau commentaire")
                .setContentText(String.format("%s a commenté le projet \"%s\"", commenterName, projectTitle))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getColor(R.color.accent_purple));
        
        notificationManager.notify(generateNotificationId(), builder.build());
    }
    
    /**
     * Notification de rappel d'investissement
     */
    public void showInvestmentReminderNotification(int projectId, String projectTitle, int daysLeft) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("project_id", projectId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentTitle("Derniers jours pour investir")
                .setContentText(String.format("Il ne reste que %d jours pour investir dans \"%s\"", daysLeft, projectTitle))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getColor(R.color.warning));
        
        notificationManager.notify(generateNotificationId(), builder.build());
    }
    
    /**
     * Génération d'un ID unique pour les notifications
     */
    private int generateNotificationId() {
        return (int) System.currentTimeMillis();
    }
    
    /**
     * Annulation de toutes les notifications
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
    
    /**
     * Vérification des permissions de notification
     */
    public boolean areNotificationsEnabled() {
        return notificationManager.areNotificationsEnabled();
    }
}

