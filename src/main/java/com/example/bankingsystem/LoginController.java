package com.example.bankingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in both username and password.");
            return;
        }

        // Dummy check: accept only admin/admin123
        if ("admin".equals(username) && "admin123".equals(password)) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/bankingsystem/hello-view.fxml")
                );
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
                stage.show();
            } catch (Exception e) {
                messageLabel.setText("Failed to load dashboard.");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Invalid login credentials.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/bankingsystem/welcome.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Banking System — Welcome");
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Failed to return to welcome screen.");
            e.printStackTrace();
        }
    }
}
