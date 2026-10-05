package com.placement.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.getInstance().init(primaryStage);
        SceneManager.getInstance().switchScene("/fxml/login.fxml", "Login - Placement System");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
