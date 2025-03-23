package com.example.bankingsystem;

// JavaFX Controller (BankingController.java)

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Random;

class BankingController {

    @FXML
    private TextField accountNumberField;
    @FXML
    private TextField amountField;
    @FXML
    private TextArea transactionLog;

    private double balance = 1000.0; // Initial balance
    private Random random = new Random();

    @FXML
    public void handleDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0) {
                balance += amount;
                logTransaction("Deposit: +" + amount);
                fraudDetection(amount, "Deposit");
            } else {
                showAlert("Invalid Amount", "Please enter a positive amount.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
        }
    }

    @FXML
    public void handleWithdrawal() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                logTransaction("Withdrawal: -" + amount);
                fraudDetection(amount, "Withdrawal");
            } else if (amount <= 0){
                showAlert("Invalid Amount", "Please enter a positive amount.");
            } else {
                showAlert("Insufficient Funds", "You do not have enough funds.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
        }
    }

    private void logTransaction(String transaction) {
        transactionLog.appendText(transaction + "\n");
        transactionLog.appendText("Current Balance: " + balance + "\n");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void fraudDetection(double amount, String transactionType) {
        // Simple fraud detection logic (can be improved significantly)
        if (amount > 5000) { // Example: Large transaction
            showAlert("Fraud Alert", "Large transaction detected: " + amount + " " + transactionType + ". Please verify.");
        }

        if (random.nextDouble() < 0.01) { // 1% chance of a random fraud flag
            showAlert("Fraud Alert", "Unusual activity detected. Contact support.");
        }

        // Example: Check for unusual patterns (e.g., rapid multiple small withdrawals)
        // This would require more sophisticated tracking of previous transactions.
    }

    public void setAccountNumber(String username) {
    }
}