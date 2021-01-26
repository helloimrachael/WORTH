package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import utility.Chat;
import utility.ClientNotificationInterface;
import utility.Utente;
import utility.UpdateHashmapInterface;

public class UpdateHashmap extends RemoteServer implements UpdateHashmapInterface {

    private static final long serialVersionUID = 1L;
    private List<ClientNotificationInterface> clients;

    public UpdateHashmap() throws RemoteException {
        super();
        clients = new ArrayList<ClientNotificationInterface>();
    }

    @Override
    public void registerForCB(ClientNotificationInterface clientInterface) throws RemoteException {
        if (!clients.contains(clientInterface)) {
            clients.add(clientInterface);
            System.out.println("< Sistema: nuovo utente registrato al servizio di notifica per le chat");
        }

    }

    @Override
    public void unregisterForCB(ClientNotificationInterface clientInterface) throws RemoteException {
        clients.remove(clientInterface);

    }

    public void updateHashMap(String projectName, Chat chat, ConcurrentHashMap<String, Utente> users) {
        doCallbacks2(projectName, chat, users);
    }

    private synchronized void doCallbacks2(String projectName, Chat c, ConcurrentHashMap<String, Utente> users) {
        Iterator<ClientNotificationInterface> iter = clients.iterator();
        while (iter.hasNext()) {
            ClientNotificationInterface client = (ClientNotificationInterface) iter.next();
            try {
                client.notifyEvent2(projectName, c, users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("< Sistema: aggiornamento chat inviato");
    }

}
