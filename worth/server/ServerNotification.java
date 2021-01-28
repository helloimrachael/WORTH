package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import utility.ClientNotificationInterface;
import utility.ServerNotificationInterface;

public class ServerNotification extends RemoteServer implements ServerNotificationInterface {
    private static final long serialVersionUID = 1L;
    private List<ClientNotificationInterface> clients;

    public ServerNotification() throws RemoteException {
        super();
        clients = new ArrayList<ClientNotificationInterface>();
    }

    @Override
    public synchronized void registerForCallBack(ClientNotificationInterface clientInterface) throws RemoteException {
        if (!clients.contains(clientInterface)) {
            clients.add(clientInterface);
            System.out.println("< Sistema: nuovo utente registrato al servizio di notifica");
        }
    }

    @Override
    public synchronized void unregisterForCallback(ClientNotificationInterface clientInterface) {
        clients.remove(clientInterface);
    }

    public void updateDB(ConcurrentHashMap<String, String> user_status) {
        doCallbacks(user_status);
    }

    private synchronized void doCallbacks(ConcurrentHashMap<String, String> user_status) {
        Iterator<ClientNotificationInterface> iter = clients.iterator();
        String username;
        String status;
        while (iter.hasNext()) {
            ClientNotificationInterface c = (ClientNotificationInterface) iter.next();
            try {
                for (String u : user_status.keySet()) {
                    username = u;
                    status = user_status.get(u);
                    c.notifyEvent(username, status);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        System.out.println("< Sistema: aggiornamento inviato");
    }

}
