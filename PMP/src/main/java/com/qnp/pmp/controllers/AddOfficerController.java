package com.qnp.pmp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.io.File;

public class AddOfficerController {
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
        System.out.println("Chọn ảnh...");
        // TODO: mở fileChooser
    }

    @FXML
    private void handleCancel() {
        System.out.println("Hủy bỏ thêm cán bộ");
        // TODO: close window
    }

    @FXML
    private void handleSave() {
        System.out.println("Lưu cán bộ...");
        // TODO: validate và gọi service lưu
    }

}
