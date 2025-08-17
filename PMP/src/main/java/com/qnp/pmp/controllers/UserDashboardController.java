package com.qnp.pmp.controllers;

import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class UserDashboardController {
    @FXML
    private ListView<String> menuList;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Label labelOfficer;
    private final OfficeService officeService=new OfficerServiceImpl();
    @FXML
    public void initialize() {
        menuList.getItems().addAll("📊 Dashboard","👨‍💼 Can bo","🚪 Logout");

        menuList.getSelectionModel().select(0);
        showDefaultDashboard(); // nạp dashboard đúng cách

        menuList.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n == null) return;
            String key = n.replaceAll("[^a-zA-Z ]", "").trim();
            switch (key) {
                case "Dashboard" -> showDefaultDashboard();
                case "Can bo"    -> loadView("Officer/OfficerUserView");
                case "Logout"    -> logOut();
                default          -> System.out.println("Không xác định menu: " + key);
            }
        });
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
    private void loadDashboardData() {
        int countOfficer= officeService.countOfficers();
        labelOfficer.setText("📁 " + countOfficer + " cán bộ");
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

    private void showDefaultDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/qnp/pmp/UserDashboard/DefaultDashboard.fxml"));
            loader.setController(this);                 // dùng chính controller hiện tại
            Parent view = loader.load();               // lúc này labelOfficer đã được inject

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
