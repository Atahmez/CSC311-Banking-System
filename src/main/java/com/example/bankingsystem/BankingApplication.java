package com.example.bankingsystem;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class BankingApplication extends Application {

    private static Stage primaryStage;

    private static final String RES_FOLDER   = "/bankingsystem/";
    private static final String SPLASH_FXML  = RES_FOLDER + "splash.fxml";
    private static final String WELCOME_FXML = RES_FOLDER + "welcome.fxml";
    private static final double SPLASH_TIME  = 2.5; // seconds

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // 1) Show the splash screen in its own undecorated Stage
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        Parent splashRoot = FXMLLoader.load(
                getClass().getResource(SPLASH_FXML)
        );
        splashStage.setScene(new Scene(splashRoot));
        splashStage.show();

        // 2) After a delay, close splash and show welcome
        PauseTransition pause = new PauseTransition(Duration.seconds(SPLASH_TIME));
        pause.setOnFinished(e -> {
            splashStage.close();
            showWelcomeScreen();
        });
        pause.play();
    }

    private void showWelcomeScreen() {
        try {
            Parent welcomeRoot = FXMLLoader.load(
                    getClass().getResource(WELCOME_FXML)
            );
            Scene welcomeScene = new Scene(welcomeRoot, 600, 400);

            primaryStage.setTitle("Banking System – Welcome");
            primaryStage.setScene(welcomeScene);
            primaryStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Utility for swapping any other FXML-based screen in the same folder.
     *
     * @param fxmlFileName the FXML file name (e.g. "login-view.fxml")
     * @param windowTitle  the new window title
     * @param width        scene width
     * @param height       scene height
     */
    public static void switchScene(String fxmlFileName,
                                   String windowTitle,
                                   int width, int height) {
        try {
            Parent root = FXMLLoader.load(
                    BankingApplication.class
                            .getResource(RES_FOLDER + fxmlFileName)
            );
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Banking System – " + windowTitle);
            primaryStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
