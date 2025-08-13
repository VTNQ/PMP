package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OfficerUserDetailViewController {
    @FXML private Label lblFullName;
    @FXML private Label lblBirthYear;
    @FXML private Label lblPosition;
    @FXML private Label lblUnit;
    @FXML private Label lblHomeTown;
    @FXML private Label lblNote;
    @FXML private Label lblTotalAllowance;

    @FXML private Button btnClose;
    public void setOfficer(OfficerViewDTO officer){
        lblFullName.setText("Họ tên:"+officer.fullNameProperty().getValue());
        lblBirthYear.setText("Năm sinh:"+officer.birthYearProperty().getValue());
        lblPosition.setText("Chức vụ:"+officer.levelNameProperty().getValue());
        lblUnit.setText("Đơn vị:"+officer.unitProperty().getValue());
        lblHomeTown.setText("Quê quán:"+officer.homeTownProperty().getValue());
        lblTotalAllowance.setText("Tổng phụ cấp: " + officer.getAllowanceMonths());
        lblNote.setText("ghi chú:"+officer.noteProperty().getValue());
    }
    @FXML
    private void closePopup(){
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
