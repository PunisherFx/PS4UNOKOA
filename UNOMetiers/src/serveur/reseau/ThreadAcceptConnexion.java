package serveur.reseau;

import serveur.serveurMetier.ServeurExceptions;
import serveur.serveurMetier.ServeurUno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
Cette classe représente le thread qui écoute les nouvelles connexions clientes sur le serveur.
Elle tourne en boucle et accepte chaque client qui se connecte
 */
public class ThreadAcceptConnexion extends Thread{
    private ServeurUno serveur;
    private ServerSocket serverSocket;

    public ThreadAcceptConnexion(ServeurUno serveur) {
        this.serveur = serveur;
        try {
            this.serverSocket = new ServerSocket(serveur.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connexion !");
                Utilisateur connexion = new Utilisateur(socket, serveur);
                serveur.add(connexion);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServeurExceptions e) {
            throw new RuntimeException(e);
        }
    }
}