package com.welltech.model;

import java.time.LocalDateTime;

/**
 * Model class for articles in the system
 */
public class Article {
    private int id;
    private String title;
    private String content;
    private int authorId;
    private String authorName; // For display purposes
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublished;
    
    // Default constructor
    public Article() {
    }
    
    // Constructor with required fields
    public Article(String title, String content, int authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }
    
    // Full constructor
    public Article(int id, String title, String content, int authorId, String authorName, 
                  String category, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isPublished) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPublished = isPublished;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isPublished() {
        return isPublished;
    }
    
    public void setPublished(boolean published) {
        isPublished = published;
    }
    
    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authorId=" + authorId +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", isPublished=" + isPublished +
                '}';
    }
} 