package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import utility.RegistrazioneInterface;
import utility.UsersDatabase;
import utility.Utente;

public class Registrazione extends RemoteServer implements RegistrazioneInterface {

    private static final long serialVersionUID = 1L;
    private final int port_RMI = 3001;
    private ConcurrentHashMap<String, String> user_status;
    UsersDatabase usersDB;
    private ServerNotification serverN;

    public Registrazione(UsersDatabase usersDB, ConcurrentHashMap<String, String> user_status,
            ServerNotification serverN) {
        this.user_status = user_status;
        this.serverN = serverN;
        this.usersDB = usersDB;
    }

    public void start() {
        try {
            RegistrazioneInterface stub = (RegistrazioneInterface) UnicastRemoteObject.exportObject(this, 0);
            LocateRegistry.createRegistry(port_RMI);
            Registry reg = LocateRegistry.getRegistry(port_RMI);
            reg.rebind("RegistrazioneUtente", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean register(String username, String password)
            throws RemoteException, NullPointerException {
        synchronized (usersDB) {
            if (usersDB.getUtenti() == null) {
                Utente utente = new Utente(username, password);
                usersDB.addUser(utente);
                user_status.putIfAbsent(username, "Offline");
                serverN.updateDB(user_status);
                System.out.println("SERVER: " + username + " registered");
                return true;
            } else if (!usersDB.checkUsername(username)) {
                Utente utente = new Utente(username, password);
                usersDB.addUser(utente);
                user_status.putIfAbsent(username, "Offline");
                serverN.updateDB(user_status);
                System.out.println("SERVER: " + username + " registered");
                return true;
            }
        }
        System.out.println("SERVER: " + username + " already registered");
        return false;
    }
}
