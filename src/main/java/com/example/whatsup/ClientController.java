package com.example.whatsup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    public Button regresarButton;
    @FXML
    public Button plainSendButton;
    @FXML
    public TextField keyValue;
    @FXML
    public Button decoderButton;
    @FXML
    public Button confirmKey;
    @FXML
    public TextArea textArea;
    @FXML
    private Label contactLabel;
    @FXML
    private TextField tfMessage;

    private Client client;
    BusinessLogic data = BusinessLogic.getInstance();

    // Recibir mensajes
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            keyValue.setVisible(false);
            confirmKey.setVisible(false);

            if (data.activeUser.equals("Miguel") && data.choosenContact.equals("Pilar") || data.activeUser.equals("Pilar") && data.choosenContact.equals("Miguel")) {
                textArea.setText(data.readChatFile(1));
            }
            else if (data.activeUser.equals("Miguel") && data.choosenContact.equals("Santiago") || data.activeUser.equals("Santiago") && data.choosenContact.equals("Miguel")) {
                textArea.setText(data.readChatFile(2));
            }
            else if (data.activeUser.equals("Pilar") && data.choosenContact.equals("Santiago") || data.activeUser.equals("Santiago") && data.choosenContact.equals("Pilar")) {
                textArea.setText(data.readChatFile(3));
            }

            contactLabel.setText(data.choosenContact);
            client = new Client(new ServerSocket(data.userPorts.get(data.activeUser)));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        client.receiveMessage(textArea);
    }

    // Enviar mensajes
    public void sendPlainText() {
        client.sendMessage(tfMessage.getText(), textArea);
        tfMessage.clear();
    }

    // Buton para regresar
    public void regresarButtonAction() throws IOException {
        client.serverSocket.close();

        Stage stage = (Stage) regresarButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainClientApp.class.getResource("chooseChat.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("WhatsUp");
        stage.setScene(scene);
        stage.show();
    }

    public void ssButton(ActionEvent event) {
        keyValue.setText("");
        keyValue.setVisible(true);
        confirmKey.setVisible(true);
        data.cypherType = ((Button)event.getSource()).getText();
    }

    public void openDecoder() {
        try {
            data.cypherType = textArea.getSelectedText().substring(0, 6);
            data.detectedEncryptedText = textArea.getSelectedText().substring(7);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("decoderWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("WhatsUp Decoder");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendSSMessage() {
        int encryptionKey = Integer.parseInt(keyValue.getText());
        String message = tfMessage.getText().toLowerCase();
        String alphabet = "abcdefghijklmnñopqrstuvwxyz0123456789¿?áéíóú ";
        StringBuilder encryptedMessage = new StringBuilder();

        if (!message.isEmpty()) {
            for (int i = 0; i < message.length(); i++)
            {
                int pos = alphabet.indexOf(message.charAt(i));

                int encryptPos = (encryptionKey + pos) % 45;
                char encryptChar = alphabet.charAt(encryptPos);

                encryptedMessage.append(encryptChar);
            }
            tfMessage.clear();
            keyValue.setVisible(false);
            confirmKey.setVisible(false);
            client.sendMessage("[" + data.cypherType + "]\n" + encryptedMessage, textArea);
        }
    }
}

// Clase para enviar datos
class PackageSent implements Serializable {
    private String user, message, receiver, timestamp;

    public String getUser() {return user;}
    public void setUser(String user) {this.user = user;}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public String getReceiver() {return receiver;}
    public void setReceiver(String receiver) {this.receiver = receiver;}

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp() {
        this.timestamp = DateTimeFormatter.ofPattern("dd-MM-yyyy  |  HH:mm").format(LocalDateTime.now());
    }
}
