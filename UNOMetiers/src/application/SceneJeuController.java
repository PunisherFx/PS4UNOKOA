package application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class SceneJeuController {
    @FXML private HBox mainJoueur;
    @FXML private Button btnPioche;
    @FXML private ImageView imagePioche;
    @FXML ImageView imageDefausse ;
    @FXML private HBox joueursDeLaPartie;
    @FXML private TextArea zoneMessage;
    @FXML private Button btnEncaisser;
    @FXML private Button btnUno;
    @FXML private Button btnFinTour;
    private ClientChat client;
    private AppClient appClient;
    private String pseudo;
    @FXML private VBox partieAffichage;
    private String gagnant;

    /**
     * lorsque on recoit @MAJ du serveur on envoie cest protocle afin de mattre achauqe fois l'affichage a jour
     */
    public void miseajour(){
        System.out.println("MISE A JOUR");
        client.messageauserveur("@MAIN");
        client.messageauserveur("@CARTE_TAS");
        client.messageauserveur("@JOUEURS");
    }

    public void setGagnant(String gagnant) {
        this.gagnant = gagnant;
    }

    public String getGagnant() {
        return gagnant;
    }

    /**
     * on lui fait appel lors du chragement de la scne pour initialiser l'affichages des cartes etc..
     * @param client
     * @param pseudo
     */
    public void initialiserDonnees(ClientChat client, String pseudo) {
        this.client = client;
        this.pseudo = pseudo;
        client.messageauserveur("@MAIN");
        client.messageauserveur("@CARTE_TAS");
        client.messageauserveur("@JOUEURS");

        Image imageDos = new Image(getClass().getResourceAsStream("/ressources/pioche.png"));
        imagePioche.setImage(imageDos);
        imagePioche.setFitHeight(100);
        imagePioche.setPreserveRatio(true);

        btnPioche.setOnAction(e -> {
            client.messageauserveur("@PIOCHE");
            System.out.println("🎯 Carte piochée !");
        });
        btnEncaisser.setOnAction(e -> {
            client.messageauserveur("@ENCAISSE");
        });
        btnUno.setOnAction(e -> {
            client.messageauserveur("@UNO");
        });
        btnFinTour.setOnAction(e -> {
            client.messageauserveur("@FIN_TOUR");
            client.messageauserveur("@JOUEURS");
            client.messageauserveur("@MAIN");
        });
    }

    public void ajouterCarteDansMain(String message) {
        System.out.println("🟩 afficherMainJoueur() avec : " + message);

        mainJoueur.getChildren().clear();
        String[] cartes = message.split(";");
        for (String c : cartes) {
            String chemin = "/ressources/carte_" + c + ".png";
            System.out.println("🔍 Chargement : " + chemin);
            try {
                Image image = new Image(getClass().getResourceAsStream(chemin));
                ImageView iv = new ImageView(image);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);

                Button btnCarte = new Button();
                btnCarte.setGraphic(iv);
                btnCarte.setStyle("-fx-background-color: transparent; -fx-padding: 5;");

                btnCarte.setOnAction(event -> {
                    System.out.println("🟥 Carte jouée : " + c);
                    StringBuilder carte = new StringBuilder();
                    String resultat;
                    if ((c.startsWith("CHANGEMENT" )||(c.startsWith("PLUS")))){
                        int lastIndex = c.lastIndexOf("_");
                        resultat = c.substring(0, lastIndex) + " " + c.substring(lastIndex + 1);
                    }else {
                         resultat = c.replace("_", " ");
                    }
                    client.messageauserveur("@CARTE_JOUEE " + resultat);
                });

                mainJoueur.getChildren().add(btnCarte);
            } catch (Exception e) {
                System.out.println(" Erreur chargement image : " + chemin);
            }
        }
        }
    public void ajouterCarteTas(String message) {
        try {
            System.out.println(message);
            String chemin = "/ressources/carte_" + message + ".png";

            Image img = new Image(getClass().getResourceAsStream(chemin));
            imageDefausse.setImage(img);
            imageDefausse.setFitHeight(100);
            imageDefausse.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println(" Erreur chargement carte défausse : " + e.getMessage());
        }
    }

    public void afficherJoueurs(String message) {
        joueursDeLaPartie.setAlignment(Pos.CENTER);
        joueursDeLaPartie.getChildren().clear();

        String contenu = message.replace("@AUTRES ", "").trim(); // ex: "alice:7"
        String[] parts = contenu.split(":");
        if (parts.length != 2) return;

        String nom = parts[0];
        int nbCartes = Integer.parseInt(parts[1]);

        VBox joueurBox = new VBox(8);
        joueurBox.setAlignment(Pos.CENTER);
        joueurBox.setPadding(new Insets(10));
        joueurBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 12; -fx-border-color: #cccccc; -fx-border-radius: 12;");
        joueurBox.setPrefWidth(200);

        Label nomLabel = new Label("Tour de : " + nom.toUpperCase());
        nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox cartes = new HBox(5);
        cartes.setAlignment(Pos.CENTER);
        for (int i = 0; i < nbCartes; i++) {
            ImageView dos = new ImageView(new Image(getClass().getResourceAsStream("/ressources/carte_dos.png")));
            dos.setFitHeight(60);
            dos.setPreserveRatio(true);
            cartes.getChildren().add(dos);
        }

        joueurBox.getChildren().addAll(nomLabel, cartes);
        joueursDeLaPartie.getChildren().add(joueurBox);
    }

    @FXML
    public void handleEncaisser (){
        client.messageauserveur("@ENCAISSE");
    }
    @FXML
    public void handleUno(){
        client.messageauserveur("@UNO");
    }
    @FXML
    public void handleFinTour(){
        client.messageauserveur("@FIN_TOUR");
    }
    public void afficherConsole(String message) {
        Platform.runLater(() -> {
            try {
                zoneMessage.appendText(message + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void changerSceneFin () {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FinDeJeu.fxml"));
            Parent root = loader.load();

            FinDeJeuController controller = loader.getController();
            controller.afficherVainqueur(getGagnant());
            controller.initialiser(this.client);
            Stage stage = (Stage) btnFinTour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

