package com.qnp.pmp.controllers;


import com.qnp.pmp.Enum.RoleUser;
import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccountController {
    private UserService userService;
    public AccountController() {
        userService=new UserServiceImpl();
    }
    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<RoleUser> roleComboBox;
    @FXML private Button createButton;
    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll(RoleUser.USER, RoleUser.ADMIN);
        roleComboBox.setValue(RoleUser.USER);
    }


    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String fullName = fullNameField.getText().trim();
        RoleUser role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            Dialog.displayErrorMessage( "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setRole(role);
            userService.saveUser(user);
            Dialog.displaySuccessFully("Tạo tài khoản thành công");
            usernameField.clear();
            passwordField.clear();
            fullNameField.clear();
            roleComboBox.setValue(RoleUser.USER);
        }catch (Exception e) {
            Dialog.displayErrorMessage( "Tạo tài khoản bị lỗi");
        }
    }
}
