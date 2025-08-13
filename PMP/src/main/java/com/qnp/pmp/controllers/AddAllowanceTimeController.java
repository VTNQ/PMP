package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.Allowance;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.AllowanceService;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.AllowanceServiceImpl;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class AddAllowanceTimeController {
    private final OfficeService officeService = new OfficerServiceImpl();
    private final AllowanceService allowanceService = new AllowanceServiceImpl();
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<Officer> officerComboBox;
    @FXML
    private TextArea decisionTextArea;
    @FXML
    public void initialize() {
        List<Officer> officerList = officeService.getOfficers();
        officerComboBox.setEditable(true); // Cho phép nhập tìm
        FilteredList<Officer> filteredItems = new FilteredList<>(FXCollections.observableList(officerList), p -> true);
        officerComboBox.setItems(filteredItems);
        officerComboBox.setConverter(new StringConverter<Officer>() {
            @Override
            public String toString(Officer officer) {
                return officer != null ? officer.getFullName() : "";
            }

            @Override
            public Officer fromString(String s) {
                return officerComboBox.getItems().stream()
                        .filter(officer -> officer.getFullName().equals(s))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Lọc theo tên nhập vào
        officerComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if(officerComboBox.getValue()!=null && officerComboBox.getValue().getFullName().equals(newVal)){
                return;
            }
            filteredItems.setPredicate(office->{
                if(newVal==null || newVal.isEmpty())return true;
                String lowerCaseFilter = newVal.toLowerCase();
                return office.getFullName().toLowerCase().contains(lowerCaseFilter);
            });
            if(!filteredItems.isEmpty()){
                officerComboBox.show();
            }
        });
    }

    @FXML
    private void onSave(){
        try {
            Allowance allowance=new Allowance();
            allowance.setOfficerId(officerComboBox.getValue().getId());
            allowance.setStartDate(fromDatePicker.getValue());
            allowance.setEndDate(toDatePicker.getValue());
            allowance.setDecision(decisionTextArea.getText());
            allowanceService.insert(allowance);
            Dialog.displaySuccessFully("Lưu thời giản hưởng thành công");
            officerComboBox.getSelectionModel().clearSelection();
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            decisionTextArea.clear();
        }catch (Exception e){
            e.printStackTrace();
            Dialog.displayErrorMessage("Lưu thời gian hưởng thấp bải");
        }
    }
    @FXML
    private void onCancel(){
        Stage stage = (Stage) officerComboBox.getScene().getWindow();
        stage.close();
    }
}
