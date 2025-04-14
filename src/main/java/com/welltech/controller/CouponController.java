package com.welltech.controller;

import com.welltech.WellTechApplication;
import com.welltech.dao.CouponDAO;
import com.welltech.model.Coupon;
import com.welltech.model.User;
import com.welltech.service.RewardService;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the coupon management screen
 */
public class CouponController implements Initializable {
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private TableView<Coupon> couponsTable;
    
    @FXML
    private TableColumn<Coupon, Integer> idColumn;
    
    @FXML
    private TableColumn<Coupon, String> nameColumn;
    
    @FXML
    private TableColumn<Coupon, String> discountColumn;
    
    @FXML
    private TableColumn<Coupon, String> codeColumn;
    
    @FXML
    private TableColumn<Coupon, Boolean> activeColumn;
    
    @FXML
    private TableColumn<Coupon, String> expirationColumn;
    
    @FXML
    private TableColumn<Coupon, Integer> usageColumn;
    
    @FXML
    private TableColumn<Coupon, String> actionColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label formTitle;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField discountField;
    
    @FXML
    private TextField codeField;
    
    @FXML
    private DatePicker expirationDatePicker;
    
    @FXML
    private CheckBox activeCheckbox;
    
    @FXML
    private Button assignCouponButton;
    
    private CouponDAO couponDAO;
    private RewardService rewardService;
    private ObservableList<Coupon> couponsList = FXCollections.observableArrayList();
    private Coupon currentCoupon;
    private Coupon selectedCoupon;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize DAO and service
            couponDAO = new CouponDAO();
            rewardService = new RewardService();
            
            // Get current user
            User currentUser = LoginController.getCurrentUser();
            if (currentUser != null) {
                userNameLabel.setText("Welcome, " + currentUser.getFullName());
            }
            
