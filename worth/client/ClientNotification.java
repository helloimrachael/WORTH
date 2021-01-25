package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import utility.Chat;
import utility.ClientNotificationInterface;

public class ClientNotification extends RemoteObject implements ClientNotificationInterface {

    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<String, String> user_status;
    private ConcurrentHashMap<String, Chat> project_chat;

    public ClientNotification(ConcurrentHashMap<String, String> user_status,
            ConcurrentHashMap<String, Chat> project_chat) {
        super();
        this.user_status = user_status;
        this.project_chat = project_chat;
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
    public void notifyEvent2(String projectName, Chat c, ArrayList<String> listProjects) throws IOException {
        MulticastSocket group = c.getMulticast();
        if (group == null) {
            group = new MulticastSocket(c.getPortUDP());
        }
        if (listProjects.contains(projectName)) {
            if (!project_chat.containsKey(projectName)) {
                project_chat.putIfAbsent(projectName, c);
                InetAddress address = c.getAddress();
                group.joinGroup(address);
                new Thread(c).start();
            }
        }
    }

}
