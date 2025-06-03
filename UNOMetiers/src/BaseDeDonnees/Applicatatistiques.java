package BaseDeDonnees;

import java.util.Scanner;

public class Applicatatistiques {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UtilisateurDAO dao = new UtilisateurDAO();

        char choix = ' ';
        while (choix != '0') {
            System.out.println("\n=== | MENU STATISTIQUES | ===");
            System.out.println("1. Joueur avec le plus de victoires");
            System.out.println("2. Joueur avec le plus de parties");
            System.out.println("3. Top 3 des joueurs avec le plus de victoires");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");
            choix = scanner.next().charAt(0);

            switch (choix) {
                case '1' -> System.out.println(dao.getJoueurAvecMaxVictoires());
                case '2' -> System.out.println(dao.getJoueurAvecMaxParties());
                case '3' -> dao.getTop3Victoires().forEach(System.out::println);
                case '0' -> System.out.println("Fin du programme.");
                default -> System.out.println("Choix invalide.");
            }
        }
    }
}
