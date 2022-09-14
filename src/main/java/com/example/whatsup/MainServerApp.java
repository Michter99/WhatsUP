package com.example.whatsup;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainServerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainServerApp.class.getResource("serverWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("WhatsUp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
