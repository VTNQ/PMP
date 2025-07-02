package com.qnp.pmp.controllers;
import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public class LoginController {
    private final UserService userService;
    public LoginController() {
        this.userService = new UserServiceImpl();
    }
    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void onLoginClick(ActionEvent event) {
        String username=usernameField.getText();
        String password=passwordField.getText();
        if(username.isEmpty() || password.isEmpty()){
            Dialog.displayErrorMessage("Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        User user=userService.loginUser(username, password);
        if (user == null) {
            Dialog.displayErrorMessage("Tài khoản không tồn tại.");
        } else {
            Dialog.displaySuccessFully("Đăng nhập thành công!");

        }
    }
}
