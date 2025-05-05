package com.example.bankingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        // Simple dummy check: pretend "admin" is already taken
        if (username.equalsIgnoreCase("admin")) {
            messageLabel.setText("Username already taken.");
        } else {
            messageLabel.setText("User registered successfully!");
            System.out.println("Registered: " + username + " / " + password);
        }
    }
}
