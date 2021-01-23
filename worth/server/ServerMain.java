package server;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import utility.*;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        int portRMI_CB = 3000;
        int portTCP = 3002;
        Database DB = new Database();
        UsersDatabase usersDB = new UsersDatabase();
        ConcurrentHashMap<String, String> user_status = new ConcurrentHashMap<>();
        Thread th = null;
        try {
            ServerNotification serverN = new ServerNotification();
            ServerNotificationInterface stub = (ServerNotificationInterface) UnicastRemoteObject.exportObject(serverN,
                    39000);
            LocateRegistry.createRegistry(portRMI_CB);
            Registry reg = LocateRegistry.getRegistry(portRMI_CB);
            reg.bind("ServizioNotifiche", stub);
            new Registrazione(usersDB, user_status, serverN).start();
            System.out.println("Server pronto");
            th = new Thread(new ServerTCP(user_status, serverN, DB, usersDB, portTCP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        th.start();
    }
}
