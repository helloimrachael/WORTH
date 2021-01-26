package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import utility.*;

public class ClientTCP {
    private ConcurrentHashMap<String, String> user_status;
    private ConcurrentHashMap<String, Chat> chatList;
    private int portTCP;
    private int portRMI;
    private int portRMI_CB;
    private boolean logIN;

    public ClientTCP(ConcurrentHashMap<String, String> user_status, ConcurrentHashMap<String, Chat> chatList,
            int portTCP, int portRMI, int portRMI_CB) {
        this.user_status = user_status;
        this.chatList = chatList;
        this.portTCP = portTCP;
        this.portRMI = portRMI;
        this.portRMI_CB = portRMI_CB;
        logIN = false;
    }

    public void startConnection() throws IOException {

        boolean connesso = false;
        Scanner scanner = new Scanner(System.in);
        ServerNotificationInterface server = null;
        ClientNotificationInterface clientCB = null;
        ClientNotificationInterface stub = null;
        RegistrazioneInterface r = null;
        UpdateHashmapInterface up = null;
        String cmd;
        String username = null;
        String password;
        String cmdCompleto = "";
        boolean effettuato = false;
        boolean cmdTrovato = false;
        Boolean alreadyConnect = false;
        boolean notSend = false;
        boolean registrazione = true;

        System.out.println("WELCOME TO WORTH");
        System.out.println(" ");
        System.out.println("Per poter accedere al servizio effettuare registrazione e login.");
        System.out.println(" ");
        System.out.println("Digitare 'login' o 'register' per effettuare una delle due operazioni.");
        System.out.println(" ");
        System.out.println("NB: Se siete già registrati basta effettuare il login con l'apposito comando.");
        System.out.println(" ");
        System.out.println("Per sapere quali operazioni offre il servizio digitare 'Help'");
        System.out.println(" ");

        boolean right = false;
        cmd = scanner.nextLine();
        while (!right) {
            if (!cmd.equals("register") && !cmd.equals("login")) {
                System.out.println(" ");
                System.out.println("Digitare 'login' o 'register' per effettuare una delle due operazioni.");
                System.out.println(" ");
                cmd = scanner.nextLine();
            } else {
                right = true;
            }
        }
        while (true) {

            if (cmd.startsWith("register")) {
                System.out.println(" ");
                System.out.println("< Inserire username");
                username = scanner.nextLine();
                System.out.println(" ");
                System.out.println("< Inserire password");
                password = scanner.nextLine();
                System.out.println(" ");

                while (!connesso) {
                    try {
                        Registry reg = LocateRegistry.getRegistry(portRMI);
                        r = (RegistrazioneInterface) reg.lookup("RegistrazioneUtente");
                        Registry reg_2 = LocateRegistry.getRegistry(portRMI_CB);
                        server = (ServerNotificationInterface) reg_2.lookup("ServizioNotifiche");
                        Registry reg_3 = LocateRegistry.getRegistry(3003);
                        up = (UpdateHashmapInterface) reg_3.lookup("AggiornaHash");
                        clientCB = new ClientNotification(user_status, chatList, username);
                        stub = (ClientNotificationInterface) UnicastRemoteObject.exportObject(clientCB, 0);
                        connesso = true;
                    } catch (RemoteException | NotBoundException e) {
                        e.getSuppressed();
                        registrazione = false;
                        System.out.println("Servizio non disponibile");
                        System.out.println(" ");
                        System.out.println("Digitare SI per ritentare la connessione al servizio oppure NO per uscire");
                        System.out.println(" ");
                        String digit = scanner.nextLine();
                        if (digit.startsWith("SI")) {
                            try {
                                System.out.println(" ");
                                System.out.println("Attendere prego...");
                                System.out.println(" ");
                                Thread.sleep(3000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } else if (digit.startsWith("NO")) {
                            scanner.close();
                            return;
                        }
                    }
                }
                if (!registrazione) {
                    System.out.println("< Digitare");
                    System.out.println(" ");
                    System.out.println("< Inserire username");
                    username = scanner.nextLine();
                    System.out.println(" ");
                    System.out.println("< Inserire password");
                    password = scanner.nextLine();
                    System.out.println(" ");
                }
                RemoteClient remote = new RemoteClient();
                String mes = remote.registerC(username, password, r);
                System.out.println(mes);
                if (!mes.equals("< utente già registrato")) {
                    up.registerForCB(stub);
                    System.out.println(" ");
                    cmd = scanner.nextLine();
                    break;
                }
                System.out.println(" ");
                cmd = scanner.nextLine();
            } else if (cmd.equals("login")) {
                while (!connesso) {
                    try {
                        Registry reg = LocateRegistry.getRegistry(portRMI);
                        r = (RegistrazioneInterface) reg.lookup("RegistrazioneUtente");
                        Registry reg_2 = LocateRegistry.getRegistry(portRMI_CB);
                        server = (ServerNotificationInterface) reg_2.lookup("ServizioNotifiche");
                        Registry reg_3 = LocateRegistry.getRegistry(3003);
                        up = (UpdateHashmapInterface) reg_3.lookup("AggiornaHash");
                        clientCB = new ClientNotification(user_status, chatList, username);
                        stub = (ClientNotificationInterface) UnicastRemoteObject.exportObject(clientCB, 0);
                        connesso = true;
                    } catch (RemoteException | NotBoundException e) {
                        e.getSuppressed();
                        System.out.println("Servizio non disponibile");
                        System.out.println(" ");
                        System.out.println("Digitare SI per ritentare la connessione al servizio oppure NO per uscire");
                        System.out.println(" ");
                        String digit = scanner.nextLine();
                        if (digit.startsWith("SI")) {
                            try {
                                System.out.println(" ");
                                System.out.println("Attendere prego...");
                                System.out.println(" ");
                                Thread.sleep(3000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } else if (digit.startsWith("NO")) {
                            scanner.close();
                            return;
                        }
                    }
                }
                System.out.println(" ");
                System.out.println("< Inserire username");
                username = scanner.nextLine();
                System.out.println(" ");
                System.out.println("< Inserire password");
                password = scanner.nextLine();
                cmdCompleto = "login " + username + " " + password;
                server.registerForCallBack(stub);
                effettuato = true;
                cmdTrovato = true;
                logIN = true;
                break;
            }
        }
        SocketChannel client = null;
        try {
            boolean open = false;
            while (!open) {
                try {
                    client = SocketChannel.open(new InetSocketAddress(portTCP));
                    open = true;
                } catch (ConnectException e) {
                    // System.out.println(e.toString());
                    e.getSuppressed();
                    System.out.println("Servizio non disponibile");
                    System.out.println(" ");
                    System.out.println("Digitare SI per ritentare la connessione al servizio oppure NO per uscire");
                    System.out.println(" ");
                    String digit = scanner.nextLine();
                    if (digit.startsWith("SI")) {
                        try {
                            System.out.println(" ");
                            System.out.println("Attendere prego...");
                            System.out.println(" ");
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else if (digit.startsWith("NO")) {
                        scanner.close();
                        return;
                    }
                }
            }
            // Buffer per la scrittura.
            ByteBuffer bufW;
            // Buffer per la lettura.
            ByteBuffer bufR;
            boolean finito;
            String s;
            ArrayList<String> messages = null;
            ArrayList<String> chatAvviata = new ArrayList<>();

            while (true) {
                if (cmd.startsWith("Help")) {
                    System.out.println(" ");
                    System.out.println("Benvenuti nel supporto utenti.");
                    System.out.println(" ");
                    System.out.println("Lista comandi disponibili:");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'register' per registrarsi al servizio");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'login' per accedere al servizio");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'logout' per scollegarsi dal servizio");
                    System.out.println(" ");
                    System.out.println(
                            " - Digitare 'list_users' per ottenere la lista degli utenti registrati al servizio e il loro stato");
                    System.out.println(" ");
                    System.out.println(
                            " - Digitare 'list_online_users' per ottenere la lista degli utenti online registrati al servizio");
                    System.out.println(" ");
                    System.out.println(
                            " - Digitare 'list_projects' per ottenere la lista dei progetti di cui sei membro");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'create_project' per crare un nuovo progetto");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'add_member' per aggiungere un utente a un progetto");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'show_member' per ottenere la lista dei membri di un progetto");
                    System.out.println(" ");
                    System.out
                            .println(" - Digitare 'show_cards' per ottenere la lsita di card associate a un progetto");
                    System.out.println(" ");
                    System.out.println(
                            " - Digitare 'show_card' per ottenere le informazioni (nome, descrizione, lista di appartenenza) di una card di un progetto");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'add_card' per aggiungere una card a un progetto");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'move_card' per spostare una card da una lista ad un'altra lista");
                    System.out.println(" ");
                    System.out.println(
                            " - Digitare 'get_card_history' per ottenere la sequenza di eventi di spostamento di una card");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'read_chat' per visualizzare i messaggi della chat di un progetto");
                    System.out.println(" ");
                    System.out
                            .println(" - Digitare 'send_chat_msg' per inviare un messaggio nella chat di un progetto");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'cancel_project' per cancellare un progetto");
                    System.out.println(" ");
                    System.out.println(" - Digitare 'exit' chiudere il sistema");
                    System.out.println(" ");
                } else if (cmd.startsWith("login") && !effettuato) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        alreadyConnect = true;
                        System.out.println(" ");
                        System.out.println("< Operazione negata: un utente ha già fatto il login");
                    } else {
                        System.out.println(" ");
                        System.out.println("< Inserire username");
                        username = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire password");
                        password = scanner.nextLine();
                        cmdCompleto = "login " + username + " " + password;
                        server.registerForCallBack(stub);
                    }
                } else if (cmd.startsWith("logout")) {
                    cmdTrovato = true;
                    System.out.println(" ");
                    System.out.println("< Inserire username");
                    String usernameLogout = scanner.nextLine();
                    if (!logIN || !username.equals(usernameLogout)) {
                        System.out.println(" ");
                        System.out.println("< Operazione negata: utente già disconnesso o username errato");
                        notSend = true;
                    } else if (logIN) {
                        cmdCompleto = "logout " + username;
                    }
                } else if (cmd.startsWith("list_users")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println("");
                        System.out.println("< Lista utenti registrati con stato");
                        for (String u : user_status.keySet()) {
                            System.out.println("  " + u + " " + user_status.get(u));
                        }
                        System.out.println("");
                    } else {
                        System.out.println(" ");
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                    }
                    notSend = true;
                } else if (cmd.startsWith("list_online_users")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Lista utenti online");
                        for (String u : user_status.keySet()) {
                            if (user_status.get(u).equals("Online")) {
                                System.out.println("  " + u);
                            }
                        }
                        System.out.println("");
                    } else {
                        System.out.println(" ");
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                    }
                    notSend = true;
                } else if (cmd.startsWith("list_projects")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        cmdCompleto = username + " list_projects ";
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("create_project")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        cmdCompleto = username + " create_project " + projectName;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("add_member")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire username");
                        String nomeUtente = scanner.nextLine();
                        cmdCompleto = username + " add_member " + projectName + " " + nomeUtente;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("show_members")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        cmdCompleto = username + " show_members " + projectName;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("show_cards")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        cmdCompleto = username + " show_cards " + projectName;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("show_card")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire nome card");
                        String nomeCard = scanner.nextLine();
                        cmdCompleto = username + " show_card " + projectName + " " + nomeCard;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("add_card")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire nome card");
                        String nomeCard = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire descrizione");
                        String descrizione = scanner.nextLine();
                        cmdCompleto = username + " add_card " + projectName + " " + nomeCard + " " + descrizione;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("move_card")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire nome card");
                        String nomeCard = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire lista di partenza");
                        String listaP = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire lista di destinazione");
                        String listaD = scanner.nextLine();
                        cmdCompleto = username + " move_card " + projectName + " " + nomeCard + " " + listaP + " "
                                + listaD;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("get_card_history")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        System.out.println("< Inserire nome card");
                        String nomeCard = scanner.nextLine();
                        cmdCompleto = username + " get_card_history " + projectName + " " + nomeCard;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                } else if (cmd.startsWith("read_chat")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        if (chatList.containsKey(projectName)) {
                            if (!chatAvviata.contains(projectName)) {
                                chatAvviata.add(projectName);
                                System.out.println("< Non ci sono nuovi messaggi.");
                                System.out.println(" ");
                            } else {
                                messages = chatList.get(projectName).getMessages();
                                if (messages.isEmpty()) {
                                    System.out.println("< Non ci sono nuovi messaggi.");
                                    System.out.println(" ");
                                } else {
                                    System.out.println("< Messaggi della chat:");
                                    for (int i = 0; i < messages.size(); i++) {
                                        if (messages.get(i).equals("< System: Chiusura")) {
                                            chatList.get(projectName).closeChat();
                                            chatList.get(projectName).getMulticast()
                                                    .leaveGroup(chatList.get(projectName).getAddress());
                                            chatList.remove(projectName);
                                            continue;
                                        } else {
                                            System.out.println("  " + messages.get(i));
                                        }
                                    }
                                    System.out.println(" ");
                                }
                            }
                        } else {
                            System.out.println("< Non fai ancora parte della chat di questo progetto.");
                            System.out.println(" ");
                        }
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                    }
                    notSend = true;
                } else if (cmd.startsWith("send_chat_msg")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        System.out.println(" ");
                        if (chatList.containsKey(projectName)) {
                            System.out.println("< Scrivere il messaggio da inviare");
                            String message = scanner.nextLine();
                            System.out.println(" ");
                            chatList.get(projectName).sendMessage(username, message);
                            System.out.println("< Messaggio inviato");
                            System.out.println(" ");
                        } else {
                            System.out.println("< Non fai ancora parte della chat di questo progetto.");
                            System.out.println(" ");
                        }
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                    }
                    notSend = true;
                } else if (cmd.startsWith("cancel_project")) {
                    cmdTrovato = true;
                    if (logIN == true) {
                        System.out.println(" ");
                        System.out.println("< Inserire nome progetto.");
                        String projectName = scanner.nextLine();
                        cmdCompleto = username + " cancel_project " + projectName;
                    } else {
                        System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                        System.out.println(" ");
                        notSend = true;
                    }
                }
                if (!cmdTrovato) {
                    System.out.println(" ");
                    System.out.println("< Operazione negata: comando non esistente.");
                    System.out.println(" ");
                    System.out.println("Digitare 'Help' per avere la lista dei comandi disponibili.");
                    System.out.println(" ");
                } else if (cmdTrovato && alreadyConnect) {
                    cmdTrovato = false;
                    continue;
                } else if (cmdTrovato && !notSend) {
                    bufW = ByteBuffer.wrap(cmdCompleto.getBytes());
                    bufR = ByteBuffer.allocate(1024);
                    finito = false;
                    s = "";
                    // System.out.println("CLIENT --> Scrittura sul buffer.");
                    while (bufW.hasRemaining()) {
                        client.write(bufW);
                    }
                    bufW.clear();
                    bufW.flip();
                    // System.out.println("CLIENT --> Lettura dal buffer.");
                    while (!finito) {
                        bufR.clear();
                        int numByte = client.read(bufR);
                        bufR.flip();
                        s = s + StandardCharsets.UTF_8.decode(bufR).toString();
                        bufR.flip();
                        if (numByte < 1024) {
                            finito = true;
                        }
                    }
                    if (s.equals("< login effettuato")) {
                        logIN = true;
                        System.out.println(" ");
                        System.out.println(s);
                        System.out.println(" ");
                    } else if (s.startsWith("< logout")) {
                        logIN = false;
                        alreadyConnect = false;
                        server.unregisterForCallback(stub);
                        for (String prog : chatList.keySet()) {
                            chatList.get(prog).closeChat();
                            chatList.get(prog).getMulticast().leaveGroup(chatList.get(prog).getAddress());
                        }
                        break;
                    } else if (s.startsWith("< Progetto")) {
                        String[] token = s.split(" ");
                        Arrays.toString(token);
                        String projectName = token[2];
                        Chat c = chatList.get(projectName);
                        new Thread(c).start();
                        System.out.println(token[0] + " " + token[1] + " " + token[2] + " " + token[3]);
                        System.out.println(" ");
                    } else {
                        System.out.println(" ");
                        System.out.println(s);
                        System.out.println(" ");
                    }
                    cmdTrovato = false;
                } else if (cmdTrovato && notSend) {
                    cmdTrovato = false;
                    notSend = false;
                }
                // Lettura messaggio da terminale.
                cmd = scanner.nextLine();
            }
            scanner.close();
            UnicastRemoteObject.unexportObject(clientCB, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
