package Metier.LogiqueDeJeu;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Carte {

    public enum eValeur {ZERO, UN, DEUX, TROIS, QUATRE, CINQ, SIX, SEPT, HUIT, NEUF, PLUS_2, PLUS_4, PASSE, CHANGEMENT_SENS,CHANGEMENT_COULEUR} ;
    public enum eCouleur {ROUGE, VERT, BLEU, JAUNE, NOIR};

    private eValeur valeur;
    private eCouleur couleur;

    public Carte(eValeur valeur, eCouleur couleur) {
        this.valeur = valeur;
        this.couleur = couleur;
    }

    public eValeur getValeur() {
        return valeur;
    }

    public eCouleur getCouleur() {
        return couleur;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Carte carte)) return false;
        return valeur == carte.valeur && couleur == carte.couleur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valeur, couleur);
    }

    @Override
    public String toString() {
        return "Carte{" +
                "valeur=" + valeur +
                ", couleur=" + couleur +
                '}';
    }
    public static ArrayList<Carte> initialiserCarteDuJeu() {
        ArrayList<Carte> cartes = new ArrayList<>();

        for (eCouleur couleur : eCouleur.values()) {
            if (couleur == eCouleur.NOIR) continue;

            // 1 ZERO
            cartes.add(new Carte(eValeur.ZERO, couleur));

            // 2 cartes pour 1 à 9
            for (eValeur v : List.of(
                    eValeur.UN, eValeur.DEUX, eValeur.TROIS, eValeur.QUATRE, eValeur.CINQ,
                    eValeur.SIX, eValeur.SEPT, eValeur.HUIT, eValeur.NEUF)) {
                cartes.add(new Carte(v, couleur));
                cartes.add(new Carte(v, couleur));
            }

            // Cartes spéciales x2
            cartes.add(new Carte(eValeur.PLUS_2, couleur));
            cartes.add(new Carte(eValeur.PLUS_2, couleur));
            cartes.add(new Carte(eValeur.CHANGEMENT_SENS, couleur));
            cartes.add(new Carte(eValeur.CHANGEMENT_SENS, couleur));
            cartes.add(new Carte(eValeur.PASSE, couleur));
            cartes.add(new Carte(eValeur.PASSE, couleur));
        }

        // Cartes noires (spéciales)
        for (int i = 0; i < 4; i++) {
            cartes.add(new Carte(eValeur.CHANGEMENT_COULEUR , eCouleur.NOIR));
            cartes.add(new Carte(eValeur.PLUS_4, eCouleur.NOIR));
        }

        // Mélanger aléatoirement
        Collections.shuffle(cartes);
        return cartes;
    }
    public static Image getImageFromCarte(Carte carte) {
        String nomFichier = carte.getValeur() + "_" + carte.getCouleur() + ".png";
        String chemin = "/" + nomFichier;
        return new Image(Carte.class.getResourceAsStream(chemin));
    }

}
