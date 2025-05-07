package com.example.bankingsystem;

import com.example.bankingsystem.screens.LoginScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BankingApplication extends Application {

    private static final int SPLASH_WIDTH = 600;
    private static final int SPLASH_HEIGHT = 400;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    // main entrypoint to the application
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create splash screen layout
        // todo: its buggy on my mac at initial load
        Image image = new Image(BankingApplication.class.getResourceAsStream("farmingdalechecks.png"));
        ImageView logoView = new ImageView(image);
        logoView.setFitWidth(300);
        logoView.setPreserveRatio(true);

        Label loadingLabel = new Label("Farmingdale State College Alumni Portal");
        loadingLabel.setId("splash-loading-label");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(SPLASH_WIDTH - 60);

        VBox root = new VBox(20, logoView,
                new Label("Farmingdale State College Alumni Portal"), progressBar);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(SPLASH_WIDTH, SPLASH_HEIGHT);

        // Create splash scene
        Scene splashScene = new Scene(root);
        splashScene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());

        // Configure splash stage
        Stage splash = new Stage(StageStyle.UNDECORATED);
        splash.setScene(splashScene);
        splash.show();

        // Here we create a task which will initialize the database
        // We do this in a separate thread so that the UI is not blocked
        Task<Void> initTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // We pass in a lambda to update the progress bar as the database is initialized
                DatabaseManager.initializeDatabase((cur, tot) -> updateProgress(cur, tot));
                return null;
            }

            @Override
            protected void succeeded() {
                // Once the database is initialized, we close the splash screen
                // and show the login screen
                Platform.runLater(() -> {
                    splash.close();
                    showLoginScreen();
                });
            }
        };

        // We bind the progress bar to the task's progress property
        // so that the progress bar updates as the database is initialized
        progressBar.progressProperty().bind(initTask.progressProperty());

        // We start the task in a new thread
        new Thread(initTask, "db-init").start();
    }

    // This method shows the login screen
    private void showLoginScreen() {
        try {
            LoginScreen loginScreen = new LoginScreen(primaryStage);
            loginScreen.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
