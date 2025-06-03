package BaseDeDonnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UtilisateurDAO {

    /**
     * Ajoute un utilisateur avec un pseudo donné.
     * Si le pseudo existe déjà, il est ignoré
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
     * Incrémente le nombre de parties jouées .
     */
    public static void incrementerNombreParties(String pseudo) {
        String sql = "UPDATE utilisateurs SET nb_parties = nb_parties + 1 WHERE pseudo = ?";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pseudo);
            pst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation du nombre de parties pour " + pseudo);
            e.printStackTrace();
        }
    }

    /**
     * Incrémente le nombre de victoires pour le joueur
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
     * on applique des requtes SQL pour afficher le jouers avec le plus de victoire
     * @return
     */
    public static String getJoueurAvecMaxVictoires() {
        String sql = "SELECT pseudo FROM utilisateurs ORDER BY nb_victoires DESC LIMIT 1";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getString("pseudo");
            }

        } catch (SQLException e) {
            System.err.println("Erreur getJoueurAvecPlusDeVictoires");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * le jouerus avec la plus de parties
     * @return
     */
    public static String getJoueurAvecMaxParties() {
        String sql = "SELECT pseudo FROM utilisateurs ORDER BY nb_parties DESC LIMIT 1";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getString("pseudo");
            }

        } catch (SQLException e) {
            System.err.println("Erreur getJoueurAvecPlusDeParties");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * le top 3
     * @return
     */
    public static ArrayList<String> getTop3Victoires() {
        ArrayList<String> top3 = new ArrayList<>();
        String sql = "SELECT pseudo FROM utilisateurs ORDER BY nb_victoires DESC LIMIT 3";
        try (Connection con = ConnexionBD.getConnexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                top3.add(rs.getString("pseudo"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur getTop3Victoires");
            e.printStackTrace();
        }
        return top3;
    }


}
