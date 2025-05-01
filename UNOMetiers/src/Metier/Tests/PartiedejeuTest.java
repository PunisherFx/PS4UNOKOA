package Metier.Tests;

import static org.junit.jupiter.api.Assertions.*;

import Metier.Exceptions.PartieException;
import Metier.Exceptions.PiocheException;
import Metier.LogiqueDeJeu.*;
import Metier.LogiqueDeJeu.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PartiedejeuTest {
    private static Pioche pioche;
    private static Defausse defausse;
    private static Joueur alice;
    private static Joueur bob;
    private static Joueur charles;
    private static ArrayList<Joueur> joueurs;

    private Partiedejeu partie;

    @BeforeEach
    public void setUp() {
        alice = new Joueur("Alice");
        bob = new Joueur("Bob");
        charles = new Joueur("Charles");

        joueurs = new ArrayList<>();
        joueurs.add(alice);
        joueurs.add(bob);
        joueurs.add(charles);

        alice.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE),
                new Carte(Carte.eValeur.UN, Carte.eCouleur.ROUGE)
        )));

        bob.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.JAUNE),
                new Carte(Carte.eValeur.NEUF, Carte.eCouleur.ROUGE)
        )));

        charles.initialiserMain(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.NEUF, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.ZERO, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU)
        )));
       pioche = new Pioche(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.CINQ, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.BLEU),
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.ROUGE),
                new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE)
        )));
        defausse = new Defausse(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT)
        )));
        partie = new Partiedejeu(joueurs, true, 0, pioche, defausse);
    }

    @Test
    public void testCoupsLegauxAvecDesCarteSimples() {
        Assertions.assertEquals(alice, partie.joueurCourant());
        Assertions.assertEquals(3, alice.getNbCarteEnMain());
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        partie.jouer(DeuxVert);
        Assertions.assertEquals(2,alice.getNbCarteEnMain());
        Carte SixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        Carte UnRouge = new Carte(Carte.eValeur.UN, Carte.eCouleur.ROUGE);
        Assertions.assertTrue(alice.aLaCarte(SixJaune));
        Assertions.assertTrue(alice.aLaCarte(UnRouge));
        assertEquals(DeuxVert, defausse.carteAJouer());
        assertEquals(2,defausse.getNbDeCarteDuTas());
        alice.finirSonTour(partie);
        Assertions.assertEquals(bob, partie.joueurCourant());

        Assertions.assertEquals(3,bob.getNbCarteEnMain());
        Carte deuxBleu = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.BLEU);
        partie.jouer(deuxBleu);
        Assertions.assertEquals(2,bob.getNbCarteEnMain());
        Carte QuatreJaune = new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.JAUNE);
        Carte NeufRouge = new Carte(Carte.eValeur.NEUF, Carte.eCouleur.ROUGE);
        Assertions.assertTrue(bob.aLaCarte(QuatreJaune));
        Assertions.assertTrue(bob.aLaCarte(NeufRouge));
        assertEquals(deuxBleu, defausse.carteAJouer());
        assertEquals(3,defausse.getNbDeCarteDuTas());
        bob.finirSonTour(partie);
        Assertions.assertEquals(charles, partie.joueurCourant());
    }
    @Test
    public void testDuneCarteIllegale(){
        Carte SixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        Assertions.assertTrue(alice.aLaCarte(SixJaune));
        Assertions.assertEquals(alice, partie.joueurCourant());
        assertThrows(IllegalArgumentException.class, () -> partie.jouer(SixJaune));
        Assertions.assertEquals(5, alice.getNbCarteEnMain());
        Assertions.assertTrue(alice.getCrtEnMain().contains(SixJaune));
    }
   @Test
    public void testDunJoueurQuiPoseDeuxCartesLegalesDeSuite (){
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        partie.jouer(DeuxVert);
        alice.finirSonTour(partie);
        Carte deuxBleu = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.BLEU);
        partie.jouer(deuxBleu);
        bob.finirSonTour(partie);
        Carte neufBleu = new Carte(Carte.eValeur.NEUF, Carte.eCouleur.BLEU);
        partie.jouer(neufBleu);
        Assertions.assertEquals(2,charles.getNbCarteEnMain());
        Carte septBleu = new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU);
        assertThrows(PartieException.class, () -> partie.jouer(septBleu));
    }
    @Test
    public void testFinirSonTourSansRienFaire (){
        assertThrows(PartieException.class, () -> alice.finirSonTour(partie));
        Assertions.assertEquals(3,alice.getNbCarteEnMain());
    }
    @Test
    public void testDunJoueurQuiJouePuisPioche (){
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        partie.jouer(DeuxVert);
        assertThrows(PiocheException.class, () -> partie.piocherUneCarte(alice));
        Assertions.assertEquals(2,alice.getNbCarteEnMain());
        Carte sixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        Assertions.assertEquals(sixJaune,pioche.getCarteAPiocher());
    }
    @Test
    public void testDeLaPunitionPourUnCoupIllegal(){
        Assertions.assertEquals(alice,partie.joueurCourant());
        Carte sixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        assertThrows(IllegalArgumentException.class, () -> partie.jouer(sixJaune));
        Assertions.assertEquals(bob,partie.joueurCourant());
        Assertions.assertEquals(5,alice.getNbCarteEnMain());
        Carte quatreRouge = new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.ROUGE);
        Assertions.assertTrue(alice.getCrtEnMain().contains(sixJaune));
        Assertions.assertTrue(alice.getCrtEnMain().contains(quatreRouge));
        Carte deuxVert = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        Assertions.assertEquals(deuxVert,pioche.getCarteAPiocher());
    }
    @Test
    public void testDuneActionDeBobLorsqueCeNestPasSonTour(){
        Assertions.assertEquals(alice,partie.joueurCourant());
        assertThrows(PiocheException.class,()-> partie.piocherUneCarte(bob));
        Assertions.assertEquals(alice,partie.joueurCourant());
        Assertions.assertEquals(5,bob.getNbCarteEnMain());
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        Assertions.assertEquals(DeuxVert,pioche.getCarteAPiocher());
    }
}