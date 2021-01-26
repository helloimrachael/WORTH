package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateHashmapInterface extends Remote {

    public void registerForCB(ClientNotificationInterface clientInterface) throws RemoteException;

    public void unregisterForCB(ClientNotificationInterface clientInterface) throws RemoteException;
}
