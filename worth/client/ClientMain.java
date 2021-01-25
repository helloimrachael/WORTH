package client;

import utility.Chat;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ClientMain {

    private static int portRMI_CB = 3000;
    private static int portRMI = 3001;
    private static int portTCP = 3002;
    private static ConcurrentHashMap<String, String> utenti;
    private static ConcurrentHashMap<String, Chat> chatList;

    public static void main(String[] args) {
        try {
            utenti = new ConcurrentHashMap<>();
            chatList = new ConcurrentHashMap<>();
            ClientTCP connectionTCP = new ClientTCP(utenti, chatList, portTCP, portRMI, portRMI_CB);
            connectionTCP.startConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
