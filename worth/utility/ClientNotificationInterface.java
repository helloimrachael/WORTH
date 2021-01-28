package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientNotificationInterface extends Remote {

    public void notifyEvent(String username, String status) throws RemoteException;

}
