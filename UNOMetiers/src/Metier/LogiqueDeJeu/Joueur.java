package Metier.LogiqueDeJeu;

import java.util.ArrayList;
import java.util.Objects;
/*Dans cette classe on fait pas de test ni de verification (elles sont faites dans la classe Partie)
cette classe elle est simple on gere le nom du joeurs les cartes (donc ajouter une carte et retirer une carte de sa main)
on peut dire aussi Uno
 */

public class Joueur {
    private String nom;
    private ArrayList<Carte> cartesEnMain = new ArrayList();
    private boolean aDitUno;
    public Joueur(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<Carte> getCrtEnMain() {
        return cartesEnMain;
    }
    //on verifie bien que la carte est presente dans la main du jouers
    public boolean aLaCarte(Carte c) {
        return cartesEnMain.contains(c);
    }
    public int getNbCarteEnMain(){
        return cartesEnMain.size();
    }

    public void initialiserMain(ArrayList<Carte> listCartes) {
        if (listCartes==null) throw new IllegalArgumentException("liste est vide pour initialiser la main");
        cartesEnMain.clear();
        getCrtEnMain().addAll(listCartes);

    }
    //on retire la carte au joueur la vérification se fera dans la classe partie
    public Carte jouerCarte(Carte carte) {
        if (cartesEnMain.contains(carte)) {
            cartesEnMain.remove(carte);
            return carte;
        }else {
            throw new IllegalArgumentException("Tu ne dispose pas de cette carte");
        }


    }
    //on ajoute une carte au joueur
    public void ajouterUneCarte(Carte carte) {
        cartesEnMain.add(carte);
    }

// on retourne vrai si le joeurs a dit UNO encore une fois le test si il a une carte etc.. se fera dans Partie
    public boolean isaDitUno() {
        return aDitUno;
    }

    public void setaDitUno(boolean aDitUno) {
        this.aDitUno = aDitUno;
    }
//Le joueurs dit Uno on met le boolean VRai
    public void DireUno (Partiedejeu p) {
        aDitUno = true;
        p.direUno(this);
    }
    //ajouter pour plustarde Pour faire les tests de fin de Partie mais utile pour le moment
    public boolean aGagner (){
        if (cartesEnMain.isEmpty()) return true;
        return false;
    }
    /*cette fonction fait la meme chose que la méthode finiTourDe dans Partie on la ajouter
    ici dans jeourrs son le nom de FinirSon tour pour plus de logique et de lisbilite surout
    pour les tests afin de montrer que c'est le joueur qui met volontairement fin a sa partie
     */
    public void finirSonTour(Partiedejeu partie) {
        partie.finirTourDe(this);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Joueur joueur)) return false;
        return Objects.equals(nom, joueur.nom) && Objects.equals(cartesEnMain, joueur.cartesEnMain);
    }

    @Override
    public String toString() {
        int nb = getNbCarteEnMain();
        return "Joueur{" +
                "nom='" + nom + '\'' + "Carte= " + nb +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, cartesEnMain);
    }

}