package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadConsole extends Thread{
    private ClientChat client;
    private BufferedReader in;

    ThreadConsole(ClientChat client) {
        this.client = client;
        try {
            this.in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        } catch (IOException e) {
            client.app.afficherConsole("Erreur : Impossible de récupérer le flux d'entrée du serveur.");
        }
        // TODO à vous de compléter ce qu'il faut
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) { // Lire les messages du serveur
                client.afficherMessage(message);
            }
        } catch (IOException e) {
            client.app.afficherConsole("Connexion perdue avec le serveur.");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                client.app.afficherConsole("Erreur lors de la fermeture du flux.");
            }
        }

    }
}
