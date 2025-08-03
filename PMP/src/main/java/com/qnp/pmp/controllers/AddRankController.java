package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.LevelDTO;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.service.LevelService;
import com.qnp.pmp.service.impl.LevelServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class AddRankController {
    private LevelService  levelService;
    public AddRankController() {
        levelService=new LevelServiceImpl();
    }
    @FXML
    private TextField rankNameField;
    @FXML
    private TextField salaryField;
    @FXML
    private void onSave(){
        String name = rankNameField.getText().trim();
        String salaryText = salaryField.getText().trim();
        if (name.isEmpty() || salaryText.isEmpty()) {
            Dialog.displayErrorMessage("Thiếu dữ liệu,Vui lòng nhập đầy đủ tên cấp bậc và lương.");
            return;
        }
        Double salary=null;
        try {
          salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            Dialog.displayErrorMessage("Sai định dạng,Lương phải là số hợp lệ.");
            return;
        }
        LevelDTO level = new LevelDTO();
        level.setName(name);
        level.setSalary(salary);
        levelService.save(level);
        rankNameField.setText("");
        salaryField.setText("");
        Dialog.displaySuccessFully("Thêm cấp bậc thành công");
    }
    @FXML
    private void onCancel(ActionEvent event) {
        // có thể thêm stage.close() nếu bạn muốn
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
