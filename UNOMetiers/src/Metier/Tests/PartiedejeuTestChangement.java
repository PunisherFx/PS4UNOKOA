package Metier.Tests;

import Metier.Exceptions.PartieException;
import Metier.Exceptions.UnoException;
import Metier.LogiqueDeJeu.*;
import Metier.LogiqueDeJeu.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartiedejeuTestChangement {
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
                new Carte(Carte.eValeur.NEUF, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.JAUNE)

        )));

        bob.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.SIX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.JAUNE),
                new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU)
        )));

        charles.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.VERT),
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
    public void TestInverseSens(){
        assertEquals(alice,partie.joueurCourant());
        Carte inverseVert = new Carte(Carte.eValeur.CHANGEMENT_SENS, Carte.eCouleur.VERT);
        partie.jouer(inverseVert);
        assertEquals(2,alice.getNbCarteEnMain());
        partie.finirTourDe(alice);
        assertEquals(charles,partie.joueurCourant());
        partie.jouer(inverseVert);
        assertEquals(2,charles.getNbCarteEnMain());
        Carte unBleu = new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU);
        assertThrows(PartieException.class, () -> partie.jouer(unBleu));
        assertEquals(2,charles.getNbCarteEnMain());
        partie.finirTourDe(charles);
        assertEquals(alice,partie.joueurCourant());
        Carte neufBleu = new Carte(Carte.eValeur.NEUF, Carte.eCouleur.VERT);
        partie.jouer(neufBleu);
        assertEquals(1,alice.getNbCarteEnMain());
       assertThrows(UnoException.class,() -> partie.finirTourDe(alice));
        assertEquals(4,alice.getNbCarteEnMain());
        assertEquals(bob,partie.joueurCourant());

    }
  /*  @Test
    public void TestChangementdeCouleur(){

    }*/

}