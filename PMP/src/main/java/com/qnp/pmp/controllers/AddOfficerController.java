package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;


import java.io.File;

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

}
