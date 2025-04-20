import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartiedejeuTestCartePlusDeux {
    private static Pioche pioche;
    private static Defausse defausse;
    private static Joueur alice;
    private static Joueur bob;
    private static Joueur charles;
    private static ArrayList<Joueur> joueurs;
    private Partiedejeu partie;

    @BeforeEach
    void setUp() {
        alice = new Joueur("Alice");
        bob = new Joueur("Bob");
        charles = new Joueur("Charles");

        joueurs = new ArrayList<>();
        joueurs.add(alice);
        joueurs.add(bob);
        joueurs.add(charles);
        alice.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.NEUF, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.JAUNE)

        )));

        bob.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.SIX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE),
                new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU)
        )));

        charles.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT)
        )));
        pioche = new Pioche(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.ROUGE),
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.ZERO, Carte.eCouleur.BLEU)
        )));
        defausse = new Defausse(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.NEUF, Carte.eCouleur.VERT)
        )));
        partie = new Partiedejeu(joueurs, true, 0, pioche, defausse);
    }
@Test
public void testDunCoupeLegaleAvecUneCartePlusDEUX() {
    assertEquals(alice,partie.joueurCourant());
    Carte plus2Vert = new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT);
    partie.jouer(plus2Vert);
    partie.finirTourDe(alice);
    assertEquals(bob,partie.joueurCourant());
    assertEquals(3,bob.getNbCarteEnMain());
    partie.encaisserAttaque();
    assertEquals(5,bob.getNbCarteEnMain());
    assertEquals(charles,partie.joueurCourant());
    Carte unVert = new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT);
    partie.jouer(unVert);
    partie.finirTourDe(charles);
    assertEquals(2,charles.getNbCarteEnMain());
    }
@Test
public void testDunCoupLegalavecCumuldeCartesPlus2 (){
        assertEquals(alice,partie.joueurCourant());
        partie.piocherUneCarte(alice);
        assertEquals(4,alice.getNbCarteEnMain());
        partie.finirTourDe(alice);
        assertEquals(bob,partie.joueurCourant());
        partie.piocherUneCarte(bob);
        partie.finirTourDe(bob);
        assertEquals(charles,partie.joueurCourant());
        Carte plus2Vert = new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT);
        partie.jouer(plus2Vert);
        partie.finirTourDe(charles);
        assertEquals(alice,partie.joueurCourant());
        partie.jouer(plus2Vert);
        partie.finirTourDe(alice);
        assertEquals(bob,partie.joueurCourant());
        assertEquals(4,bob.getNbCarteEnMain());
        partie.encaisserAttaque();
        assertEquals(8,bob.getNbCarteEnMain());
}
}
