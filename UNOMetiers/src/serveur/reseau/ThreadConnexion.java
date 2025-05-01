package serveur.reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadConnexion extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Utilisateur connexion;
    private boolean fin = false;

    public ThreadConnexion(Utilisateur connexion, Socket socket) {
        this.connexion = connexion;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    @Override
    public void run() {
        String message;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            this.out.println("Bienvenue sur le serveur !");
            this.out.flush();
            Utilisateur connexion = this.connexion;
            while (!fin) {
                message = in.readLine();
                connexion.controlerMessage(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                System.out.println("Connexion fermée.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void envoyerMessageAuClient(String message) {
        this.out.println(message);
        this.out.flush();
    }
    public void fin() {
        fin = true;
    }
}
