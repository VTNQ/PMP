package com.qnp.pmp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SuperAdminDashBoardController {
    @FXML private Button maximizeButton;

    private double xOffset, yOffset;

    private Stage stage() {
        return (Stage) contentArea.getScene().getWindow();
    }

    /* ===== Nút cửa sổ ===== */
    @FXML
    private void onMinimizeClick() { stage().setIconified(true); }

    @FXML
    private void onMaximizeRestoreClick() {
        Stage s = stage();
        boolean after = !s.isMaximized();
        s.setMaximized(after);
        if (maximizeButton != null) maximizeButton.setText(after ? "🗗" : "⬜");
    }

    @FXML
    private void onCloseClick() { stage().close(); }

    /* ===== Kéo cửa sổ trên titlebar ===== */
    @FXML
    private void onTitleBarMousePressed(javafx.scene.input.MouseEvent e) {
        if (stage().isMaximized()) return;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void onTitleBarMouseDragged(javafx.scene.input.MouseEvent e) {
        Stage s = stage();
        if (s.isMaximized()) return;
        s.setX(e.getScreenX() - xOffset);
        s.setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void onTitleBarMouseClicked(javafx.scene.input.MouseEvent e) {
        if (e.getClickCount() == 2) onMaximizeRestoreClick();
    }

    @FXML
    private ListView<String> menuList;

    @FXML

    private AnchorPane contentArea;


    @FXML
    public void initialize() {
        // Thêm các mục vào menu
        menuList.getItems().addAll(
                "📊 Dashboard",
                "🏢 Manager Officer",
                "👤 Manager User",
                "⚙ Settings",
                "🚪 Logout"
        );

        // Mặc định chọn Dashboard
        menuList.getSelectionModel().select(0);
        loadView("SuperAdmin/DefaultDashboard");

        // Xử lý khi thay đổi menu
        menuList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String viewKey = newValue.replaceAll("[^a-zA-Z ]", "").trim();
                switch (viewKey) {
                    case "Dashboard":
                        loadView("SuperAdmin/DefaultDashboard");
                        break;
                    case "Manager Officer":
                        loadView("Officer/OfficerView");
                        break;
                    case "Manager User":
                        loadView("User/UserManagementView");
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

    /**
     * Load một view FXML vào contentArea và đặt lại kích thước nếu cần.
     */
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

    /**
     * Chuyển về màn hình đăng nhập.
     */
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
