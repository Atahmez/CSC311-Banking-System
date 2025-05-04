package com.example.bankingsystem;

// JavaFX Controller (BankingController.java)

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankingController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea transactionLog;

    private double balance = 1000.0; // Initial balance
    private final Random random = new Random();

    @FXML
    public void handleDeposit() {
        String accountNumber = accountNumberField.getText();
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            showAlert("Missing Info", "Please enter your account number.");
            return;
        }

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
        String accountNumber = accountNumberField.getText();
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            showAlert("Missing Info", "Please enter your account number.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                logTransaction("Withdrawal: -" + amount);
                fraudDetection(amount, "Withdrawal");
            } else if (amount <= 0) {
                showAlert("Invalid Amount", "Please enter a positive amount.");
            } else {
                showAlert("Insufficient Funds", "You do not have enough funds.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
        }
    }

    @FXML
    public void handleClear() {
        accountNumberField.clear();
        amountField.clear();
        transactionLog.clear();
    }

    private void logTransaction(String transaction) {
        String account = accountNumberField.getText();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Transaction by Account: " + account + " at " + time + " - " + transaction);
        transactionLog.appendText(transaction + " at " + time + "\n");
        transactionLog.appendText("Current Balance: " + balance + "\n");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void fraudDetection(double amount, String transactionType) {
        if (amount > 5000) {
            showAlert("Fraud Alert", "Large transaction detected: " + amount + " " + transactionType + ". Please verify.");
        }

        if (random.nextDouble() < 0.01) {
            showAlert("Fraud Alert", "Unusual activity detected. Contact support.");
        }
    }

    public void setAccountNumber(String username) {
        // Currently unused. Can be used later for dynamic account binding.
    }
}
