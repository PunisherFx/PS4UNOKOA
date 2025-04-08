import static org.junit.jupiter.api.Assertions.*;
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
        assertEquals(alice, partie.joueurCourant());
        assertEquals(3, alice.getNbCarteEnMain());
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        partie.jouer(DeuxVert);
        assertEquals(2,alice.getNbCarteEnMain());
        Carte SixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        Carte UnRouge = new Carte(Carte.eValeur.UN, Carte.eCouleur.ROUGE);
        assertTrue(alice.aLaCarte(SixJaune));
        assertTrue(alice.aLaCarte(UnRouge));
        assertEquals(DeuxVert, defausse.carteAJouer());
        assertEquals(2,defausse.getNbDeCarteDuTas());
        alice.finirSonTour(partie);
        assertEquals(bob, partie.joueurCourant());

        assertEquals(3,bob.getNbCarteEnMain());
        Carte deuxBleu = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.BLEU);
        partie.jouer(deuxBleu);
        assertEquals(2,bob.getNbCarteEnMain());
        Carte QuatreJaune = new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.JAUNE);
        Carte NeufRouge = new Carte(Carte.eValeur.NEUF, Carte.eCouleur.ROUGE);
        assertTrue(bob.aLaCarte(QuatreJaune));
        assertTrue(bob.aLaCarte(NeufRouge));
        assertEquals(deuxBleu, defausse.carteAJouer());
        assertEquals(3,defausse.getNbDeCarteDuTas());
        bob.finirSonTour(partie);
        assertEquals(charles, partie.joueurCourant());
    }
    @Test
    public void testDuneCarteIllegale(){
        Carte SixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        assertTrue(alice.aLaCarte(SixJaune));
        assertEquals(alice, partie.joueurCourant());
        assertThrows(IllegalArgumentException.class, () -> partie.jouer(SixJaune));
        assertEquals(3, alice.getNbCarteEnMain());
        assertTrue(alice.getCrtEnMain().contains(SixJaune));
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
        assertEquals(2,charles.getNbCarteEnMain());
        Carte septBleu = new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU);
        assertThrows(PartieException.class, () -> partie.jouer(septBleu));
    }
    @Test
    public void testFinirSonTourSansRienFaire (){
        assertThrows(PartieException.class, () -> alice.finirSonTour(partie));
        assertEquals(3,alice.getNbCarteEnMain());
    }
    @Test
    public void testDunJoueurQuiJouePuisPioche (){
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        partie.jouer(DeuxVert);
        assertThrows(PiocheException.class, () -> partie.piocherUneCarte(alice));
        assertEquals(2,alice.getNbCarteEnMain());
        Carte sixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        assertEquals(sixJaune,pioche.getCarteAPiocher());
    }
    @Test
    public void testDeLaPunitionPourUnCoupIllegal(){
        assertEquals(alice,partie.joueurCourant());
        Carte sixJaune = new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE);
        assertThrows(IllegalArgumentException.class, () -> partie.jouer(sixJaune));
        assertEquals(bob,partie.joueurCourant());
        assertEquals(5,alice.getNbCarteEnMain());
        Carte quatreRouge = new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.ROUGE);
        assertTrue(alice.getCrtEnMain().contains(sixJaune));
        assertTrue(alice.getCrtEnMain().contains(quatreRouge));
        Carte deuxVert = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        assertEquals(deuxVert,pioche.getCarteAPiocher());
    }
    @Test
    public void testDuneActionDeBobLorsqueCeNestPasSonTour(){
        assertEquals(alice,partie.joueurCourant());
        assertThrows(PiocheException.class,()-> partie.piocherUneCarte(bob));
        assertEquals(alice,partie.joueurCourant());
        assertEquals(5,bob.getNbCarteEnMain());
        Carte DeuxVert= new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        assertEquals(DeuxVert,pioche.getCarteAPiocher());
    }
}