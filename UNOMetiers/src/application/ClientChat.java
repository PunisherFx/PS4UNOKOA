package application;

import Metier.LogiqueDeJeu.Joueur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * La classe ClientChat crée un thread pour traiter les messages reçus du serveur (voir ThreadConsole).
 * La classe est centrée
 */
public class ClientChat {
    private static final String SERVEUR = "localhost";
    private static final int PORT = 4567;
    private SceneJeuController sceneJeu;
    AppClient app;
    private ThreadConsole threadConsole; // gère la récepotion des messages du serveur
    private Socket socket; // La connexion
    private PrintWriter out; // Le flux vers le serveur (le flux d'entrée est unqiement utile dans le thread)
    private String pseudo; // Le pseudo de ce client

    public AppClient getApp() {
        return this.app;
    }

    public void setSceneJeuController(SceneJeuController controller) {
        this.sceneJeu = controller;
    }
    public String getPseudo() {
        return this.pseudo;
    }

    /**
     * pour se connecter au serveur
     * @param appClient
     */
    public ClientChat(AppClient appClient) {
        this.app = appClient;
        // TODO A vous d'initialiser ce qu'il faut...
        try {
            this.socket = new Socket(SERVEUR, PORT);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.threadConsole = new ThreadConsole(this);
            this.threadConsole.start();
        } catch (IOException e) {
            app.afficherConsole("Erreur : Impossible de se connecter au serveur.");
        }

    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * La méthode afficherMessage est appelée par le threadConsole lorsqu'un message a été reçu du serveur.
     * on somme dans la classe utilisateur la fonction diffuser lmessage et envoyer message au client
     * envoie des protocoles c'est ou on les detcte pour faire ce quil faut au niveau de l'interface
     * @param message Le message reçu du serveur
     */
    public void afficherMessage(String message) {
        System.out.println("📨 Message reçu : " + message);
        String[] mots = message.split(" ");
        switch (mots[0]) {
            case "@PUBLIC_FROM" -> {
                afficherMessagePublic(mots);
            }
            case "@MP_FROM" -> afficherMessagePrive(mots);
            case "@TOURDE" ->{
                /**
                 * afficher dans la console le tour du joeurs courant
                 */
                sceneJeu.afficherConsole(message);
            }
            case "@ERROR" ->{
                /**
                 * on l'utilise que dans le minochat
                 */
                afficherErreur(mots);
            }
            case "@INFO" -> Platform.runLater(() -> {
                String msg = message.substring("@INFO".length());
                 sceneJeu.afficherConsole(msg);
            });
            case "@LANCER" -> javafx.application.Platform.runLater(() -> {
                /**
                 * donc la lorsque on envoie demarrer partie le serveru renvoie @LANCER et la on fait appel a la fonction
                 * pour changer de scen et afficher la scen du jeu
                 */
                app.changerScene();
            });
            case "@CRTENMAIN" -> {
                /**
                 * afficher les cartes en lain de chaque joeurs
                 */
                String donnees = message.substring("@CRTENMAIN ".length());
                Platform.runLater(() -> sceneJeu.ajouterCarteDansMain(donnees));
                return;
            }
            /**
             * AUTRES pour afficher le jouers courant et ses cartes retournes
             */
            case "@AUTRES" -> Platform.runLater(() -> sceneJeu.afficherJoueurs(message));
            /**
             * la on utilisera pour afficher les exception our les erruers mais quand dans la console
             * du jeu et non dans le mini chat
             */
            case"@EXCEPTION" ->{
                String msg = message.substring("@EXCEPTION ".length());
                Platform.runLater(() -> sceneJeu.afficherConsole(msg));
            }
            /**
             * pour charger la scne de fin du jeu et on stocje le joeurs gagnat recu de servvr dans gagnat du controleur
             */
            case"@FIN" ->{
                String msg = message.substring("@FIN ".length());
                Platform.runLater(() -> sceneJeu.setGagnant(msg));
                Platform.runLater(() ->  sceneJeu.changerSceneFin());
                return;
            }
            /**
             * mettre  a jour l'affichage
             */
            case "@MAJ" -> Platform.runLater(() -> sceneJeu.miseajour());
            /**
             * dans la snce de fin de jeu on affiche le vainquer precedemment stocker dans le servuer
             */
            case "@VAINQUEUR" -> {
                String donnees = message.substring("@VAINQUEUR".length());
                Platform.runLater(() -> sceneJeu.setGagnant(donnees));
                return;
            }
            /**
             * afficher la carte du tas
             */
            case"@DEFAUSSE" ->{
                String donnees = message.substring("@DEFAUSSE ".length());
                Platform.runLater(() -> sceneJeu.ajouterCarteTas(donnees));
                return;
            }
        }
    }
    public void messageauserveur(String message) {
        out.println(message);
        out.flush();
    }

    /**
     * on envoi vers le serveur @De.. pour lancer une nouvelle partie on soit c'est le serveur qui lance la partie
     * verifie que tout est nickel puis le serveur renvoie un autres ortocle pour charger la scene
     */
    public void lancerUnePartie() {
                out.println("@DEMARRER_PARTIE");
                out.flush();
    }
    private void afficherMessagePrive(String[] mots) {
        String str = "**" + mots[1] + "** : " + getContenu(Arrays.copyOfRange(mots, 2, mots.length));
        app.afficherConsole(str);
    }

    private void afficherMessagePublic(String[] mots) {
        String str = mots[1] + " : " + getContenu(Arrays.copyOfRange(mots, 2, mots.length));
        app.afficherConsole(str);
    }

    private void afficherErreur(String[] mots) {
        String str = "!!!ERREUR!!! : " + getContenu(Arrays.copyOfRange(mots, 1, mots.length));
        app.afficherConsole(str);
    }

    private String getContenu(String[] mots) {
        String str = "";

        for (int i = 0; i < mots.length - 1; i++) {
            str += mots[i] + " ";
        }
        str += mots[mots.length - 1];

        return str;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer la connexion. Cette méthode est appelée
     * par le bouton "Connexion!"
     */
    public void envoyerConnexion() {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            app.afficherConsole("Erreur : Veuillez entrer un pseudo.");
            return;
        }
        if (out == null) {
            app.afficherConsole("Erreur : Connexion non initialisée.");
            return;
        }
        out.println("@CONNEXION " + pseudo);
        out.flush();
        app.afficherConsole("Vous êtes connecté en tant que : " + pseudo);
    }


    /**
     * Mise en forme de la phrase du protocole pour déclarer la déconnexion. Cette méthode est appelée
     * par le bouton de fermeture de la fenêtre
     */
    public void envoyerDeconnexion() {
        out.println("@DECONNEXION");
        out.flush();
        app.afficherConsole("Vous vous êtes déconnecté.");
        try {
            socket.close();
        } catch (IOException e) {
            app.afficherConsole("Erreur lors de la fermeture du socket.");

            // TODO
        }
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer l'envoi d'un message public.
     * Cette méthode est appelée par le bouton "Public"
     *
     * @param message C'est le contenu du la zone de message
     */
    public void envoyerMessagePublic(String message) {
        if (message == null || message.trim().isEmpty()) {
            app.afficherConsole("Erreur : Le message ne peut pas être vide.");
            return;
        }
        out.println("@TO_ALL " + message);
        out.flush();
        app.afficherConsole("Message public envoyé : " + message);

        // TODO
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer l'envoi d'un message public.
     * Cette méthode est appelée par le bouton "Public". Si le pseudo est vide, alors on affiche
     * un message d'erreur dans la zone console
     *
     * @param message    C'est le contenu du la zone de message
     * @param pseudoDest C'est le pseudo du destinaire trouvé dans la zone pseudo
     */
    public void envoyerMessagePrive(String pseudoDest, String message) {
        // TODO
        if (pseudoDest == null || pseudoDest.trim().isEmpty()) {
            app.afficherConsole("Erreur : Le pseudo du destinataire ne peut pas être vide.");
            return;
        }

        if (message == null || message.trim().isEmpty()) {
            app.afficherConsole("Erreur : Le message ne peut pas être vide.");
            return;
        }

        out.println("@MP_TO " + pseudoDest + " " + message);
        out.flush();
        app.afficherConsole("Message privé envoyé à " + pseudoDest + " : " + message);
    }
}
