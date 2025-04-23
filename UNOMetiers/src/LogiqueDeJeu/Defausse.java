package LogiqueDeJeu;

import java.util.List;

public class Defausse {
    private List<Carte> tas;

    public int getNbDeCarteDuTas(){
        return tas.size();
    }
    public Defausse(List<Carte> tas) {
        this.tas = tas;
    }

    public void poserUneCarte(Carte c){
        tas.add(c);
    }
    public Carte carteAJouer(){
        if(!(tas.isEmpty())) {
            return tas.get(tas.size()-1);
        }else{
            throw new IllegalArgumentException("aucune carte sur le Tas");
        }
    }
    // pour gerer le test d'une carte simple illégale sur un passeTT
    public Carte avantDerniéreCarteJouer(Carte c){
        if (tas.size() >= 2) {
            return tas.get(tas.size()-2);
        }else {
            throw new IllegalArgumentException("Pas de carte ");
        }
    }
    public void reinitialiserTas (){
        tas.clear();
    }
    public void rendreLaCarte(){
        tas.remove(tas.size()-1);
    }
    }
