package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import utility.Chat;
import utility.UpdateClientInterface;
import utility.Utente;
import utility.UpdateHashmapInterface;

public class UpdateHashmap extends RemoteServer implements UpdateHashmapInterface {

    private static final long serialVersionUID = 1L;
    private List<UpdateClientInterface> clients;

    public UpdateHashmap() throws RemoteException {
        super();
        clients = new ArrayList<UpdateClientInterface>();
    }

    @Override
    public void registerForCB(UpdateClientInterface clientInterface) throws RemoteException {
        if (!clients.contains(clientInterface)) {
            clients.add(clientInterface);
            System.out.println("< Sistema: nuovo utente registrato al servizio di notifica per le chat");
        }

    }

    @Override
    public void unregisterForCB(UpdateClientInterface clientInterface) throws RemoteException {
        clients.remove(clientInterface);

    }

    public void updateHashMap(String projectName, Chat chat, ConcurrentHashMap<String, Utente> users) {
        doCallbacks2(projectName, chat, users);
    }

    private synchronized void doCallbacks2(String projectName, Chat c, ConcurrentHashMap<String, Utente> users) {
        Iterator<UpdateClientInterface> iter = clients.iterator();
        while (iter.hasNext()) {
            UpdateClientInterface client = (UpdateClientInterface) iter.next();
            try {
                client.notifyEvent2(projectName, c, users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("< Sistema: aggiornamento chat inviato");
    }

    public void updateAtLogin(ConcurrentHashMap<String, Chat> C, ConcurrentHashMap<String, Utente> U) {
        doCallbacks3(C, U);
    }

    private synchronized void doCallbacks3(ConcurrentHashMap<String, Chat> chats,
            ConcurrentHashMap<String, Utente> users) {
        Iterator<UpdateClientInterface> iter = clients.iterator();
        while (iter.hasNext()) {
            UpdateClientInterface client = (UpdateClientInterface) iter.next();
            try {
                client.notifyEvent3(chats, users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("< Sistema: aggiornamento chat inviato");
    }

    public void updateCancelProject(String projectName, ConcurrentHashMap<String, Utente> users) {
        doCallbacks4(projectName, users);
    }

    private synchronized void doCallbacks4(String projectName, ConcurrentHashMap<String, Utente> users) {
        Iterator<UpdateClientInterface> iter = clients.iterator();
        while (iter.hasNext()) {
            UpdateClientInterface client = (UpdateClientInterface) iter.next();
            try {
                client.notifyEvent4(projectName, users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("< Sistema: aggiornamento chat inviato");
    }

}
