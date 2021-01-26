package server;

import java.util.ArrayList;

import utility.IPGenerator;
import utility.UDPportGenerator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

public class Progetto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nameProject;

    public ArrayList<Card> TODOList;
    public ArrayList<Card> INPROGRESSList;
    public ArrayList<Card> TOBEREVISEDList;
    public ArrayList<Card> DONEList;

    private DatagramSocket socket;
    private String IP;
    private int portUDP;

    public Progetto(String nameProject) {
        this.nameProject = nameProject;

        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            System.out.println("ERROR: Server can't reach chat");
        }

        IP = IPGenerator.generateIPAddress();
        portUDP = UDPportGenerator.generatePortUDP();

        TODOList = new ArrayList<>();
        INPROGRESSList = new ArrayList<>();
        TOBEREVISEDList = new ArrayList<>();
        DONEList = new ArrayList<>();
    }

    public String getName() {
        return nameProject;
    }

    public int getPort() {
        return portUDP;
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
        String message = Message;
        try {
            DatagramPacket datagram = new DatagramPacket(message.getBytes(), message.length(),
                    InetAddress.getByName(this.IP), this.portUDP);
            socket.send(datagram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
