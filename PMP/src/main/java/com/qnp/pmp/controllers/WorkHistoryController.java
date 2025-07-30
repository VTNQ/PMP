package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.WorkTimeViewDTO;
import com.qnp.pmp.service.WorkTimeService;
import com.qnp.pmp.service.impl.WorkTimeServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkHistoryController {
    @FXML
    private TableView<WorkTimeViewDTO> workTable;
    @FXML
    private TableColumn<WorkTimeViewDTO, Number> roundCol;
    @FXML
    private TableColumn<WorkTimeViewDTO, String> startDateCol;
    @FXML
    private TableColumn<WorkTimeViewDTO, String> endDateCol;
    private final WorkTimeService workTimeService = new WorkTimeServiceImpl();
    private int officeId;

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
        loadWorkHistory();
    }

    private void loadWorkHistory() {
        List<WorkTimeViewDTO> list = workTimeService.getWorkTimesByOfficerId(officeId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        roundCol.setCellValueFactory(data -> data.getValue().roundProperty());
        startDateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().startDateProperty().get() != null
                                ? data.getValue().startDateProperty().get().format(formatter)
                                : ""
                )
        );
        endDateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().endDateProperty().get() != null
                                ? data.getValue().endDateProperty().get().format(formatter)
                                : ""
                )
        );

        ObservableList<WorkTimeViewDTO> observableList = FXCollections.observableArrayList(list);
        workTable.setItems(observableList);
    }

}
