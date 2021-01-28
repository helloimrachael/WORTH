package client;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import utility.ClientNotificationInterface;

public class ClientNotification extends RemoteObject implements ClientNotificationInterface {

    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<String, String> user_status;

    public ClientNotification(ConcurrentHashMap<String, String> user_status) {
        super();
        this.user_status = user_status;
    }

    @Override
    public void notifyEvent(String username, String status) throws RemoteException {
        if (user_status.containsKey(username)) {
            user_status.replace(username, status);
        } else {
            user_status.putIfAbsent(username, status);
        }
    }
}
