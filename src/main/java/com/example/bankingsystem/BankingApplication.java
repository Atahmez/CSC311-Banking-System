package com.example.bankingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class BankingApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Load the welcome screen
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/bankingsystem/welcome.fxml")
        );

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Banking System — Welcome");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Utility method for switching to any other FXML-based screen.
     * @param fxmlFileName filename (without .fxml) under resources/com/example/bankingsystem/
     * @param windowTitle  the new window title
     * @param width        scene width
     * @param height       scene height
     */
    public static void switchScene(String fxmlFileName, String windowTitle, int width, int height) {
        try {
            Parent pane = FXMLLoader.load(
                    BankingApplication.class.getResource(
                            "/com/example/bankingsystem/" + fxmlFileName + ".fxml"
                    )
            );
            primaryStage.setScene(new Scene(pane, width, height));
            primaryStage.setTitle("Banking System — " + windowTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
