package com.project.waragainstbots;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ChooseController {

    @FXML
    private Button easyLevelButton;

    @FXML
    private Button mediumLevelButton;

    @FXML
    private Button hardLevelButton;
    static int level =0;
    @FXML
    void easyLevel(ActionEvent event) {
        easyLevelButton.setStyle("-fx-background-color: #39240c");
        mediumLevelButton.setStyle("-fx-background-color: #6B503B");
        hardLevelButton.setStyle("-fx-background-color: #6B503B");
        level = 1;
    }


    @FXML
    void mediumLevel(ActionEvent event) {
        easyLevelButton.setStyle("-fx-background-color: #6B503B");
        mediumLevelButton.setStyle("-fx-background-color: #39240c");
        hardLevelButton.setStyle("-fx-background-color: #6B503B");
        level = 2;
    }

    @FXML
    void hardLevel(ActionEvent event){
        easyLevelButton.setStyle("-fx-background-color: #6B503B");
        mediumLevelButton.setStyle("-fx-background-color: #6B503B");
        hardLevelButton.setStyle("-fx-background-color: #39240c");
        level = 3;
    }

    @FXML
    void receiveChoice(ActionEvent event) throws IOException {
        if(level!=0){
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/waragainstbots/gamef2-view.fxml"));


            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();

            GameController gc = loader.getController();
            gc.setL(level);
            stage.show();
        }

    }

}
