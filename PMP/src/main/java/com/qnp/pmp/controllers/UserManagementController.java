package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.UserViewModel;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class UserManagementController {
    private final UserService userService;
    public UserManagementController() {
        userService=new UserServiceImpl();
    }
    @FXML
    private TableView<UserViewModel>userTable;
    @FXML
    private TableColumn<UserViewModel,String>usernameCol;
    @FXML
    private TableColumn<UserViewModel,String>statusCol;
    @FXML
    private TableColumn<UserViewModel,Void>actionsCol;
    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(cell -> cell.getValue().usernameProperty());
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());
        setupActionsColumn();

    }
    private void loadUserData(){
        ObservableList<UserViewModel>userViewModels= FXCollections.observableArrayList(userService.getUserByRoleUser());
        userTable.setItems(userViewModels);
    }
    private void setupActionsColumn(){
        actionsCol.setCellFactory(col->new TableCell<>(){
            private final Button changePasswordBtn = new Button("🔐");
            {
                changePasswordBtn.setOnAction(event -> {
                    UserViewModel user = getTableView().getItems().get(getIndex());
                    showChangePasswordDialog(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, changePasswordBtn);
                    setGraphic(box);
                }
            }
        });
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
