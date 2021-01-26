package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrazioneInterface extends Remote {
    public boolean register(String username, String password) throws RemoteException, NullPointerException;
}
