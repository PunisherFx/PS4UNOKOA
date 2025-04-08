import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartiedejeuTestUno {
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
            new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
            new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE)
    )));

    bob.initialiserMain(new ArrayList<>(List.of(
            new Carte(Carte.eValeur.DEUX, Carte.eCouleur.BLEU),
            new Carte(Carte.eValeur.QUATRE, Carte.eCouleur.JAUNE)
    )));

    charles.initialiserMain(new ArrayList<>(List.of(
            new Carte(Carte.eValeur.NEUF, Carte.eCouleur.BLEU),
            new Carte(Carte.eValeur.SEPT, Carte.eCouleur.BLEU)
    )));
    pioche = new Pioche(new ArrayList<>(List.of(
            new Carte(Carte.eValeur.TROIS, Carte.eCouleur.BLEU),
            new Carte(Carte.eValeur.ZERO, Carte.eCouleur.VERT),
            new Carte(Carte.eValeur.CINQ, Carte.eCouleur.BLEU),
            new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT),
            new Carte(Carte.eValeur.SIX, Carte.eCouleur.JAUNE)
    )));
    defausse = new Defausse(new ArrayList<>(List.of(
            new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT)
    )));
    partie = new Partiedejeu(joueurs, true, 0, pioche, defausse);
}
    @Test
    public void testAliceDitUnoAuBonMoment(){
     assertEquals(2,alice.getNbCarteEnMain());
     Carte deuxVert = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
     partie.jouer(deuxVert);
     alice.DireUno(partie);
     partie.finirTourDe(alice);
     assertEquals(deuxVert,defausse.carteAJouer());
     assertEquals(bob,partie.joueurCourant());
    }
    @Test
    public void OubliDeDireUno(){
        Carte deuxVert = new Carte(Carte.eValeur.DEUX, Carte.eCouleur.VERT);
        partie.jouer(deuxVert);
        assertThrows(UnoException.class, () -> partie.finirTourDe(alice));
        assertEquals(4,alice.getNbCarteEnMain());
        Carte huitVert = new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT);
        assertEquals(huitVert,defausse.carteAJouer());
        assertEquals(bob,partie.joueurCourant());
    }
    @Test
    public void direUnoLorsqueCeNestPasSonTour (){
        assertEquals(alice,partie.joueurCourant());
        assertThrows(UnoException.class, () -> bob.DireUno(partie));
        assertEquals(4,bob.getNbCarteEnMain());
        assertEquals(alice,partie.joueurCourant());
        Carte huitVert = new Carte(Carte.eValeur.HUIT, Carte.eCouleur.VERT);
        assertEquals(huitVert,defausse.carteAJouer());

    }
}