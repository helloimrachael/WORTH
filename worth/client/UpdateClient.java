package client;

import java.io.IOException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import utility.Chat;
import utility.UpdateClientInterface;
import utility.Utente;

public class UpdateClient extends RemoteObject implements UpdateClientInterface {
    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<String, Chat> project_chat;
    String username;

    public UpdateClient(ConcurrentHashMap<String, Chat> project_chat, String username) {
        super();
        this.project_chat = project_chat;
        this.username = username;
    }

    @Override
    public void notifyEvent2(String projectName, Chat c, ConcurrentHashMap<String, Utente> users) throws IOException {
        if (users.get(username).listProjects().contains(projectName)) {
            if (project_chat.isEmpty()) {
                project_chat.putIfAbsent(projectName, c);
                Chat chat = project_chat.get(projectName);
                new Thread(chat).start();
            } else if (!project_chat.containsKey(projectName)) {
                project_chat.putIfAbsent(projectName, c);
                Chat chat = project_chat.get(projectName);
                new Thread(chat).start();
            }
        }

    }

    @Override
    public void notifyEvent3(ConcurrentHashMap<String, Chat> C, ConcurrentHashMap<String, Utente> U)
            throws IOException {
        ArrayList<String> prog = U.get(username).listProjects();
        if (project_chat.isEmpty()) {
            for (int i = 0; i < prog.size(); i++) {
                project_chat.putIfAbsent(prog.get(i), C.get(prog.get(i)));
                Chat chat = project_chat.get(prog.get(i));
                new Thread(chat).start();
            }
        } else {
            for (int i = 0; i < prog.size(); i++) {
                if (!project_chat.containsKey(prog.get(i))) {
                    project_chat.putIfAbsent(prog.get(i), C.get(prog.get(i)));
                    Chat chat = project_chat.get(prog.get(i));
                    new Thread(chat).start();
                }
            }
        }

    }

    @Override
    public void notifyEvent4(String projectName, ConcurrentHashMap<String, Utente> users) throws IOException {
        ArrayList<String> progetti = users.get(username).listProjects();
        for (String prog : project_chat.keySet()) {
            if (!progetti.contains(prog)) {
                project_chat.get(prog).closeChat();
                project_chat.remove(prog);
            }
        }
    }
}
