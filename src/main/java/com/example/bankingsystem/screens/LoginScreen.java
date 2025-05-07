package com.example.bankingsystem.screens;

import com.example.bankingsystem.BankingController;
import com.example.bankingsystem.BankingApplication;
import com.example.bankingsystem.model.User;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.example.bankingsystem.DatabaseManager;

import java.io.IOException;
import java.sql.SQLException;

public class LoginScreen {

    private Stage stage;

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Create main container
        VBox root = new VBox(15); // Adjusted spacing
        root.setPadding(new Insets(40, 50, 40, 50)); // Adjusted padding
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("login-container");

        // Logo (headtext.png)
        Image image = new Image(BankingApplication.class.getResourceAsStream("register2.png"));
        ImageView logoView = new ImageView(image);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(250);
        VBox.setMargin(logoView, new Insets(0, 0, 20, 0));

        // Email field (using TextField for username)
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("login-field");

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("login-field");

        // Log In Button
        Button loginButton = new Button("Log In");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));
        loginButton.setMaxWidth(Double.MAX_VALUE);

        // Register Link (Replaces Forgot Password)
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
        registerLink.getStyleClass().add("link-button");
        registerLink.setOnAction(e -> handleRegister());
        VBox.setMargin(registerLink, new Insets(10, 0, 0, 0));

        // Add components to root
        root.getChildren().addAll(logoView, emailField, passwordField, loginButton, registerLink);

        // Create scene - Use consistent dimensions
        Scene scene = new Scene(root, 500, 550);
        scene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());

        // Configures stage
        stage.setTitle("Farmingdale Checks - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleLogin(String email, String password) { // Renamed username to email for clarity
        // Mock authentication
        System.out.println("Attempting login for user: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "Please enter both Email and Password.", Alert.AlertType.WARNING);
            return;
        }

        // Ensure the user is authenticated
        try {
            User authenticatedUser = DatabaseManager.authenticateUser(email, password);

            if (authenticatedUser != null) {
                System.out.println("Login successful for: " + authenticatedUser.username());
                showBankingScreen(authenticatedUser.username()); // Pass authenticated username
            } else {
                System.out.println("Login failed for: " + email);
                showAlert("Login Failed", "Invalid email or password.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error during login for user: " + email + " - " + e.getMessage());
            showAlert("Login Error", "A database error occurred. Please try again later.", Alert.AlertType.ERROR);
        }
        // TODO: Remove this once DB is implemented
         // Pass email to next screen
    }

    private void showBankingScreen(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("banking.fxml"));
            BorderPane bankingRoot = loader.load();

            BankingController controller = loader.getController();
            controller.setUser(username);

            // Create scene with explicit dimensions
            Scene bankingScene = new Scene(bankingRoot, 500, 700);
            bankingScene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());

            stage.setScene(bankingScene);
            stage.setTitle("Farmingdale Checks - Account: " + username);
            stage.setResizable(true);
            // stage.setMaximized(false);

            // bring stage to front and request focus
            stage.toFront();
            stage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the banking application screen.", Alert.AlertType.ERROR);
        }
    }

    private void handleRegister() {
        System.out.println("Register link clicked");
        // Create and show the RegistrationScreen
        RegistrationScreen registrationScreen = new RegistrationScreen(stage);
        registrationScreen.show();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}