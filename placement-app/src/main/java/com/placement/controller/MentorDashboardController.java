package com.placement.controller;

import com.placement.model.Application;
import com.placement.service.EvaluationService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MentorDashboardController {

    @FXML private TableView<Application> studentTable;
    @FXML private TableColumn<Application, String> nameCol;
    @FXML private TableColumn<Application, String> internshipCol;
    @FXML private TableColumn<Application, String> statusCol;

    private EvaluationService evaluationService;

    @FXML
    public void initialize() {
        evaluationService = new EvaluationService();
        nameCol.setCellValueFactory(new PropertyValueFactory<>("company")); // repurposing company for studentName for brevity
        internshipCol.setCellValueFactory(new PropertyValueFactory<>("internshipTitle"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadStudents();
    }

    private void loadStudents() {
        ObservableList<Application> data = FXCollections.observableArrayList(
                new Application("1", "Software Engineering", "Alice Brown", "Interviewing"),
                new Application("2", "Data Science", "Charlie Davis", "Offered")
        );
        studentTable.setItems(data);
    }

    @FXML
    public void handleEvaluate() {
        Application selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a student to evaluate.");
            return;
        }

        // Mocking an evaluation submission
        evaluationService.submitEvaluation(selected.getCompany(), 5, "Excellent performance").thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) showAlert("Success", "Evaluation submitted for " + selected.getCompany());
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
}
