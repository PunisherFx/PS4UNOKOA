package Tests;
import static org.junit.jupiter.api.Assertions.*;

import Exceptions.*;
import LogiqueDeJeu.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class PartiedejeuTestCrateSpecial {
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
                new Carte(Carte.eValeur.PASSE, Carte.eCouleur.ROUGE),
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
                new Carte(Carte.eValeur.UN, Carte.eCouleur.ROUGE),
                new Carte(Carte.eValeur.PASSE, Carte.eCouleur.VERT)
        )));
        pioche = new Pioche(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.QUATRE , Carte.eCouleur.ROUGE),
                new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT),
                new Carte(Carte.eValeur.ZERO, Carte.eCouleur.BLEU)
        )));
        defausse = new Defausse(new ArrayList<>(List.of(
                new Carte(Carte.eValeur.NEUF, Carte.eCouleur.ROUGE)
        )));
        partie = new Partiedejeu(joueurs, true, 0, pioche, defausse);
    }
    @Test
    public void testDeCoupsLegauxAvecDesCartes () {
        assertEquals(alice, partie.joueurCourant());
        Carte passeTTRouge = new Carte(Carte.eValeur.PASSE, Carte.eCouleur.ROUGE);
        partie.jouer(passeTTRouge);
        partie.finirTourDe(alice);
        assertEquals(charles, partie.joueurCourant());
        assertEquals(passeTTRouge, defausse.carteAJouer());

        Carte passeTTVert = new Carte(Carte.eValeur.PASSE, Carte.eCouleur.VERT);
        partie.jouer(passeTTVert);
        partie.finirTourDe(charles);
        assertEquals(bob, partie.joueurCourant());
        assertEquals(passeTTVert, defausse.carteAJouer());

        Carte sixVert = new Carte(Carte.eValeur.SIX, Carte.eCouleur.VERT);
        partie.jouer(sixVert);
        partie.finirTourDe(bob);
        Assertions.assertEquals(charles, partie.joueurCourant());
        assertEquals(sixVert, defausse.carteAJouer());

    }
    @Test
    public void testDuneCarteSimpleIllegalSurUnPassTT(){
        Assertions.assertEquals(alice, partie.joueurCourant());
        Carte passeTTRouge = new Carte(Carte.eValeur.PASSE, Carte.eCouleur.ROUGE);
        partie.jouer(passeTTRouge);
        partie.finirTourDe(alice);

        Assertions.assertEquals(charles, partie.joueurCourant());
        Assertions.assertEquals(3,charles.getNbCarteEnMain());
        Carte unBleu = new Carte(Carte.eValeur.UN, Carte.eCouleur.BLEU);
        partie.jouer(unBleu);
        //partie.finirTourDe(charles);
        assertThrows(PartieException.class, () -> partie.finirTourDe(charles));
        Assertions.assertEquals(3, charles.getNbCarteEnMain());
    }

    @Test
    public void testDunPasseTonTourIllegalSurUneCarteSimple (){
        Assertions.assertEquals(alice, partie.joueurCourant());
        Carte neufBleu = new Carte(Carte.eValeur.NEUF, Carte.eCouleur.BLEU);
        partie.jouer(neufBleu);
        partie.finirTourDe(alice);

        Carte septBleu = new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU);
        partie.jouer(septBleu);
        partie.finirTourDe(bob);

        Assertions.assertEquals(charles, partie.joueurCourant());
        Assertions.assertEquals(3,charles.getNbCarteEnMain());

        Carte passeTTVert = new Carte(Carte.eValeur.PASSE, Carte.eCouleur.VERT);
        partie.jouer(passeTTVert);
        assertThrows(PartieException.class, () ->  partie.finirTourDe(charles));
        Assertions.assertEquals(3, charles.getNbCarteEnMain());
    }
}