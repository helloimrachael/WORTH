package utility;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class Chats implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ConcurrentHashMap<String, Chat> chats;

    public Chats() {
        chats = new ConcurrentHashMap<>();
    }

    public void addChat(String projectName, Chat c) {
        chats.putIfAbsent(projectName, c);
    }

    public Chat getChat(String projectName) {
        return chats.get(projectName);
    }

    public ConcurrentHashMap<String, Chat> getChats() {
        return chats;
    }

}
