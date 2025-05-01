package serveur.serveurMetier;

import serveur.reseau.ThreadAcceptConnexion;
import serveur.reseau.Utilisateur;

import java.util.ArrayList;

public class ServeurUno {
    private int port;
    private ArrayList<Utilisateur> users = new ArrayList<>();

    public ServeurUno(int port) {
        this.port = port;
        initCensure();
        new ThreadAcceptConnexion(this);
    }
    public boolean add(Utilisateur utilisateur) throws ServeurExceptions {
        if (utilisateur == null)
            throw new ServeurExceptions("La connexion utilisateur vaut null");

        return users.add(utilisateur);
    }

    public int getPort() {
        return port;
    }
    public boolean remove(Utilisateur utilisateur) throws ServeurExceptions {
        if (utilisateur == null)
            throw new ServeurExceptions("La connexion utilisateur vaut null");

        return users.remove(utilisateur);
    }
    public Utilisateur get(String pseudo) throws ServeurExceptions {
        for (Utilisateur utilisateur : users) {
            if (pseudo.equals(utilisateur.getPseudo()))
                return utilisateur;
        }

        throw new ServeurExceptions("L'utilisateur "+pseudo+" n'existe pas...");
    }
    public ArrayList<String> motsCensures = new ArrayList<>();
    public String censure = "@#$?!";

    private void initCensure() {
        motsCensures.add("NIGGA");
        motsCensures.add("MERDE");
        motsCensures.add("PUTAIN");
        motsCensures.add("SALE");
        motsCensures.add("FUCK");
    }
    public void diffuser(String message) {
        MessagePublic(+message);
    }

}
