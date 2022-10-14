package com.example.whatsup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.FileDialog;
import java.net.URL;
import java.util.ResourceBundle;

public class FilesController implements Initializable {

    @FXML
    public Button nextWBtn;
    public TextField certTxt;
    public TextField keyTxt;
    public Label labelTxt;

    BusinessLogic data = BusinessLogic.getInstance();

    @FXML
    void openExplorer(ActionEvent event) throws IOException {
        String documentSelected = ((Button)event.getSource()).getText();
        if (documentSelected.equals("Selecciona tu certificado")) {
            FileDialog fd = new FileDialog(new JFrame());
            fd.setVisible(true);
            File[] f = fd.getFiles();
            certTxt.setText(fd.getFiles()[0].getName());
            if (f.length > 0) {
                File file = new File(fd.getFiles()[0].getAbsolutePath());
                FileReader fr = new FileReader(file);
                BufferedReader br=new BufferedReader(fr);   // creates a buffering character input stream
                String line;
                int lineCounter = 0;
                while((line=br.readLine())!=null) {
                    if (lineCounter == 0) {
                        data.activeUserCert = Integer.parseInt(line);
                        lineCounter++;
                    }
                }
                fr.close(); // closes the stream and release the resources
            }
        } else {
            FileDialog fd = new FileDialog(new JFrame());
            fd.setVisible(true);
            File[] f = fd.getFiles();
            keyTxt.setText(fd.getFiles()[0].getName());
            if (f.length > 0) {
                File file = new File(fd.getFiles()[0].getAbsolutePath());
                FileReader fr = new FileReader(file);
                BufferedReader br=new BufferedReader(fr);   // creates a buffering character input stream
                String line;
                int lineCounter = 0;
                while((line=br.readLine())!=null) {
                    if (lineCounter == 0) {
                        data.activeUserPriv = Integer.parseInt(line);
                        lineCounter++;
                    }
                }
                fr.close(); // closes the stream and release the resources
            }
        }
    }

    @FXML
    public void nextWindow() throws IOException {
        if (keyTxt.getText().equals("") || certTxt.getText().equals("")) {
            labelTxt.setText("¡Debe seleccionar un archivo!");
            labelTxt.setLayoutX(27);
        } else {
            Stage stage = (Stage) nextWBtn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainClientApp.class.getResource("chooseChat.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("WhatsUp de " + data.activeUser);
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelTxt.setLayoutX(67);
    }
}
