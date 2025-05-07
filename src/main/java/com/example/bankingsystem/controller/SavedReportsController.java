package com.example.bankingsystem.controller;

import com.example.bankingsystem.BankingController;
import com.example.bankingsystem.BankingApplication;
import com.example.bankingsystem.model.SavedReportData;
import com.example.bankingsystem.model.CheckData;
import com.example.bankingsystem.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SavedReportsController {

    @FXML private ListView<SavedReportData> savedReportsListView;
    @FXML private Button viewReportButton;
    @FXML private Button deleteReportButton;

    private Stage stage;
    private String currentUsername;
    private ObservableList<SavedReportData> savedReportsList = FXCollections.observableArrayList();

    public void initializeData(Stage stage, String username) {
        this.stage = stage;
        this.currentUsername = username;
        System.out.println("SavedReportsController initialized for user: " + currentUsername);
        loadSavedReports();
    }

    @FXML
    public void initialize() {
        // Bind the ListView to the observable list
        savedReportsListView.setItems(savedReportsList);

        // Enable/disable buttons based on selection
        savedReportsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean reportSelected = newSelection != null;
            viewReportButton.setDisable(!reportSelected);
            deleteReportButton.setDisable(!reportSelected);
        });

        // Add double-click listener to view report
        savedReportsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && savedReportsListView.getSelectionModel().getSelectedItem() != null) {
                handleViewReport();
            }
        });
    }

    private void loadSavedReports() {
        try {
            List<SavedReportData> reports = DatabaseManager.getSavedReportsForUser(currentUsername);
            savedReportsList.setAll(reports);
            if (reports.isEmpty()) {
                savedReportsListView.setPlaceholder(new Label("No reports saved yet."));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            savedReportsListView.setPlaceholder(new Label("Error loading saved reports."));
            showAlert("Database Error", "Could not load saved reports: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleViewReport() {
        SavedReportData selectedReportMeta = savedReportsListView.getSelectionModel().getSelectedItem();
        if (selectedReportMeta != null) {
            System.out.println("View Report clicked for saved report: " + selectedReportMeta.reportId());

            try {
                // 1. Fetch the actual report data using the saved metadata
                List<CheckData> reportData = DatabaseManager.getChecksForReportCriteria(
                    selectedReportMeta.generatedByUsername(),
                    selectedReportMeta.startDate(), 
                    selectedReportMeta.endDate(), 
                    selectedReportMeta.statusFilter()
                );
                System.out.println("[SavedReportsController] Fetched " + reportData.size() + " checks for saved report view.");

                // 2. Navigate to the report view screen
                FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("report_view.fxml"));
                Parent reportViewRoot = loader.load();

                ReportViewController controller = loader.getController();
                // Pass data to the report view controller
                controller.initializeData(stage, 
                                        selectedReportMeta.generatedByUsername(), 
                                        selectedReportMeta.reportType(), 
                                        selectedReportMeta.startDate(), 
                                        selectedReportMeta.endDate(), 
                                        selectedReportMeta.statusFilter(), 
                                        reportData);

                Scene reportViewScene = new Scene(reportViewRoot, 500, 700);
                reportViewScene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());

                stage.setScene(reportViewScene);
                stage.setTitle("Farmingdale Checks - Report: " + selectedReportMeta.reportType());
                stage.setResizable(true);
                stage.toFront();
                stage.requestFocus();

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Could not load check data for the report: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the report view screen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            
            // Old placeholder:
            // showAlert("View Report", "Viewing report ID " + selectedReport.reportId() + " is not implemented yet.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleDeleteReport() {
        SavedReportData selectedReport = savedReportsListView.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            System.out.println("Delete Report clicked for: " + selectedReport.reportId());

            // Confirmation dialog
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Report");
            confirmation.setHeaderText("Are you sure you want to delete this report?");
            confirmation.setContentText(selectedReport.toString());

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = DatabaseManager.deleteSavedReport(selectedReport.reportId(), currentUsername);
                    if (deleted) {
                        savedReportsList.remove(selectedReport);
                        showAlert("Report Deleted", "The selected report was deleted.", Alert.AlertType.INFORMATION);
                    } else {
                        // This case might mean the report didn't exist or didn't belong to the user, 
                        // though the SQL query is designed to prevent accidental deletion of other users' reports.
                        showAlert("Delete Failed", "Could not find the report to delete, or it was already deleted.", Alert.AlertType.WARNING);
                    }
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    showAlert("Database Error", "Failed to delete the report: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Back to dashboard clicked");
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("banking.fxml"));
            Parent bankingRoot = loader.load();
            BankingController controller = loader.getController();
            controller.setUser(currentUsername);
            Scene bankingScene = new Scene(bankingRoot, 500, 700);
            bankingScene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());
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

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 