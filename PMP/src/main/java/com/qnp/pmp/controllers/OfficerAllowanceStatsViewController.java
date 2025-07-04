package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OfficerAllowanceStatsViewController {
    private final OfficeService officeService;
    public OfficerAllowanceStatsViewController() {
        this.officeService=new OfficerServiceImpl();
    }
    @FXML
    private TableView<OfficerViewDTO>officerTable;
    @FXML
    private TableColumn<OfficerViewDTO,String>fullNameCol;
    @FXML
    private TableColumn<OfficerViewDTO,String>positionCol;
    @FXML
    public void initialize() {
        fullNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        positionCol.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        loadOfficerAllowance();
    }
    private void loadOfficerAllowance(){
        ObservableList<OfficerViewDTO>officerViewDTOS= FXCollections.observableArrayList(officeService.getOfficerAllowanceStatus());
        officerTable.setItems(officerViewDTOS);
    }
    @FXML
    private void refreshTable() {
        loadOfficerAllowance();
    }

}
