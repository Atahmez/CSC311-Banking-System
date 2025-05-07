package com.example.bankingsystem.controller;

import com.example.bankingsystem.BankingController;
import com.example.bankingsystem.BankingApplication;
import com.example.bankingsystem.model.CheckData;
import com.example.bankingsystem.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ManualCheckEntryController {

    @FXML private TextField checkNumberField;
    @FXML private TextField payeeField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextField memoField;
    @FXML private Button submitButton;

    private Stage stage;
    private String currentUsername;

    // Called by BankingController to pass stage and user info
    public void initializeData(Stage stage, String username) {
        this.stage = stage;
        this.currentUsername = username;
        System.out.println("ManualCheckEntryController initialized for user: " + currentUsername);
    }

    @FXML
    private void handleSubmitCheck() {
        String checkNumber = checkNumberField.getText();
        String payee = payeeField.getText();
        String amountStr = amountField.getText();
        LocalDate date = datePicker.getValue();
        String memo = memoField.getText();

        System.out.println("Submit Check clicked by " + currentUsername);
        System.out.println("Data: Check#=" + checkNumber + ", Payee=" + payee + ", Amount=" + amountStr + ", Date=" + date + ", Memo=" + memo);

        // --- Basic Validation --- 
        if (checkNumber.isEmpty() || payee.isEmpty() || amountStr.isEmpty() || date == null) {
            showAlert("Missing Information", "Please fill in Check Number, Payee, Amount, and Date.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Validate and parse amount
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert("Invalid Amount", "Amount must be positive.", Alert.AlertType.WARNING);
                return;
            }

            // Create CheckData object
            String initialStatus = "Pending"; // Default status for new checks
            LocalDateTime timestamp = LocalDateTime.now();
            CheckData newCheck = new CheckData(
                checkNumber, payee, amount, date, memo, initialStatus, currentUsername, timestamp
            );

            // Save to database
            DatabaseManager.saveCheck(newCheck);

            // Provide feedback based on the result from the service
            // String resultMessage = "Check #" + submittedCheck.checkNumber() + 
            //                        " submitted. Status: " + submittedCheck.status();
            // showAlert("Check Submitted", resultMessage, Alert.AlertType.INFORMATION);
            showAlert("Check Saved", "Check #" + newCheck.checkNumber() + " has been saved.", Alert.AlertType.INFORMATION);
            
            navigateToDashboard(); // Navigate back after successful submission

        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid number for the amount.", Alert.AlertType.WARNING);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel check entry clicked");
        navigateToDashboard();
    }

    // Navigate back to the main dashboard
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("banking.fxml"));
            Parent bankingRoot = loader.load();

            BankingController controller = loader.getController();
             // Pass username back
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

    // Helper method for alerts
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 