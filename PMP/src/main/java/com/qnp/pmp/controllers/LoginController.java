package com.qnp.pmp.controllers;
import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    private final UserService userService;
    @FXML
    private Button closeButton;
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
    public void onLoginClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Dialog.displayErrorMessage("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        User user = userService.loginUser(username, password);

        if (user == null) {
            Dialog.displayErrorMessage("Tài khoản không tồn tại.");
            return;
        }

        Dialog.displaySuccessFully("Đăng nhập thành công!");

        try {
            FXMLLoader loader;
            Parent root;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene;

            switch (user.getRole()) {
                case SUPERADMIN:
                    loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/SuperAdmin/Dashboard.fxml"));
                    root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("SuperAdmin Dashboard");
                    enableWindowDragging(stage, root);
                    stage.show();
                    break;

                case ADMIN:
                    loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/AdminDashboard/Dashboard.fxml"));
                    root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Admin Dashboard");
                    enableWindowDragging(stage, root);
                    stage.show();
                    break;
                case USER:
                    loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/UserDashboard/Dashboard.fxml"));
                    root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Admin Dashboard");
                    enableWindowDragging(stage, root);
                    stage.show();
                    break;

                default:
                    Dialog.displayErrorMessage("Vai trò người dùng không hợp lệ.");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Lỗi khi tải giao diện: " + e.getMessage());
        }
    }
    private void enableWindowDragging(Stage stage, Parent root) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

}
