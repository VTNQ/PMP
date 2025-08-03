package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;

public class OfficerDetailViewController {
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label identifierLabel;
    @FXML
    private Label birthYearLabel;
    @FXML
    private Label sinceLabel;
    @FXML
    private Button btnClose;

    @FXML
    private Label utilLabel;
    @FXML
    private Label homeTownLabel;
    @FXML
    private Label allowanceLabel;
    @FXML
    private Label noteLabel;
    @FXML
    private Label levelNameLabel;
    @FXML
    private Label unitLabel;
    private final OfficeService officeService=new OfficerServiceImpl();
    private int officerId;
    public void setOfficerId(int officerId) {
        this.officerId = officerId;
        loadOfficerDetail();
    }
    private void loadOfficerDetail(){
        OfficerViewDTO dto = officeService.getOfficerById(officerId);
        if(dto!=null){
            fullNameLabel.setText(dto.fullNameProperty().getValue());
            identifierLabel.setText(dto.identifierCodeProperty().getValue());
            birthYearLabel.setText(dto.birthYearProperty().getValue().toString());
            sinceLabel.setText(dto.sinceProperty().getValue().toString());
            LocalDate utilDate = dto.utilProperty().getValue();
            utilLabel.setText(utilDate != null ? utilDate.toString() : "Chưa xác định");
            homeTownLabel.setText(dto.homeTownProperty().getValue());
            allowanceLabel.setText(dto.allowanceMonthsProperty().getValue().toString());
            noteLabel.setText(dto.noteProperty().getValue());
            levelNameLabel.setText(dto.levelNameProperty().getValue());
            unitLabel.setText(dto.unitProperty().getValue());
        }
    }
    @FXML
    private void onClose(){
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
