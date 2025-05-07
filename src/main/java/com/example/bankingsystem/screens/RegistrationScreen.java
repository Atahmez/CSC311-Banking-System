package com.example.bankingsystem.screens;

import com.example.bankingsystem.BankingApplication;
import com.example.bankingsystem.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.bankingsystem.DatabaseManager;
import java.sql.SQLException;

public class RegistrationScreen {

    private Stage stage;
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;

    public RegistrationScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Main container
        VBox root = new VBox(15);
        root.setPadding(new Insets(40, 50, 40, 50));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("registration-container");

        // Logo Image (replaces title label)
        Image image = new Image(BankingApplication.class.getResourceAsStream("register2.png"));
        ImageView logoView = new ImageView(image);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(250);
        VBox.setMargin(logoView, new Insets(0, 0, 20, 0));

        // Name Field
        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("text-field");

        // Email Field
        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");

        // Password Field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field");

        // Sign Up Button
        Button signUpButton = new Button("SIGN UP");
        signUpButton.getStyleClass().add("primary-button");
        signUpButton.setOnAction(e -> handleRegister(emailField.getText(), passwordField.getText()));
        signUpButton.setMaxWidth(Double.MAX_VALUE);

        // Log in Link (replaces back button)
        Hyperlink loginLink = new Hyperlink("Have an account? Log in");
        loginLink.getStyleClass().add("link-button");
        loginLink.setOnAction(e -> handleBackToLogin());
        VBox.setMargin(loginLink, new Insets(10, 0, 0, 0));

        // Add components to root
        root.getChildren().addAll(logoView, nameField, emailField, passwordField, signUpButton, loginLink);

        // Create scene - Use consistent dimensions
        Scene scene = new Scene(root, 500, 550);
        scene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());

        // Configure stage
        stage.setTitle("Farmingdale Checks - Register");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleBackToLogin() {
        System.out.println("Log in link clicked");
        // Reuse the LoginScreen instance logic
        LoginScreen loginScreen = new LoginScreen(stage);
        loginScreen.show();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleRegister(String email, String password) {
        System.out.println("Register button clicked (Using Mock Service)");
        System.out.println("Email: " + email + ", Password: " + password);

        // Basic Validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Registration Failed", "Please fill in all fields.", Alert.AlertType.WARNING);
            return;
        }

        // Simple email format validation (can be more robust)
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Registration Failed", "Please enter a valid email address.", Alert.AlertType.WARNING);
            return;
        }

        // TODO: Add password complexity checks if desired

        // --- Call Mock Service for Registration ---
        // User registeredUser = MockCheckService.registerUser(email, password);

        try {
            User registeredUser = DatabaseManager.registerUser(email, password);

            if (registeredUser != null) {
                System.out.println("Registration successful for: " + registeredUser.username());
                showAlert("Registration Successful", "Account created successfully! You can now log in.", Alert.AlertType.INFORMATION);
                // Navigate back to Login Screen after successful registration
                showLoginScreen();
            } else {
                // Registration failed (likely username already exists in the mock service)
                System.out.println("Registration failed for: " + email + " (username might already exist).");
                showAlert("Registration Failed", "This email address is already registered.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error during registration for user: " + email + " - " + e.getMessage());
            showAlert("Registration Error", "A database error occurred during registration. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    // Navigate back to Login Screen
    private void showLoginScreen() {
        try {
            // Assuming LoginScreen has a constructor that takes the Stage
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to return to the login screen.", Alert.AlertType.ERROR);
        }
    }
} 