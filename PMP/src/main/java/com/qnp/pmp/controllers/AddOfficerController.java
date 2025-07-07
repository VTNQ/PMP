package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;


import java.io.File;

public class AddOfficerController {
    private final OfficeService officeService;

    public AddOfficerController() {
        this.officeService = new OfficerServiceImpl();
    }

    @FXML
    private TextField phoneField;
    @FXML
    private TextField fullNameField;
    @FXML
    private ComboBox<String> levelIdField;
    @FXML
    private TextField unitField;
    @FXML
    private TextField sinceField;
    @FXML
    private TextField identifierField;
    @FXML
    private TextField homeTownField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private void handleCancel() {
        Stage stage=(Stage) fullNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSave() {
        try {

            Officer officer = new Officer();
            officer.setUnit(unitField.getText());
            officer.setPhone(phoneField.getText());
            officer.setFullName(fullNameField.getText());
            officer.setSince(sinceField.getText());
            officer.setIdentifier(identifierField.getText());
            officer.setHomeTown(homeTownField.getText());
            officeService.saveOfficer(officer);
            Dialog.displaySuccessFully("Luu cán bộ thành công");

        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception ignored) {
        }
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
