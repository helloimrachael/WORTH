package utility;

import java.io.Serializable;
import java.util.ArrayList;

public class Utente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String password;
    private String status;
    private ArrayList<String> projectsList;

    public Utente(String name, String password) {
        this.name = name;
        this.password = password;
        status = "Offline";
        projectsList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public void setOFFLINE() {
        status = "Offline";
    }

    public void setONLINE() {
        status = "Online";
    }

    public void createProject(String projectName, Database db) {
        projectsList.add(projectName);
        db.createProject(projectName, this.getName());
    }

    public ArrayList<String> listProjects() {
        return this.projectsList;
    }

    public void addProject(String projectName) {
        projectsList.add(projectName);
    }

    public void removeProject(String projectName) {
        if (listProjects().contains(projectName)) {
            listProjects().remove(projectName);
        }
    }
}