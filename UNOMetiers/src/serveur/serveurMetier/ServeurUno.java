package serveur.serveurMetier;

import Metier.Exceptions.PartieException;
import Metier.LogiqueDeJeu.Joueur;
import Metier.LogiqueDeJeu.Partiedejeu;
import Metier.LogiqueDeJeu.Pioche;
import serveur.reseau.ThreadAcceptConnexion;
import serveur.reseau.ThreadConnexion;
import serveur.reseau.Utilisateur;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServeurUno {
    private int port;
    public ArrayList<Utilisateur> users = new ArrayList<>();
     ThreadConnexion connexion = null;
     private boolean partieEnCour;
     private Partiedejeu partiedejeu;

    public Partiedejeu getPartiedejeu() {
        return partiedejeu;
    }

    public void setPartiedejeu(Partiedejeu partiedejeu) {
        this.partiedejeu = partiedejeu;
    }

    public ServeurUno(int port) {
        this.port = port;
        initCensure();
        new ThreadAcceptConnexion(this);
    }

    public boolean isPartieEnCour() {
        return partieEnCour;
    }

    public void setPartieEnCour(boolean partieEnCour) {
        this.partieEnCour = partieEnCour;
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
        // On remplace les mots tabous du message
        for (String mot : motsCensures) {
            message = message.replace(mot, censure);
        }

        // Et on parcours la liste des utilisateurs pour leur envoyer le message à chacun (sauf à soi-même)
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


   /* public boolean present(String pseudo) {
        for (int i = 0; i < users.size(); i++) {
            String p = users.get(i).getPseudo();
            if (p != null && p.equalsIgnoreCase(pseudo)) {
                return true;
            }
        }
        return false;
    }*/

/*   public void lancerPartie() {
       // on verifie qu'il ya au minimum 2 joueurs
       if (getUtilisateurs() < 1) {
           connexion.envoyerMessageAuClient("@ERROR il faut au minimum 2 joueurs pour lancer la partie ");
           return;
       }
       ArrayList<Joueur> joueurs = new ArrayList<>();
       for (Utilisateur u : users) {
           Joueur j = new Joueur(u.getPseudo());
           joueurs.add(j);
       }
       Partiedejeu nvPartie = new Partiedejeu();
       nvPartie.initialiserPartie(joueurs);     // ✅ c’est ici qu’on fait la vraie initialisation
       setPartiedejeu(nvPartie);                 // très bien
       partieEnCour = true;
       diffuserMessage("@PARTIELANCER" + "que la fete commence");
   }*/


   /*cette methode du serveur qui s'occuper d'informer les autres jouers en cas de nouvelles ex carte jouer
    une nouvlle connexion etc... */
   public void diffuserMessage(String message){
       for (Utilisateur u : users) {
         //  if (u.equals(expediteur)) continue;
           ThreadConnexion cnx = u.getThreadConnexion();
           if (cnx != null && u.isValide()) {
               cnx.envoyerMessageAuClient("@INFO " +  message);
           }
       }
   }
}
