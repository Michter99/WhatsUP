package com.example.whatsup;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    public TextArea textAreaLog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Server server;
        try {
            server = new Server(new ServerSocket(5000));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        textAreaLog.setWrapText(true);
        server.receiveMessageAndResend(textAreaLog);
    }

}
