package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.TextField;
import java.io.IOException;

public class OfficeController {

    // FXML bindings
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private TableView<OfficerDTO> officerTable;
    @FXML
    private TableColumn<OfficerDTO, String> codeColumn;
    @FXML
    private TableColumn<OfficerDTO, String> fullNameColumn;
    @FXML
    private TableColumn<OfficerDTO, Integer> birthYearColumn;
    @FXML
    private TableColumn<OfficerDTO, String> rankColumn;
    @FXML
    private TableColumn<OfficerDTO, String> positionColumn;
    @FXML
    private TableColumn<OfficerDTO, String> unitColumn;
    @FXML
    private TableColumn<OfficerDTO, String> statusColumn;
    @FXML
    private TableColumn<OfficerDTO, String> avatarColumn;

    private final ObservableList<OfficerDTO> officerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cài đặt CellValueFactory
        codeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCode()));
        fullNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        birthYearColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBirthYear()).asObject());
        rankColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRank()));
        positionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPosition()));
        unitColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnit()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWorkingStatus()));
        avatarColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAvatar()));
        // Gắn dữ liệu mẫu (tạm thời)
        officerTable.setItems(officerList);

        // Test data

    }

    @FXML
    private void handleAddOfficer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/views/AddOfficer.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Thêm cán bộ");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: mở form thêm mới
    }

    @FXML
    private void handleEditOfficer() {
        System.out.println("Sửa cán bộ");
        // TODO: mở form sửa
    }

    @FXML
    private void handleDeleteOfficer() {
        OfficerDTO selected = officerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            officerList.remove(selected);
            System.out.println("Đã xoá: " + selected.getFullName());
        }
    }

    @FXML
    private void handleViewDetail() {
        OfficerDTO selected = officerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Chi tiết: " + selected.getFullName());
            // TODO: hiển thị thông tin chi tiết
        }
    }
}
