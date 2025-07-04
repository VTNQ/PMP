package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;

public class AddOfficerController {
    private final OfficeService officeService;
    public AddOfficerController(){
        this.officeService=new OfficerServiceImpl();
    }
    @FXML private TextField codeField;
    @FXML private TextField fullNameField;
    @FXML private TextField birthYearField;
    @FXML private TextField rankField;
    @FXML private TextField positionField;
    @FXML private TextField unitField; // Đã sửa
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label imageLabel;
    @FXML
    private TableView<OfficerDTO> officerTable;
    private final ObservableList<OfficerDTO> officerList = FXCollections.observableArrayList();
    private File selectedImage;
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImage = fileChooser.showOpenDialog(null);
        if (selectedImage != null) {
            imageLabel.setText(selectedImage.getName());
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Hủy bỏ thêm cán bộ");
        // TODO: close window
    }

    @FXML
    private void handleSave() {
    try {
        if (codeField.getText().isEmpty() || fullNameField.getText().isEmpty()) {
            System.out.println("Vui lòng điền đầy đủ thông tin.");
            return;
        }
        OfficerDTO officer = new OfficerDTO();
        officer.setCode(codeField.getText());
        officer.setFullName(fullNameField.getText());
        officer.setRank(rankField.getText());
        officer.setPosition(positionField.getText());
        officer.setUnit(unitField.getText());
        officer.setFullName(fullNameField.getText());
        officer.setBirthYear(Integer.parseInt(birthYearField.getText()));
        officer.setHometown("");
        officer.setWorkingStatus(statusComboBox.getValue());
        officeService.saveOfficer(selectedImage, officer);
        Dialog.displaySuccessFully("Luu cán bộ thành công");
    }catch (Exception e) {
        e.printStackTrace();
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
                    if (row.getRowNum() == 0) continue; // bỏ header

                    OfficerDTO dto = new OfficerDTO();
                    dto.setCode(getCellString(row, 0));
                    dto.setFullName(getCellString(row, 1));
                    dto.setBirthYear(parseBirthYear(row, 2));
                    dto.setRank(getCellString(row, 3));
                    dto.setPosition(getCellString(row, 4));
                    dto.setUnit(getCellString(row, 5));
                    dto.setWorkingStatus(getCellString(row, 6));
                    dto.setAvatar(getCellString(row, 7)); // hoặc để trống nếu không có

                    if (!dto.getCode().isEmpty()) {
                        imported.add(dto);
                    }
                }

                officerList.addAll(imported); // Gán vào bảng
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
