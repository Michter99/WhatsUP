package com.example.whatsup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
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
    public TextField publicKeyValue;
    @FXML
    private Label contactLabel;
    @FXML
    private TextField tfMessage;

    private Client client;
    BusinessLogic data = BusinessLogic.getInstance();
    String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789 ";

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
        stage.setTitle("WhatsUp de " + data.activeUser);
        stage.setScene(scene);
        stage.show();
    }

    public void ssButton(ActionEvent event) {
        keyValue.setText("");
        publicKeyValue.setText("");
        keyValue.setVisible(true);
        data.cypherType = ((Button)event.getSource()).getText();
        confirmKey.setVisible(true);
    }

    public void openDecoder() {
        try {
            data.cypherType = textArea.getSelectedText().substring(0, 6).trim();
            if (data.cypherType.equals("[F.D.]")) {
                String secondPart = textArea.getSelectedText().substring(7).trim();
                int getLineBreakPosition = 0;
                while (secondPart.charAt(getLineBreakPosition) != 10) { // Detectar salto de línea de la firma
                    getLineBreakPosition++;
                }
                data.detectedFD = secondPart.substring(0, getLineBreakPosition).trim();
                data.detectedEncryptedText = textArea.getSelectedText().substring(8 + getLineBreakPosition).trim();
            } else if (data.cypherType.equals("[S.D.]")) {
                String secondPart = textArea.getSelectedText().substring(7).trim();
                int getLineBreakPosition = 0;
                while (secondPart.charAt(getLineBreakPosition) != 10) { // Detectar salto de línea de la firma
                    getLineBreakPosition++;
                }
                data.detectedClaveAleatoriaCifrada = secondPart.substring(0, getLineBreakPosition).trim();////
                String thirdPart = secondPart.substring(getLineBreakPosition).trim();
                int getPipePosition = 0;
                while (thirdPart.charAt(getPipePosition) != 124) {
                    getPipePosition++;
                }
                data.detectedFDCif = thirdPart.substring(0, getPipePosition);
                data.detectedEncryptedMessageSD = thirdPart.substring(getPipePosition + 1);
            } else {
                data.detectedEncryptedText = textArea.getSelectedText().substring(7).trim();
            }
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
        StringBuilder encryptedMessage = new StringBuilder();
        if (!message.isEmpty()) {
            for (int i = 0; i < message.length(); i++)
            {
                int pos = alphabet.indexOf(message.charAt(i));
                int encryptPos = (encryptionKey + pos) % 37;
                char encryptChar = alphabet.charAt(encryptPos);
                encryptedMessage.append(encryptChar);
            }
            tfMessage.clear();
            keyValue.setVisible(false);
            confirmKey.setVisible(false);
            client.sendMessage("[" + data.cypherType + "]\n" + encryptedMessage, textArea);
        }
    }

    public void sendFDMessage() { // Firma digital
        data.cypherType = "F.D.";
        int privateKey = data.activeUserPriv;
        String message = tfMessage.getText().toLowerCase().trim();
        int preResumen = 0;
        StringBuilder firmaDigital = new StringBuilder();

        // Función hash
        for (int i = 0; i < message.length(); i++) {
            preResumen += message.charAt(i) * i;
        }

        String resumen = String.valueOf(preResumen);

        // Cifrar resumen (cifrado sustitución simple)

        for (int i = 0; i < resumen.length(); i++)
        {
            int pos = alphabet.indexOf(resumen.charAt(i));

            int encryptPos = (privateKey + pos) % 37;
            char encryptChar = alphabet.charAt(encryptPos);
            firmaDigital.append(encryptChar);
        }

        tfMessage.clear();
        keyValue.setVisible(false);
        client.sendMessage("[" + data.cypherType + "]\n" + firmaDigital + "\n" + message, textArea);
    }

    public void sendSDMessage() throws IOException { // Sobre digital
        data.cypherType = "S.D.";
        int privateKey = data.activeUserPriv;
        String message = tfMessage.getText().toLowerCase().trim();
        int preResumen = 0;
        StringBuilder firmaDigital = new StringBuilder();

        // Función hash
        for (int i = 0; i < message.length(); i++) {
            preResumen += message.charAt(i) * i;
        }

        String resumen = String.valueOf(preResumen);

        // Cifrar resumen (cifrado sustitución simple)

        for (int i = 0; i < resumen.length(); i++)
        {
            int pos = alphabet.indexOf(resumen.charAt(i));

            int encryptPos = (privateKey + pos) % 37;
            char encryptChar = alphabet.charAt(encryptPos);
            firmaDigital.append(encryptChar);
        }

        tfMessage.clear();
        keyValue.setVisible(false);

        Random random = new Random();
        int claveSimAlet = random.nextInt(37) + 1; // De 1 a 37


        //**** Cifrar documento firmado ****//

        // Cifrar mensaje
        StringBuilder mensajeCif = new StringBuilder();
        for (int i = 0; i < message.length(); i++)
        {
            int pos = alphabet.indexOf(message.charAt(i));

            int encryptPos = (claveSimAlet + pos) % 37;
            char encryptChar = alphabet.charAt(encryptPos);
            mensajeCif.append(encryptChar);
        }

        // Cifrar firmaDigital
        StringBuilder firmaDigitalCif = new StringBuilder();
        for (int i = 0; i < firmaDigital.length(); i++)
        {
            int pos = alphabet.indexOf(firmaDigital.charAt(i));

            int encryptPos = (claveSimAlet + pos) % 37;
            char encryptChar = alphabet.charAt(encryptPos);
            firmaDigitalCif.append(encryptChar);
        }

        // Cifrar claveSimAlet
        String claveSimAletStr = String.valueOf(claveSimAlet);

        //**** Abrir certificado dest ****//
        FileDialog fd = new FileDialog(new JFrame());
        fd.setVisible(true);
        File[] f = fd.getFiles();
        int publicKeyDest = 0;
        if (f.length > 0) {
            File file = new File(fd.getFiles()[0].getAbsolutePath());
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);   // creates a buffering character input stream
            String line;
            int lineCounter = 0;
            while ((line = br.readLine()) != null) {
                if (lineCounter == 2) {
                    publicKeyDest = Integer.parseInt(line);
                }
                lineCounter++;
            }
            fr.close(); // closes the stream and release the resources
        }
        //********************************//

        StringBuilder claveAletCif = new StringBuilder();
        for (int i = 0; i < claveSimAletStr.length(); i++)
        {
            int pos = alphabet.indexOf(claveSimAletStr.charAt(i));

            int encryptPos = (publicKeyDest + pos) % 37;
            char encryptChar = alphabet.charAt(encryptPos);
            claveAletCif.append(encryptChar);
        }

        String sobreDigital = "[" + data.cypherType + "]\n" + claveAletCif + "\n" + firmaDigitalCif + "|" + mensajeCif;
        client.sendMessage(sobreDigital, textArea);
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
