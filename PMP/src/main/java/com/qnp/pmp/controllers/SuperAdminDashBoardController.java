package com.qnp.pmp.controllers;

import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.UserService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import com.qnp.pmp.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private final UserService userService=new UserServiceImpl();
    private final OfficeService officeService=new OfficerServiceImpl();
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
        menuList.getItems().addAll(
                "📊 Dashboard",
                "🏢 Manager Officer",
                "👤 Manager User",
                "⚙ Settings",
                "🚪 Logout"
        );

        menuList.getSelectionModel().select(0);
        showDefaultDashboard();

        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String viewKey = newVal.replaceAll("[^a-zA-Z ]", "").trim();
            switch (viewKey) {
                case "Dashboard" -> showDefaultDashboard();                 // <— dùng hàm mới
                case "Manager Officer" -> loadView("Officer/OfficerView");
                case "Manager User" -> loadView("User/UserManagementView");
                case "Logout" -> logOut();
                default -> System.out.println("Không xác định menu: " + viewKey);
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
    private void showDefaultDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/qnp/pmp/SuperAdmin/DefaultDashboard.fxml"));
            Parent view = loader.load();

            // set số liệu
            Label lu = (Label) view.lookup("#labelUser");
            Label lo = (Label) view.lookup("#labelOfficer");
            if (lu != null) lu.setText("👤 " + userService.countUserByRoleAdmin() + " người dùng");
            if (lo != null) lo.setText("📁 " + officeService.countOfficers() + " cán bộ");

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
