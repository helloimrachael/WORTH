package server;

import java.io.Serializable;
import java.util.ArrayList;

public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private static ArrayList<String> cardHistory;

    public Card(String name, String description) {
        this.name = name;
        this.description = description;
        cardHistory = new ArrayList<>();
        cardHistory.add("TODO");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getHistory() {
        return cardHistory;
    }

    public static String lastStatus(ArrayList<String> cardH) {
        return cardH.get(cardH.size() - 1);
    }

}
