package com.example.bankingsystem.screens;

import com.example.bankingsystem.BankingApplication;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LandingScreen {

    private Stage stage;

    public LandingScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Create root layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-container");

        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        root.setTop(menuBar);

        // Create tabular data view
        TableView<DataItem> tableView = createTableView();

        // Create tab pane with tables
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab dataTab1 = new Tab("DataTable");
        dataTab1.setContent(tableView);

        Tab dataTab2 = new Tab("ReportsTable");
        TableView<DataItem> table2 = createTableView();
        dataTab2.setContent(table2);

        tabPane.getTabs().addAll(dataTab1, dataTab2);
        root.setCenter(tabPane);

        // Create status bar
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.getStyleClass().add("status-bar");

        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        root.setBottom(statusBar);

        // Create scene
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(BankingApplication.class.getResource("styles.css").toExternalForm());

        // Configure stage
        stage.setTitle("Farmingdale Checks - Portal");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private TableView<DataItem> createTableView() {
        TableView<DataItem> tableView = new TableView<>();
        tableView.getStyleClass().add("data-table");

        // Create columns
        TableColumn<DataItem, String> idColumn = new TableColumn<>("ID");
        TableColumn<DataItem, String> nameColumn = new TableColumn<>("Name");
        TableColumn<DataItem, String> dateColumn = new TableColumn<>("Date");
        TableColumn<DataItem, String> amountColumn = new TableColumn<>("Amount");

        tableView.getColumns().addAll(java.util.Arrays.asList(idColumn, nameColumn, dateColumn, amountColumn));

        // Sample data 

        return tableView;
    }

    // Data model for table
    public static class DataItem {
        private String id;
        private String name;
        private String date;
        private String amount;

        public DataItem(String id, String name, String date, String amount) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.amount = amount;
        }

        // Getters/ setters would be here
    }
}
