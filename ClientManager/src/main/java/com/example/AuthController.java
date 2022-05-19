package com.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    private Network network;


    @FXML
    TextField loginArea;
    @FXML
    PasswordField passwordField;
    @FXML
    VBox authWindow, regWindow;
    @FXML
    TextField loginReg, nickReg;
    @FXML
    PasswordField  passReg;

    @FXML
    Button buttonTestAuth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network();
    }

    public void tryAuthorization() {
        String login = loginArea.getText();
        int password = passwordField.getText().hashCode();
        passwordField.clear();
        network.tryAuth(login, password);

    }

    public void btnToRegWindowAction(ActionEvent actionEvent) {
        authWindow.setVisible(false);
        regWindow.setVisible(true);
    }

    public void btnBackToAuthWindowAction() {
        loginReg.clear();
        passReg.clear();
        nickReg.clear();
        authWindow.setVisible(true);
        regWindow.setVisible(false);
    }

    @FXML
    public static void switchToMain() throws IOException {
        MainApp.setRoot("/mainStage.fxml");
    }

    public void nextScene() {
        try {
            switchToMain();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnRegistrationAction(ActionEvent actionEvent) {
        String login = loginReg.getText();
        int password = passReg.getText().hashCode();
        String nickName = nickReg.getText();
        network.tryRegistration(login, nickName, password);
        loginReg.clear();
        passReg.clear();
        nickReg.clear();
        authWindow.setVisible(true);
        regWindow.setVisible(false);
    }

    public static void showAlertMsg(String str){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR, str, ButtonType.OK);
            alert.showAndWait();
        });


    }
    public static void showDoneMsg(String str){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, str, ButtonType.OK);
            alert.showAndWait();
        });

    }

}
