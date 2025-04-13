package com.welltech.dao;

import com.welltech.db.DatabaseConnection;
import com.welltech.model.Article;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Article entities
 */
public class ArticleDAO {
    
    /**
     * Insert a new article into the database
     * @param article Article to insert
     * @return ID of the inserted article, or -1 if insertion failed
     */
    public int insertArticle(Article article) {
        String query = "INSERT INTO articles (title, content, author_id, category, is_published) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setInt(3, article.getAuthorId());
            statement.setString(4, article.getCategory());
            statement.setBoolean(5, article.isPublished());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting article: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Update an existing article in the database
     * @param article Article to update
     * @return true if the update was successful
     */
    public boolean updateArticle(Article article) {
        String query = "UPDATE articles SET title = ?, content = ?, category = ?, is_published = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setString(3, article.getCategory());
            statement.setBoolean(4, article.isPublished());
            statement.setInt(5, article.getId());
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating article: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete an article from the database
     * @param articleId ID of the article to delete
     * @return true if the deletion was successful
     */
    public boolean deleteArticle(int articleId) {
        String query = "DELETE FROM articles WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, articleId);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting article: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get an article by ID
     * @param articleId ID of the article to get
     * @return Article object if found, null otherwise
     */
    public Article getArticleById(int articleId) {
        String query = "SELECT a.*, u.full_name AS author_name FROM articles a " +
                       "JOIN users u ON a.author_id = u.id " +
                       "WHERE a.id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, articleId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractArticleFromResultSet(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting article by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all articles
     * @return List of all articles
     */
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT a.*, u.full_name AS author_name FROM articles a " +
                       "JOIN users u ON a.author_id = u.id " +
                       "ORDER BY a.created_at DESC";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                articles.add(extractArticleFromResultSet(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all articles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return articles;
    }
    
    /**
     * Get all published articles
     * @return List of published articles
     */
    public List<Article> getPublishedArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT a.*, u.full_name AS author_name FROM articles a " +
                       "JOIN users u ON a.author_id = u.id " +
                       "WHERE a.is_published = TRUE " +
                       "ORDER BY a.created_at DESC";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                articles.add(extractArticleFromResultSet(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting published articles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return articles;
    }
    
    /**
     * Get articles by author
     * @param authorId ID of the author
     * @return List of articles by the author
     */
    public List<Article> getArticlesByAuthor(int authorId) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT a.*, u.full_name AS author_name FROM articles a " +
                       "JOIN users u ON a.author_id = u.id " +
                       "WHERE a.author_id = ? " +
                       "ORDER BY a.created_at DESC";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, authorId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    articles.add(extractArticleFromResultSet(resultSet));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting articles by author: " + e.getMessage());
            e.printStackTrace();
        }
        
        return articles;
    }
    
    /**
     * Extract an Article object from a ResultSet
     * @param resultSet ResultSet to extract from
     * @return Article object
     * @throws SQLException if a database access error occurs
     */
    private Article extractArticleFromResultSet(ResultSet resultSet) throws SQLException {
        Article article = new Article();
        
        article.setId(resultSet.getInt("id"));
        article.setTitle(resultSet.getString("title"));
        article.setContent(resultSet.getString("content"));
        article.setAuthorId(resultSet.getInt("author_id"));
        article.setAuthorName(resultSet.getString("author_name"));
        article.setCategory(resultSet.getString("category"));
        
        // Convert SQL Timestamp to LocalDateTime
        Timestamp createdTimestamp = resultSet.getTimestamp("created_at");
        if (createdTimestamp != null) {
            article.setCreatedAt(createdTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedTimestamp = resultSet.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            article.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }
        
        article.setPublished(resultSet.getBoolean("is_published"));
        
        return article;
    }
} 