package utility;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public interface ClientNotificationInterface extends Remote {

    public void notifyEvent(String username, String status) throws RemoteException;

    public void notifyEvent2(String projectName, Chat c, ConcurrentHashMap<String, Utente> users) throws IOException;
}
