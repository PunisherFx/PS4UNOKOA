package serveur.reseau;

import serveur.serveurMetier.ServeurUno;

import java.net.Socket;
import java.util.Objects;
import java.util.regex.Pattern;

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
        if (!(o instanceof Utilisateur that)) return false;
        return Objects.equals(pseudo, that.pseudo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pseudo);
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

    private final static String regexMP_FROM = "^@MP_FROM \\p{Alnum}+ .*$";
    private final static String regexPUBLIC_FROM = "^@PUBLIC_FROM \\p{Alnum}+ .*$";
    private final static String regexERROR = "^@ERROR .*$";

    private final static String[] protocole = {regexCONNEXION, regexDECONNEXION, regexMP_TO, regexTO_ALL,
            regexDEMARRER, regexCARTE_JOUEE, regexFIN_TOUR, regexPIOCHE};

    public void controlerMessage(String message) {
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
            case "@@DEMARRER_PARTIE" -> lancerPartie();

        }
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

        if (getPseudo().equals(serveur.get(pseudo))) {
            threadConnexion.envoyerMessageAuClient("@ERROR Ce pseudo est déjà utilisé.");
            return;
        }
        this.pseudo = pseudo;
        this.valide = true;
        threadConnexion.envoyerMessageAuClient("@CONNEXION_OK Bienvenue " + pseudo + " !");
        serveur.add(this);
        serveur.diffuser("@PUBLIC_FROM Serveur " + pseudo + " a rejoint la partie !");
    }

}


