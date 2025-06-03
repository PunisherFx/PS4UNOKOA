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

/**
  Cette classe représente un joueur connecté côté serveur.On gère les communication, le pseudo,
 * sa participation à la partie en cours,
 * et le traitement des messages reçus selon notre protocole personnalisé. »
 */

public class Utilisateur {
    private ThreadConnexion threadConnexion = null;
    private String pseudo;
    private ServeurUno serveur;
    private Socket socket;
    private boolean valide = false;

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
    private final static String regexJOUEURS= "^@JOUEURS";


    private final static String regexINFO = "^@INFO .*$";
    private final static String regexMP_FROM = "^@MP_FROM \\p{Alnum}+ .*$";
    private final static String regexPUBLIC_FROM = "^@PUBLIC_FROM \\p{Alnum}+ .*$";
    private final static String regexERROR = "^@ERROR .*$";
    private final static String regexLISTE_JOUEURS = "^@LISTE_JOUEURS (\\[\\w+;\\d+] ?)+$";

    private final static String[] protocole = {regexCONNEXION, regexDECONNEXION, regexMP_TO, regexTO_ALL,
            regexDEMARRER, regexCARTE_JOUEE, regexFIN_TOUR, regexPIOCHE,regexENCAISSE,regexUNO,regexMAIN,regexCARTETAS,
            regexTOUR ,regexJOUEURS };

