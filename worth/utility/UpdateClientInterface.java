package utility;

import java.io.IOException;
import java.rmi.Remote;
import java.util.concurrent.ConcurrentHashMap;

public interface UpdateClientInterface extends Remote {

    public void notifyEvent2(String projectName, Chat c, ConcurrentHashMap<String, Utente> users) throws IOException;

    public void notifyEvent3(ConcurrentHashMap<String, Chat> C, ConcurrentHashMap<String, Utente> U) throws IOException;

    public void notifyEvent4(String projectName, ConcurrentHashMap<String, Utente> users) throws IOException;
}
