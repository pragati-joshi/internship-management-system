package com.placement.controller;

import com.placement.app.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;

public class MainShellController {
    @FXML private Label userRoleLabel;
    @FXML private StackPane contentArea;

    public void initialize() {
        com.placement.model.User user = com.placement.app.UserContext.getCurrentUser();
        if (user != null) {
            userRoleLabel.setText("Role: " + user.getRole());
            
            String fxmlPath = switch (user.getRole().toLowerCase()) {
                case "mentor" -> "/fxml/mentor_dashboard.fxml";
                case "administrator" -> "/fxml/admin_dashboard.fxml";
                default -> "/fxml/student_dashboard.fxml";
            };
            loadDashboard(fxmlPath);
        } else {
            loadDashboard("/fxml/student_dashboard.fxml");
        }
    }

    private void loadDashboard(String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Node node = loader.load();
            contentArea.getChildren().setAll(node);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.getInstance().switchScene("/fxml/login.fxml", "Login - Placement System");
    }
}
