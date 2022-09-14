package com.example.whatsup;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DecoderController implements Initializable {

    @FXML
    private TextArea inputText;

    @FXML
    private TextField keyValue;

    @FXML
    private TextArea outputText;

    BusinessLogic data = BusinessLogic.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputText.setText(data.detectedEncryptedText);
    }

    public void decodeAction() {
        String alphabet = "abcdefghijklmnñopqrstuvwxyz0123456789¿?áéíóú ";
        String input = inputText.getText().toLowerCase();
        StringBuilder output = new StringBuilder();
        String result = "Llave inválida";
        int key = (!keyValue.getText().isEmpty()) ? Integer.parseInt(keyValue.getText()) : 0;

        if (data.cypherType.equals("[C.A.]") && key >= 0) {
            for (int i = 0; i < input.length(); i++)
            {
                int pos = alphabet.indexOf(input.charAt(i));
                int decryptPos = (pos - key) % 45;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                output.append(decryptChar);
            }
            result = output.toString();
        } else if (data.cypherType.equals("[C.S.]") && key < 45 && key >= 0) {
            for (int i = 0; i < input.length(); i++)
            {
                int pos = alphabet.indexOf(input.charAt(i));
                int decryptPos = (pos - key);

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                output.append(decryptChar);
            }
            result = output.toString();
        }

        outputText.setText(result);
    }

}
