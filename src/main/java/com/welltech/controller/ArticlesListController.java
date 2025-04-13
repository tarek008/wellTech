package com.welltech.controller;

import com.welltech.WellTechApplication;
import com.welltech.dao.ArticleDAO;
import com.welltech.model.Article;
import com.welltech.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller for the articles list view
 */
public class ArticlesListController implements Initializable {
    
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
    private Button createArticleButton;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<Article> articlesTable;
    
    @FXML
    private TableColumn<Article, Integer> idColumn;
    
    @FXML
    private TableColumn<Article, String> titleColumn;
    
    @FXML
    private TableColumn<Article, String> authorColumn;
    
    @FXML
    private TableColumn<Article, String> categoryColumn;
    
    @FXML
    private TableColumn<Article, String> createdAtColumn;
    
    @FXML
    private TableColumn<Article, String> statusColumn;
    
    @FXML
    private TableColumn<Article, Void> actionsColumn;
    
    @FXML
    private Label statusLabel;
    
    private final ArticleDAO articleDAO = new ArticleDAO();
    private ObservableList<Article> articlesData = FXCollections.observableArrayList();
    private User currentUser;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.out.println("Initializing ArticlesListController");
            
            // Get current user
            currentUser = LoginController.getCurrentUser();
            
            if (currentUser != null) {
                // Update welcome label
                userNameLabel.setText("Welcome, " + currentUser.getFullName());
                
                // Configure table columns
                setupTableColumns();
                
                // Configure filter combobox
                setupFilterComboBox();
                
                // Set up search field
                setupSearchField();
                
                // Load articles based on user role
                loadArticles();
                
                // Hide create button for patients
                if (currentUser.getRole() == User.UserRole.PATIENT) {
                    createArticleButton.setVisible(false);
                }
            } else {
                System.err.println("No user is logged in");
                // Redirect to login if no user
                WellTechApplication.loadFXML("login");
            }
            
        } catch (Exception e) {
            System.err.println("Error initializing ArticlesListController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Set up the filter combo box options
     */
    private void setupFilterComboBox() {
        filterComboBox.getItems().addAll("All", "Published", "Drafts");
        filterComboBox.setValue("All");
        
        filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterArticles();
        });
    }
    
    /**
     * Set up the search field
     */
    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterArticles();
        });
    }
    
    /**
     * Filter articles based on search text and status filter
     */
    private void filterArticles() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();
        
        if (searchText.isEmpty() && "All".equals(filterValue)) {
            articlesTable.setItems(articlesData);
            return;
        }
        
        // Create predicates for filtering
        Predicate<Article> searchPredicate = article -> 
            searchText.isEmpty() || 
            article.getTitle().toLowerCase().contains(searchText) ||
            article.getContent().toLowerCase().contains(searchText) ||
            (article.getCategory() != null && article.getCategory().toLowerCase().contains(searchText));
        
        Predicate<Article> statusPredicate = article -> {
            if ("All".equals(filterValue)) return true;
            if ("Published".equals(filterValue)) return article.isPublished();
            if ("Drafts".equals(filterValue)) return !article.isPublished();
            return true;
        };
        
        // Apply filters
        List<Article> filteredList = articlesData.stream()
            .filter(searchPredicate)
            .filter(statusPredicate)
            .collect(Collectors.toList());
        
        // Update table
        articlesTable.setItems(FXCollections.observableArrayList(filteredList));
    }
    
    /**
     * Configure table columns and cell factories
     */
    private void setupTableColumns() {
        // Configure columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        // Format date display
        createdAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                    () -> cellData.getValue().getCreatedAt().format(dateFormatter));
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });
        
        // Format status display
        statusColumn.setCellValueFactory(cellData -> {
            boolean isPublished = cellData.getValue().isPublished();
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> isPublished ? "Published" : "Draft");
        });
        
        // Create action buttons for each row
        actionsColumn.setCellFactory(createActionsCellFactory());
    }
    
    /**
     * Create cell factory for action buttons in the table
     */
    private Callback<TableColumn<Article, Void>, TableCell<Article, Void>> createActionsCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Article, Void> call(TableColumn<Article, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("View");
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    
                    {
                        // Set button styles
                        viewBtn.getStyleClass().add("button-sm");
                        editBtn.getStyleClass().add("button-sm");
                        deleteBtn.getStyleClass().add("button-danger-sm");
                        
                        // Set button actions
                        viewBtn.setOnAction(event -> {
                            Article article = getTableRow().getItem();
                            if (article != null) {
                                viewArticle(article);
                            }
                        });
                        
                        editBtn.setOnAction(event -> {
                            Article article = getTableRow().getItem();
                            if (article != null) {
                                editArticle(article);
                            }
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Article article = getTableRow().getItem();
                            if (article != null) {
                                deleteArticle(article);
                            }
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Article article = getTableRow().getItem();
                            
                            // Create HBox for buttons
                            HBox hbox = new HBox(5);
                            hbox.getChildren().add(viewBtn);
                            
                            // Only show edit/delete if user is admin or is the author
                            if (article != null && (currentUser.getRole() == User.UserRole.ADMIN || 
                                                   article.getAuthorId() == currentUser.getId())) {
                                hbox.getChildren().add(editBtn);
                                hbox.getChildren().add(deleteBtn);
                            }
                            
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };
    }
    
    /**
     * Load articles based on user role
     */
    private void loadArticles() {
        List<Article> articles;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            // Admins see all articles
            articles = articleDAO.getAllArticles();
        } else if (currentUser.getRole() == User.UserRole.PSYCHIATRIST) {
            // Psychiatrists see all published articles and their own drafts
            articles = articleDAO.getArticlesByAuthor(currentUser.getId());
            
            // Add published articles from other authors
            List<Article> publishedArticles = articleDAO.getPublishedArticles();
            
            // Remove duplicates and merge lists
            for (Article article : publishedArticles) {
                if (article.getAuthorId() != currentUser.getId()) {
                    articles.add(article);
                }
            }
        } else {
            // Patients see only published articles
            articles = articleDAO.getPublishedArticles();
        }
        
        articlesData = FXCollections.observableArrayList(articles);
        articlesTable.setItems(articlesData);
        
        statusLabel.setText(articlesData.size() + " articles found");
    }
    
    /**
     * View an article
     */
    private void viewArticle(Article article) {
        System.out.println("Viewing article: " + article.getTitle());
        // Store article ID in session and navigate to view page
        // In a real app, this would navigate to an article view page
    }
    
    /**
     * Edit an article
     */
    private void editArticle(Article article) {
        System.out.println("Editing article: " + article.getTitle());
        // Store article ID in session and navigate to edit page
        WellTechApplication.setCurrentArticle(article);
        WellTechApplication.loadFXML("articleEdit");
    }
    
    /**
     * Delete an article
     */
    private void deleteArticle(Article article) {
        System.out.println("Deleting article: " + article.getTitle());
        
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Article");
        alert.setHeaderText("Delete Article");
        alert.setContentText("Are you sure you want to delete the article '" + article.getTitle() + "'?");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Delete the article
                boolean deleted = articleDAO.deleteArticle(article.getId());
                
                if (deleted) {
                    articlesData.remove(article);
                    statusLabel.setText("Article deleted successfully");
                } else {
                    statusLabel.setText("Failed to delete article");
                }
            }
        });
    }
    
    /**
     * Handle create article button click
     */
    @FXML
    private void handleCreateArticle(ActionEvent event) {
        System.out.println("Creating new article");
        WellTechApplication.setCurrentArticle(null); // Set to null for new article
        WellTechApplication.loadFXML("articleEdit");
    }
    
    /**
     * Handle navigation to dashboard
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
     * Handle navigation to profile
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