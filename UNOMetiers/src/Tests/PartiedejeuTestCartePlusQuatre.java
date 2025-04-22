package Tests;

import Exceptions.PartieException;
import LogiqueDeJeu.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartiedejeuTestCartePlusQuatre {
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
                new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT),
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
                new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT)
        )));
        pioche = new Pioche(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.ROUGE),
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.ZERO, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT),
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
    public void TestCoupLegaleAvecCartePlus4 (){
        assertEquals(alice,partie.joueurCourant());
        Carte plus4Vert = new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT);
        Carte Jaune6 = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        partie.jouer(plus4Vert);
        partie.finirTourDe(alice);
        Assertions.assertEquals(bob,partie.joueurCourant());
        Assertions.assertEquals(3,bob.getNbCarteEnMain());
        assertThrows(PartieException.class ,() -> partie.jouer(Jaune6));
        partie.encaisserAttaque();
        Assertions.assertEquals(7,bob.getNbCarteEnMain());
        Assertions.assertEquals(charles,partie.joueurCourant());
        Carte unVert = new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT);
        partie.jouer(unVert);
        partie.finirTourDe(charles);
        Assertions.assertEquals(2,charles.getNbCarteEnMain());
    }
    @Test
    public void testDunCoupLegalavecCumuldeCartesPlus4 (){
        Assertions.assertEquals(alice,partie.joueurCourant());
        partie.piocherUneCarte(alice);
        Assertions.assertEquals(4,alice.getNbCarteEnMain());
        partie.finirTourDe(alice);
        Assertions.assertEquals(bob,partie.joueurCourant());
        partie.piocherUneCarte(bob);
        partie.finirTourDe(bob);
        Assertions.assertEquals(charles,partie.joueurCourant());
        Carte plus4Vert = new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT);
        partie.jouer(plus4Vert);
        partie.finirTourDe(charles);
        Assertions.assertEquals(alice,partie.joueurCourant());
        partie.jouer(plus4Vert);
        partie.finirTourDe(alice);
        Assertions.assertEquals(bob,partie.joueurCourant());
        Assertions.assertEquals(4,bob.getNbCarteEnMain());
        partie.encaisserAttaque();
        assertEquals(12,bob.getNbCarteEnMain());
    }
}