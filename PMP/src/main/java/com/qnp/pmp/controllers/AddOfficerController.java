package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.LevelService;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.LevelServiceImpl;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;


import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class AddOfficerController {
    private final OfficeService officeService;
    private final LevelService levelService;

    public AddOfficerController() {
        this.officeService = new OfficerServiceImpl();
        this.levelService = new LevelServiceImpl();
    }


    @FXML
    private TextField fullNameField;
    @FXML
    private DatePicker sinceDatePicker;
    @FXML
    private ComboBox<Level> levelComboBox;
    @FXML
    private TextField unitField;
    @FXML
    private TextField birthYearField;
    @FXML
    private TextArea noteField;
    @FXML
    private TextField homeTownField;
    @FXML
    public void initialize() {
        List<Level>levelList=levelService.getAll();
        levelComboBox.setItems(FXCollections.observableList(levelList));
        levelComboBox.setConverter(new StringConverter<Level>() {
            @Override
            public String toString(Level level) {
                return level != null ? level.getName() : "";
            }

            @Override
            public Level fromString(String s) {
                return levelComboBox.getItems().stream()
                        .filter(level -> level.getName().equals(s))
                        .findFirst()
                        .orElse(null);

            }
        });
    }
    @FXML
    private void handleCancel() {
        Stage stage=(Stage) fullNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSave() {
        try {

            Officer officer = new Officer();
            officer.setUnit(unitField.getText());
            officer.setFullName(fullNameField.getText());
            officer.setLevelId(levelComboBox.getValue().getId());
            officer.setHomeTown(homeTownField.getText());
            officer.setBirthYear(Integer.valueOf(birthYearField.getText()));
            officer.setSince(LocalDate.parse(sinceDatePicker.getValue().toString()));
            officer.setNote(noteField.getText());
            officeService.saveOfficer(officer);
            fullNameField.clear();
            unitField.clear();
            birthYearField.clear();
            homeTownField.clear();
            levelComboBox.getSelectionModel().clearSelection();
            noteField.clear();

            Dialog.displaySuccessFully("Luu cán bộ thành công");
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Luu cán bộ thất bại");
        }
    }

}
