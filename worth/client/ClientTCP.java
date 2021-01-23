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
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import utility.*;

public class ClientTCP {
    private ConcurrentHashMap<String, String> user_status;
    private int portTCP;
    private int portRMI;
    private int portRMI_CB;
    private boolean logIN;

    public ClientTCP(ConcurrentHashMap<String, String> user_status, int portTCP, int portRMI, int portRMI_CB) {
        this.user_status = user_status;
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

        while (!connesso) {
            try {
                Registry reg = LocateRegistry.getRegistry(portRMI);
                r = (RegistrazioneInterface) reg.lookup("RegistrazioneUtente");
                Registry reg_2 = LocateRegistry.getRegistry(portRMI_CB);
                server = (ServerNotificationInterface) reg_2.lookup("ServizioNotifiche");
                clientCB = new ClientNotification(user_status);
                stub = (ClientNotificationInterface) UnicastRemoteObject.exportObject(clientCB, 0);
                connesso = true;
            } catch (RemoteException | NotBoundException e) {
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
            // System.out.println("Client connesso al server");
            System.out.println(" ");
            // Buffer per la scrittura.
            ByteBuffer bufW;
            // Buffer per la lettura.
            ByteBuffer bufR;
            String cmd;
            boolean finito;
            String s;
            String username = null;
            String password;
            String cmdCompleto = "";
            boolean notSend = false;

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
            while (true) {
                // Lettura messaggio da terminale.
                cmd = scanner.nextLine();
                if (cmd.startsWith("register")) {
                    System.out.println(" ");
                    System.out.println("< Inserire username");
                    username = scanner.nextLine();
                    System.out.println(" ");
                    System.out.println("< Inserire password");
                    password = scanner.nextLine();
                    System.out.println(" ");
                    RemoteClient remote = new RemoteClient();
                    remote.registerC(username, password, r);
                } else if (cmd.startsWith("Help")) {
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
                } else {
                    boolean cmdTrovato = false;
                    Boolean alreadyConnect = false;
                    if (cmd.startsWith("login")) {
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
                        } else if (logIN) {
                            cmdCompleto = "logout " + username;
                        }
                    } else if (cmd.startsWith("list_users")) {
                        cmdTrovato = true;
                        System.out.println(user_status.size());
                        if (logIN == true) {
                            System.out.println("< Lista utenti registrati con stato");
                            for (String u : user_status.keySet()) {
                                System.out.println(u + " " + user_status.get(u));
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
                            System.out.println("< Lista utenti online");
                            for (String u : user_status.keySet()) {
                                if (user_status.get(u).equals("Online")) {
                                    System.out.println(u);
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
                            cmdCompleto = "list_projects ";
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
                            String nomeProg = scanner.nextLine();
                            cmdCompleto = "create_project " + nomeProg;
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
                            String nomeProg = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire username");
                            String nomeUtente = scanner.nextLine();
                            cmdCompleto = "add_member " + nomeProg + " " + nomeUtente;
                        } else {
                            System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                            System.out.println(" ");
                            notSend = true;
                        }
                    } else if (cmd.startsWith("show_member")) {
                        cmdTrovato = true;
                        if (logIN == true) {
                            System.out.println(" ");
                            System.out.println("< Inserire nome progetto.");
                            String nomeProg = scanner.nextLine();
                            cmdCompleto = "show_member " + nomeProg;
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
                            String nomeProg = scanner.nextLine();
                            cmdCompleto = "show_cards " + nomeProg;
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
                            String nomeProg = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire nome card");
                            String nomeCard = scanner.nextLine();
                            cmdCompleto = "show_card " + nomeProg + " " + nomeCard;
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
                            String nomeProg = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire nome card");
                            String nomeCard = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire descrizione");
                            String descrizione = scanner.nextLine();
                            cmdCompleto = "add_card " + nomeProg + " " + nomeCard + " " + descrizione;
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
                            String nomeProg = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire nome card");
                            String nomeCard = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire lista di partenza");
                            String listaP = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire lista di destinazione");
                            String listaD = scanner.nextLine();
                            cmdCompleto = "move_card " + nomeProg + " " + nomeCard + " " + listaP + " " + listaD;
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
                            String nomeProg = scanner.nextLine();
                            System.out.println(" ");
                            System.out.println("< Inserire nome card");
                            String nomeCard = scanner.nextLine();
                            cmdCompleto = "get_card_history " + nomeProg + " " + nomeCard;
                        } else {
                            System.out.println("< Operazione negata: utente non connesso. Effettuare login.");
                            System.out.println(" ");
                            notSend = true;
                        }
                    } else if (cmd.startsWith("read_chat")) {
                        cmdTrovato = true;
                        cmdCompleto = null;
                    } else if (cmd.startsWith("send_chat_msg")) {
                        cmdTrovato = true;
                        cmdCompleto = null;
                    } else if (cmd.startsWith("cancel_project")) {
                        cmdTrovato = true;
                        if (logIN == true) {
                            System.out.println(" ");
                            System.out.println("< Inserire nome progetto.");
                            String nomeProg = scanner.nextLine();
                            cmdCompleto = "cancel_project " + nomeProg;
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
                            System.out.println(" ");
                            System.out.println(s);
                            System.out.println(" ");
                        }
                        if (s.equals("< Operazione negata: utente non collegato. Si prega di effettuare il login.")) {
                            logIN = false;
                        } else if (s.equals("< login effettuato")) {
                            logIN = true;
                        }
                        if (s.startsWith("< logout")) {
                            logIN = false;
                            alreadyConnect = false;
                            server.unregisterForCallback(stub);
                            System.out.println("Digitare 'exit' per uscire dal servizio o 'continue' per continuare");
                            System.out.println(" ");
                            String choice = scanner.nextLine();
                            if (choice.startsWith("exit")) {
                                scanner.close();
                                break;
                            } else if (choice.startsWith("continue")) {
                                System.out.println(" ");
                                continue;
                            }
                        }
                        cmdTrovato = false;
                    } else if (cmdTrovato && notSend) {
                        cmdTrovato = false;
                        notSend = false;
                    }
                }
            }
            UnicastRemoteObject.unexportObject(clientCB, true);
        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }
}
