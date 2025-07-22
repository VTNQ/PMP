package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class ImportPreviewController {
    @FXML private TableColumn<OfficerViewDTO, String> fullNameCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearCol;
    @FXML private TableColumn<OfficerViewDTO, LocalDate> sinceCol;
    @FXML private TableColumn<OfficerViewDTO, String> positionCol;
    @FXML private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML private TableColumn<OfficerViewDTO, String> homeTownCol;
    @FXML private TableColumn<OfficerViewDTO, String> noteCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> allowanceCol;

    @FXML
    public void initialize() {
        fullNameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        birthYearCol.setCellValueFactory(data -> data.getValue().birthYearProperty().asObject());
        sinceCol.setCellValueFactory(cellData -> cellData.getValue().sinceProperty());
        positionCol.setCellValueFactory(data -> data.getValue().levelNameProperty());
        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        homeTownCol.setCellValueFactory(data -> data.getValue().homeTownProperty());
        noteCol.setCellValueFactory(data -> data.getValue().noteProperty());
        allowanceCol.setCellValueFactory(data -> data.getValue().allowanceMonthsProperty().asObject());
    }

    @FXML
    private TableView<OfficerViewDTO> previewTable;
    private ObservableList<OfficerViewDTO> previewData;
    private Consumer<List<OfficerViewDTO>> onConfirmCallback;

    public void setPreviewData(List<OfficerViewDTO> data, Consumer<List<OfficerViewDTO>> callback) {
        this.previewData = FXCollections.observableArrayList(data);
        this.onConfirmCallback = callback;
        previewTable.setItems(previewData);
    }

    @FXML
    private void onConfirm() {
        if (onConfirmCallback != null) {
            onConfirmCallback.accept(previewData);
        }
        ((Stage) previewTable.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel() {
        ((Stage) previewTable.getScene().getWindow()).close();
    }

}
