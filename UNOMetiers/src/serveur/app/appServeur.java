package serveur.app;

import serveur.serveurMetier.ServeurUno;
/** cette classe consiste a lancer le serveur pour demarrer le serveur uno Dans le main,
je crée une nouvelle instance de ServeurUno, en lui passant le port 4567. Cela démarre immédiatement
le serveur sur ce port, prêt à recevoir des connexions clients
 */
public class appServeur {
    public static void main(String[] args) {
        new ServeurUno(4567);
    }
}
