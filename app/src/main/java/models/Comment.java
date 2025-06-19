package com.crowdfundpro.android.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Entité Comment pour la base de données Room
 */
@Entity(tableName = "comments",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                       parentColumns = "id",
                       childColumns = "userId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Project.class,
                       parentColumns = "id", 
                       childColumns = "projectId",
                       onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("userId"), @Index("projectId")})
public class Comment {
    
    @PrimaryKey
    private int id;
    
    private int projectId;
    private int userId;
    private String content;
    private long createdAt;
    private long updatedAt;
    private boolean isReported;
    private boolean isDeleted;
    
    // Informations de l'utilisateur (dénormalisées pour éviter les jointures)
    private String userName;
    private String userAvatarUrl;
    
    public Comment() {}
    
    public Comment(int projectId, int userId, String content) {
        this.projectId = projectId;
        this.userId = userId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.isReported = false;
        this.isDeleted = false;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isReported() {
        return isReported;
    }
    
    public void setReported(boolean reported) {
        isReported = reported;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }
    
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
    
    /**
     * Vérifie si le commentaire a été modifié
     */
    public boolean isEdited() {
        return updatedAt > createdAt;
    }
    
    /**
     * Calcule le temps écoulé depuis la création
     */
    public long getTimeAgo() {
        return System.currentTimeMillis() - createdAt;
    }
    
    /**
     * Formate le temps écoulé en texte lisible
     */
    public String getFormattedTimeAgo() {
        long timeAgo = getTimeAgo();
        
        if (timeAgo < 60000) { // Moins d'une minute
            return "À l'instant";
        } else if (timeAgo < 3600000) { // Moins d'une heure
            int minutes = (int) (timeAgo / 60000);
            return minutes + " min";
        } else if (timeAgo < 86400000) { // Moins d'un jour
            int hours = (int) (timeAgo / 3600000);
            return hours + " h";
        } else if (timeAgo < 604800000) { // Moins d'une semaine
            int days = (int) (timeAgo / 86400000);
            return days + " j";
        } else {
            int weeks = (int) (timeAgo / 604800000);
            return weeks + " sem";
        }
    }
}

