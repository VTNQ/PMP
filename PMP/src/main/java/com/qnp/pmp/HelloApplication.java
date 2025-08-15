package com.qnp.pmp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/qnp/pmp/login/login.fxml"));
        Parent root = fxml.load();                 // ← đổi StackPane thành Parent
        Scene scene = new Scene(root, 1000, 600);
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    public static void main(String[] args) { launch(); }
}

