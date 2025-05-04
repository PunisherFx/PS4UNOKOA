package serveur.serveurMetier;

import Metier.Exceptions.PartieException;
import Metier.LogiqueDeJeu.Joueur;
import Metier.LogiqueDeJeu.Partiedejeu;
import serveur.reseau.ThreadAcceptConnexion;
import serveur.reseau.ThreadConnexion;
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
    public int getUtilisateurs(){
        return users.size();
    }
    public boolean add(Utilisateur utilisateur) throws ServeurExceptions {
        if (utilisateur == null) {
            throw new ServeurExceptions("La connexion utilisateur vaut null");
        }
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
    public void messagePublic(Utilisateur emetteur, String message) {
        for (String mot : motsCensures) {
            message = message.replace(mot, censure);
        }
        for (Utilisateur utilisateur : users) {
            if (utilisateur.equals(emetteur)) {
                continue;
            }
            utilisateur.envoyerMessagePublic(emetteur, message);
        }
    }
    public void messagePrive(Utilisateur emetteur, String pseudoDestination, String message) throws ServeurExceptions {
        Utilisateur dest = get(pseudoDestination);

        dest.envoyerMessagePrive(emetteur, message);
    }


    public boolean present(String pseudo) {
        for (int i = 0; i < users.size(); i++) {
            String p = users.get(i).getPseudo();
            if (p != null && p.equalsIgnoreCase(pseudo)) {
                return true;
            }
        }
        return false;
    }

    public void lancerPartie() {
        if (getUtilisateurs()<2){
            throw new PartieException("il faut au minimun 2 joueurs");
        }
        ArrayList<Joueur> joueurs = new ArrayList<>();
        for (Utilisateur u : users) {
            Joueur j = new Joueur(u.getPseudo());
            joueurs.add(j);
        }
        Partiedejeu p = null;
        p.initialiserJoueurs(joueurs);

    }
}
