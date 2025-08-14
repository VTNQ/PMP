package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.BenefitDetailDTO;
import com.qnp.pmp.entity.Allowance;
import com.qnp.pmp.service.AllowanceService;
import com.qnp.pmp.service.impl.AllowanceServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BenefitEditController {
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private TextField tfDecisionStart;
    @FXML private TextField tfDecisionEnd;
    private final AllowanceService allowanceService=new AllowanceServiceImpl();
    private Allowance original;                 // đổi theo model thật
    public void setData(BenefitDetailDTO original) {
       dpStart.setValue(original.getStartDateActual());
       dpEnd.setValue(original.getEndDateActual());
       tfDecisionStart.setText(original.getDecisionStart());
       tfDecisionEnd.setText(original.getDecisionEnd());
       original.setId(original.getId());

    }
    @FXML
    private void onSave(){

        original.setDecisionStart(tfDecisionStart.getText());
        original.setDecisionEnd(tfDecisionEnd.getText());
        original.setStartDate(dpStart.getValue());
        original.setEndDate(dpEnd.getValue());
        allowanceService.update(original,Integer.valueOf(original.getId()));
    }
    @FXML
    private void onCancel() {
        Stage st = (Stage) dpStart.getScene().getWindow();
        st.close();
    }
}
