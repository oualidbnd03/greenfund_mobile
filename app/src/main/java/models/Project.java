package com.crowdfundpro.android.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

/**
 * Entité Room représentant un projet dans la base de données locale
 */
@Entity(tableName = "projects",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "creator_id",
                                  onDelete = ForeignKey.CASCADE))
public class Project {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "target_amount")
    private double targetAmount;

    @ColumnInfo(name = "current_amount")
    private double currentAmount;

    @ColumnInfo(name = "status")
    private String status; // ACTIVE, COMPLETED, CANCELLED

    @ColumnInfo(name = "creator_id")
    private int creatorId;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "end_date")
    private long endDate;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    // Constructeurs
    public Project() {}

    public Project(int id, String title, String description, double targetAmount, 
                   double currentAmount, String status, int creatorId, int categoryId,
                   String imageUrl, long createdAt, long endDate, long updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.status = status;
        this.creatorId = creatorId;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.endDate = endDate;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Méthodes utilitaires
    public double getProgressPercentage() {
        if (targetAmount <= 0) return 0;
        return Math.min((currentAmount / targetAmount) * 100, 100);
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
}

