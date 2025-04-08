import java.util.ArrayList;
import java.util.Objects;

import static java.lang.System.exit;

public class Partiedejeu {
    private ArrayList<Joueur> joueursDelaPartie = new ArrayList<>();
    private boolean sensHoraire;
    private int indiceDuJoueurCourant;
    private Pioche pioche;
    private Defausse tas;
    private boolean tourCourant ;
    private boolean aFaitUneAction;

    public Partiedejeu(ArrayList<Joueur> joueursDelaPartie, boolean sensHoraire, int indiceDuJoueurCourant, Pioche pioche, Defausse tas) {
        this.joueursDelaPartie = joueursDelaPartie;
        this.sensHoraire = sensHoraire;
        this.indiceDuJoueurCourant = 0;
        this.pioche = pioche;
        this.tas = tas;
        this.tourCourant = true;
    }

    public Joueur joueurCourant() {
        if (indiceDuJoueurCourant >= 0 && indiceDuJoueurCourant < joueursDelaPartie.size()) {
            //tourCourant = true;
            return joueursDelaPartie.get(indiceDuJoueurCourant);
        } else {
            throw new PartieException("Le joueur n'est pas dans la liste des joueurs");
        }
    }
    public void passeTour() {
        int nbJoueurs = joueursDelaPartie.size();

        if (sensHoraire) {
            indiceDuJoueurCourant = (indiceDuJoueurCourant + 1) % nbJoueurs;
        } else {
            indiceDuJoueurCourant = (indiceDuJoueurCourant - 1 + nbJoueurs) % nbJoueurs;
        }

        tourCourant = true;
    }
    public void jouer(Carte c) {
        if (tourCourant == false) {
            throw new PartieException("Vous n'avez pas le droit de jouer");
        }
        Joueur j = joueurCourant();
        Carte carteJeu = tas.carteAJouer();
        if (j.aLaCarte(c)) {
            if ((c.getValeur() == carteJeu.getValeur()) || (c.getCouleur() == (carteJeu.getCouleur()))) {
                j.jouerCarte(c);
                tas.poserUneCarte(c);
                aFaitUneAction = true;
                tourCourant = false;
            } else {
                punir(joueurCourant());
                throw new IllegalArgumentException("Tu ne peux pas jouer cette carte");
            }
        }else {
            throw new PartieException("vous ne possedez pa cette carte");
        }
    }
    public void punir(Joueur joueur){
        try {
            ajouterLaCartePioche(joueur);
            ajouterLaCartePioche(joueur);
            passeTour();
        }catch (PiocheException e) {
            throw new PiocheException("la pioche est vide");
        }
    }
    public void finirTourDe (Joueur joueur){
        if (joueur != joueurCourant()) {
            throw new PartieException("Ce n'est pas à vous de finir le tour !");
        } else if (!aFaitUneAction) {
            throw new PartieException("Vous devez jouer Une carte si vous n'avez pas de carte piochezz");
        }
        if (joueur.getNbCarteEnMain() == 1 && joueur.isaDitUno()==false) {
            punir(joueur);
            Carte c = tas.carteAJouer();
            joueur.ajouterUneCarte(c);
            tas.rendreLaCarte();
            throw new UnoException("a oublier de dire UNO");
        }else if (joueur.getNbCarteEnMain() == 1 && joueur.isaDitUno()==true){
            joueur.setaDitUno(true);
        }else{
            throw new UnoException("il vous reste plus d'une carte");
        }
        passeTour();

    }
    private Carte ajouterLaCartePioche(Joueur joueur){
        Carte c = pioche.depiler();
        joueur.ajouterUneCarte(c);
        return c;
    }

    public Carte piocherUneCarte(Joueur joueur) {
       if (tourCourant == false ||!joueur.equals(joueurCourant())){
           ajouterLaCartePioche(joueur);
           ajouterLaCartePioche(joueur);
           throw new PiocheException("Vous ne pouvez pas piocher ce n'est pas votre tour ");
       }
        Carte c = pioche.depiler();
       Joueur j = joueurCourant();
       j.ajouterUneCarte(c);
       aFaitUneAction = true;
       return c;
    }
    // cette fonction pour gerer uniquement quand un joueur dit UNO alors que ce n'est pas son tour
    //pour les verification de UNO elle sont faits dans la fonction finitTourDe
    public void direUno(Joueur j){
        if (joueurCourant() != j && j.isaDitUno()==true) {
            ajouterLaCartePioche(j);
            ajouterLaCartePioche(j);
            j.setaDitUno(false);
            throw new UnoException("Ce n'est pas votre tour vous ne pouvez pas dire UNO");
        }
    }

}
