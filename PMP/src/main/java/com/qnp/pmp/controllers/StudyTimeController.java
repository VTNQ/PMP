package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.dto.StudyRoundDTO;
import com.qnp.pmp.dto.StudyRoundViewDTO;
import com.qnp.pmp.service.StudyTimeService;
import com.qnp.pmp.service.impl.StudyTimeServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.List;

public class StudyTimeController {
    @FXML
    private TableView<StudyRoundViewDTO> studyTable;
    @FXML private TableColumn<StudyRoundViewDTO,Integer> roundCol;
    @FXML private TableColumn<StudyRoundViewDTO, String> startDateCol;
    @FXML private TableColumn<StudyRoundViewDTO, String> endDateCol;
    private OfficerViewDTO officer;
    private final StudyTimeService studyTimeService = new StudyTimeServiceImpl();
    @FXML
    public void initialize() {
        configureColumns();
    }
    public void setOfficer(OfficerViewDTO officer) {
        this.officer = officer;
        loadStudyTime();
    }
    private void configureColumns(){
        roundCol.setCellValueFactory(data->data.getValue().roundProperty().asObject());
        startDateCol.setCellValueFactory(data->data.getValue().getStartDate());
        endDateCol.setCellValueFactory(data->data.getValue().getEndDate());
    }
    private void loadStudyTime(){
        List<StudyRoundViewDTO>data=studyTimeService.getByOfficeId(officer.getId().get());
        ObservableList<StudyRoundViewDTO> observableData= FXCollections.observableArrayList(data);
        studyTable.setItems(observableData);
    }
    private <T> void centerCell(TableColumn<StudyRoundViewDTO, T> column) {
        column.setCellFactory(new Callback<TableColumn<StudyRoundViewDTO, T>, TableCell<StudyRoundViewDTO, T>>() {
            @Override
            public TableCell<StudyRoundViewDTO, T> call(TableColumn<StudyRoundViewDTO, T> param) {
                return new TableCell<StudyRoundViewDTO, T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.toString());
                        setStyle("-fx-alignment: CENTER;");
                    }
                };
            }
        });
    }

    @SafeVarargs
    private final void centerAllColumns(TableColumn<StudyRoundViewDTO, ?>... columns) {
        for (TableColumn<StudyRoundViewDTO, ?> col : columns) {
            centerCell(col);
        }
    }

}
