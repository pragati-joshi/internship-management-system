package com.placement.controller;

import com.placement.app.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import com.placement.service.AuthService;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleSelector;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        roleSelector.getItems().addAll("Student", "Mentor", "Administrator");
        roleSelector.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleSelector.getValue();
        
        // Disable button during auth
        loginButton.setDisable(true);
        loginButton.setText("Authenticating...");
        
        AuthService authService = new AuthService();
        authService.login(username, password, role).thenAccept(user -> {
            javafx.application.Platform.runLater(() -> {
                com.placement.app.UserContext.setCurrentUser(user);
                System.out.println("Login success! Token: " + user.getToken());
                SceneManager.getInstance().switchScene("/fxml/main_shell.fxml", "Dashboard - " + user.getRole());
            });
        }).exceptionally(ex -> {
            javafx.application.Platform.runLater(() -> {
                loginButton.setDisable(false);
                loginButton.setText("Login");
                // In real app, show JavaFX Alert here
                System.err.println("Login Error: " + ex.getMessage());
            });
            return null;
        });
    }
}
