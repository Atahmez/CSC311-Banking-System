package com.example.bankingsystem.controller;

import com.example.bankingsystem.BankingController;
import com.example.bankingsystem.HelloApplication;
import com.example.bankingsystem.screens.LoginScreen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import com.example.bankingsystem.DatabaseManager;
import java.io.IOException;
import java.util.Optional;
import java.sql.SQLException;

public class SettingsController {

    @FXML private Button logoutButton;
    @FXML private Button deleteAccountButton;

    private Stage stage;
    private String currentUsername;

    public void initializeData(Stage stage, String username) {
        this.stage = stage;
        this.currentUsername = username;
        System.out.println("SettingsController initialized for user: " + currentUsername);
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logout clicked by " + currentUsername);
        // Simple logout: just navigate back to the Login screen
        navigateToLoginScreen();
    }

    @FXML
    private void handleDeleteAccount() {
        System.out.println("Delete Account clicked by " + currentUsername);
        
        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Account");
        confirmation.setHeaderText("Are you sure you want to delete your account?");
        confirmation.setContentText("This action is permanent and will delete all your check data.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("User confirmed account deletion.");
            try {
                boolean deleted = DatabaseManager.deleteUser(currentUsername);
                if(deleted) {
                    showAlert("Account Deleted", "Your account and data have been deleted.", Alert.AlertType.INFORMATION);
                    navigateToLoginScreen(); // Go to login after deletion
                } else {
                     // This case might indicate the user was already deleted or didn't exist.
                     System.err.println("Deletion failed for " + currentUsername + ", user might not exist.");
                     showAlert("Deletion Failed", "Could not delete the account. User might not exist.", Alert.AlertType.WARNING);
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while deleting the account: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("User cancelled account deletion.");
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Back to dashboard clicked from settings");
        navigateToDashboard();
    }

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
    
    private void navigateToLoginScreen() {
        try {
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
            // TODO: Stage title and properties will be set within loginScreen.show()
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to the login screen.", Alert.AlertType.ERROR);
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