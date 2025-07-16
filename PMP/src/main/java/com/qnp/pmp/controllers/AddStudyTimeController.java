package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.entity.StudyTime;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.StudyTimeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import com.qnp.pmp.service.impl.StudyTimeServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.util.StringConverter;
import javafx.scene.control.SpinnerValueFactory;

import java.util.List;

public class AddStudyTimeController {
    private StudyTimeService  studyTimeService;
    private OfficeService officeService;
    public AddStudyTimeController() {
        this.studyTimeService=new StudyTimeServiceImpl();
        this.officeService=new OfficerServiceImpl();
    }
    @FXML
    private Spinner<Integer> roundSpinner;
    @FXML
    private ComboBox<Officer>officerComboBox;
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;
    @FXML
    public void initialize() {
        List<Officer>officerList=officeService.getOfficers();
        officerComboBox.setItems(FXCollections.observableList(officerList));
        officerComboBox.setConverter(new StringConverter<Officer>() {
            @Override
            public String toString(Officer officer) {
                return officer != null ? officer.getFullName() : "";
            }

            @Override
            public Officer fromString(String s) {
                return officerComboBox.getItems().stream()
                        .filter(officer->officer.getFullName().equals(s))
                        .findFirst()
                        .orElse(null);

            }
        });
        roundSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1)
        );
    }
    @FXML
    private void onSave() {
        StudyTime studyTime = new StudyTime();
        studyTime.setOfficerId(officerComboBox.getValue().getId());
        studyTime.setRound(roundSpinner.getValue());
        studyTime.setStartDate(startDatePicker.getValue());
        studyTime.setEndDate(endDatePicker.getValue());
        studyTimeService.saveStudyTime(studyTime);
        officerComboBox.getSelectionModel().clearSelection();
        roundSpinner.getValueFactory().setValue(1); // reset về 1
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        Dialog.displaySuccessFully("Luu Thời gian học thành công");
    }
    @FXML
    private void onCancel() {}

}
