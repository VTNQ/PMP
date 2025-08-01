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

    @FXML
    private TableView<OfficerViewDTO> officerUserTable;
    @FXML
    private TableColumn<OfficerViewDTO, String> fullNameCol;
    @FXML
    private TableColumn<OfficerViewDTO, String> positionCol;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> allowanceCol;
    @FXML
    private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML
    private TableColumn<OfficerViewDTO, Void> detailButtonCol;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<OfficerViewDTO, Void> studyTimeButtonCol;
    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {
        // Gán dữ liệu cho các cột
        fullNameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        positionCol.setCellValueFactory(data -> data.getValue().levelNameProperty());
        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        allowanceCol.setCellValueFactory(data->data.getValue().allowanceMonthsProperty().asObject());
        officerUserTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        centerAllColumns(fullNameCol, positionCol, unitCol, detailButtonCol, studyTimeButtonCol,allowanceCol);
        addDetailButtonToTable();
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

    private void addDetailButtonToTable() {
        Callback<TableColumn<OfficerViewDTO, Void>, TableCell<OfficerViewDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Chi tiết");

            {
                btn.setOnAction(event -> {
                    OfficerViewDTO officer = getTableView().getItems().get(getIndex());
                    showDetailPopup(officer);
                });
                btn.setStyle(
                        "-fx-background-color: #007bff;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10 5 10;"
                );
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(javafx.geometry.Pos.CENTER); // Căn giữa nút
                }
            }
        };
        detailButtonCol.setCellFactory(cellFactory);
    }

    private void showDetailPopup(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/OfficerUserDetailView.fxml"));
            Parent root = loader.load();
            OfficerUserDetailViewController controller = loader.getController();
            controller.setOfficer(officer);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết cán bộ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void enableWindowDragging(Stage stage, Parent root) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
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
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
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
