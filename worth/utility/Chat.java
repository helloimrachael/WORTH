package utility;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

public class Chat implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private static String projectName;
    private String IP;
    private int portUDP;
    private InetAddress address;

    private ArrayList<String> messagesChat;
    private transient MulticastSocket group;

    public Chat(String username, String project, String IP, int portUDP) {
        this.username = username;
        projectName = project;
        this.IP = IP;
        this.portUDP = portUDP;

        try {
            this.address = InetAddress.getByName(this.IP);
            group = new MulticastSocket(this.portUDP);
            group.joinGroup(address);
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
                group.receive(datagram);
                String message = new String(datagram.getData(), "US-ASCII");
                if (message.equals("System: Chiusura"))
                    return;
                messagesChat.add(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getMessaes() {
        return messagesChat;
    }

    public void sendMessage(String message) {
        String newMess = username + ": " + message;
        try {
            DatagramPacket datagram = new DatagramPacket(newMess.getBytes("US-ASCII"), newMess.length(), getAddress(),
                    getPortUDP());
            group.send(datagram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeChat() {
        group.close();
    }
}
