package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utility.Chat;
import utility.ClientNotificationInterface;
import utility.updateHashmapInterface;

public class updateHashmap extends RemoteServer implements updateHashmapInterface {

    private static final long serialVersionUID = 1L;
    private List<ClientNotificationInterface> clients;

    public updateHashmap() throws RemoteException {
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

    public void updateHashMap(String projectName, Chat chat, ArrayList<String> listProjects) {
        doCallbacks2(projectName, chat, listProjects);
    }

    private synchronized void doCallbacks2(String projectName, Chat c, ArrayList<String> listProjects) {
        Iterator<ClientNotificationInterface> iter = clients.iterator();
        while (iter.hasNext()) {
            ClientNotificationInterface client = (ClientNotificationInterface) iter.next();
            try {
                client.notifyEvent2(projectName, c, listProjects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("< Sistema: aggiornamento chat inviato");
    }

}
