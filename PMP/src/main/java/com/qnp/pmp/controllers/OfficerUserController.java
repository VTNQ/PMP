package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class OfficerUserController {

    @FXML private TableView<OfficerViewDTO> officerUserTable;
    @FXML private TableColumn<OfficerViewDTO, String> fullNameCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearCol;
    @FXML private TableColumn<OfficerViewDTO, LocalDate> sinceCol;
    @FXML private TableColumn<OfficerViewDTO, String> positionCol;
    @FXML private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML private TableColumn<OfficerViewDTO, String> homeTownCol;
    @FXML private TableColumn<OfficerViewDTO, String> noteCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> totalAllowance;
    @FXML private TableColumn<OfficerViewDTO, Void> studyTimeButtonCol;

    @FXML private TextField searchField;

    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {
        // Gán dữ liệu cho các cột
        fullNameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        birthYearCol.setCellValueFactory(data -> data.getValue().birthYearProperty().asObject());
        sinceCol.setCellValueFactory(data -> data.getValue().sinceProperty());
        positionCol.setCellValueFactory(data -> data.getValue().levelNameProperty());
        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        homeTownCol.setCellValueFactory(data -> data.getValue().homeTownProperty());
        noteCol.setCellValueFactory(data -> data.getValue().noteProperty());
        totalAllowance.setCellValueFactory(data -> data.getValue().allowanceMonthsProperty().asObject());

        officerUserTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        centerAllColumns(fullNameCol, birthYearCol, sinceCol, positionCol, unitCol, homeTownCol, noteCol, totalAllowance);
        addStudyTimeButtonToTable();

        loadOfficerAllowance();
    }

    private void addStudyTimeButtonToTable() {
        Callback<TableColumn<OfficerViewDTO, Void>, TableCell<OfficerViewDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("⏱ Xem");

            {
                btn.setOnAction(event -> {
                    OfficerViewDTO officer = getTableView().getItems().get(getIndex());
                    showStudyTime(officer);
                });
                btn.setStyle(
                        "-fx-background-color: #007bff;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;"
                );
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };

        studyTimeButtonCol.setCellFactory(cellFactory);
    }

    private void showStudyTime(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/StudyTime/StudyTimeView.fxml"));
            Parent root = loader.load();

            StudyTimeController controller = loader.getController();
            controller.setOfficer(officer);

            Stage stage = new Stage();
            stage.setTitle("Thời gian học");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadOfficerAllowance() {
        List<OfficerViewDTO> data = officeService.getOfficerAllowanceStatus();
        ObservableList<OfficerViewDTO> observableData = FXCollections.observableArrayList(data);
        officerUserTable.setItems(observableData);
    }

    @FXML
    private void onSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                ObservableList<OfficerViewDTO> result = FXCollections.observableArrayList(officeService.findByName(keyword));
                officerUserTable.setItems(result);
            } else {
                loadOfficerAllowance();
            }
        }
    }

    private <T> void centerCell(TableColumn<OfficerViewDTO, T> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    @SafeVarargs
    private final void centerAllColumns(TableColumn<OfficerViewDTO, ?>... columns) {
        for (TableColumn<OfficerViewDTO, ?> col : columns) {
            centerCell(col);
        }
    }
}
