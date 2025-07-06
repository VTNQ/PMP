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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/AddOfficerDialog.fxml"));
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
    @FXML
    private void handleImportFromExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tệp Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);

                ObservableList<OfficerDTO> imported = FXCollections.observableArrayList();
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    OfficerDTO dto = new OfficerDTO();
                    dto.setCode(getCellString(row, 0));
                    dto.setFullName(getCellString(row, 1));
                    dto.setBirthYear(parseBirthYear(row, 2));
                    dto.setRank(getCellString(row, 3));
                    dto.setPosition(getCellString(row, 4));
                    dto.setUnit(getCellString(row, 5));
                    dto.setWorkingStatus(getCellString(row, 6));
                    dto.setAvatar(getCellString(row, 7));

                    if (!dto.getCode().isEmpty()) {
                        imported.add(dto);
                    }
                }

                officerList.addAll(imported); // cập nhật danh sách chính
                officerTable.setItems(officerList);
                showAlert("Import thành công", "Đã import " + imported.size() + " cán bộ.", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể đọc file Excel.", Alert.AlertType.ERROR);
            }
        }
    }

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        return (cell != null) ? cell.toString().trim() : "";
    }

    private int parseBirthYear(Row row, int colIndex) {
        try {
            Cell cell = row.getCell(colIndex);
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell != null) {
                return Integer.parseInt(cell.toString().trim());
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
