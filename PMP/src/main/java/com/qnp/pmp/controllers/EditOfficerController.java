package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class EditOfficerController {
    @FXML private TextField fullNameField, phoneField, levelField, unitField, homeTownField, dobField;
    private OfficerViewDTO officer;
    public void setOfficer(OfficerViewDTO officer) {
        this.officer = officer;
        fullNameField.setText(officer.fullNameProperty().getValue());
        phoneField.setText(officer.phoneProperty().getValue());
        levelField.setText(officer.phoneProperty().getValue());
        unitField.setText(officer.phoneProperty().getValue());
        homeTownField.setText(officer.homeTownProperty().getValue());
        dobField.setText(officer.dobProperty().getValue());
    }
    @FXML
    private void onSave() {

        closeWindow();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.close();
    }
}
