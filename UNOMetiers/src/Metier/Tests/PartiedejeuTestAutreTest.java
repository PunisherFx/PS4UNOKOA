package Metier.Tests;

import Metier.Exceptions.UnoException;
import Metier.LogiqueDeJeu.*;
import Metier.LogiqueDeJeu.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartiedejeuTestAutreTest {
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
                new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.PASSE, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.PLUS_2, Carte.eCouleur.JAUNE)

        )));

        bob.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.SIX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.JAUNE),
                new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT)
        )));

        charles.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT)
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
    public void testLogiqueDesCartesSpe(){
        assertEquals(alice, partie.joueurCourant());
        Carte changeVert = new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.VERT);
        partie.jouer(changeVert);
        /*on test dire Uno alors qu'il nous reste plus d'une carte selon la logique il n'a pas le droit
        de dire Uno donc sa carte jouer est annuler et dois piocher 2 cartes
         */
        alice.DireUno(partie);
        assertThrows(UnoException.class, () -> partie.finirTourDe( alice));
        assertEquals(bob, partie.joueurCourant());
        assertEquals(5,alice.getNbCarteEnMain());
        //on joue une carte simple legale
        Carte sixVert = new Carte(Carte.eValeur.SIX, Carte.eCouleur.VERT);
        partie.jouer(sixVert);
        partie.finirTourDe(bob);
        partie.jouer(changeVert);
        partie.finirTourDe(charles);
        //on oublie de dire Uno
        assertEquals(bob, partie.joueurCourant());
        Carte plus4Vert = new Carte(Carte.eValeur.PLUS_4, Carte.eCouleur.VERT);
        partie.jouer(plus4Vert);
        assertThrows(UnoException.class,()-> partie.finirTourDe(bob));
        assertEquals(alice, partie.joueurCourant());

    }
}