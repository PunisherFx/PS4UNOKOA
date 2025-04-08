import java.util.ArrayList;
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
    public void reinitialiserTas (){
        tas.clear();
    }
    public void rendreLaCarte(){
        tas.remove(tas.size()-1);
    }
    }
