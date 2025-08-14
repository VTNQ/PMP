package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OfficerDetailViewController {
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label identifierLabel;
    @FXML
    private Label birthYearLabel;

    @FXML
    private Button btnClose;
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
    private void enableWindowDragging(Stage stage, Parent root) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
    @FXML
    private void onBenefitDetails(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/AllowanceTime/benefit-detail.fxml"));
            Parent root = loader.load();
            BenefitDetailsController controller = loader.getController();
            controller.loadData(officerId);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết được hưởng");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (Exception e){
            Dialog.displayErrorMessage("Không thể mở cửa sổ chi tiết được hưởng");
        }
    }

}
