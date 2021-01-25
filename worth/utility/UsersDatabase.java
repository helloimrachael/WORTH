package utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class UsersDatabase implements Serializable {

    private static final long serialVersionUID = 1L;
    private static ConcurrentHashMap<String, Utente> utenti;

    public UsersDatabase() {
        utenti = new ConcurrentHashMap<String, Utente>();
    }

    public ConcurrentHashMap<String, Utente> getUtenti() {
        return utenti;
    }

    public void addUser(Utente utente) {
        utenti.putIfAbsent(utente.getName(), utente);
    }

    public Utente getUser(String name) {
        return utenti.get(name);
    }

    public ArrayList<String> listUsers() {
        ArrayList<String> listaUtenti = new ArrayList<>();
        for (String u : utenti.keySet()) {
            listaUtenti.add(u.toString() + " " + utenti.get(u).getStatus());
        }
        return listaUtenti;
    }

    public void setAllOffline() {
        for (Utente utente : utenti.values()) {
            utente.setOFFLINE();
        }
    }

    public boolean checkUsername(String name) {
        if (utenti.containsKey(name)) {
            return true;
        }
        return false;
    }

    public ArrayList<String> listOnlineusers() {
        ArrayList<String> nickUtenteList = new ArrayList<>();
        for (Utente utente : utenti.values()) {
            if (utente.getStatus() == "Online") {
                nickUtenteList.add(utente.getName());
            }
        }
        return (ArrayList<String>) nickUtenteList;
    }

    public void updateDB(UsersDatabase newDB) {
        utenti = newDB.getUtenti();
    }
}
