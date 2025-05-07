package com.example.bankingsystem.controller;

import com.example.bankingsystem.BankingController;
import com.example.bankingsystem.HelloApplication;
import com.example.bankingsystem.model.CheckData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class ReportViewController {

    @FXML private Label reportTitleLabel;
    @FXML private TableView<CheckData> reportTableView;
    @FXML private TableColumn<CheckData, String> checkNumberCol;
    @FXML private TableColumn<CheckData, LocalDate> dateCol;
    @FXML private TableColumn<CheckData, String> payeeCol;
    @FXML private TableColumn<CheckData, Double> amountCol;
    @FXML private TableColumn<CheckData, String> statusCol;
    @FXML private Label summaryLabel;

    private Stage stage;
    private String currentUsername;
    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String statusFilter;

    // Called by GenerateReportController to pass data
    public void initializeData(Stage stage, String username, String reportType, 
                               LocalDate startDate, LocalDate endDate, String statusFilter, 
                               List<CheckData> reportData) {
        this.stage = stage;
        this.currentUsername = username;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.statusFilter = statusFilter;

        System.out.println("[ReportViewController] Received report data size: " + reportData.size());

        System.out.println("ReportViewController initialized for report: " + reportType);
        
        reportTitleLabel.setText(reportType); // Set dynamic title
        setupTableColumns();
        populateTable(reportData);
        
        // Display summary info
        summaryLabel.setText(String.format("Checks found: %d", reportData.size()));
    }

    private void setupTableColumns() {
        // Associate columns with CheckData properties using lambda expressions
        checkNumberCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().checkNumber()));
            
        dateCol.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().date()));
            
        payeeCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().payee()));
            
        amountCol.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().amount()));
            
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().status()));

        // Format Date column
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        dateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(item));
                }
            }
        });
        
        amountCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });
    }

    private void populateTable(List<CheckData> reportData) {
        ObservableList<CheckData> data = FXCollections.observableArrayList(reportData);
        reportTableView.setItems(data);
    }

    @FXML
    private void handleBackToGenerator() {
        // Navigate back to the report generation screen
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("generate_report.fxml"));
            Parent reportGenRoot = loader.load();

            GenerateReportController controller = loader.getController();
            controller.initializeData(stage, currentUsername);

            Scene reportGenScene = new Scene(reportGenRoot, 500, 700);
            reportGenScene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());

            stage.setScene(reportGenScene);
            stage.setTitle("Farmingdale Checks - Generate Report"); 
            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the report generator screen.", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleBackToDashboard() {
       System.out.println("Back to dashboard clicked from report view");
       // Use the navigateToDashboard logic from other controllers
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
    

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 