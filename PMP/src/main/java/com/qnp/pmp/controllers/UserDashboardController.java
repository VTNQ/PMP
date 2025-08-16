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
            menuList.getItems().addAll(
                    "📊 Dashboard",
                    "👨‍💼 Can bo",
                    "🚪 Logout"
            );
            loadDashboardData();
        menuList.getSelectionModel().select(0);
        loadView("UserDashboard/DefaultDashboard");
        menuList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String viewKey = newValue.replaceAll("[^a-zA-Z ]", "").trim();
                switch (viewKey) {
                    case "Dashboard":
                        loadView("UserDashboard/DefaultDashboard");
                        break;
                    case "Can bo":  // "👨‍💼 Cán bộ" sau khi loại emoji và dấu
                        loadView("Officer/OfficerUserView");
                        break;
                    case "Logout":
                        logOut();
                        break;
                    default:
                        System.out.println("Không xác định menu: " + viewKey);
                        break;
                }
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
}
