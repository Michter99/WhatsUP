package com.example.whatsup;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class DecoderController implements Initializable {

    @FXML
    public Button descBtn;
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
        if (!data.cypherType.equals("[C.A.]") && !data.cypherType.equals("[C.S.]")) {
            inputText.setText("");
            keyValue.setVisible(false);
            descBtn.setText("Certificado del emisor");
            descBtn.setLayoutX(100);
            descBtn.setPrefWidth(170);
            inputText.setVisible(false);
        }
    }

    public void decodeAction() throws IOException {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789 ";
        String input = inputText.getText().toLowerCase().trim();
        StringBuilder output = new StringBuilder();
        String result = "Llave no verifica";
        int key = (!keyValue.getText().isEmpty()) ? Integer.parseInt(keyValue.getText()) : 0;

        if (data.cypherType.equals("[C.A.]") && key >= 0) {
            for (int i = 0; i < input.length(); i++)
            {
                int pos = alphabet.indexOf(input.charAt(i));
                int decryptPos = (pos - key) % 37;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                output.append(decryptChar);
            }
            result = output.toString();
        } else if (data.cypherType.equals("[C.S.]") && key < 37 && key >= 0) {
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
        } else if (data.cypherType.equals("[F.D.]")) {
            int preResumen = 0;
            input = data.detectedEncryptedText;
            String firmaDigital = data.detectedFD;
            StringBuilder decryptedSignature = new StringBuilder();

            //**** Abrir certificado emisor ****//
            FileDialog fd = new FileDialog(new JFrame());
            fd.setVisible(true);
            File[] f = fd.getFiles();
            key = 0;
            if (f.length > 0) {
                File file = new File(fd.getFiles()[0].getAbsolutePath());
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);   // creates a buffering character input stream
                String line;
                int lineCounter = 0;
                while ((line = br.readLine()) != null) {
                    if (lineCounter == 2) {
                        key = Integer.parseInt(line);
                    }
                    lineCounter++;
                }
                fr.close(); // closes the stream and release the resources
            }
            //********************************//

            // Función hash
            for (int i = 0; i < input.length(); i++) {
                preResumen += input.charAt(i) * i;
            }
            String resumen = String.valueOf(preResumen);

            for (int i = 0; i < firmaDigital.length(); i++)
            {
                int pos = alphabet.indexOf(firmaDigital.charAt(i));
                int decryptPos = (pos - key) % 37;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                decryptedSignature.append(decryptChar);
            }

            result = (decryptedSignature.toString().equals(resumen)) ? "Firma valida" : "Firma no valida";
        } else if (data.cypherType.equals("[S.D.]")) {
            //**** Descifrar clave simétrica aleatoria ****//
            String claveAleatoriaCifrada = data.detectedClaveAleatoriaCifrada;
            int llavePrivadaDestinatario = data.activeUserPriv;

            StringBuilder llaveSimetricaAleatoriaPre = new StringBuilder();

            for (int i = 0; i < claveAleatoriaCifrada.length(); i++)
            {
                int pos = alphabet.indexOf(claveAleatoriaCifrada.charAt(i));
                int decryptPos = (pos - llavePrivadaDestinatario) % 37;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                llaveSimetricaAleatoriaPre.append(decryptChar);
            }

            int llaveSimetricaAleatoria = Integer.parseInt(llaveSimetricaAleatoriaPre.toString());

            //**** Descifrar mensaje firmado ****//
            String mensajeCifradoSD = data.detectedEncryptedMessageSD;
            String firmaDigCifradaSD = data.detectedFDCif;

            StringBuilder mensajeOriginalPre = new StringBuilder();
            StringBuilder firmaDigitalPre = new StringBuilder();

            for (int i = 0; i < mensajeCifradoSD.length(); i++)
            {
                int pos = alphabet.indexOf(mensajeCifradoSD.charAt(i));
                int decryptPos = (pos - llaveSimetricaAleatoria) % 37;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                mensajeOriginalPre.append(decryptChar);
            }

            for (int i = 0; i < firmaDigCifradaSD.length(); i++)
            {
                int pos = alphabet.indexOf(firmaDigCifradaSD.charAt(i));
                int decryptPos = (pos - llaveSimetricaAleatoria) % 37;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                firmaDigitalPre.append(decryptChar);
            }


            //**** Abrir certificado emisor ****//
            FileDialog fd = new FileDialog(new JFrame());
            fd.setVisible(true);
            File[] f = fd.getFiles();
            key = 0;
            if (f.length > 0) {
                File file = new File(fd.getFiles()[0].getAbsolutePath());
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);   // creates a buffering character input stream
                String line;
                int lineCounter = 0;
                while ((line = br.readLine()) != null) {
                    if (lineCounter == 2) {
                        key = Integer.parseInt(line);
                    }
                    lineCounter++;
                }
                fr.close(); // closes the stream and release the resources
            }
            //********************************//


            //**** Validar firma digital ****//
            String mensajeOriginal = mensajeOriginalPre.toString();
            String firmaDigital = firmaDigitalPre.toString();

            int preResumen = 0;
            StringBuilder decryptedSignature = new StringBuilder();

            // Función hash
            for (int i = 0; i < mensajeOriginal.length(); i++) {
                preResumen += mensajeOriginal.charAt(i) * i;
            }
            String resumen = String.valueOf(preResumen);

            for (int i = 0; i < firmaDigital.length(); i++)
            {
                int pos = alphabet.indexOf(firmaDigital.charAt(i));
                int decryptPos = (pos - key) % 37;

                if (decryptPos < 0) {
                    decryptPos = alphabet.length() + decryptPos;
                }
                char decryptChar = alphabet.charAt(decryptPos);

                decryptedSignature.append(decryptChar);
            }

            result = (decryptedSignature.toString().equals(resumen)) ? "Firma valida\n" : "Firma no valida\n";
            result += mensajeOriginal;
        }

        outputText.setText(result);
    }

}
