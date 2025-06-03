package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class FinDeJeuController {
    private ClientChat client;
    @FXML
    private VBox root;
    @FXML
    private Label vainqueur;
    @FXML
    private Button quitter;
    @FXML
    private Button accueil;

    @FXML
    public void initialize() {
        root.setStyle(
                "-fx-background-image: url('/ressources/back.png');" +  // Chemin relatif depuis src/main/resources
                        "-fx-background-size: cover;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-position: center center;"
        );

    }

    public void afficherVainqueur(String nom) {
        vainqueur.setText("Le vainqueur de la partie est : " + nom);
    }

    @FXML
    private void quitter(ActionEvent event) {
        if (client != null) {
            client.messageauserveur("@DECONNEXION");
        }
        Platform.exit();
    }

    @FXML
    private void retournerAccueil() {
        if (client != null) {
            client.getApp().revenirAccueilDepuisFinDeJeu(); // 🔁 revient à la scène de départ
        }

    }

    public void initialiser(ClientChat client) {
        this.client = client;
    }
}
