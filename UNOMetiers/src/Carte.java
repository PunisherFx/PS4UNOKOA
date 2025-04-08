import java.util.Objects;

public class Carte {

    public enum eValeur {ZERO, UN, DEUX, TROIS, QUATRE, CINQ, SIX, SEPT, HUIT, NEUF, PLUS_2, PLUS_4, PASSE, CHANGEMENT_SENS} ;
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
}
