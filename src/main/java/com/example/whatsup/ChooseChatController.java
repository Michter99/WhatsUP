package com.example.whatsup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseChatController implements Initializable {

    BusinessLogic data = BusinessLogic.getInstance();

    @FXML
    private Button clientXButton;

    @FXML
    private Button clientYButton;

    @FXML
    private Button logOutButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switch (data.activeUser) {
            case "Miguel" -> {
                clientXButton.setText("Pilar");
                clientYButton.setText("Santiago");
            }
            case "Pilar" -> {
                clientXButton.setText("Miguel");
                clientYButton.setText("Santiago");
            }
            case "Santiago" -> {
                clientXButton.setText("Miguel");
                clientYButton.setText("Pilar");
            }
        }
    }

    public void clientXAction() throws IOException {
        Stage stage = (Stage) clientXButton.getScene().getWindow();
        data.choosenContact = clientXButton.getText();
        FXMLLoader fxmlLoader = new FXMLLoader(MainClientApp.class.getResource("chatWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("WhatsUp de " + data.activeUser);
        stage.setScene(scene);
        stage.show();
    }

    public void clientYAction() throws IOException {
        Stage stage = (Stage) clientYButton.getScene().getWindow();
        data.choosenContact = clientYButton.getText();
        FXMLLoader fxmlLoader = new FXMLLoader(MainClientApp.class.getResource("chatWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("WhatsUp de " + data.activeUser);
        stage.setScene(scene);
        stage.show();
    }

    public void logoutButton() throws IOException {
        Stage stage = (Stage)  logOutButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainClientApp.class.getResource("askUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("WhatsUp");
        stage.setScene(scene);
        stage.show();
    }
}
