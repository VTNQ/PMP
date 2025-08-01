package com.qnp.pmp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    // Lưu tọa độ chuột để kéo cửa sổ
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/qnp/pmp/login/login.fxml"));
        StackPane root = fxmlLoader.load();

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Đăng nhập");
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        stage.setResizable(true);

        // Sự kiện nhấn chuột
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // Sự kiện kéo chuột
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
