package com.example.whatsup;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AskUserController implements Initializable {

    public Button usuarioButton;
    public ChoiceBox<String> nombreUsuario;
    BusinessLogic data;

    public void userButtonAction() throws IOException {
        Stage stage = (Stage) usuarioButton.getScene().getWindow();
        data.activeUser = "Miguel";
        data.activeUser = nombreUsuario.getValue();
        FXMLLoader fxmlLoader = new FXMLLoader(MainClientApp.class.getResource("filesWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("WhatsUp de " + data.activeUser);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        data = BusinessLogic.getInstance();
        nombreUsuario.setStyle("-fx-font: 13px \"Century Gothic\";");
        nombreUsuario.getItems().addAll("Miguel", "Pilar", "Santiago");
    }
}
