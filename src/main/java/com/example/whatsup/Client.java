package com.example.whatsup;

import javafx.scene.control.TextArea;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Client {

    public final ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream inputPackageStream;
    BusinessLogic data = BusinessLogic.getInstance();

    public Client(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void receiveMessage(TextArea textAreaLog) {
        new Thread(() -> {
            PackageSent inputPackage;
            String user, message;
            while (true) {
                try {
                    socket = serverSocket.accept();
                    inputPackageStream = new ObjectInputStream(socket.getInputStream());
                    inputPackage = (PackageSent) inputPackageStream.readObject();

                    user = inputPackage.getUser();
                    message = inputPackage.getMessage();

                    if (data.choosenContact.equals(user)) {
                        textAreaLog.appendText(user.toUpperCase() + ":\n");
                        textAreaLog.appendText("[" + inputPackage.getTimestamp() + "]\n");
                        textAreaLog.appendText(message + "\n\n");
                    }

                    inputPackageStream.close();
                    socket.close();
                } catch (SocketException ex) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(String message, TextArea textArea) {

        if (!message.isEmpty()) {
            String activeUser = data.activeUser;
            String receiver = data.choosenContact;

            PackageSent data = new PackageSent();

            data.setMessage(message);
            data.setUser(activeUser);
            data.setReceiver(receiver);
            data.setTimestamp();

            textArea.setWrapText(true);
            textArea.appendText(activeUser.toUpperCase() + ":\n");
            textArea.appendText("[" + data.getTimestamp() + "]\n");
            textArea.appendText(message + "\n\n");

            try {
                Socket mySocket = new Socket("localhost", 5000);

                ObjectOutputStream outputPackage = new ObjectOutputStream(mySocket.getOutputStream());
                outputPackage.writeObject(data);

                mySocket.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
