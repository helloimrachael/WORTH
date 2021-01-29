package client;

import java.rmi.RemoteException;

import utility.RegistrazioneInterface;

public class RemoteClient {
    public RemoteClient() throws RemoteException {

    }

    public String registerC(String username, String password, RegistrazioneInterface stub) {
        try {
            if (stub.register(username, password) == true) {
                return "< ok";
                // System.out.println(" ");
            } else {
                return "< Utente già registrato";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
