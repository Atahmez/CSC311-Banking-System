package com.example.bankingsystem.controller;

import com.example.bankingsystem.BankingController;
import com.example.bankingsystem.HelloApplication;
import com.example.bankingsystem.model.CheckData;
import com.example.bankingsystem.model.SavedReportData;
import com.example.bankingsystem.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class GenerateReportController {

    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private Button generateButton;
    @FXML private CheckBox saveReportCheckBox;

    private Stage stage;
    private String currentUsername;

    // Called by BankingController to pass stage and user info
    public void initializeData(Stage stage, String username) {
        this.stage = stage;
        this.currentUsername = username;
        System.out.println("GenerateReportController initialized for user: " + currentUsername);
        populateComboBoxes();
    }

    @FXML
    public void initialize() {
        // Initialization that doesn't depend on passed data can go here
        // TODO: populate ComboBoxes after initializeData
        // depending on options
    }

    private void populateComboBoxes() {
        // Populate Report Type ComboBox
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
                "All Checks Log",       // Simple list of all checks entered
                "Checks by Payee",      // Grouped or filtered by payee
                "Checks by Date Range", // Already handled by date pickers, but maybe a specific format
                "Amount Summary",       // Total amount, count, average etc.
                "Flagged Checks Only"   // Filtered list of flagged checks
        ));
        reportTypeComboBox.setPromptText("Select report type"); // Re-set prompt text if needed

        // Populate Status Filter ComboBox
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All Statuses",
                "Clear",
                "Flagged" // Reflecting the statuses used in populateRecentActivity
                // Add "Pending" here if it becomes a distinct status in the database
        ));
        statusFilterComboBox.setPromptText("All Statuses"); // Re-set prompt text
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = reportTypeComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String statusFilter = statusFilterComboBox.getValue();
        boolean saveReport = saveReportCheckBox.isSelected();

        System.out.println("Generate Report clicked by " + currentUsername);
        System.out.println("Options: Type=" + reportType + ", Start=" + startDate + ", End=" + endDate + ", Status=" + statusFilter);

        // --- Basic Validation --- 
        if (reportType == null || startDate == null || endDate == null) {
            showAlert("Missing Information", "Please select Report Type, Start Date, and End Date.", Alert.AlertType.WARNING);
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert("Invalid Date Range", "End Date cannot be before Start Date.", Alert.AlertType.WARNING);
            return;
        }

        // Call service to get check data
        List<CheckData> reportData = null; // Initialize to null
        try {
            reportData = DatabaseManager.getChecksForReportCriteria(
                currentUsername, startDate, endDate, statusFilter
            );
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not generate report data: " + e.getMessage(), Alert.AlertType.ERROR);
            return; // Stop further processing if data fetching fails
        }
        
        System.out.println("[GenerateReportController] Report data size from service: " + (reportData != null ? reportData.size() : 0));

        if (saveReport) {
            try {
                DatabaseManager.saveReportMetadata(reportType, startDate, endDate, statusFilter, currentUsername);
                System.out.println("Report metadata saved.");
                // showAlert("Report Saved", "Report criteria saved.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                 System.err.println("Error saving report metadata: " + e.getMessage());
                 // TODO: Show error
            }
        }

        // Route to report-view screen
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("report_view.fxml"));
            Parent reportViewRoot = loader.load();

            ReportViewController controller = loader.getController();
            // Pass all necessary data to the report view controller
            controller.initializeData(stage, currentUsername, reportType, startDate, endDate, statusFilter, reportData);

            Scene reportViewScene = new Scene(reportViewRoot, 500, 700); // Adjust size if needed
            reportViewScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());

            stage.setScene(reportViewScene);
            stage.setTitle("Farmingdale Checks - Report: " + reportType);
            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the report view screen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel report generation clicked");
        navigateToDashboard();
    }

    // Navigate back to the main dashboard (same as in ManualCheckEntryController)
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("banking.fxml"));
            Parent bankingRoot = loader.load();

            BankingController controller = loader.getController();
            controller.setUser(currentUsername);

            Scene bankingScene = new Scene(bankingRoot, 500, 700);
            bankingScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());

            stage.setScene(bankingScene);
            stage.setTitle("Farmingdale Checks - Account: " + currentUsername);
            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the banking dashboard.", Alert.AlertType.ERROR);
        }
    }

    // Helper method for alerts
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 