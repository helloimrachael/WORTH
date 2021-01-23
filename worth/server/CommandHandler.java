package server;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import utility.*;

public class CommandHandler {

    public String command(String cmd, Utente user, Database db, UsersDatabase usersDB) throws IOException {
        String buff = "< ";
        try {
            if (cmd.startsWith("list_projects ")) {
                ArrayList<String> listaProgetti = user.listProjects();
                if (listaProgetti.isEmpty()) {
                    buff = buff + "Operazione negata: non ci sono progetti associati a questo utente";
                } else {
                    for (int i = 0; i < listaProgetti.size(); i++) {
                        buff = buff + listaProgetti.get(i) + " ";
                    }
                }
            } else if (cmd.startsWith("create_project ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                user.createProject(projectName, db);
                buff = buff + "Progetto " + projectName + " creato";
            } else if (cmd.startsWith("add_member ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                String newUser = token[2];
                if (db.isMember(projectName, user.getName())) {
                    if (usersDB.checkUsername(newUser)) {
                        db.addMember(projectName, newUser);
                        usersDB.getUser(newUser).addProject(projectName);
                        buff = buff + newUser + " è stato aggiunto alla lista dei membri del progetto " + projectName;
                    } else
                        buff = buff + "Operazione negata: l'utente specificato non è registrato al servizio. ";
                } else
                    buff = buff + "Operazione negata: l'utente che ha fatto la richiesta non è membro del progetto";
            } else if (cmd.startsWith("show_member ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                ArrayList<String> listaMembriProgetto = db.showMembers(projectName);
                for (int i = 0; i < listaMembriProgetto.size(); i++) {
                    buff = buff + listaMembriProgetto.get(i) + " ";
                }
            } else if (cmd.startsWith("show_cards ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                if (db.showCards(projectName) == null) {
                    buff = buff + "Operazione negata: non ci sono card associate a questo progetto";
                } else {
                    ArrayList<Card> listaCardProgetto = db.showCards(projectName);
                    for (int i = 0; i < listaCardProgetto.size(); i++) {
                        buff = buff + listaCardProgetto.get(i).getName() + " ";
                    }
                }
            } else if (cmd.startsWith("show_card ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                String cardName = token[2];
                Card c = db.showCard(projectName, cardName);
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
            } else if (cmd.startsWith("add_card ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                String cardName = token[2];
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
            } else if (cmd.startsWith("move_card ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                String cardName = token[2];
                String listaPartenza = token[3];
                String listaDestinazione = token[4];
                db.moveCard(projectName, cardName, listaPartenza, listaDestinazione);
                buff = buff + "ok";
            } else if (cmd.startsWith("get_card_history ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                String cardName = token[2];
                ArrayList<String> history = db.showCard(projectName, cardName).getHistory();
                for (int i = 0; i < history.size(); i++) {
                    buff = buff + history.get(i) + " ";
                }
            } else if (cmd.startsWith("read_chat ")) {
                return null;
            } else if (cmd.startsWith("send_chat_msg ")) {
                return null;
            } else if (cmd.startsWith("cancel_project ")) {
                String[] token = cmd.split(" ");
                Arrays.toString(token);
                String projectName = token[1];
                buff = buff + db.cancelProject(projectName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buff;
    }
}
