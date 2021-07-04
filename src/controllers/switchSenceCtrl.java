package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class switchSenceCtrl {
    Stage stage;
    public switchSenceCtrl(Stage stage) {
        this.stage = stage;
    }
    // کنترل صفحات لاگین و ارتباط دادن آن ها به هم
    public void sceneSwitchLogin(String sceneName) throws IOException {
        Parent root = null;
        switch (sceneName){
            case "register":
                root = FXMLLoader.load(getClass().getResource("../view/fxmls/login/RegisterPage.fxml"));
                break;
            case "loginPage":
                root = FXMLLoader.load(getClass().getResource("../view/fxmls/login/LoginPage.fxml"));
                break;
            case "admLogin":
                root = FXMLLoader.load(getClass().getResource("../view/fxmls/login/admLoginPage.fxml"));
                break;

        }

        Scene scene = new Scene(root,800,600);
        stage.setScene(scene);
        stage.show();

    }

    // کنترل صفحات ادمین و ارتباط دادن آن ها به هم
    public void sceneSwitchManagement(String sceneName) throws IOException {
        Parent root = null;
        switch (sceneName){
            case "dashboard":
                root = FXMLLoader.load(getClass().getResource("../view/fxmls/management/mngDashboard.fxml"));
                break;
        }

        Scene scene = new Scene(root,1050,576);
        stage.setScene(scene);
        stage.show();

    }
}
