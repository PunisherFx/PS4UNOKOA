package Metier.LogiqueDeJeu;

import Metier.Exceptions.PiocheException;

import java.util.ArrayList;

public class Pioche {
    private ArrayList<Carte> LaPioche =  new ArrayList();

    public Pioche(ArrayList<Carte> laPioche) {
        LaPioche = laPioche;
    }
    //On retourne la carte a pioche autrement dit la carte tout en haut de la pioche
    public Carte getCarteAPiocher(){
        if (LaPioche.isEmpty()) {
            throw new PiocheException("La pioche est vide");
        }
        return LaPioche.get(LaPioche.size()-1);
    }
    //on retire une carte de la pioche les verifs se feront dans Partie
    public Carte depiler(){
        if (LaPioche.size() == 0 ) {
            throw  new PiocheException("on ne peut pas piocher d'une pioche vide ");
        }
        return LaPioche.remove(LaPioche.size()- 1);
    }

    public void initialiser (ArrayList<Carte> collection){
        if(collection== null) throw new IllegalArgumentException("collection vide");
        LaPioche.clear();
        LaPioche.addAll(collection);
    }
}
