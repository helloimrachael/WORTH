package client;

import java.rmi.RemoteException;

import utility.RegistrazioneInterface;

public class RemoteClient {
    public RemoteClient() throws RemoteException {

    }

    public void registerC(String username, String password, RegistrazioneInterface stub) {
        try {
            if (stub.register(username, password) == true) {
                System.out.println("< ok");
                System.out.println(" ");
            } else {
                System.out.println("< utente già registrato");
                System.out.println(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
