package com.example.whatsup;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream inputPackageStream;
    private ObjectOutputStream outputPackageStream;
    BusinessLogic data = BusinessLogic.getInstance();

    public Server(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
    }

    public void receiveMessageAndResend(TextArea textAreaLog) {
        new Thread(() -> {
            PackageSent inputPackage;
            String user, message, receiver, messageToSave;
            while (true) {
                try {
                    socket = serverSocket.accept();
                    inputPackageStream = new ObjectInputStream(socket.getInputStream());
                    inputPackage = (PackageSent)inputPackageStream.readObject();

                    user = inputPackage.getUser();
                    message = inputPackage.getMessage();
                    receiver = inputPackage.getReceiver();
                    textAreaLog.appendText("DE " + user.toUpperCase() + " PARA " + receiver.toUpperCase() + "\n");
                    textAreaLog.appendText(inputPackage.getTimestamp() + "\n" + message + "\n\n");

                    messageToSave = user.toUpperCase() + ":\n" + "[" + inputPackage.getTimestamp() + "]\n" + message + "\n\n";
                    data.saveChatFile(receiver, user, messageToSave);

                    // Reenviar paquete al cliente destino
                    Socket resendPackage = new Socket("localhost", data.userPorts.get(receiver));
                    outputPackageStream = new ObjectOutputStream(resendPackage.getOutputStream());
                    outputPackageStream.writeObject(inputPackage);

                    closeEverything(socket, inputPackageStream, outputPackageStream);
                } catch (Exception e) {
                    closeEverything(socket, inputPackageStream, outputPackageStream);
                    System.out.println("Usuario desconectado");
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
