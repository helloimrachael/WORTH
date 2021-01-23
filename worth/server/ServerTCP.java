package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import utility.*;

public class ServerTCP implements Runnable {

    private Database DB;
    private UsersDatabase usersDB;
    private int portTCP;
    ServerNotification serverN;
    ConcurrentHashMap<String, String> user_status;

    public ServerTCP(ConcurrentHashMap<String, String> user_status, ServerNotification serverN, Database DB,
            UsersDatabase usersDB, int portTCP) {
        this.serverN = serverN;
        this.DB = DB;
        this.usersDB = usersDB;
        this.portTCP = portTCP;
        this.user_status = user_status;
    }

    @Override
    public void run() {
        connectionTCP();

    }

    public void connectionTCP() {
        CommandHandler comm = new CommandHandler();
        ServerSocketChannel serverSocketChannel;
        ServerSocket socket;
        Selector selector = null;

        String username = null;
        String password = null;

        try {
            // Apetura canale per la connessione client-server.
            serverSocketChannel = ServerSocketChannel.open();
            socket = serverSocketChannel.socket();
            socket.bind(new InetSocketAddress(portTCP));
            // Metto il canale in modalità non bloccante.
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                System.out.println("SERVER --> In attesa sulla select");
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            // Scorro tutte le chiavi del selettore.
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        // Accettazione connesssione.
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("SERVER --> Accepted connection");
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isWritable()) {
                        System.out.println("SERVER --> Scrittura");
                        SocketChannel client = (SocketChannel) key.channel();
                        String s = (String) key.attachment();
                        ByteBuffer buf = ByteBuffer.wrap(s.getBytes());
                        // Salvo il numero di byte letti in una variabile.
                        int numBytesW = client.write(buf);
                        if (numBytesW == -1) { // Write non riuscita.
                            System.out.println("SERVER --> Write finita | Socket chiusa");
                            key.cancel();
                            key.channel().close();
                            System.out.println("SERVER --> Canale chiuso.");
                        }
                        if (buf.hasRemaining()) { // C'è ancora qualcosa nel buffer.
                            buf.flip();
                            String mes = StandardCharsets.UTF_8.decode(buf).toString();
                            key.attach(mes);
                        } else { // numBytesW == buf.length().
                            key.attach(null);
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        System.out.println("SERVER --> Lettura");
                        SocketChannel client = (SocketChannel) key.channel();
                        String risposta = (String) key.attachment();
                        String cmd;
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        buf.clear();
                        // Salvo il numero di byte letti in una variabile.
                        int numBytesR = client.read(buf);
                        if (numBytesR == -1) { // Read non riuscita.
                            System.out.println("SERVER --> Read finita | Socket chiusa");
                            key.cancel();
                            key.channel().close();
                            System.out.println("SERVER --> Canale chiuso.");
                        } else {
                            buf.flip();
                            cmd = StandardCharsets.UTF_8.decode(buf).toString();
                            if (cmd.startsWith("login")) {
                                String[] token = cmd.split(" ");
                                Arrays.toString(token);
                                username = token[1];
                                password = token[2];
                                if (usersDB.getUtenti().containsKey(username)) {
                                    if (usersDB.getUser(username).getPassword().equals(password)) {
                                        // synchronized (usersDB) {
                                        usersDB.getUser(username).setONLINE();
                                        user_status.replace(username, "Online");
                                        try {
                                            serverN.updateDB(user_status);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        // }
                                        risposta = "< login effettuato";
                                    } else {
                                        risposta = "< Operazione negata: password errata";
                                    }
                                } else {
                                    risposta = "< Operazione negata: utente non registrato.";
                                }
                            } else if (cmd.startsWith("logout")) {
                                String[] token = cmd.split(" ");
                                Arrays.toString(token);
                                username = token[1];
                                // synchronized (usersDB) {
                                usersDB.getUser(username).setOFFLINE();
                                user_status.replace(username, "Offline");
                                try {
                                    serverN.updateDB(user_status);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // }
                                risposta = "< logout effettuato";
                            } else if (usersDB.getUser(username).getStatus() == "Online") {
                                risposta = comm.command(cmd, usersDB.getUser(username), DB, usersDB);
                            } else if (usersDB.getUser(username).getStatus() == "Offline") {
                                risposta = "< Operazione negata: utente non collegato. Si prega di effettuare il login.";
                            }
                            key.attach(risposta);
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

}