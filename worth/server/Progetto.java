package server;

import java.util.ArrayList;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

public class Progetto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nameProject;

    private static ArrayList<Card> TODOList;
    private ArrayList<Card> INPROGRESSList;
    private ArrayList<Card> TOBEREVISEDList;
    private ArrayList<Card> DONEList;

    private DatagramSocket socket;
    private String IP;
    private int port;

    public Progetto(String nameProject) {
        this.nameProject = nameProject;

        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            System.out.println("ERROR: Server can't reach chat");
        }

        TODOList = new ArrayList<>();
        INPROGRESSList = new ArrayList<>();
        TOBEREVISEDList = new ArrayList<>();
        DONEList = new ArrayList<>();

    }

    public String getName() {
        return nameProject;
    }

    public void setPort(int Port) {
        port = Port;
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return IP;
    }

    public ArrayList<Card> getTODOList() {
        return TODOList;
    }

    public ArrayList<Card> getINPROGRESSList() {
        return INPROGRESSList;
    }

    public ArrayList<Card> getTOBEREVISEDList() {
        return TOBEREVISEDList;
    }

    public ArrayList<Card> getDONEList() {
        return DONEList;
    }

    public void sendMessage(String Message) {
        String message = "System: " + Message;
        try {
            DatagramPacket datagram = new DatagramPacket(message.getBytes(), message.length(),
                    InetAddress.getByName(this.IP), this.port);
            socket.send(datagram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
