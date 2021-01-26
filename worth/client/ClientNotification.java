package client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import utility.Chat;
import utility.ClientNotificationInterface;
import utility.Utente;

public class ClientNotification extends RemoteObject implements ClientNotificationInterface {

    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<String, String> user_status;
    private ConcurrentHashMap<String, Chat> project_chat;
    String username;

    public ClientNotification(ConcurrentHashMap<String, String> user_status,
            ConcurrentHashMap<String, Chat> project_chat, String username) {
        super();
        this.user_status = user_status;
        this.project_chat = project_chat;
        this.username = username;
    }

    @Override
    public void notifyEvent(String username, String status) throws RemoteException {
        if (user_status.containsKey(username)) {
            user_status.replace(username, status);
        } else {
            user_status.putIfAbsent(username, status);
        }
    }

    @Override
    public void notifyEvent2(String projectName, Chat c, ConcurrentHashMap<String, Utente> users) throws IOException {
        if (users.get(username).listProjects().contains(projectName)) {
            if (project_chat.isEmpty()) {
                project_chat.putIfAbsent(projectName, c);
            } else if (!project_chat.containsKey(projectName)) {
                project_chat.putIfAbsent(projectName, c);
            }
        }
    }

}
