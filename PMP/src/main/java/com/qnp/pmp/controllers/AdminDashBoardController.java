package com.qnp.pmp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminDashBoardController {
    @FXML
    private ListView<String> menuList;

    @FXML
    private StackPane contentArea;
    @FXML
    public void initialize() {
        menuList.getItems().addAll(
                "📊 Dashboard",
                "👤 Manager User",
                "⚙ Settings",
                "🚪 Logout"
        );
        menuList.getSelectionModel().select(0);
    loadView("UserDashBoard/DefaultDashboard");
        menuList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String viewKey = newValue.replaceAll("[^a-zA-Z ]", "").trim();
                switch (viewKey) {
                    case "Dashboard":
                        loadView("Admin/DefaultDashboard");
                        break;
                    case "Manager User":
                        loadView("UserDashBoard/UserManagementView");
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

            // Nếu view là Region (ví dụ AnchorPane, VBox, BorderPane...), đặt kích thước
            if (view instanceof Region) {
                ((Region) view).setPrefWidth(contentArea.getWidth());
                ((Region) view).setPrefHeight(contentArea.getHeight());
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
