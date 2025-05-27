package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;

import static java.io.FileDescriptor.out;

/**
 * C'est l'application avec interface utilisateur. Toutefois, dans cette classe, on ne traite que des
 * communications de haut niveau. On utilise pas le protocole. C'est la classe ClientChat qui se chargera
 * de cà, grâce aux méthodes traiterXXX.
 * Cette classe est complète. En principe, vous n'avez pas à la modifier
 */
public class AppClient extends Application {
    VBox rootPrincipale = new VBox();
    Label lblTitre = new Label("JEU UNO ");
    Button btnJouer = new Button("Jouer");
    Button btnRejoindre = new Button("Rejoindre");
    VBox root = new VBox();
    TextArea console = new TextArea();
    Label lblTonPseudo = new Label("Ton pseudo :");
    TextField tfTonPseudo = new TextField();
    Button btnConnexion = new Button("Connexion!");
    Button bPublic = new Button("Public");
    Button bPrivate = new Button("Privé");
    Label lblPseudo = new Label("Pseudo : ");
    TextField tfPseudo = new TextField();
    Label lblMessage = new Label("Message :");
    TextField tfMessage = new TextField();

    ClientChat client = new ClientChat(this);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(rootPrincipale, 600, 500);
        stage.setTitle("Client MiniChat");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> traiterDeconnexion() );
        rootPrincipale.setSpacing(10);
        rootPrincipale.setAlignment(Pos.CENTER); // ✅ centrer tous les enfants (titre, chat, bouton)

        lblTitre.setStyle(
                "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: linear-gradient(to right, red, yellow);" +
                        "-fx-effect: dropshadow(gaussian, black, 3, 0.5, 2, 2);"
        );
        btnJouer.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ff0000, #cc0000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, black, 2, 0.5, 1, 1);"
        );




        root.setSpacing(20);

        HBox boxConnexion = new HBox();
        boxConnexion.setAlignment(Pos.CENTER);
        boxConnexion.setSpacing(10);

        boxConnexion.getChildren().add(lblTonPseudo);
        boxConnexion.getChildren().add(tfTonPseudo);
        boxConnexion.getChildren().add(btnConnexion);

        root.getChildren().add(boxConnexion);

        root.getChildren().add(console);

        HBox boxBouton = new HBox();
        boxBouton.setAlignment(Pos.CENTER);
        boxBouton.setSpacing(10);

        boxBouton.getChildren().add(bPublic);
        boxBouton.getChildren().add(bPrivate);
        boxBouton.getChildren().add(lblPseudo);
        boxBouton.getChildren().add(tfPseudo);

        root.getChildren().add(boxBouton);


        HBox boxMessage = new HBox();
        boxMessage.setAlignment(Pos.CENTER);
        boxMessage.setSpacing(10);

        boxMessage.getChildren().add(lblMessage);
        boxMessage.getChildren().add(tfMessage);
        tfMessage.setPrefWidth(300);

        root.getChildren().add(boxMessage);

        console.setEditable(false);
        tfMessage.setEditable(false);
        tfPseudo.setEditable(false);
        bPublic.setDisable(true);
        bPrivate.setDisable(true);
        rootPrincipale.setSpacing(10);
         HBox Partie = new HBox();
         Partie.setAlignment(Pos.CENTER);
         Partie.setSpacing(10);
         btnRejoindre.setDisable(true);
         Partie.getChildren().addAll(btnJouer,btnRejoindre);
        rootPrincipale.getChildren().addAll(lblTitre,root,Partie);



        /*
            On s'occupe des contrôleurs des différents composants
            qui vont créer les messages du protocoles et les envoyer
            au serveur
         */
        btnConnexion.setOnAction(actionEvent -> traiterConnexion());
        bPublic.setOnAction(actionEvent -> traiterMessagePublic());
        bPrivate.setOnAction(actionEvent -> traiterMessagePrivate());
        btnJouer.setOnAction(actionEvent -> lancerPartie());
       // btnRejoindre.setOnAction(actionEvent e -> changerScene(e));

    }
    public void changerScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sceneJeu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPrincipale.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lancerPartie() {
        client.lancerUnePartie();
      /*  btnJouer.setDisable(true);
        btnRejoindre.setDisable(false);*/
    }
    private void traiterMessagePrivate() {
        client.envoyerMessagePrive(tfPseudo.getText(),tfMessage.getText());
    }

    private void traiterMessagePublic() {
        client.envoyerMessagePublic(tfMessage.getText());
    }

    private void traiterDeconnexion() {
        client.envoyerDeconnexion();
    }

    private void traiterConnexion() {
        String tonPseudo = tfTonPseudo.getText();
        if (tonPseudo.trim().equals("")) {
            console.appendText("Ton pseudo ne doit pas être vide....");
        }
        else {
            client.setPseudo(tonPseudo);
            client.envoyerConnexion();

            console.setEditable(true);
            tfMessage.setEditable(true);
            tfPseudo.setEditable(true);
            bPublic.setDisable(false);
            bPrivate.setDisable(false);
            tfTonPseudo.setEditable(false);
            btnConnexion.setDisable(true);
        }
    }

    public void afficherConsole(String str) {
        console.appendText(str + "\n");
    }
  /*  public void changerBouton() {
        System.out.println("⚙️ changerBouton() appelé");
        btnJouer.setDisable(true); // grise le bouton Jouer
        btnRejoindre.setDisable(false); // active Rejoindre
        afficherConsole("@INFO Une partie a été lancée. Cliquez sur 'Rejoindre' pour participer.");
    }*/

    }