            // Initialize table columns
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            
            // Format discount column to show percentage
            discountColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDiscountPercentage() + "%"));
            
            codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
            
            // Format active column as checkbox
            activeColumn.setCellValueFactory(cellData -> 
                new SimpleBooleanProperty(cellData.getValue().isActive()));
            activeColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item ? "Yes" : "No");
                    }
                }
            });
            
            // Format expiration date column
            expirationColumn.setCellValueFactory(cellData -> {
                LocalDateTime expirationDate = cellData.getValue().getExpirationDate();
                if (expirationDate != null) {
                    return new SimpleStringProperty(expirationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                return new SimpleStringProperty("Never");
            });
            
            usageColumn.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
            
            // Add action buttons to the table
            actionColumn.setCellFactory(createActionCellFactory());
            
            // Load initial data
            loadCoupons();
            
            // Set up table selection listener for the assign button
            couponsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedCoupon = newSelection;
                assignCouponButton.setDisable(selectedCoupon == null);
            });
            
            // Initially disable the assign button
            assignCouponButton.setDisable(true);
            
            System.out.println("CouponController initialized");
        } catch (Exception e) {
            System.err.println("Error initializing CouponController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load all coupons from the database
     */
    private void loadCoupons() {
        couponsList.clear();
        couponsList.addAll(couponDAO.getAllCoupons());
        couponsTable.setItems(couponsList);
    }
    
    /**
     * Create cell factory for action column
     */
    private Callback<TableColumn<Coupon, String>, TableCell<Coupon, String>> createActionCellFactory() {
        return column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(5, editButton, deleteButton);
            
            {
                // Setup edit button
                editButton.setOnAction(event -> {
                    Coupon coupon = getTableView().getItems().get(getIndex());
                    populateFormForEdit(coupon);
                });
                
                // Setup delete button
                deleteButton.setOnAction(event -> {
                    Coupon coupon = getTableView().getItems().get(getIndex());
                    handleDelete(coupon);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        };
    }
    
    /**
     * Handle search button action
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            List<Coupon> allCoupons = couponDAO.getAllCoupons();
            couponsList.clear();
            
            for (Coupon coupon : allCoupons) {
                if (coupon.getName().toLowerCase().contains(searchText) || 
                    coupon.getCode().toLowerCase().contains(searchText)) {
                    couponsList.add(coupon);
                }
            }
            
            couponsTable.setItems(couponsList);
        } else {
            loadCoupons();
        }
    }
    
    /**
     * Handle show active only button action
     */
    @FXML
    private void handleShowActiveOnly(ActionEvent event) {
        couponsList.clear();
        couponsList.addAll(couponDAO.getActiveCoupons());
        couponsTable.setItems(couponsList);
    }
    
    /**
     * Handle show all button action
     */
    @FXML
    private void handleShowAll(ActionEvent event) {
        loadCoupons();
    }
    
    /**
     * Handle save button action (create or update)
     */
    @FXML
    private void handleSave(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            String discountText = discountField.getText().trim();
            String code = codeField.getText().trim();
            LocalDate expirationDate = expirationDatePicker.getValue();
            boolean isActive = activeCheckbox.isSelected();
            
            // Validate inputs
            if (name.isEmpty() || discountText.isEmpty() || code.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
                return;
            }
            
            // Parse discount percentage
            int discountPercentage;
            try {
                discountPercentage = Integer.parseInt(discountText);
                if (discountPercentage < 0 || discountPercentage > 100) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Discount must be between 0 and 100%.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Discount must be a number.");
                return;
            }
            
            // Convert expiration date to LocalDateTime (end of day)
            LocalDateTime expirationDateTime = null;
            if (expirationDate != null) {
                expirationDateTime = expirationDate.atTime(LocalTime.MAX);
            }
            
            if (currentCoupon == null) {
                // Create new coupon
                int couponId = rewardService.createCoupon(name, discountPercentage, code, expirationDateTime);
                
                if (couponId > 0) {
                    // If successful, update the coupon's active status if needed
                    if (!isActive) {
                        Coupon newCoupon = couponDAO.getCouponById(couponId);
                        newCoupon.setActive(false);
                        couponDAO.updateCoupon(newCoupon);
                    }
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Coupon created successfully!");
                    clearForm();
                    loadCoupons();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create coupon. Code may be duplicate.");
                }
            } else {
                // Update existing coupon
                currentCoupon.setName(name);
                currentCoupon.setDiscountPercentage(discountPercentage);
                currentCoupon.setCode(code);
                currentCoupon.setExpirationDate(expirationDateTime);
                currentCoupon.setActive(isActive);
                
                boolean updated = couponDAO.updateCoupon(currentCoupon);
                if (updated) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Coupon updated successfully!");
                    clearForm();
                    loadCoupons();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update coupon.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle delete button action
     */
    private void handleDelete(Coupon coupon) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Coupon");
        confirmDialog.setContentText("Are you sure you want to delete this coupon: " + coupon.getName() + "?\n\n" +
                                    "This will also delete any objectives associated with this coupon.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = couponDAO.deleteCoupon(coupon.getId());
                if (deleted) {
                    loadCoupons();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Coupon deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete coupon.");
                }
            }
        });
    }
    
    /**
     * Handle clear button action
     */
    @FXML
    private void handleClear(ActionEvent event) {
        clearForm();
    }
    
    /**
     * Handle assign coupon button action
     */
    @FXML
    private void handleAssignCoupon(ActionEvent event) {
        if (selectedCoupon == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a coupon from the table first.");
            return;
        }
        
        // Store the selected coupon ID in a field that can be accessed by the objectives controller
        try {
            // Navigate to objectives page to create a new objective with this coupon
            WellTechApplication.loadFXML("objectives");
            
            // This part requires coordination with the ObjectivesController
            // We need to pass the selected coupon ID to the ObjectivesController somehow
            // One approach could be using a static field/method in the ObjectivesController
            // Another approach could be using a shared service or data store
            
            // For now, we'll just show a message to the user
            showAlert(Alert.AlertType.INFORMATION, "Navigation", 
                     "Now create a new objective and select this coupon: " + selectedCoupon.getName());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Populate form for editing an existing coupon
     */
    private void populateFormForEdit(Coupon coupon) {
        currentCoupon = coupon;
        formTitle.setText("Edit Coupon");
        nameField.setText(coupon.getName());
        discountField.setText(String.valueOf(coupon.getDiscountPercentage()));
        codeField.setText(coupon.getCode());
        
        // Set expiration date if available
        if (coupon.getExpirationDate() != null) {
            expirationDatePicker.setValue(coupon.getExpirationDate().toLocalDate());
        } else {
            expirationDatePicker.setValue(null);
        }
        
        activeCheckbox.setSelected(coupon.isActive());
    }
    
    /**
     * Clear form and reset to "Add New Coupon" mode
     */
    private void clearForm() {
        currentCoupon = null;
        formTitle.setText("Add New Coupon");
        nameField.clear();
        discountField.clear();
        codeField.clear();
        expirationDatePicker.setValue(null);
        activeCheckbox.setSelected(true);
    }
    
    /**
     * Show an alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Navigate to dashboard based on user role
     */
    @FXML
    private void navigateToDashboard(ActionEvent event) {
        User currentUser = LoginController.getCurrentUser();
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
            }
        }
    }
    
    /**
     * Navigate to objectives page
     */
    @FXML
    private void navigateToObjectives(ActionEvent event) {
        WellTechApplication.loadFXML("objectives");
    }
    
    /**
     * Navigate to profile page
     */
    @FXML
    private void navigateToProfile(ActionEvent event) {
        WellTechApplication.loadFXML("profile");
    }
    
    /**
     * Navigate to articles page
     */
    @FXML
    private void navigateToArticles(ActionEvent event) {
        WellTechApplication.loadFXML("articlesList");
    }
    
    /**
     * Handle logout button click
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        LoginController.logout();
    }
} 