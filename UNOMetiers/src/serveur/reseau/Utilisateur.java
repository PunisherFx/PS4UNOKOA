package serveur.reseau;

import BaseDeDonnees.ConnexionBD;
import BaseDeDonnees.UtilisateurDAO;
import application.AppClient;
import Metier.Exceptions.PartieException;
import Metier.Exceptions.PiocheException;
import Metier.Exceptions.UnoException;
import Metier.LogiqueDeJeu.Carte;
import Metier.LogiqueDeJeu.Joueur;
import Metier.LogiqueDeJeu.Partiedejeu;
import serveur.serveurMetier.ServeurExceptions;
import serveur.serveurMetier.ServeurUno;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Utilisateur {
    private ThreadConnexion threadConnexion = null;
    private String pseudo;
    private ServeurUno serveur;
    private Socket socket;
    private boolean valide = false;
    private Partiedejeu partie;
    private AppClient appClient;
    //BdUno bd;

    public Utilisateur(Socket socket, ServeurUno serveur) {
        this.socket = socket;
        this.serveur = serveur;
        this.threadConnexion = new ThreadConnexion(this, socket);

    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Utilisateur that = (Utilisateur) o;
        return Objects.equals(pseudo, that.pseudo);
    }

    @Override
    public String toString() {
        return "Utilisateurs{" +
                "pseudo='" + pseudo + '\'' +
                '}';
    }


    private final static String regexCONNEXION = "^@CONNEXION \\p{Alnum}+$";
    private final static String regexDECONNEXION = "^@DECONNEXION$";
    private final static String regexDEMARRER = "^@DEMARRER_PARTIE$";
    private final static String regexMP_TO = "^@MP_TO \\p{Alnum}+ .*$";
    private final static String regexTO_ALL = "^@TO_ALL .*$";
    private final static String regexCARTE_JOUEE = "^@CARTE_JOUEE \\w+ \\w+$";
    private final static String regexFIN_TOUR = "^@FIN_TOUR$";
    private final static String regexPIOCHE = "^@PIOCHE$";
    private final static String regexENCAISSE = "^@ENCAISSE";
    private final static String regexUNO = "^@UNO$";
    private final static String regexMAIN = "^@MAIN";
    private final static String regexCARTETAS = "^@CARTE_TAS";
    private final static String regexTOUR= "^@AQUILETOUR";
    private final static String regexSCENE= "^@LANCER_SCENE_JEU";

    private final static String regexINFO = "^@INFO .*$";
    private final static String regexMP_FROM = "^@MP_FROM \\p{Alnum}+ .*$";
    private final static String regexPUBLIC_FROM = "^@PUBLIC_FROM \\p{Alnum}+ .*$";
    private final static String regexERROR = "^@ERROR .*$";
    private final static String regexLISTE_JOUEURS = "^@LISTE_JOUEURS (\\[\\w+;\\d+] ?)+$";

    private final static String[] protocole = {regexCONNEXION, regexDECONNEXION, regexMP_TO, regexTO_ALL,
            regexDEMARRER, regexCARTE_JOUEE, regexFIN_TOUR, regexPIOCHE,regexENCAISSE,regexUNO,regexMAIN,regexCARTETAS,
            regexTOUR};

    public void controlerMessage(String message) throws IOException {
        if (message == null) {
            valide = false;
            try {
                serveur.remove(this);
            } catch (ServeurExceptions e) {
                // De toute façon, c'est mort et on ignore
            }
            return;
        }
        if (!verifieProtocole(message)) {
            threadConnexion.envoyerMessageAuClient("@ERROR ce que vous dites n'a aucun sens. Votre message est ignoré");
            return;
        }
        // On récupère le premier mot pour savoir comment traiter le message
        String typeMessage = message.split(" ")[0];
        switch (typeMessage) {
            case "@CONNEXION" -> traiterConnexion(message);
            case "@DECONNEXION" -> traiterDeconnexion();
            case "@MP_TO" -> traiterMP_TO(message);
            case "@TO_ALL" -> traiterTO_ALL(message);
            case "@DEMARRER_PARTIE" -> {
                lancerPartie();
            }
            case "@CARTE_JOUEE" -> carteJouer(message);
            case "@FIN_TOUR" -> finTour();
            case "@PIOCHE" -> pioche();
            case "@ENCAISSE" -> encaisse();
            case "@UNO" -> uno();
            case "@MAIN" -> carteEnMain();
            case"@CARTE_TAS" -> carteAJOUER();
            case "@AQUILETOUR" -> tourDe();
            default -> System.err.println("Ce type de message nexiste pas : " + typeMessage);

        }
    }
    public void lancerPartie() {
        // on verifie qu'il ya au minimum 2 joueurs
        ArrayList<Joueur> joueurs = new ArrayList<>();
        for (Utilisateur u : serveur.users) {
            Joueur j = new Joueur(u.getPseudo());
            joueurs.add(j);
        }
        Partiedejeu nvPartie = new Partiedejeu();
        nvPartie.initialiserPartie(joueurs);     // ✅ c’est ici qu’on fait la vraie initialisation
        serveur.setPartiedejeu(nvPartie);                 // très bien
        serveur.setPartieEnCour(true);
        serveur.diffuserMessage( "que la fete commence");
        serveur.messagePublic(this,"@DEMARRER_PARTIE");
        appClient.changerScene();
        //serveur.diffuserMessage("@LANCER_SCENE_JEU");

    }
    public String tourDe(){
        Joueur j = serveur.getPartiedejeu().joueurCourant();
        threadConnexion.envoyerMessageAuClient("TOUR DE " +j);
        return "";
    }
    public String carteEnMain(){
        List<Carte> main = serveur.getPartiedejeu().getMainDe(this.pseudo);

        System.out.println("Main de " + this.pseudo + " : " + main);
        for (Carte c : main) {
            threadConnexion.envoyerMessageAuClient("@CARTE" + c.toString());
        }

        return "";
    }
    public void carteAJOUER(){
            Carte c = serveur.getPartiedejeu().carteDuTas();
            threadConnexion.envoyerMessageAuClient("@CARTE" + c.toString());
    }
    /*public void lancerPartie() {
        // on verifie qu'il ya au minimum 2 joueurs
        if (serveur.getUtilisateurs() < 1) {
            threadConnexion.envoyerMessageAuClient("@ERROR il faut au minimum 2 joueurs pour lancer la partie ");
            return;
        }
        ArrayList<Joueur> joueurs = new ArrayList<>();
        for (Utilisateur u : serveur.users) {
            Joueur j = new Joueur(u.getPseudo());
            joueurs.add(j);
        }
        //partieEnCour = true;
        partie.initialiserJoueurs(joueurs);
    }*/
    public void uno(){
        Joueur j = serveur.getPartiedejeu().getJoueurDepuisPseudo(this.pseudo);
        j.DireUno(partie);
        serveur.messagePublic(this,"UNOOOOOOOOOO!!!!");
    }
    public void encaisse(){
        int nb = serveur.getPartiedejeu().encaisserAttaque();
        serveur.messagePublic(this,"j'ai encaisser l'attaque" +nb );
    }
    public void finTour() {
        try {
            Joueur joueur = serveur.getPartiedejeu().getJoueurDepuisPseudo(this.pseudo);
            if (joueur == null) {
                threadConnexion.envoyerMessageAuClient("@ERROR Joueur introuvable.");
                return;
            }

            serveur.getPartiedejeu().finirTourDe(joueur);
            if (serveur.getPartiedejeu().isFinManche()){
                serveur.diffuserMessage("La partie est terminer ");
                serveur.diffuserMessage("le vainquer est "  + serveur.getPartiedejeu().getVainqueur());
                return;
            }
            serveur.messagePublic(this, "a terminé son tour.");
            threadConnexion.envoyerMessageAuClient(carteEnMain());
            serveur.diffuserMessage(serveur.getPartiedejeu().messageListeJoueurs());
            serveur.diffuserMessage(tourDe());
        } catch (PartieException | UnoException e) {
            threadConnexion.envoyerMessageAuClient("@ERROR " + e.getMessage());
        }
    }
    public void pioche(){
        try {
            Joueur j = serveur.getPartiedejeu().getJoueurDepuisPseudo(this.pseudo);
            Carte c = serveur.getPartiedejeu().piocherUneCarte(j);
            serveur.messagePublic(this, "j'ai pioche une carte");
            threadConnexion.envoyerMessageAuClient("la carte " + c + "a ete ajouter a vitre main");
        }catch (PiocheException e){
            threadConnexion.envoyerMessageAuClient("@ERROR" + e.getMessage());
        }
        }
    public void carteJouer(String message) {
        try {
            String Carte = message.substring(13);
            String[] partie = Carte.split(" ");
            if (partie.length != 2) {
                threadConnexion.envoyerMessageAuClient("@ERROR Format de carte incorrect ");
                return;
            }
            String valeur = partie[0];
            String couleur = partie[1];
            Carte carte = new Carte(Metier.LogiqueDeJeu.Carte.eValeur.valueOf(valeur.toUpperCase()),
                    Metier.LogiqueDeJeu.Carte.eCouleur.valueOf(couleur.toUpperCase()));
            serveur.getPartiedejeu().jouer(carte);
            serveur.messagePublic(this, "j'ai jouer la" + carte);
        } catch (PartieException | IllegalArgumentException e) {
            threadConnexion.envoyerMessageAuClient("@ERROR" + e.getMessage());
        }
    }
    private void traiterMP_TO(String message) {
        String[] mots = message.split(" ", 3);

        if (mots.length < 3) {
            threadConnexion.envoyerMessageAuClient("@ERROR Format incorrect. Utilisez : @MP_TO pseudo message");
            return;
        }
        String destinataire = mots[1];
        String contenu = mots[2];

        try {
            serveur.messagePrive(this, destinataire, contenu);
        } catch (ServeurExceptions e) {
            threadConnexion.envoyerMessageAuClient("@ERROR " + e.getMessage());
        }
    }
    private void traiterTO_ALL(String message) {
        serveur.messagePublic(this, message);

    }
        public boolean verifieProtocole(String message) {
        for (String phraseDuProtocole : protocole) {
            if (verifiePhraseDuProtocole(message, phraseDuProtocole))
                return true;
        }
        return false;
    }
    public boolean verifiePhraseDuProtocole(String message, String phrase) {
        Pattern pattern = Pattern.compile(phrase, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).matches();
    }
    public void traiterConnexion(String message) {
        String[] mots = message.split(" ");

        if (mots.length != 2) {
            threadConnexion.envoyerMessageAuClient("@ERROR Format invalide. Utilisez : @CONNEXION <pseudo>");
            return;
        }
        String pseudo = mots[1];
        if (serveur.isPartieEnCour()) {
            threadConnexion.envoyerMessageAuClient(" Une partie est déjà en cours. Connexion refusée.");
            try {
                socket.close();
            } catch (IOException e) {
               threadConnexion.envoyerMessageAuClient("@ERROR  UNE partie est deja encour" + e.getMessage());
            }

            return;
        }
        try {
            serveur.get(pseudo);
            threadConnexion.envoyerMessageAuClient("@ERROR Pseudo déjà utilisé");
        } catch (ServeurExceptions e) {
            this.pseudo = pseudo;
            this.valide = true;
            threadConnexion.envoyerMessageAuClient("@OK Bienvenue " + pseudo + " ! ");
            UtilisateurDAO.ajouterUtilisateur(pseudo);
            //BdUno.ajouterJoueur(pseudo);
            //serveur.add(this);
            //serveur.messagePublic(this ,pseudo+ " a rejoint le serveur" );
        }
       /* if (serveur.present(pseudo)) {
            threadConnexion.envoyerMessageAuClient("@ERROR Ce pseudo est déjà utilisé.");
            return;
        }*/
        serveur.diffuserMessage( pseudo + " a rejoint le serveur" );
    }
    private void traiterDeconnexion() {
        try {
            this.serveur.messagePublic(this, "Je suis parti"); // Notifie les autres
            this.socket.close();
            this.serveur.remove(this);
            this.threadConnexion.fin();// Retire de la liste du serveur
        } catch (ServeurExceptions | IOException e) {
            throw new RuntimeException("Erreur lors de la déconnexion de l'utilisateur : " + pseudo, e);
        }
    }
    public void envoyerMessagePublic(Utilisateur emetteur, String message) {
        threadConnexion.envoyerMessageAuClient("@PUBLIC_FROM " + emetteur.getPseudo() + " " + message);
    }

    public void envoyerMessagePrive(Utilisateur emetteur, String message) {
        threadConnexion.envoyerMessageAuClient("@MP_FROM " + emetteur.getPseudo() + " " + message);
    }

    public ThreadConnexion getThreadConnexion() {
        return threadConnexion;
    }
}


