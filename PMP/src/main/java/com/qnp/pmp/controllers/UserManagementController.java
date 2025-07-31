package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.dto.UserViewDTO;
import com.qnp.pmp.dto.UserViewModel;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class UserManagementController {
    private final UserService userService;
    public UserManagementController() {
        userService=new UserServiceImpl();
    }
    @FXML
    private TableView<UserViewDTO>userTable;
    @FXML
    private TableColumn<UserViewDTO,String>usernameCol;
    @FXML
    private TableColumn<UserViewDTO,Integer>IndexCol;
    @FXML
    private TableColumn<UserViewDTO,String>fullNameCol;
    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(cell -> cell.getValue().usernameProperty());
        fullNameCol.setCellValueFactory(cell -> cell.getValue().fullNameProperty());
        IndexCol.setCellValueFactory(cell->cell.getValue().indexProperty().asObject());
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    centerAllColumns(fullNameCol, IndexCol,usernameCol);
    loadUserData();
    }
    private <T> void centerCell(TableColumn<UserViewDTO, T> column) {
        column.setCellFactory(new Callback<TableColumn<UserViewDTO, T>, TableCell<UserViewDTO, T>>() {
            @Override
            public TableCell<UserViewDTO, T> call(TableColumn<UserViewDTO, T> param) {
                return new TableCell<UserViewDTO, T>() {
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
    private final void centerAllColumns(TableColumn<UserViewDTO, ?>... columns) {
        for (TableColumn<UserViewDTO, ?> col : columns) {
            centerCell(col);
        }
    }
    private void loadUserData(){
        List<UserViewDTO>data=userService.getUserByRoleAdmin();
        ObservableList<UserViewDTO>userViewModels= FXCollections.observableArrayList(data);
        userTable.setItems(userViewModels);
    }

    @FXML
    private void addAction(){
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/User/AdminCreateView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Thêm Admin");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }catch (IOException e){
        e.printStackTrace();
    }
    }
    @FXML
    private void refreshTable() {
        loadUserData();
    }
    private void showChangePasswordDialog(UserViewModel user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Đổi mật khẩu");
        dialog.setHeaderText("Đổi mật khẩu cho tài khoản: " + user.getUsername());
        dialog.setContentText("Nhập mật khẩu mới:");
    }
}
