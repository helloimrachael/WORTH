package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class UpdateListProjects implements Runnable {

    private SocketChannel client;
    private String username;
    private ArrayList<String> listProject;

    public UpdateListProjects(String username, SocketChannel client) {
        this.username = username;
        this.client = client;
        listProject = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean finito;
        String s;
        String cmdCompleto;
        ByteBuffer bufW;
        ByteBuffer bufR;
        while (true) {
            cmdCompleto = "list_projects hash " + username;
            // String req = request("list_projects", bufW, bufR, client);
            bufW = ByteBuffer.wrap(cmdCompleto.getBytes());
            bufR = ByteBuffer.allocate(1024);
            finito = false;
            s = "";
            // System.out.println("CLIENT --> Scrittura sul buffer.");
            while (bufW.hasRemaining()) {
                try {
                    client.write(bufW);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bufW.clear();
            bufW.flip();
            // System.out.println("CLIENT --> Lettura dal buffer.");
            while (!finito) {
                bufR.clear();
                int numByte = 0;
                try {
                    numByte = client.read(bufR);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bufR.flip();
                s = s + StandardCharsets.UTF_8.decode(bufR).toString();
                bufR.flip();
                if (numByte < 1024) {
                    finito = true;
                }
            }
            if (!s.equals("Operazione negata: non ci sono progetti associati a questo utente")) {
                String[] token = s.split(" ");
                Arrays.toString(token);
                for (int i = 3; i < token.length; i++) {
                    if (!listProject.contains(token[i])) {
                        listProject.add(token[i]);
                    }
                }
            }
        }

    }

    public ArrayList<String> getList() {
        return listProject;
    }

}
