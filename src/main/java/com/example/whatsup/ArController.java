package com.example.whatsup;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ArController implements Initializable {

    @FXML
    public TextArea logTextArea;
    @FXML
    public Label labelAR;

    ServerSocket arSocket;
    int portUsed = 6000;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        while (true) {
            try {
                arSocket = new ServerSocket(portUsed);
                labelAR.setText("AR " + portUsed);
                break;
            } catch (Exception ignored) {
                portUsed++;
            }
        }
        receiveRequests();
    }

    private void receiveRequests() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = arSocket.accept();
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    ARPackage tempPackage = (ARPackage) inputStream.readObject();
                    if (!tempPackage.certificadoEncontrado) {
                        logTextArea.appendText("Solicitud recibida para certificado " + tempPackage.idCertificado + "\n");
                        if (checkLocalAR(tempPackage)) {
                            logTextArea.appendText("Certificado encontrado\n");
                            tempPackage.certificadoEncontrado = true;
                            if (tempPackage.puertoOrigenAR == portUsed || tempPackage.puertoOrigenAR == 0)
                                sendPackage(tempPackage);
                            else {
                                requestToOtherAR(tempPackage); // Reenviar a AR que recibió solicitud original
                            }
                        } else {
                            tempPackage.puertoOrigenAR = portUsed;
                            logTextArea.appendText("Certificado no encontrado\n");
                            requestToOtherAR(tempPackage);
                        }
                    } else {
                        sendPackage(tempPackage);
                    }
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendPackage(ARPackage tempPackage) throws IOException {
        logTextArea.appendText("Enviando solicitud procesada\n\n");
        Socket socket = new Socket("localhost", tempPackage.puertoOrigenCliente);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(tempPackage);
        socket.close();
    }

    private boolean checkLocalAR(ARPackage request) throws IOException {
        File folder = new File("C:\\Users\\migue\\Documents\\WhatsUP\\Certificados\\AR" + portUsed);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().equals(request.idCertificado + "_cer.txt")){
                    File file = new File(".\\Certificados\\AR" + portUsed + "\\" + request.idCertificado + "_cer.txt");
                    FileReader fr = new FileReader(file);

                    BufferedReader br = new BufferedReader(fr);   // creates a buffering character input stream
                    String line;
                    int lineCounter = 0;
                    while ((line = br.readLine()) != null) {
                        if (lineCounter == 1) {
                            request.nombreCertificado = line;
                            System.out.println();
                        }
                        if (lineCounter == 2) {
                            request.llavePublica = Integer.parseInt(line);
                        }
                        lineCounter++;
                    }
                    fr.close(); // closes the stream and release the resources
                    return true;
                }
            }
        }
        return false;
    }

    private void requestToOtherAR(ARPackage tempPackage) throws IOException {
        int portDestination = portUsed == 6000 ? 6001 : 6000;
        logTextArea.appendText("Reenviando solicitud a AR " + portDestination + "\n ");
        Socket socket = new Socket("localhost", portDestination);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(tempPackage);
        socket.close();
    }

    public void clearLog() {
        logTextArea.clear();
    }
}
