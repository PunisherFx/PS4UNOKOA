package Metier.LogiqueDeJeu;

import Metier.Exceptions.PartieException;
import Metier.Exceptions.PiocheException;
import Metier.Exceptions.UnoException;
import serveur.serveurMetier.ServeurUno;

import java.util.ArrayList;
import java.util.List;

public class Partiedejeu {
    private ArrayList<Joueur> joueursDelaPartie = new ArrayList<>();
    private boolean sensHoraire;
    private int indiceDuJoueurCourant;
    private Pioche pioche;
    private Defausse tas;
    private boolean tourCourant ;
    private boolean aFaitUneAction;
    private boolean controleDeCartePasseTT;
    private boolean effetPlus2;
    private boolean effetPlus4;
    private int compteurCarteSpe = 1 ;
    private boolean finManche;
    private Joueur vainqueur;
    private Carte carte;
    private ArrayList<Carte> carteDuJeu = new ArrayList<>();
   //ti private ServeurUno serveur;

    public Partiedejeu() {
    }
    public ArrayList<Joueur> getJoueursDelaPartie() {
        return joueursDelaPartie;
    }

    public Joueur getVainqueur() {
        return vainqueur;
    }

    public void setFinManche(boolean finManche) {
        this.finManche = finManche;
    }

    public Partiedejeu(ArrayList<Joueur> joueursDelaPartie, boolean sensHoraire, int indiceDuJoueurCourant, Pioche pioche, Defausse tas) {
        this.joueursDelaPartie = joueursDelaPartie;
        this.sensHoraire = sensHoraire;
        this.indiceDuJoueurCourant = 0;
        this.pioche = pioche;
        this.tas = tas;
        this.tourCourant = true;
    }

   /* public void initialiserPartie(ArrayList<Joueur> joueursDelaPartie, boolean sensHoraire, int indiceDuJoueurCourant, Pioche pioche, Defausse tas){
        this.joueursDelaPartie = joueursDelaPartie;
        this.sensHoraire = sensHoraire;
        this.indiceDuJoueurCourant = 0;
        this.tourCourant = true;
        this.pioche = new Pioche();
        this.pioche.initialiser(Carte.initialiserCarteDuJeu());
        for (Joueur j : joueursDelaPartie) {
            for (int i = 0; i < 7; i++) {
                j.ajouterUneCarte(pioche.depiler());
            }
        }
        this.tas = new Defausse();
        this.tas.poserUneCarte(this.pioche.depiler());
    }*/

    public boolean isFinManche() {
        return finManche;
    }

