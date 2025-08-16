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

import java.time.LocalDate;
import java.util.List;

public class AddAllowanceTimeController {
    private final OfficeService officeService = new OfficerServiceImpl();
    private final AllowanceService allowanceService = new AllowanceServiceImpl();
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<Officer> officerComboBox;
    @FXML
    private TextArea decisionStartTextArea;
    @FXML
    private TextArea decisionEndTextArea;
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
            if(officerComboBox.getValue()!=null || fromDatePicker.getValue()!=null || toDatePicker.getValue()!=null || decisionEndTextArea.getText()!=null || decisionStartTextArea.getText()!=null){
                Dialog.displayErrorMessage("Vui lòng điền đầy đủ thông tin.");
                return;
            }
            LocalDate startDate=fromDatePicker.getValue();
            LocalDate endDate=toDatePicker.getValue();
            LocalDate today = LocalDate.now();
            if(startDate.isBefore(endDate)){
                Dialog.displayErrorMessage("Ngày bắt đầu phải từ hôm nay trở đi.");
                return;
            }
            if(!endDate.isAfter(startDate)){
                Dialog.displayErrorMessage("Ngày kết thúc phải lớn hơn ngày bắt đầu.");
                return;
            }
            LocalDate lastEndDate=allowanceService.getLastEndDateByOfficerId(officerComboBox.getValue().getId());
            if(lastEndDate!=null && !startDate.isAfter(lastEndDate)){
                Dialog.displayErrorMessage("Ngày bắt đầu phải lớn hơn ngày kết thúc lần được hưởng trước (" + lastEndDate + ").");
                return;
            }
            allowance.setOfficerId(officerComboBox.getValue().getId());
            allowance.setStartDate(fromDatePicker.getValue());
            allowance.setEndDate(toDatePicker.getValue());
            allowance.setDecisionStart(decisionStartTextArea.getText());
            allowance.setDecisionEnd(decisionEndTextArea.getText());
            allowanceService.insert(allowance);
            Dialog.displaySuccessFully("Lưu thời giản hưởng thành công");
            officerComboBox.getSelectionModel().clearSelection();
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            decisionStartTextArea.clear();
            decisionEndTextArea.clear();
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
