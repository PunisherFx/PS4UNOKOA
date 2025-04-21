package LogiqueDeJeu;

import java.util.ArrayList;
import java.util.Objects;

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
    public boolean aLaCarte(Carte c) {
        return cartesEnMain.contains(c);
    }
    public int getNbCarteEnMain(){
        return cartesEnMain.size();
    }
    public ArrayList getCartesJouables(Carte carteDuTas){
        ArrayList<Carte> CartesJouables = new ArrayList();
        for (Carte carte : cartesEnMain) {
            if ((carte.getCouleur().equals(carteDuTas.getCouleur()))||carte.getValeur().equals(carteDuTas.getValeur())){
            CartesJouables.add(carte);
            }
        }
        return CartesJouables;
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
    /*public Carte piocherUneCarte(Pioche pioche) throws PiocheException {
        Carte c = pioche.depiler();
        cartesEnMain.add(c);
        return c;
    }*/
    public void ajouterUneCarte(Carte carte) {
        cartesEnMain.add(carte);
    }
    /*public boolean UNO (){
        if (cartesEnMain.size()==1) return true;
        return false;
    }*/

    public boolean isaDitUno() {
        return aDitUno;
    }

    public void setaDitUno(boolean aDitUno) {
        this.aDitUno = aDitUno;
    }

    public void DireUno (Partiedejeu p) {
        aDitUno = true;
        p.direUno(this);
    }
    public boolean aGagner (){
        if (cartesEnMain.isEmpty()) return true;
        return false;
    }
    public void finirSonTour(Partiedejeu partie) {
        partie.finirTourDe(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Joueur joueur)) return false;
        return Objects.equals(nom, joueur.nom) && Objects.equals(cartesEnMain, joueur.cartesEnMain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, cartesEnMain);
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "nom='" + nom + '\'' +
                ", cartesEnMain=" + cartesEnMain +
                '}';
    }
}