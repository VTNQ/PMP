package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
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

import java.io.IOException;

public class OfficerViewController {

    @FXML private TableView<OfficerViewDTO> officerTable;

    @FXML private TableColumn<OfficerViewDTO, String> fullNameCol;
    @FXML private TableColumn<OfficerViewDTO, String> phoneCol;
    @FXML private TableColumn<OfficerViewDTO, String> positionCol;
    @FXML private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML private TableColumn<OfficerViewDTO, String> identifierCol;
    @FXML private TableColumn<OfficerViewDTO, String> homeTownCol;
    @FXML private TableColumn<OfficerViewDTO, String> dobCol;
    @FXML
    private TextField searchField;

    @FXML private Label totalLabel;

    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {

        // Gán dữ liệu cho các cột
        fullNameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        phoneCol.setCellValueFactory(data -> data.getValue().phoneProperty());
        positionCol.setCellValueFactory(data -> data.getValue().levelIdProperty());
        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        identifierCol.setCellValueFactory(data -> data.getValue().identifierProperty());
        homeTownCol.setCellValueFactory(data -> data.getValue().homeTownProperty());
        dobCol.setCellValueFactory(data -> data.getValue().dobProperty());
        officerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Căn giữa dữ liệu cho tất cả cột
        centerAllColumns(fullNameCol, phoneCol, positionCol, unitCol, identifierCol, homeTownCol, dobCol);

        // Tải dữ liệu ban đầu
        loadOfficerAllowance();
        officerTable.setRowFactory(tv->{
            TableRow<OfficerViewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
               if(event.getClickCount() == 2 && !row.isEmpty()) {
                    OfficerViewDTO officerViewDTO = row.getItem();
                    showEditDialog(officerViewDTO);
                    officerTable.refresh();
               }
            });
            return row;
        });
    }
    private void showEditDialog(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/EditOfficer.fxml"));
            Parent root = loader.load();

            EditOfficerController controller = loader.getController();
            controller.setOfficer(officer);

            Stage stage = new Stage();
            stage.setTitle("Chỉnh sửa cán bộ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> void centerCell(TableColumn<OfficerViewDTO, T> column) {
        column.setCellFactory(tc -> new TableCell<>() {
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

    private void loadOfficerAllowance() {
        ObservableList<OfficerViewDTO> data =
                FXCollections.observableArrayList(officeService.getOfficerAllowanceStatus());
        officerTable.setItems(data);
        totalLabel.setText("Tổng: " + data.size() + " cán bộ");
    }
    @FXML
    private void onSearch(KeyEvent event) {
            if(event.getCode()==KeyCode.ENTER) {
                String keyword=searchField.getText().trim();
                if (!keyword.isEmpty()) {
                    ObservableList<OfficerViewDTO> result =
                            FXCollections.observableArrayList(officeService.findByName(keyword));
                    officerTable.setItems(result);
                    totalLabel.setText("Tổng: " + result.size() + " cán bộ");
                } else {
                    loadOfficerAllowance(); // Nếu rỗng thì tải lại toàn bộ
                }
            }
    }
    @FXML
    private void refreshTable() {
        loadOfficerAllowance();
    }
}
