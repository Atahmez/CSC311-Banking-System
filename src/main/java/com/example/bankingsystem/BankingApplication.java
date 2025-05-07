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

        // Create task to simulate loading bar and transition to login screen
        Task<Void> initTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                DatabaseManager.initializeDatabase((cur, tot) -> updateProgress(cur, tot));
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    splash.close();
                    showLoginScreen();
                });
            }
        };

        progressBar.progressProperty().bind(initTask.progressProperty());
        new Thread(initTask, "db-init").start();
    }

    private void showLoginScreen() {
        try {
            LoginScreen loginScreen = new LoginScreen(primaryStage);
            loginScreen.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
