package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerNotificationInterface extends Remote {

    public void registerForCallBack(ClientNotificationInterface clientInterface) throws RemoteException;

    public void unregisterForCallback(ClientNotificationInterface clientInterface) throws RemoteException;
}
