package com.example.bankingsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private StackPane rootPane;  // must match fx:id in splash.fxml

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // after 3 seconds, load welcome.fxml
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(evt -> {
            try {
                // loads welcome.fxml from the same package
                Parent welcome = FXMLLoader.load(
                        getClass().getResource("welcome.fxml")
                );
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(welcome));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        delay.play();
    }
}
