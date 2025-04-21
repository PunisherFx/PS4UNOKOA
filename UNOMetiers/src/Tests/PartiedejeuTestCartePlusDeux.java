package Tests;

import static org.junit.jupiter.api.Assertions.*;

import Exceptions.*;
import LogiqueDeJeu.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


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
    Assertions.assertEquals(alice,partie.joueurCourant());
    Carte plus2Vert = new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT);
    partie.jouer(plus2Vert);
    partie.finirTourDe(alice);
    Assertions.assertEquals(bob,partie.joueurCourant());
    Assertions.assertEquals(3,bob.getNbCarteEnMain());
    partie.encaisserAttaque();
    Assertions.assertEquals(5,bob.getNbCarteEnMain());
    Assertions.assertEquals(charles,partie.joueurCourant());
    Carte unVert = new Carte(Carte.eValeur.UN, Carte.eCouleur.VERT);
    partie.jouer(unVert);
    partie.finirTourDe(charles);
    Assertions.assertEquals(2,charles.getNbCarteEnMain());
    }
@Test
public void testDunCoupLegalavecCumuldeCartesPlus2 (){
        Assertions.assertEquals(alice,partie.joueurCourant());
        partie.piocherUneCarte(alice);
        Assertions.assertEquals(4,alice.getNbCarteEnMain());
        partie.finirTourDe(alice);
        Assertions.assertEquals(bob,partie.joueurCourant());
        partie.piocherUneCarte(bob);
        partie.finirTourDe(bob);
        Assertions.assertEquals(charles,partie.joueurCourant());
        Carte plus2Vert = new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.VERT);
        partie.jouer(plus2Vert);
        partie.finirTourDe(charles);
        Assertions.assertEquals(alice,partie.joueurCourant());
        partie.jouer(plus2Vert);
        partie.finirTourDe(alice);
        Assertions.assertEquals(bob,partie.joueurCourant());
        Assertions.assertEquals(4,bob.getNbCarteEnMain());
        partie.encaisserAttaque();
        Assertions.assertEquals(8,bob.getNbCarteEnMain());
}
}
