package com.example.bankingsystem;

// JavaFX Controller (BankingController.java)

import com.example.bankingsystem.controller.ManualCheckEntryController;
import com.example.bankingsystem.controller.GenerateReportController;
import com.example.bankingsystem.controller.SavedReportsController;
import com.example.bankingsystem.controller.SettingsController;
import com.example.bankingsystem.model.CheckData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class BankingController {

    @FXML private Button newCheckButton;
    @FXML private Button generateReportButton;
    @FXML private Button savedReportsButton;
    @FXML private Button settingsButton;
    @FXML private ScrollPane activityScrollPane;
    @FXML private VBox recentActivityBox;
    @FXML private Button dashboardNavButton;
    @FXML private Button createNavButton;
    @FXML private Button settingsNavButton;

    private String currentUsername = "User";

    @FXML
    public void initialize() {
        System.out.println("BankingController initialized for user: " + currentUsername);
        populateRecentActivity();
        // Set Dashboard nav button as active initially
        setActiveNav(dashboardNavButton);
    }

    // Called by other controllers to set the current user when navigating to the dashboard
    public void setUser(String username) {
        this.currentUsername = username;
        System.out.println("Username set in BankingController: " + currentUsername);
        // Potentially update profile icon or other user-specific elements here
        populateRecentActivity(); // Ensure activity is populated AFTER username is set
    }

    // Action Handlers for Dashboard Buttons
    @FXML
    private void handleNewCheckEntry() {
        System.out.println("New Check Entry clicked");
        try {
            // Load the Manual Check Entry FXML
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("manual_check_entry.fxml"));
            Parent manualCheckRoot = loader.load();

            // Get the controller and pass data
            ManualCheckEntryController controller = loader.getController();
             // Fetches current stage
            Stage stage = (Stage) newCheckButton.getScene().getWindow();
            controller.initializeData(stage, currentUsername); 

            // Create and set the new scene
            Scene manualCheckScene = new Scene(manualCheckRoot, 500, 700);
            manualCheckScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(manualCheckScene);
            stage.setTitle("Farmingdale Checks - Manual Check Entry");
            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Manual Check Entry screen.");
        }
    }

    @FXML
    private void handleGenerateReport() {
        System.out.println("Generate Report clicked");
        try {
            // Load the Generate Report FXML
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("generate_report.fxml"));
            Parent reportRoot = loader.load();

            // Get the controller and pass data
            GenerateReportController controller = loader.getController();
            Stage stage = (Stage) generateReportButton.getScene().getWindow();
            controller.initializeData(stage, currentUsername); 

            // Create and set the new scene
            Scene reportScene = new Scene(reportRoot, 500, 700);
            reportScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(reportScene);
            stage.setTitle("Farmingdale Checks - Generate Report");
            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Generate Report screen.");
        }
    }

    @FXML
    private void handleSavedReports() {
        System.out.println("Saved Reports clicked");
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("saved_reports.fxml"));
            Parent savedReportsRoot = loader.load();

            SavedReportsController controller = loader.getController();
            Stage stage = (Stage) savedReportsButton.getScene().getWindow(); 
            controller.initializeData(stage, currentUsername); 

            Scene savedReportsScene = new Scene(savedReportsRoot, 500, 700);
            savedReportsScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(savedReportsScene);
            stage.setTitle("Farmingdale Checks - Saved Reports");
            stage.setResizable(true); 
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Saved Reports screen.");
        }
    }

    @FXML
    private void handleSettings() {
        System.out.println("Settings clicked");
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("settings.fxml"));
            Parent settingsRoot = loader.load();

            SettingsController controller = loader.getController();
            Stage stage = (Stage) settingsButton.getScene().getWindow(); 
            controller.initializeData(stage, currentUsername); 

            Scene settingsScene = new Scene(settingsRoot, 500, 700);
            settingsScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(settingsScene);
            stage.setTitle("Farmingdale Checks - Settings");
            stage.setResizable(true); 
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Settings screen.");
        }
    }

    // Utils for Bottom Navigation
    @FXML
    private void handleNavDashboard() {
        System.out.println("Dashboard Nav clicked");
        setActiveNav(dashboardNavButton);
        // populateRecentActivity();
    }

    @FXML
    private void handleNavCreate() {
        System.out.println("Create Nav clicked");
        setActiveNav(createNavButton);
        // Navigate to Manual Check Entry screen
        handleNewCheckEntry();
    }

    @FXML
    private void handleNavSettings() {
        System.out.println("Settings Nav clicked");
        setActiveNav(settingsNavButton);
        // Navigate to Settings screen
        handleSettings();
    }

    // Helper Methods

    private void populateRecentActivity() {
        System.out.println("[BankingController] Populating recent activity for user: " + currentUsername);
        recentActivityBox.getChildren().clear();

        try {
            // Get recent checks from the DatabaseManager
            List<CheckData> recentChecks = DatabaseManager.getRecentChecksForUser(currentUsername, 5); // Get latest 5
            System.out.println("[BankingController] Found " + recentChecks.size() + " recent checks from database.");

            if (recentChecks.isEmpty()) {
                recentActivityBox.getChildren().add(new Label("No recent activity."));
                System.out.println("[BankingController] Added 'No recent activity' label.");
            } else {
                for (CheckData check : recentChecks) {
                    Node activityNode = createActivityItem(check);
                    if (activityNode != null) {
                        recentActivityBox.getChildren().add(activityNode);
                        System.out.println("[BankingController] Added activity item node for check: " + check.checkNumber());
                    } else {
                        System.err.println("[BankingController] Failed to create activity item node for check: " + check.checkNumber());
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace(); // Log the exception
            recentActivityBox.getChildren().add(new Label("Error loading recent activity."));
            showAlert("Database Error", "Could not load recent activity: " + e.getMessage());
        }
        System.out.println("[BankingController] Finished populating. recentActivityBox has " + recentActivityBox.getChildren().size() + " children.");
    }

    // Updated to take CheckData object
    private Node createActivityItem(CheckData check) {
        System.out.println("[BankingController] Creating activity item for check: " + check.checkNumber());
        HBox itemBox = new HBox();
        itemBox.setSpacing(10);
        itemBox.getStyleClass().add("activity-item");
        itemBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10, 15, 10, 15));

        VBox textInfo = new VBox();
        // Display Check Number and Payee
        Label checkLabel = new Label("Check #" + check.checkNumber() + " - " + check.payee());
        checkLabel.getStyleClass().add("activity-title");
        // Format date nicely
        Label dateLabel = new Label(check.date().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))); 
        dateLabel.getStyleClass().add("activity-date");
        textInfo.getChildren().addAll(checkLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Display status from CheckData
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-badge");
        String status = check.status();
        if ("Clear".equalsIgnoreCase(status)) {
            statusLabel.setText("✓ Clear");
            statusLabel.getStyleClass().add("status-clear");
        } else if ("Flagged".equalsIgnoreCase(status)) {
            statusLabel.setText("⚠ Flagged");
            statusLabel.getStyleClass().add("status-flagged");
        } else {
            statusLabel.setText("? Pending");
            // TODO: Add a .status-pending style
        }

        itemBox.getChildren().addAll(textInfo, spacer, statusLabel);
        return itemBox;
    }

    private void setActiveNav(Button activeButton) {
        // Reset all nav buttons
        dashboardNavButton.getStyleClass().remove("nav-button-active");
        createNavButton.getStyleClass().remove("nav-button-active");
        settingsNavButton.getStyleClass().remove("nav-button-active");

        // Apply active style to the clicked button
        activeButton.getStyleClass().add("nav-button-active");
    }

    private void showAlert(String title, String content) {
        // Simple alert for placeholder actions
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}