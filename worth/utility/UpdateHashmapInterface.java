package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateHashmapInterface extends Remote {

    public void registerForCB(UpdateClientInterface clientInterface) throws RemoteException;

    public void unregisterForCB(UpdateClientInterface clientInterface) throws RemoteException;
}
