package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.LevelService;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.LevelServiceImpl;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;


public class EditOfficerController {
    private final OfficeService officeService;
    private final LevelService levelService;
    public EditOfficerController() {
        this.officeService = new OfficerServiceImpl();
        this.levelService = new LevelServiceImpl();
    }
    @FXML
    private TextField fullNameField;
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
    private int id;
    private OfficerViewDTO officer;

    public void setOfficer(OfficerViewDTO officer) {
        this.officer = officer;
        fullNameField.setText(officer.fullNameProperty().getValue());
        birthYearField.setText(String.valueOf(officer.birthYearProperty().get()));
        unitField.setText(officer.unitProperty().getValue());
        noteField.setText(officer.noteProperty().getValue());
        homeTownField.setText(officer.homeTownProperty().getValue());
        this.id=officer.getId().getValue();

        // Tìm Level phù hợp theo ID trong ComboBox
        int officerLevelId = officer.levelIdProperty().get();
        for (Level level : levelComboBox.getItems()) {
            if (level.getId() == officerLevelId) {
                levelComboBox.getSelectionModel().select(level);
                break;
            }
        }

    }
    @FXML
    public void initialize() {
        List<Level> levelList=levelService.getAll();
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
    private void onSave() {
      try {
          Officer officerDto=new Officer();
          officerDto.setFullName(fullNameField.getText());
          officerDto.setUnit(unitField.getText());
          officerDto.setHomeTown(homeTownField.getText());
          officerDto.setBirthYear(Integer.valueOf(birthYearField.getText()));
          officerDto.setNote(noteField.getText());
          officerDto.setId(id);
          officerDto.setLevelId(levelComboBox.getSelectionModel().getSelectedItem().getId());
          officeService.updateOfficer(id,officerDto);
          Dialog.displaySuccessFully("Luu cán bộ thành công");
          closeWindow();

      }catch (Exception e) {
          Dialog.displayErrorMessage("Luu cán bộ thất bại");
      }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.close();
    }
}