    /**
     * c'est la methode pricipale du serveur , a chaque message recu via la socket on verifie si le message
     * respecte le protocole si ya des commande qui commence par le @ qui sont recu par le serveur
     * @param message
     * @throws IOException
     */
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
        String typeMessage = message.split(" ")[0];
        switch (typeMessage) {
            case "@CONNEXION" -> traiterConnexion(message);
            case "@DECONNEXION" -> traiterDeconnexion();
            case "@MP_TO" -> traiterMP_TO(message);
            case "@TO_ALL" -> traiterTO_ALL(message);
            case "@DEMARRER_PARTIE" -> lancerPartie();
            case "@CARTE_JOUEE" -> carteJouer(message);
            case "@FIN_TOUR" -> finTour();
            case "@PIOCHE" -> pioche();
            case "@ENCAISSE" -> encaisse();
            case "@UNO" -> uno();
            case "@MAIN" -> carteEnMain();
            case"@CARTE_TAS" -> carteAJOUER();
            case "@AQUILETOUR" -> tourDe();
            case "@JOUEURS" -> afficherTour();
            default -> System.err.println("Ce type de message nexiste pas : " + typeMessage);

        }
    }

    /**
     * donc la onfait appel a la focntion de la partie metier pour retrouver le jouers courant
     * puis recupere son nom et nombre de carte en main on envoie le protocole @AUTRES pour afficher
     * donc le jouers courant et le dos de ses carte donc 7 carte 7 carte retournes et cela au niveau de l'interface
     * @throws IOException
     */
    public void afficherTour() throws IOException {
        StringBuilder sb = new StringBuilder();

        Joueur j = serveur.getPartiedejeu().joueurCourant();
        sb.append(j.getNom()).append(":").append(j.getNbCarteEnMain());

        System.out.println(sb.toString());
        serveur.diffuserMessage("@AUTRES " + sb.toString());
    }

    /**
     * on fait appel a cet fonction au niveau de la methode fin tour comme ca a chaque foi qun joeurs fini son tour
     * on envoie les jeours et leurs nombre de carte
     * @throws IOException
     */
  public void joueursDeLapartie() throws IOException {

        StringBuilder sb = new StringBuilder();

        for (Joueur j : serveur.getPartiedejeu().getJoueursDelaPartie()) {
            sb.append(j.getNom()).append(" : ").append(j.getCrtEnMain().size()).append(" ; ");
        }
        if (sb.charAt(sb.length() - 1) == ';') {
            sb.deleteCharAt(sb.length() - 1);
        }
        System.out.println(sb.toString());

        serveur.diffuserMessage("@INFO Joueurs de la partie : " + sb.toString());
    }

    /**
     * on verifie que on bien plus de 2 joeurs et moins de 10 ; puis on cree une nouvelle partie on l'eregistre
     * dans le serveur puis on rajoute les utilisateurs en tant que jouers on increment le nombre de partie de chaque
     * joeurs dans la BD puis on envoie un protocole @LANCER a tout les jouers pour leurs changer de scene
     */
    public void lancerPartie() {
        // on verifie qu'il ya au minimum 2 joueurs
        if (serveur.getNbUsers() < 2 || serveur.getNbUsers() > 10 ){
            threadConnexion.envoyerMessageAuClient("@ERROR faut au min 2 joueurs et au max 10 ");
            return;
        }
        ArrayList<Joueur> joueurs = new ArrayList<>();
        for (Utilisateur u : serveur.users) {
            Joueur j = new Joueur(u.getPseudo());
            joueurs.add(j);
        }for (Utilisateur joueur : serveur.users) {
            UtilisateurDAO.incrementerNombreParties(joueur.getPseudo());
        }
        Partiedejeu nvPartie = new Partiedejeu();
        nvPartie.initialiserPartie(joueurs);
        serveur.setPartiedejeu(nvPartie);
        serveur.setPartieEnCour(true);
        serveur.messagePublic(this," Que la fête commence ");
        serveur.diffuserMessage("@LANCER");
    }

    /**
     * renvoie le nom du joeurs courant
     * @return
     */
    public String tourDe(){
        Joueur j = serveur.getPartiedejeu().joueurCourant();
        serveur.diffuserMessage("@INFO Tour du joueur : " + j);
        return "";
    }

    /**
     * donc on recupere les cartes du joeurs ou du client et on en enregistre les valeurs de ses cartes
     * et on envoie un Protocole @CRTENMAIN qu'on utilisera du cote client pour recuperer donc les valeurs de
     * chauqe carte et les transformer en photo
     * @return
     */
    public String carteEnMain() {
        List<Carte> main = serveur.getPartiedejeu().getMainDe(this.pseudo);

        StringBuilder sb = new StringBuilder();

        for (Carte c : main) {
            sb.append(c.getValeur()).append("_").append(c.getCouleur()).append(";");
        }
        if (!main.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        threadConnexion.envoyerMessageAuClient("@CRTENMAIN " + sb.toString());

        return "";
    }

    /**
     * on recupere la carte du tas et on l'envoie au client pour afficher la carte sous forme d'image
     */
    public void carteAJOUER(){
        Carte c = serveur.getPartiedejeu().carteDuTas();
        StringBuilder sb = new StringBuilder();
        sb.append(c.getValeur()).append("_").append(c.getCouleur());
        serveur.diffuserMessage("@DEFAUSSE " + sb.toString());
    }
    /**
     * envoie un message que le jouerus a dit UNO et on met le boolean de la partie metier a vrai
     * puis la verification se fera dans fin tour selon la logique de notre partie metier
     */
    public void uno(){
        Joueur j = serveur.getPartiedejeu().getJoueurDepuisPseudo(this.pseudo);
        j.DireUno(serveur.getPartiedejeu());
        serveur.diffuserMessage("@INFO " + this.pseudo + " a dit UNO ");
    }

    /**
     * donc la le joueurs encaisse l'attaque et envoie un message que le joeurs a bien encaisser l'attaque
     */
    public void encaisse(){
        try {
            int nb = serveur.getPartiedejeu().encaisserAttaque();
            serveur.diffuserMessage("@INFO " + this.pseudo + " a encaisser l'attaque de " + nb + " carte");
            serveur.diffuserMessage("@MAJ");
        } catch (PartieException e) {
            serveur.diffuserMessage("@EXCEPTION " + e.getMessage());
        }
    }

    /**
     * la on fait appel a la fonction de laprtie metier on verifie que le joeurs a bien le droit de piocher
     * si c'est le cas on envoie le message que le jeours x a piocher une carte ET AU jouerus qui
     * a piocher un message que la carte X a ete ajouter a votre main et on met ajour l'affichage
     */
    public void pioche(){
        try {
            Joueur j = serveur.getPartiedejeu().getJoueurDepuisPseudo(this.pseudo);
            Carte c = serveur.getPartiedejeu().piocherUneCarte(j);
            serveur.diffuserMessage("@INFO " + this.pseudo + " a piocher une carte") ;
            threadConnexion.envoyerMessageAuClient("@INFO la carte " + c + " a ete ajouter a votre main");
            threadConnexion.envoyerMessageAuClient("@MAJ ");
        }catch (PiocheException e){
            threadConnexion.envoyerMessageAuClient("@EXCEPTION " + e.getMessage());
            serveur.diffuserMessage("@MAJ " + e.getMessage());
        }
    }

    /**
     * on fait appel a la nouvelle fonction qu'on creer pour retourver le jouers grace a une chaine de caraccter
     *dans ce cas il s'agut du pseudo; on fait appel a la fonction de la partie metier on fait les verifiactions
     * que la partie n'est pas terminer donc le boolean finmanche dans la classe metier n'est pas vrai si la partie est
     * fini on enregistre le joueurs gagnant et on incremnet le nombre de victoire de ce joeuru dans la BD et on
     * envoie @FIN pour lancer la scne de fin de jeu ;
     * sinon on passe au jouerus suivant .
     */
    public void finTour() {
        try {
            Joueur joueur = serveur.getPartiedejeu().getJoueurDepuisPseudo(this.pseudo);
            if (joueur == null) {
                threadConnexion.envoyerMessageAuClient("@EXCEPTION Joueur introuvable.");
                return;
            }

            serveur.getPartiedejeu().finirTourDe(joueur);
            if (serveur.getPartiedejeu().isFinManche()){
                serveur.diffuserMessage("@INFO La partie est terminer ");
                serveur.diffuserMessage("@FIN " + pseudo);
                UtilisateurDAO.incrementerVictoire(pseudo);
                serveur.setPartieEnCour(false);
                return;
            }
            serveur.diffuserMessage("@INFO " + this.pseudo + " a fini son tour");
            threadConnexion.envoyerMessageAuClient(carteEnMain());
            serveur.diffuserMessage(serveur.getPartiedejeu().messageListeJoueurs());
            serveur.diffuserMessage(tourDe());
            carteAJOUER();
            joueursDeLapartie();
        } catch (PartieException | UnoException e) {
            threadConnexion.envoyerMessageAuClient("@EXCEPTION " + e.getMessage());
            serveur.diffuserMessage("@MAJ " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * on lit la carte jouer ex: DEUX VERT selon le protocole on verifie que le format est respecter
     * on caste le message sous forme de carte donc valeur et couleur et on fait appel a la fonction jouer
     * de la partie metier pour verifie la logique ainisi le serveur envoie MAJ pour mettre a jour l'affichage
     * au niveau de l interface et un message pour dire que le joueur X a jouer la carte X
     * @param message
     */
    public void carteJouer(String message) {
        try {
            String Carte = message.substring(13);
            String[] partie = Carte.split(" ");
            if (partie.length != 2) {
                threadConnexion.envoyerMessageAuClient("@EXCEPTION Format de carte incorrect ");
                return;
            }
            String valeur = partie[0];
            String couleur = partie[1];
            Carte carte = new Carte(Metier.LogiqueDeJeu.Carte.eValeur.valueOf(valeur.toUpperCase()),
                    Metier.LogiqueDeJeu.Carte.eCouleur.valueOf(couleur.toUpperCase()));
            serveur.getPartiedejeu().jouer(carte);
            serveur.diffuserMessage("@INFO " + this.pseudo + " a jouer la carte " +carte);
            threadConnexion.envoyerMessageAuClient("@MAJ ");
        } catch (PartieException | IllegalArgumentException e) {
            threadConnexion.envoyerMessageAuClient("@EXCEPTION " + e.getMessage());
            serveur.diffuserMessage("@MAJ " + e.getMessage());
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
    /**
     * Gère la connexion d’un joueur : vérifie le pseudo,
     * empêche la connexion en cours de partie, et ajoute l’utilisateur à la base de donnees »
     */
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

            serveur.messagePublic(this ,pseudo+ " a rejoint le serveur" );
        }
    }
    private void traiterDeconnexion() {
        try {
            this.serveur.messagePublic(this, "Je suis parti");
            this.socket.close();
            this.serveur.remove(this);
            this.threadConnexion.fin();
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