    public void initialiserPartie(ArrayList<Joueur> joueurs) {
        if (joueurs==null) throw new IllegalArgumentException("liste est vide pour initialiser la main");
        joueursDelaPartie.clear();
        joueursDelaPartie.addAll(joueurs);
        this.sensHoraire = sensHoraire;
        this.indiceDuJoueurCourant = 0;
        this.tourCourant = true;
        this.pioche = new Pioche();
        this.pioche.initialiser(Carte.initialiserCarteDuJeu());
        for (Joueur j : joueursDelaPartie) {
            for (int i = 0; i < 7; i++) {
                j.ajouterUneCarte(pioche.depiler());
            }
        }
        this.tas = new Defausse();
        this.tas.poserUneCarte(this.pioche.depiler());

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
        if (finManche) {return;}
        int nbJoueurs = joueursDelaPartie.size();

        if (sensHoraire) {
            indiceDuJoueurCourant = (indiceDuJoueurCourant + 1) % nbJoueurs;
        } else {
            indiceDuJoueurCourant = (indiceDuJoueurCourant - 1 + nbJoueurs) % nbJoueurs;
        }

        tourCourant = true;
        aFaitUneAction = false;
    }
    public void jouer(Carte c) {
        if (finManche) {
            throw new PartieException("la manche est fini");
        }
        if (tourCourant == false) {
            throw new PartieException("Vous n'avez pas le droit de jouer");
        }
        Joueur j = joueurCourant();
        Carte carteJeu = tas.carteAJouer();
        if (effetPlus2 ){
            if (j.aLaCarte(c) && c.getValeur()== Carte.eValeur.PLUS_2){
                compteurCarteSpe++;
                effetPlus2 = false;
            }else {
                encaisserAttaque();
                throw new PartieException("Vous ne pouvez pas jouer; vous avez subi une attaque 2 carte de la pioche en etaient ajoutees ");
            }
        }
        if(effetPlus4){
            if (j.aLaCarte(c) && c.getValeur()== Carte.eValeur.PLUS_4){
                compteurCarteSpe++;
                effetPlus4 = false;
            }else {
                encaisserAttaque();
                throw new PartieException("Vous ne pouvez pas jouer; vous avez subi une attaque 2 carte de la pioche en etaient ajoutees ");
            }
        }
        if (j.aLaCarte(c)) {
            if ((c.getValeur() == carteJeu.getValeur()) || (c.getCouleur() == (carteJeu.getCouleur()))) {
                j.jouerCarte(c);
                tas.poserUneCarte(c);
                aFaitUneAction = true;
                tourCourant = false;
               // serveur.diffuserMessage("carte jouer" +c);
            }else {
                if (carteJeu.getValeur() == Carte.eValeur.PASSE){
                    aFaitUneAction = true;
                    tourCourant = false;
                    controleDeCartePasseTT = true;
                   // tas.poserUneCarte(c);
                }else if (c.getValeur() == Carte.eValeur.PASSE){
                    aFaitUneAction = true;
                    tourCourant = false;
                    controleDeCartePasseTT = true;
                }
                else{
                punir(joueurCourant());
                throw new IllegalArgumentException("Tu ne peux pas jouer cette carte");
            }
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
        if (finManche) {
            throw new PartieException("la manche est fini");
        }
        Carte c = tas.carteAJouer();
        if (joueur != joueurCourant()) {
            throw new PartieException("Ce n'est pas à vous de finir le tour !");
        } else if (!aFaitUneAction) {
            throw new PartieException("Vous devez jouer Une carte si vous n'avez pas de carte piochezz");
        } else if (effetPlus2) {
                encaisserAttaque();
            throw new PartieException("Ta encaisser L'attaque +2");
        } else if (effetPlus4) {
        encaisserAttaque();
        throw new PartieException("Ta encaisser L'attaque +4");
        }
        if (joueur.getNbCarteEnMain() == 1 && joueur.isaDitUno()==false) {
            punir(joueur);
            joueur.ajouterUneCarte(c);
            tas.rendreLaCarte();
            throw new UnoException("a oublier de dire UNO");
        }else if (joueur.getNbCarteEnMain() == 1 && joueur.isaDitUno()==true){
            joueur.setaDitUno(true);
        }else if (joueur.getNbCarteEnMain() > 1 && joueur.isaDitUno()==true){
            punir(joueur);
            joueur.ajouterUneCarte(c);
            tas.rendreLaCarte();
            throw new UnoException("c'est pas bien de mentir ; vous avez subi une punition +2 et votre carte n'est pas accepter");
        }
        if (controleDeCartePasseTT){
            passeTour();
            throw new PartieException("pas le droit de jouer cette carte vous ne serez pas penaliser,mais votre tour est fini");
        }
        if (c.getValeur() == Carte.eValeur.PLUS_2){
            effetPlus2 = true;
        }
        if (c.getValeur() == Carte.eValeur.PLUS_4){
            effetPlus4 = true;
        }
        if(c.getValeur()== Carte.eValeur.CHANGEMENT_SENS){
            if (sensHoraire){
                sensHoraire = false;
            }else{
                sensHoraire = true;
            }
        }
        if (joueur.getNbCarteEnMain()== 0){
            finManche = true;
            vainqueur = joueurCourant();
        }
        passeTour();
        if (c.getValeur()== Carte.eValeur.PASSE){
            passeTour();
        }

    }
    private Carte ajouterLaCartePioche(Joueur joueur){
        Carte c = pioche.depiler();
        joueur.ajouterUneCarte(c);
        return c;
    }

    public Carte piocherUneCarte(Joueur joueur) {
        if (finManche) {
            throw new PartieException("la manche est fini");
        }
        Joueur j = joueurCourant();
        if (effetPlus2){
            ajouterLaCartePioche(j);
            ajouterLaCartePioche(j);
            effetPlus2 = false;
            passeTour();
        }
        if (effetPlus4){
            ajouterLaCartePioche(j);
            ajouterLaCartePioche(j);
            ajouterLaCartePioche(j);
            ajouterLaCartePioche(j);
            effetPlus4 = false;
            passeTour();
        }
        if (aFaitUneAction){
            Carte c = tas.carteAJouer();
            tas.rendreLaCarte();
            throw new PiocheException("Vous ne pouvez pas piocher vous avez deja jouer  ");
        }
       if (tourCourant == false ||!joueur.equals(joueurCourant())){
           ajouterLaCartePioche(joueur);
           ajouterLaCartePioche(joueur);
           throw new PiocheException("Vous ne pouvez pas piocher ce n'est pas votre tour ");
       }
       Carte c = pioche.getCarteAPiocher();
               pioche.depiler();
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
     public int encaisserAttaque() {
         if (finManche) {
             throw new PartieException("la manche est fini");
         }
         if (effetPlus2) {
             Joueur joueur = joueurCourant();
             int nb = compteurCarteSpe * 2;
             for (int i = 0; i < nb; i++) {
                 Carte carte = pioche.getCarteAPiocher();
                 pioche.depiler();
                 joueur.ajouterUneCarte(carte);
             }

             effetPlus2 = false;
             passeTour();
             return nb;
         }

         if (effetPlus4) {
             Joueur joueur = joueurCourant();
             int nb = compteurCarteSpe * 4;
             for (int i = 0; i < nb; i++) {
                 Carte carte = pioche.getCarteAPiocher();
                 pioche.depiler();
                 joueur.ajouterUneCarte(carte);
             }

             effetPlus4 = false;
             passeTour();
             return nb;
         }
         return 0;
         }
    public List<Carte> getMainDe(String pseudo) {
        for (Joueur j : joueursDelaPartie) {
            if (j.getNom().equals(pseudo)) {
                return j.getCrtEnMain();
            }
        }
        throw new PartieException("Main vide");
    }
    public Carte carteDuTas(){
        return  tas.carteAJouer();
    }
    /* cette methode consiste a trouve un jouers a partir de son pseudo */
    public Joueur getJoueurDepuisPseudo(String pseudo) {
        for (Joueur j : joueursDelaPartie) {
            if (j.getNom().equals(pseudo)) {
                return j;
            }
        }
        return null;
    }
    public String messageListeJoueurs() {
        StringBuilder sb = new StringBuilder("@LISTE_JOUEURS");

        for (Joueur j : joueursDelaPartie) {
            sb.append(" (").append(j.getNom()).append(";").append(j.getCrtEnMain().size()).append(")");
        }

        return sb.toString();
    }



}

