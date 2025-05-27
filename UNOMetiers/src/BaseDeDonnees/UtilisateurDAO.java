package BaseDeDonnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDAO {

    /**
     * Ajoute un utilisateur avec un pseudo donné.
     * Si le pseudo existe déjà, il est ignoré (grâce à INSERT IGNORE).
     */
    public static void ajouterUtilisateur(String pseudo) {
        String sql = "INSERT IGNORE INTO utilisateurs (pseudo) VALUES (?)";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pseudo);
            pst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + pseudo);
            e.printStackTrace();
        }
    }

    /**
     * Incrémente le nombre de parties jouées pour le joueur donné.
     */
    public static void incrementerPartie(String pseudo) {
        String sql = "UPDATE utilisateurs SET nb_parties = nb_parties + 1 WHERE pseudo = ?";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pseudo);
            pst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrément de partie : " + pseudo);
            e.printStackTrace();
        }
    }

    /**
     * Incrémente le nombre de victoires pour le joueur donné.
     */
    public static void incrementerVictoire(String pseudo) {
        String sql = "UPDATE utilisateurs SET nb_victoires = nb_victoires + 1 WHERE pseudo = ?";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pseudo);
            pst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrément de victoire : " + pseudo);
            e.printStackTrace();
        }
    }

    /**
     * Affiche les statistiques d’un joueur (parties, victoires).
     */
    public static void afficherStats(String pseudo) {
        String sql = "SELECT nb_parties, nb_victoires FROM utilisateurs WHERE pseudo = ?";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pseudo);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int parties = rs.getInt("nb_parties");
                int victoires = rs.getInt("nb_victoires");
                System.out.println("👤 " + pseudo + " | Parties : " + parties + " | Victoires : " + victoires);
            } else {
                System.out.println("❌ Utilisateur non trouvé : " + pseudo);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des stats : " + pseudo);
            e.printStackTrace();
        }
    }
}
