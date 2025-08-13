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
    private void onSave(){
        String name = rankNameField.getText().trim();

        if (name.isEmpty() ) {
            Dialog.displayErrorMessage("Thiếu dữ liệu,Vui lòng nhập đầy đủ tên cấp bậc .");
            return;
        }

        LevelDTO level = new LevelDTO();
        level.setName(name);

        levelService.save(level);
        rankNameField.setText("");
        Dialog.displaySuccessFully("Thêm cấp bậc thành công");
    }
    @FXML
    private void onCancel(ActionEvent event) {
        // có thể thêm stage.close() nếu bạn muốn
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
