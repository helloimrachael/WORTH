package server;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import utility.*;

public class CommandHandler {
    public static Chat chat;

    public String command(String cmd, Database db, UsersDatabase usersDB, Chats C, ServerNotification serverN,
            UpdateHashmap up) throws IOException {

        String[] t = cmd.split(" ");
        Arrays.toString(t);
        String username = t[0];

        String buff = "< ";
        try {
            if (cmd.contains("list_projects ")) {
                ArrayList<String> listaProgetti = usersDB.getUser(username).listProjects();
                if (listaProgetti.isEmpty()) {
                    buff = buff + "Operazione negata: non ci sono progetti associati a questo utente ";
                } else {
                    for (int i = 0; i < listaProgetti.size(); i++) {
                        buff = buff + listaProgetti.get(i) + " ";
                    }
                }
            } else if (cmd.contains("create_project ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                usersDB.getUser(username).createProject(projectName, db);
                buff = buff + "Progetto " + projectName + " creato " + db.getProject(projectName).getIP() + " "
                        + db.getProject(projectName).getPort();
                chat = new Chat(projectName, db.getProject(projectName).getIP(), db.getProject(projectName).getPort());
                C.addChat(projectName, chat);
                try {
                    up.updateHashMap(projectName, C.getChat(projectName), usersDB.getUtenti());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (cmd.contains("add_member ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                String newUser = token[3];
                if (db.getProgetti().containsKey(projectName)) {
                    if (db.isMember(projectName, usersDB.getUser(username).getName())) {
                        if (usersDB.checkUsername(newUser)) {
                            db.addMember(projectName, newUser);
                            usersDB.getUser(newUser).addProject(projectName);
                            buff = buff + newUser + " è stato aggiunto alla lista dei membri del progetto "
                                    + projectName;
                            try {
                                up.updateHashMap(projectName, C.getChat(projectName), usersDB.getUtenti());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else
                            buff = buff + "Operazione negata: l'utente specificato non è registrato al servizio. ";
                    } else
                        buff = buff + "Operazione negata: l'utente che ha fatto la richiesta non è membro del progetto";
                } else
                    buff = buff + "Operazione negata: progetto non esistente";
            } else if (cmd.contains("show_members ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                ArrayList<String> listaMembriProgetto = db.showMembers(projectName);
                for (String s : listaMembriProgetto) {
                    buff = buff + s + " ";
                }

            } else if (cmd.contains("show_cards ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                if (db.showCards(projectName).isEmpty()) {
                    buff = buff + "Operazione negata: non ci sono card associate a questo progetto";
                } else {
                    ArrayList<Card> listaCardProgetto = db.showCards(projectName);
                    for (Card c : listaCardProgetto) {
                        buff = buff + c.getName() + " ";
                    }
                }

            } else if (cmd.contains("show_card ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                String cardName = token[3];
                Card c = db.showCard(projectName, cardName);
                if (c == null) {
                    buff = buff + "Operazione negata: la card non è associata al progetto";
                } else {
                    String listCard = null;
                    if (db.getProject(projectName).getTODOList().contains(c)) {
                        listCard = "TODOList";
                    } else if (db.getProject(projectName).getINPROGRESSList().contains(c)) {
                        listCard = "INPROGRESSList";
                    } else if (db.getProject(projectName).getTOBEREVISEDList().contains(c)) {
                        listCard = "TOBEREVISEDList";
                    } else if (db.getProject(projectName).getDONEList().contains(c)) {
                        listCard = "DONEList";
                    }
                    buff = buff + "Nome card: " + c.getName() + ", Descrizione: " + c.getDescription()
                            + ", Lista di appartenenza: " + listCard;
                }

            } else if (cmd.contains("add_card ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                String cardName = token[3];
                String descrizione = "";
                for (int i = 3; i < token.length - 1; i++) {
                    descrizione = descrizione + token[i] + " ";
                }
                descrizione = descrizione + token[token.length - 1];
                if (db.getProgetti().containsKey(projectName)) {
                    db.addCard(projectName, cardName, descrizione);
                    db.getProject(projectName).getTODOList().add(db.showCard(projectName, cardName));
                    buff = buff + "ok";
                } else {
                    buff = buff + "Operazione negata: progetto non esistente";
                }
            } else if (cmd.contains("move_card ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                String cardName = token[3];
                String listaPartenza = token[4];
                String listaDestinazione = token[5];
                db.moveCard(projectName, cardName, listaPartenza, listaDestinazione);
                db.getProject(projectName).sendMessage("Card " + cardName + " spostata nella lista " + listaDestinazione
                        + " da " + usersDB.getUser(username).getName());
                buff = buff + "ok";
            } else if (cmd.contains("get_card_history ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                String cardName = token[3];
                ArrayList<String> history = db.showCard(projectName, cardName).getHistory();
                for (int i = 0; i < history.size(); i++) {
                    buff = buff + history.get(i) + " ";
                }
            } else if (cmd.contains("cancel_project ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[2];
                db.getProject(projectName).sendMessage("System: Chiusura");
                C.getChats().remove(projectName);
                usersDB.getUser(username).listProjects().remove(projectName);
                buff = buff + db.cancelProject(projectName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buff;
    }
}
