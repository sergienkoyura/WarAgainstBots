package com.project.waragainstbots;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/waragainstbots/start-view.fxml")));
        Scene startScene = new Scene(root);
        stage.setTitle("Війна з ботами");
        stage.setScene(startScene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}