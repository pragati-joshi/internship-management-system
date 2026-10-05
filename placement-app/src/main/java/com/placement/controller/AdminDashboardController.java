package com.placement.controller;

import com.placement.model.User;
import com.placement.service.AdminService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminDashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> roleCol;

    private AdminService adminService;

    @FXML
    public void initialize() {
        adminService = new AdminService();
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();
    }

    private void loadUsers() {
        adminService.getAllUsers().thenAccept(users -> {
            Platform.runLater(() -> {
                ObservableList<User> data = FXCollections.observableArrayList(users);
                userTable.setItems(data);
                totalUsersLabel.setText(String.valueOf(users.size()));
            });
        });
    }
}
