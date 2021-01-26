package utility;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

public class Chat implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    private static String projectName;
    private String IP;
    private int portUDP;
    private InetAddress address;

    private ArrayList<String> messagesChat;
    private transient MulticastSocket group;

    public Chat(String project, String IP, int portUDP) {
        projectName = project;
        this.IP = IP;
        this.portUDP = portUDP;

        try {
            this.address = InetAddress.getByName(this.IP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        messagesChat = new ArrayList<>();
    }

    public static String getProjectName() {
        return projectName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public MulticastSocket getMulticast() {
        return this.group;
    }

    public int getPortUDP() {
        return this.portUDP;
    }

    @Override
    public void run() {
        try {
            group = new MulticastSocket(getPortUDP());
            group.joinGroup(address);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (true) {
            byte[] buf = new byte[512];
            try {
                DatagramPacket datagram = new DatagramPacket(buf, 512);
                // group.setSoTimeout(3000);
                getMulticast().receive(datagram);
                String message = new String(datagram.getData(), "US-ASCII");
                if (message.equals("System: Chiusura"))
                    return;
                messagesChat.add(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getMessages() {
        ArrayList<String> newMessages = new ArrayList<>();
        for (String s : messagesChat) {
            newMessages.add(s);
        }
        messagesChat.clear();
        return newMessages;
    }

    public void sendMessage(String username, String message) {
        String newMess = username + ": " + message;
        try {
            DatagramPacket datagram = new DatagramPacket(newMess.getBytes("US-ASCII"), newMess.length(), getAddress(),
                    getPortUDP());
            DatagramSocket ms = new DatagramSocket();
            ms.send(datagram);
            ms.close();
            // group = new MulticastSocket(getPortUDP());
            // group.send(datagram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeChat() {
        group.close();
    }
}
