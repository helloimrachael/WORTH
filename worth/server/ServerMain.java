package server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import utility.*;

public class ServerMain {

    private static int portRMI_CB = 3000;
    private static int portTCP = 3002;
    private static UsersDatabase usersDB;
    private static Database DB;

    public static void main(String[] args) throws IOException {

        DB = new Database();
        usersDB = new UsersDatabase();
        Chats C = new Chats();
        ConcurrentHashMap<String, String> user_status = new ConcurrentHashMap<>();
        Thread th = null;
        try {
            ServerNotification serverN = new ServerNotification();
            UpdateHashmap up = new UpdateHashmap();
            ServerNotificationInterface stub = (ServerNotificationInterface) UnicastRemoteObject.exportObject(serverN,
                    39000);
            UpdateHashmapInterface stub_2 = (UpdateHashmapInterface) UnicastRemoteObject.exportObject(up, 39000);
            LocateRegistry.createRegistry(portRMI_CB);
            LocateRegistry.createRegistry(3003);
            Registry reg = LocateRegistry.getRegistry(portRMI_CB);
            Registry reg_2 = LocateRegistry.getRegistry(3003);
            reg.rebind("ServizioNotifiche", stub);
            reg_2.rebind("AggiornaHash", stub_2);
            Path pathUserFile = Paths.get("./backupUsers/users.db");
            if (!Files.exists(pathUserFile)) {
                Files.createFile(pathUserFile);
            } else
                BackupUsers();
            Path pathProjectFile = Paths.get("./backupProjects/projects.db");
            if (!Files.exists(pathProjectFile)) {
                Files.createFile(pathProjectFile);
            } else {
                BackupProjects();
                BackupCard();
            }
            new Registrazione(usersDB, user_status, serverN).start();
            th = new Thread(new ServerTCP(user_status, serverN, up, DB, usersDB, C, portTCP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        th.start();
    }

    public static void updateUserFile(Object object, Class<?> type) {

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./backupUsers/users.db"));) {
            output.writeObject(type.cast(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void BackupUsers() {
        try (ObjectInputStream objectInput = new ObjectInputStream(new FileInputStream("./backupUsers/users.db"));) {
            while (true) {
                Utente users = (Utente) objectInput.readObject();
                usersDB.getUtenti().putIfAbsent(users.getName(), users);
                for (String utente : usersDB.getUtenti().keySet()) {
                    for (String prog : usersDB.getUser(utente).listProjects()) {
                        if (!DB.isProject(prog)) {
                            ArrayList<String> membri = new ArrayList<>();
                            membri.add(utente);
                            DB.getProgettiMembri().putIfAbsent(prog, membri);
                        } else
                            DB.addMember(prog, utente);
                    }
                }
            }
        } catch (EOFException eof) {
            usersDB.setAllOffline();
            System.out.println("Reached end of file");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void updateProjectFile(Object object, Class<?> type) {
        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream("./backupProjects/projects.db"));) {
            output.writeObject(type.cast(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void BackupProjects() {
        try (ObjectInputStream objectInput = new ObjectInputStream(
                new FileInputStream("./backupProjects/projects.db"));) {
            while (true) {
                Progetto prog = (Progetto) objectInput.readObject();
                DB.getProgetti().put(prog.getName(), prog);
            }
        } catch (EOFException eof) {
            System.out.println("Reached end of file");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void updateCardFile(String projectName, Object object, Class<?> type) {
        File projectDir = new File("./backupCards");
        boolean inserita = false;
        for (File directory : projectDir.listFiles()) {
            if (projectName.equals(directory.getName())) {
                try (ObjectOutputStream output = new ObjectOutputStream(
                        new FileOutputStream("./backupCards/" + projectName + "card.db"));) {
                    output.writeObject(type.cast(object));
                    inserita = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!inserita) {
            Path pathCardFile = Paths.get("./backupCards/" + projectName);
            try {
                Files.createDirectory(pathCardFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (ObjectOutputStream output = new ObjectOutputStream(
                    new FileOutputStream("./backupCards/" + projectName + "/card.db"));) {
                output.writeObject(type.cast(object));
                inserita = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void BackupCard() {
        File projectDir = new File("./backupCards");
        for (File directory : projectDir.listFiles()) {
            String projectName = directory.getName();
            try (ObjectInputStream objectInput = new ObjectInputStream(
                    new FileInputStream("./backupCards/" + projectName + "/card.db"));) {
                while (true) {
                    Card c = (Card) objectInput.readObject();
                    DB.updateCard(projectName, c);
                }
            } catch (EOFException eof) {
                System.out.println("Reached end of file");
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void replaceCard(String projectName) {
        File dir = new File("./backupCards/" + projectName);
        for (File f : dir.listFiles()) {
            f.delete();
        }
        dir.delete();
        for (String prog : DB.getProgettiCard().keySet()) {
            for (int i = 0; i < DB.getProgettiCard().size(); i++) {
                Card c = DB.getProgettiCard().get(prog).get(i);
                updateCardFile(prog, c, Card.class);
            }
        }
    }

    public static void replaceUser() {
        File dir = new File("./backupUsers");
        for (File f : dir.listFiles()) {
            f.delete();
        }
        for (String utente : usersDB.getUtenti().keySet()) {
            Utente u = usersDB.getUser(utente);
            updateUserFile(u, Utente.class);
        }
    }

    public static void replaceProject() {
        File dir = new File("./backupProjects");
        for (File f : dir.listFiles()) {
            f.delete();
        }
        for (String prog : DB.getProgetti().keySet()) {
            Progetto p = DB.getProject(prog);
            updateUserFile(p, Progetto.class);
        }
    }
}
