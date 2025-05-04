package com.example.bankingsystem;

// LoginController.java (JavaFX Controller for Login)

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    private DatabaseMetaData DatabaseManager;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {
            try {
                // Load the banking system view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("banking.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow(); // Get current stage
                stage.setScene(new Scene(root, 600, 400));
                stage.show();

                // Pass the username or account info to the banking controller if needed.
                BankingController bankingController = loader.getController();
                bankingController.setAccountNumber(username); // Example: Using username as account number.

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load banking system.");
            }

        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) { // In a real system you should hash the password.
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if a matching user is found
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred during login.");
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}