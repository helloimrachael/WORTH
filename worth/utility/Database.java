package utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import server.*;

public class Database implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ConcurrentHashMap<String, Progetto> progetti;

    private static ConcurrentHashMap<String, ArrayList<String>> progetti_membri;

    private static ConcurrentHashMap<String, ArrayList<Card>> progetti_card;

    public Database() {
        progetti = new ConcurrentHashMap<String, Progetto>();
        progetti_membri = new ConcurrentHashMap<String, ArrayList<String>>();
        progetti_card = new ConcurrentHashMap<String, ArrayList<Card>>();
    }

    //////// PROGETTI\\\\\\\\
    public ConcurrentHashMap<String, Progetto> getProgetti() {
        return progetti;
    }

    public void addProgetto(Progetto progetto) {
        progetti.putIfAbsent(progetto.getName(), progetto);
    }

    public Progetto getProject(String name) {
        return progetti.get(name);
    }

    public Collection<Progetto> listaProgetti() {
        return progetti.values();
    }
    //////////////// \\\\\\\\\\\\\\\\

    //////// PROGETTI - MEMBRI\\\\\\\\

    public ConcurrentHashMap<String, ArrayList<String>> getProgettiMembri() {
        return progetti_membri;
    }

    public void createProject(String projectName, String username) {
        Progetto prog = new Progetto(projectName);
        addProgetto(prog);
        ArrayList<String> listaMembri = new ArrayList<>();
        listaMembri.add(username);
        progetti_membri.putIfAbsent(projectName, listaMembri);
        ArrayList<Card> cardList = new ArrayList<>();
        progetti_card.putIfAbsent(prog.getName(), cardList);
    }

    public void addMember(String projectName, String username) {
        progetti_membri.get(projectName).add(username);

    }

    public ArrayList<String> showMembers(String projectName) {
        return progetti_membri.get(projectName);
    }

    public boolean isProject(String projectName) {
        if (progetti_membri.containsKey(projectName))
            return true;
        return false;
    }

    public boolean isMember(String projectName, String username) {
        ArrayList<String> listaMembri = progetti_membri.get(projectName);
        if (listaMembri.contains(username)) {
            return true;
        }
        return false;
    }

    public String cancelProject(String projectName) {

        if (getProject(projectName).getTODOList().isEmpty() && getProject(projectName).getINPROGRESSList().isEmpty()
                && getProject(projectName).getTOBEREVISEDList().isEmpty()
                && !getProject(projectName).getDONEList().isEmpty()) {
            UsersDatabase.cancellaProgetto(projectName);
            progetti_card.remove(projectName);
            progetti_membri.remove(projectName);
            return "Progetto " + projectName + " cancellato";
        } else if (getProject(projectName).getTODOList().isEmpty()
                && getProject(projectName).getINPROGRESSList().isEmpty()
                && getProject(projectName).getTOBEREVISEDList().isEmpty()
                && getProject(projectName).getDONEList().isEmpty()) {
            UsersDatabase.cancellaProgetto(projectName);
            progetti_card.remove(projectName);
            progetti_membri.remove(projectName);
            return "Progetto " + projectName + " cancellato";
        }
        return "Operazione negata: esiste almeno una card che non si trova nella lista 'DONE'";
    }
    //////////////// \\\\\\\\\\\\\\\\

    //////// PROGETTI - CARD\\\\\\\\

    public ConcurrentHashMap<String, ArrayList<Card>> getProgettiCard() {
        return progetti_card;
    }

    public boolean checkCard(String projectName, String card) {
        ArrayList<Card> cards = showCards(projectName);
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equals(card)) {
                return true;
            }
        }
        return false;
    }

    public void addCard(String projectName, String cardName, String description) {
        Card card = new Card(cardName, description);
        showCards(projectName).add(card);
    }

    public void updateCard(String projectName, Card c) {
        if (showCards(projectName) == null) {
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(c);
            getProgettiCard().putIfAbsent(projectName, cards);
        }
    }

    public ArrayList<Card> showCards(String projectName) {
        return progetti_card.get(projectName);
    }

    public Card showCard(String projectName, String cardName) {
        ArrayList<Card> listaC = progetti_card.get(projectName);
        for (int i = 0; i < listaC.size(); i++) {
            if (listaC.get(i).getName().equals(cardName)) {
                return listaC.get(i);
            }
        }
        return null;
    }

    public void moveCard(String projectName, String cardName, String listaPartenza, String listaDestinazione) {
        Card c = showCard(projectName, cardName);
        ArrayList<Card> TODOList = getProject(projectName).getTODOList();
        ArrayList<Card> INPROGRESSList = getProject(projectName).getINPROGRESSList();
        ArrayList<Card> TOBEREVISEDList = getProject(projectName).getTOBEREVISEDList();
        ArrayList<Card> DONEList = getProject(projectName).getDONEList();
        if (listaPartenza.equals("TODOList")) {
            TODOList.remove(c);
            INPROGRESSList.add(c);
            c.getHistory().add("INPROGRESS");
        } else if (listaPartenza.equals("INPROGRESSList")) {
            INPROGRESSList.remove(c);
            if (listaDestinazione.equals("TOBEREVISEDList")) {
                TOBEREVISEDList.add(c);
                c.getHistory().add("TOBEREVISED");
            } else if (listaDestinazione.equals("DONEList")) {
                DONEList.add(c);
                c.getHistory().add("DONE");
            }
        } else if (listaPartenza.equals("TOBEREVISEDList")) {
            TOBEREVISEDList.remove(c);
            if (listaDestinazione.equals("DONEList")) {
                DONEList.add(c);
                c.getHistory().add("DONE");
            } else if (listaDestinazione.equals("INPROGRESSList")) {
                INPROGRESSList.add(c);
                c.getHistory().add("INPROGRESS");
            }
        }
    }
}
