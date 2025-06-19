package com.crowdfundpro.android.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

/**
 * Entité Room représentant une catégorie dans la base de données locale
 */
@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "icon_url")
    private String iconUrl;

    @ColumnInfo(name = "color")
    private String color; // Couleur hexadécimale pour l'affichage

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructeurs
    public Category() {}

    public Category(int id, String name, String description, String iconUrl, String color, long createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.color = color;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

