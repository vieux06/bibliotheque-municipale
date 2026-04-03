import db.DatabaseManager;
import model.Livre;
import model.Exemplaire;
import model.Membre;
import model.Capteur;
import model.Vehicule;
import service.Emprunt;
import service.Caisse;
import service.Amende;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("== Bibliothèque municipale - scénario complet avec DB ==");

        try {
            // Initialisation DB
            DatabaseManager.getConnection();
            System.out.println("Base de données initialisée.\n");

            // Création et sauvegarde d'un livre
            Livre livre = new Livre("Une si longue lettre", "Mariama Ba", "2723604306", "Roman", 1979);
            livre.save();
            System.out.println("Livre créé : " + livre);

            // Création et sauvegarde d'un exemplaire
            Exemplaire exemplaire = new Exemplaire(livre, 42);
            exemplaire.save();
            System.out.println("Exemplaire créé : " + exemplaire);

            // Création et sauvegarde d'un membre
            Membre membre = new Membre("Leye", "Vieux Dame", "vieuxdameleye96@gmail.com", "+221 77 913 00 57");
            membre.save();
            System.out.println("Membre créé : " + membre);

            // Création d'un capteur
            Capteur capteur = new Capteur("Salle A - Rayon 3");
            capteur.enregistrerMesure(20.5, 45.0);
            capteur.save();
            System.out.println("Capteur créé : " + capteur);

            // Création d'un véhicule
            Vehicule vehicule = new Vehicule("DK-1234-AB", "Renault Kangoo", "Annexe Centre");
            vehicule.save();
            System.out.println("Véhicule créé : " + vehicule);

            System.out.println("\n--- État initial du système ---");
            afficherEtatSysteme();

            // Emprunt
            Emprunt emprunt = new Emprunt(1, membre, exemplaire);
            emprunt.save();
            System.out.println("\nEmprunt créé : " + emprunt);
            System.out.println("Disponible après emprunt ? " + exemplaire.isDisponible());

            System.out.println("\n--- État après emprunt ---");
            afficherEtatSysteme();

            // Retour immédiat (pas de retard)
            Amende amende = emprunt.retourner();
            emprunt.save();
            if (amende != null) {
                amende.save();
                System.out.println("Amende générée : " + amende);
                Caisse.getInstance().enregistrerAmende(amende);
                Caisse.getInstance().payerAmende(amende);
                Caisse.getInstance().saveSolde();
            } else {
                System.out.println("Retour sans retard, pas d'amende.");
            }

            System.out.println("Disponible après retour ? " + exemplaire.isDisponible());

            System.out.println("\n--- État final du système ---");
            afficherEtatSysteme();

            // Fermeture DB
            DatabaseManager.closeConnection();

        } catch (SQLException | IllegalArgumentException | IllegalStateException ex) {
            System.err.println("Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("\n== Fin du scénario ==");
    }

    private static void afficherEtatSysteme() throws SQLException {
        System.out.println("Livres en DB : " + compterEntites("livre"));
        System.out.println("Exemplaires en DB : " + compterEntites("exemplaire"));
        System.out.println("Membres en DB : " + compterEntites("membre"));
        System.out.println("Emprunts en DB : " + compterEntites("emprunt"));
        System.out.println("Amendes en DB : " + compterEntites("amende"));
        System.out.println("Capteurs en DB : " + compterEntites("capteur"));
        System.out.println("Véhicules en DB : " + compterEntites("vehicule"));

        Caisse.getInstance().loadSolde();
        Caisse.getInstance().afficherEtat();
    }

    private static int compterEntites(String table) throws SQLException {
        var conn = DatabaseManager.getConnection();
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        stmt.close();
        return count;
    }
}
