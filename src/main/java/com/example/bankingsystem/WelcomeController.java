package com.example.bankingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private Button getStartedButton;

    @FXML
    private void initialize() {
        // Optional: set up anything when the screen loads
        getStartedButton.setOnAction(event -> openMainMenu());
    }

    private void openMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankingsystem/hello-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) getStartedButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Main Menu");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
