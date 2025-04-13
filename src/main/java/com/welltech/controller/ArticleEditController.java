package com.welltech.controller;

import com.welltech.WellTechApplication;
import com.welltech.dao.ArticleDAO;
import com.welltech.model.Article;
import com.welltech.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the article editing interface
 */
public class ArticleEditController implements Initializable {
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button dashboardButton;
    
    @FXML
    private Button articlesButton;
    
    @FXML
    private Button profileButton;
    
    @FXML
    private Text pageTitle;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private TextArea contentArea;
    
    @FXML
    private CheckBox publishCheckBox;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label titleError;
    
    @FXML
    private Label contentError;
    
    @FXML
    private Label statusMessage;
    
    private final ArticleDAO articleDAO = new ArticleDAO();
    private User currentUser;
    private Article currentArticle;
    private boolean isEditMode = false;
    
    // Predefined categories for articles
    private final String[] CATEGORIES = {
        "Mental Health", "Therapy", "Self-Care", "Anxiety", "Depression", 
        "Stress Management", "Mindfulness", "Relationships", "Parenting", "Other"
    };
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.out.println("Initializing ArticleEditController");
            
            // Get current user
            currentUser = LoginController.getCurrentUser();
            
            if (currentUser != null) {
                // Update welcome label
                userNameLabel.setText("Welcome, " + currentUser.getFullName());
                
                // Initialize category dropdown
                categoryComboBox.getItems().addAll(CATEGORIES);
                
                // Set up error clearing listeners
                setupErrorClearListeners();
                
                // Check if editing existing article or creating new
                currentArticle = WellTechApplication.getCurrentArticle();
                
                if (currentArticle != null) {
                    // Edit mode
                    isEditMode = true;
                    pageTitle.setText("Edit Article");
                    populateArticleFields();
                } else {
                    // Create mode
                    isEditMode = false;
                    pageTitle.setText("Create New Article");
                    
                    // Set default values
                    publishCheckBox.setSelected(false);
                }
                
                // Verify user has permission to create/edit articles
                if (currentUser.getRole() == User.UserRole.PATIENT) {
                    showError("You don't have permission to create or edit articles");
                    saveButton.setDisable(true);
                }
            } else {
                System.err.println("No user is logged in");
                // Redirect to login if no user
                WellTechApplication.loadFXML("login");
            }
            
        } catch (Exception e) {
            System.err.println("Error initializing ArticleEditController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Set up listeners to clear error messages when input changes
     */
    private void setupErrorClearListeners() {
        titleField.textProperty().addListener((obs, old, newVal) -> {
            titleError.setText("");
            statusMessage.setText("");
        });
        
        contentArea.textProperty().addListener((obs, old, newVal) -> {
            contentError.setText("");
            statusMessage.setText("");
        });
        
        categoryComboBox.valueProperty().addListener((obs, old, newVal) -> {
            statusMessage.setText("");
        });
        
        publishCheckBox.selectedProperty().addListener((obs, old, newVal) -> {
            statusMessage.setText("");
        });
    }
    
    /**
     * Populate form fields with current article data
     */
    private void populateArticleFields() {
        titleField.setText(currentArticle.getTitle());
        contentArea.setText(currentArticle.getContent());
        
        if (currentArticle.getCategory() != null && !currentArticle.getCategory().isEmpty()) {
            categoryComboBox.setValue(currentArticle.getCategory());
        }
        
        publishCheckBox.setSelected(currentArticle.isPublished());
    }
    
    /**
     * Handle save button click
     */
    @FXML
    private void handleSave(ActionEvent event) {
        try {
            System.out.println("Save button clicked");
            
            // Validate form
            if (!validateForm()) {
                return;
            }
            
            // Get values from form
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String category = categoryComboBox.getValue();
            boolean isPublished = publishCheckBox.isSelected();
            
            if (isEditMode) {
                // Update existing article
                currentArticle.setTitle(title);
                currentArticle.setContent(content);
                currentArticle.setCategory(category);
                currentArticle.setPublished(isPublished);
                
                boolean success = articleDAO.updateArticle(currentArticle);
                
                if (success) {
                    showSuccess("Article updated successfully");
                } else {
                    showError("Failed to update article");
                }
            } else {
                // Create new article
                Article newArticle = new Article();
                newArticle.setTitle(title);
                newArticle.setContent(content);
                newArticle.setCategory(category);
                newArticle.setAuthorId(currentUser.getId());
                newArticle.setPublished(isPublished);
                
                int newId = articleDAO.insertArticle(newArticle);
                
                if (newId > 0) {
                    showSuccess("Article created successfully");
                    
                    // Clear form or navigate back to list
                    if (isPublished) {
                        // If published, go back to list
                        navigateToArticles(null);
                    } else {
                        // If draft, clear form for another article
                        clearForm();
                    }
                } else {
                    showError("Failed to create article");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error saving article: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    /**
     * Validate the form inputs
     * @return true if form is valid
     */
    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate title
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            titleError.setText("Title is required");
            isValid = false;
        } else if (title.length() < 5) {
            titleError.setText("Title must be at least 5 characters");
            isValid = false;
        }
        
        // Validate content
        String content = contentArea.getText().trim();
        if (content.isEmpty()) {
            contentError.setText("Content is required");
            isValid = false;
        } else if (content.length() < 20) {
            contentError.setText("Content must be at least 20 characters");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Clear the form fields
     */
    private void clearForm() {
        titleField.clear();
        contentArea.clear();
        categoryComboBox.setValue(null);
        publishCheckBox.setSelected(false);
        titleError.setText("");
        contentError.setText("");
    }
    
    /**
     * Show success message
     */
    private void showSuccess(String message) {
        statusMessage.setText(message);
        statusMessage.getStyleClass().remove("error-message");
        if (!statusMessage.getStyleClass().contains("success-message")) {
            statusMessage.getStyleClass().add("success-message");
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        statusMessage.setText(message);
        statusMessage.getStyleClass().remove("success-message");
        if (!statusMessage.getStyleClass().contains("error-message")) {
            statusMessage.getStyleClass().add("error-message");
        }
    }
    
    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        System.out.println("Cancel button clicked");
        
        // Go back to articles list
        navigateToArticles(null);
    }
    
    /**
     * Navigate to the articles list
     */
    @FXML
    private void navigateToArticles(ActionEvent event) {
        System.out.println("Navigating to articles list");
        WellTechApplication.loadFXML("articlesList");
    }
    
    /**
     * Navigate to the dashboard based on user role
     */
    @FXML
    private void navigateToDashboard(ActionEvent event) {
        System.out.println("Navigating to dashboard");
        
        if (currentUser != null) {
            switch (currentUser.getRole()) {
                case ADMIN:
                    WellTechApplication.loadFXML("adminDashboard");
                    break;
                case PSYCHIATRIST:
                    WellTechApplication.loadFXML("psychiatristDashboard");
                    break;
                case PATIENT:
                    WellTechApplication.loadFXML("patientDashboard");
                    break;
                default:
                    System.err.println("Unknown user role: " + currentUser.getRole());
                    WellTechApplication.loadFXML("login");
            }
        } else {
            // If no user, go to login
            WellTechApplication.loadFXML("login");
        }
    }
    
    /**
     * Navigate to the profile page
     */
    @FXML
    private void navigateToProfile(ActionEvent event) {
        System.out.println("Navigating to profile");
        WellTechApplication.loadFXML("profile");
    }
    
    /**
     * Handle logout button click
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out");
        LoginController.logout();
    }
} 