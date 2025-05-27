package BaseDeDonnees;

import BaseDeDonnees.ConnexionBD;

import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        try (Connection con = ConnexionBD.getConnexion()) {
            System.out.println("✅ Connexion réussie !");
        } catch (Exception e) {
            System.err.println("❌ Connexion échouée !");
            e.printStackTrace();
        }
    }
}
