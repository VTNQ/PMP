package com.qnp.pmp.controllers;

import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminDashBoardController {
    @FXML
    private ListView<String> menuList;

    @FXML
    private AnchorPane contentArea;
    private final UserService userService=new UserServiceImpl();
    private final OfficeService officeService=new OfficerServiceImpl();
    @FXML
    private Label labelUser;
    @FXML
    private Label labelOfficer;
    @FXML
    public void initialize() {
        menuList.getItems().addAll(
                "📊 Dashboard",
                "👤 Manager User",
                "⚙ Settings",
                "🚪 Logout"
        );

        menuList.getSelectionModel().select(0);
        showDefaultDashboard(); // thay cho loadDashboardData() + loadView(...)

        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return;
            String key = newV.replaceAll("[^a-zA-Z ]", "").trim();
            switch (key) {
                case "Dashboard"      -> showDefaultDashboard();
                case "Manager User"   -> loadView("AdminDashboard/UserManagementView");
                case "Logout"         -> logOut();
                default               -> System.out.println("Không xác định menu: " + key);
            }
        });
    }

    private void loadDashboardData(){
        int userCount=userService.countUserByRoleUser();
        int officerCount=officeService.countOfficers();
        labelUser.setText("👤 " + (userCount) + " Người dùng");
        labelOfficer.setText("📁 " + officerCount + " cán bộ");
    }
    private void logOut() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/login/login.fxml"));
            Parent loginRoot = loader.load();
            contentArea.getScene().setRoot(loginRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadView(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/" + viewName + ".fxml"));
            Parent view = loader.load();

            contentArea.getChildren().add(view);

            // Gắn chặt kích thước với contentArea
            if (view instanceof Region) {
                Region region = (Region) view;
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showDefaultDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/qnp/pmp/AdminDashboard/DefaultDashboard.fxml"));
            loader.setController(this);          // dùng chính controller hiện tại
            Parent view = loader.load();         // lúc này labelUser/labelOfficer đã được inject

            if (view instanceof Region r) {
                r.prefWidthProperty().bind(contentArea.widthProperty());
                r.prefHeightProperty().bind(contentArea.heightProperty());
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
