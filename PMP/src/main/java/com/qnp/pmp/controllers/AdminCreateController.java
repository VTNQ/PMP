package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class AdminCreateController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField fullNameField;
    @FXML private Button createButton;
    private final UserService userService = new UserServiceImpl();
    @FXML
    private void  handleCreateAccount(){
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String fullName = fullNameField.getText().trim();


        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            com.qnp.pmp.dialog.Dialog.displayErrorMessage( "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            userService.createAdmin(user);
            com.qnp.pmp.dialog.Dialog.displaySuccessFully("Tạo tài khoản thành công");
            usernameField.clear();
            passwordField.clear();
            fullNameField.clear();

        }catch (Exception e) {
            Dialog.displayErrorMessage( "Tạo tài khoản bị lỗi");
        }
    }
}
