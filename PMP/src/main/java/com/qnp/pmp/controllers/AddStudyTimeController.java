package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.entity.StudyTime;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.StudyTimeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import com.qnp.pmp.service.impl.StudyTimeServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class AddStudyTimeController {
    private final StudyTimeService studyTimeService = new StudyTimeServiceImpl();
    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML private Spinner<Integer> roundSpinner;
    @FXML private ComboBox<Officer> officerComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML
    public void initialize() {
        List<Officer> officerList = officeService.getOfficers();

        officerComboBox.setEditable(true); // Cho phép nhập tìm
        FilteredList<Officer> filteredItems = new FilteredList<>(FXCollections.observableList(officerList), p -> true);
        officerComboBox.setItems(filteredItems);

        // Gán cách hiển thị tên
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

        // Spinner lần học
        roundSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    @FXML
    private void onSave() {
        try {
            Officer selectedOfficer = officerComboBox.getValue();
            if (selectedOfficer == null || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                Dialog.displayErrorMessage("Vui lòng điền đầy đủ thông tin.");
                return;
            }

            StudyTime studyTime = new StudyTime();
            studyTime.setOfficerId(selectedOfficer.getId());
            studyTime.setRound(roundSpinner.getValue());
            studyTime.setStartDate(startDatePicker.getValue());
            studyTime.setEndDate(endDatePicker.getValue());

            studyTimeService.saveStudyTime(studyTime);

            // Reset form
            officerComboBox.getSelectionModel().clearSelection();
            officerComboBox.getEditor().clear();
            roundSpinner.getValueFactory().setValue(1);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            Dialog.displaySuccessFully("Lưu thời gian học thành công");
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Có lỗi xảy ra khi lưu.");
        }
    }

    @FXML
    private void onCancel() {
        // có thể thêm stage.close() nếu bạn muốn
    }
}
