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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OfficerUserController {

    @FXML
    private TableView<OfficerViewDTO>officerTableAbove60;
    @FXML
    private TableColumn<OfficerViewDTO,String>fullNameColAbove;
    @FXML
    private TableColumn<OfficerViewDTO,String>identifierCodeColAbove;
    @FXML
    private TableColumn<OfficerViewDTO,Integer>birthYearColAbove;
    @FXML
    private TableColumn<OfficerViewDTO,Integer>allowanceColAbove;
    @FXML
    private TableColumn<OfficerViewDTO,Void>detailColAbove;
    @FXML private TableColumn<OfficerViewDTO,Void>workColAbove;
    @FXML
    private TableColumn<OfficerViewDTO,String>unitColAbove;
    @FXML
    private TableView<OfficerViewDTO>officerTableBelow60;
    @FXML
    private TableColumn<OfficerViewDTO, String> fullNameColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, String> identifierCodeColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> birthYearColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> allowanceColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Void> detailColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Void> workColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, String> unitColBelow;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<OfficerViewDTO, Void> studyTimeButtonCol;
    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {
        // Gán dữ liệu cho các cột
        setupTable(fullNameColAbove, identifierCodeColAbove, birthYearColAbove, allowanceColAbove, unitColAbove, detailColAbove, workColAbove);
        setupTable(fullNameColBelow, identifierCodeColBelow, birthYearColBelow, allowanceColBelow, unitColBelow, detailColBelow, workColBelow);

    }

    private void setupTable(TableColumn<OfficerViewDTO, String> fullNameCol,
                            TableColumn<OfficerViewDTO, String> identifierCodeCol,
                            TableColumn<OfficerViewDTO, Integer> birthYearCol,
                            TableColumn<OfficerViewDTO, Integer> allowanceCol,
                            TableColumn<OfficerViewDTO, String> unitCol,
                            TableColumn<OfficerViewDTO, Void> detailCol,
                            TableColumn<OfficerViewDTO, Void> workCol) {

        fullNameCol.setCellValueFactory(c -> c.getValue().fullNameProperty());
        identifierCodeCol.setCellValueFactory(c -> c.getValue().identifierCodeProperty());
        birthYearCol.setCellValueFactory(c -> c.getValue().birthYearProperty().asObject());
        allowanceCol.setCellValueFactory(c -> c.getValue().allowanceMonthsProperty().asObject());
        unitCol.setCellValueFactory(c -> c.getValue().unitProperty());

        addButtonToColumn(detailCol, "Chi tiết", this::showDetailPopup);
        addButtonToColumn(workCol, "Công tác", this::showStudyTime);
        loadOfficerAllowance();
    }

    private void addButtonToColumn(TableColumn<OfficerViewDTO, Void> column,
                                   String buttonText,
                                   Consumer<OfficerViewDTO> action) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button(buttonText);

            {
                btn.setOnAction(e -> {
                    OfficerViewDTO officer = getTableView().getItems().get(getIndex());
                    action.accept(officer);
                });
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btn));
            }
        });
    }
    private void showDetailPopup(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/OfficerDetailPopup.fxml"));
            Parent root = loader.load();

            OfficerDetailViewController controller = loader.getController();
            controller.setOfficerId(officer.getId().getValue());

            Stage stage = new Stage();
            stage.setTitle("Chi tiết cán bộ");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
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
            stage.setTitle("Thời gian Công tác");
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
        List<OfficerViewDTO> allData = officeService.getOfficerAllowanceStatus();

        List<OfficerViewDTO> above60 = allData.stream()
                .filter(o -> o.getAllowanceMonths() > 60)
                .collect(Collectors.toList());

        List<OfficerViewDTO> belowOrEqual60 = allData.stream()
                .filter(o -> o.getAllowanceMonths() <= 60)
                .collect(Collectors.toList());

        officerTableAbove60.setItems(FXCollections.observableArrayList(above60));
        officerTableBelow60.setItems(FXCollections.observableArrayList(belowOrEqual60));
    }

    @FXML
    private void onSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                ObservableList<OfficerViewDTO> result = FXCollections.observableArrayList(officeService.findByName(keyword));

            } else {
                loadOfficerAllowance();
            }
        }
    }


}
