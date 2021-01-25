package server;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import utility.*;

public class ServerMain {

    private static int portRMI_CB = 3000;
    private static int portTCP = 3002;

    public static void main(String[] args) throws IOException {

        Database DB = new Database();
        UsersDatabase usersDB = new UsersDatabase();
        ConcurrentHashMap<String, String> user_status = new ConcurrentHashMap<>();
        Thread th = null;
        try {
            ServerNotification serverN = new ServerNotification();
            updateHashmap up = new updateHashmap();
            ServerNotificationInterface stub = (ServerNotificationInterface) UnicastRemoteObject.exportObject(serverN,
                    39000);
            updateHashmapInterface stub_2 = (updateHashmapInterface) UnicastRemoteObject.exportObject(up, 39000);
            LocateRegistry.createRegistry(portRMI_CB);
            LocateRegistry.createRegistry(3003);
            Registry reg = LocateRegistry.getRegistry(portRMI_CB);
            Registry reg_2 = LocateRegistry.getRegistry(3003);
            reg.bind("ServizioNotifiche", stub);
            reg_2.bind("AggiornaHash", stub_2);
            new Registrazione(usersDB, user_status, serverN).start();
            th = new Thread(new ServerTCP(user_status, serverN, up, DB, usersDB, portTCP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        th.start();
    }
}
