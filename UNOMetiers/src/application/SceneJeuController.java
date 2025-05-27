package application;

import Metier.LogiqueDeJeu.Carte;
import Metier.LogiqueDeJeu.Partiedejeu;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;

public class SceneJeuController {

    private ClientChat client;
    @FXML
private HBox mainJoueur;

    public void setCartesMain(List<Carte> cartes) {
        mainJoueur.getChildren().clear();
        for (Carte carte : cartes) {
            ImageView img = new ImageView(Carte.getImageFromCarte(carte));
            img.setFitHeight(100);
            img.setPreserveRatio(true);
            mainJoueur.getChildren().add(img);
        }
    }
    private void jouerCarte(Carte carte) {
        // Exemple de message : @JOUER 2 BLEU
        String message = "@CARTE_JOUEE " + carte.getValeur() + " " + carte.getCouleur();
        client.envoyerMessagePublic(message); // méthode déjà dans ta classe ClientChat
    }

}
