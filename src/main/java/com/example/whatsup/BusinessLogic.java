package com.example.whatsup;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

// Patrón Singleton para sólo tener una instancia de la clase
public class BusinessLogic {

    private static BusinessLogic INSTANCE;

    public HashMap<String, Integer> userPorts = new HashMap<>();
    public String activeUser = "";
    public int activeUserCert = 0;
    public int activeUserPriv = 0;
    public String choosenContact = "";
    public String cypherType = "";
    public String detectedEncryptedText = "";
    public String detectedFD = "";
    public String detectedClaveAleatoriaCifrada = "";
    public String detectedFDCif = "";
    public String detectedEncryptedMessageSD = "";


    private BusinessLogic() {
        this.userPorts.put("Miguel", 5001);
        this.userPorts.put("Pilar", 5002);
        this.userPorts.put("Santiago", 5003);
    }

    public static BusinessLogic getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BusinessLogic();
        }
        return INSTANCE;
    }

    public String readChatFile(int key) throws IOException {
        return switch (key) {
            case 1 -> Files.readString(Paths.get("chatMiguelPilar.txt"));
            case 2 -> Files.readString(Paths.get("chatMiguelSantiago.txt"));
            case 3 -> Files.readString(Paths.get("chatPilarSantiago.txt"));
            default -> "";
        };
    }

    public void saveChatFile(String sender, String receiver, String content) throws IOException {
        if (sender.equals("Miguel") && receiver.equals("Pilar") || sender.equals("Pilar") && receiver.equals("Miguel")) {
            FileWriter myWriter = new FileWriter("chatMiguelPilar.txt", true);
            myWriter.append(content);
            myWriter.close();
        }
        else if (sender.equals("Miguel") && receiver.equals("Santiago") || sender.equals("Santiago") && receiver.equals("Miguel")) {
            FileWriter myWriter = new FileWriter("chatMiguelSantiago.txt", true);
            myWriter.append(content);
            myWriter.close();
        }
        else if (sender.equals("Pilar") && receiver.equals("Santiago") || sender.equals("Santiago") && receiver.equals("Pilar")) {
            FileWriter myWriter = new FileWriter("chatPilarSantiago.txt", true);
            myWriter.append(content);
            myWriter.close();
        }
    }
}
