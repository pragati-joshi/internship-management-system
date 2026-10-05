package com.placement.controller;

import com.placement.model.Internship;
import com.placement.service.InternshipService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import com.placement.service.ApplicationService;

public class StudentDashboardController {

    @FXML private Label statusLabel;
    @FXML private TableView<Internship> internshipTable;
    @FXML private TableColumn<Internship, String> companyCol;
    @FXML private TableColumn<Internship, String> titleCol;
    @FXML private TableColumn<Internship, String> statusCol;
    @FXML private VBox loadingBox;

    private InternshipService internshipService;
    private ApplicationService applicationService;

    @FXML
    public void initialize() {
        internshipService = new InternshipService();
        applicationService = new ApplicationService();

        companyCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadInternships();
    }

    @FXML
    public void handleApply() {
        Internship selected = internshipTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an internship to apply for.");
            return;
        }

        applicationService.applyForInternship(selected.getId()).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) showAlert("Success", "Successfully applied for " + selected.getTitle() + " at " + selected.getCompanyName());
            });
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadInternships() {
        loadingBox.setVisible(true);
        internshipTable.setVisible(false);

        internshipService.getAvailableInternships().thenAccept(internships -> {
            Platform.runLater(() -> {
                ObservableList<Internship> data = FXCollections.observableArrayList(internships);
                internshipTable.setItems(data);
                loadingBox.setVisible(false);
                internshipTable.setVisible(true);
                statusLabel.setText(internships.size() + " Internships Available");
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingBox.setVisible(false);
                statusLabel.setText("Failed to load internships: " + ex.getMessage());
            });
            return null;
        });
    }
}
