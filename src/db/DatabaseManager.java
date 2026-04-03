package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire de base de données pour MySQL.
 * Fournit une connexion unique et initialise le schéma si nécessaire.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bibliotheque?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // À adapter selon votre config
    private static final String PASSWORD = ""; // À adapter selon votre config
    private static Connection connection;

    /**
     * Obtient la connexion à la base de données.
     * Initialise la connexion si elle n'existe pas.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            initializeDatabase();
        }
        return connection;
    }

    /**
     * Initialise la base de données en exécutant le schéma SQL.
     */
    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Lire et exécuter le schéma depuis schema.sql
            // Pour simplicité, on peut hardcoder ou lire le fichier
            // Ici, on exécute les CREATE TABLE directement
            String[] schemaStatements = {
                "CREATE TABLE IF NOT EXISTS livre (id INT AUTO_INCREMENT PRIMARY KEY, titre VARCHAR(255) NOT NULL, auteur VARCHAR(255) NOT NULL, isbn VARCHAR(13) NOT NULL UNIQUE, genre VARCHAR(100) NOT NULL, annee_publication INT NOT NULL);",
                "CREATE TABLE IF NOT EXISTS membre (id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(100) NOT NULL, prenom VARCHAR(100) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE, telephone VARCHAR(20) NOT NULL, date_inscription DATE NOT NULL);",
                "CREATE TABLE IF NOT EXISTS exemplaire (id INT AUTO_INCREMENT PRIMARY KEY, livre_id INT NOT NULL, etat ENUM('DISPONIBLE', 'EMPRUNTE', 'ABIME', 'PERDU') NOT NULL DEFAULT 'DISPONIBLE', numero_rayon INT NOT NULL, FOREIGN KEY (livre_id) REFERENCES livre(id) ON DELETE CASCADE);",
                "CREATE TABLE IF NOT EXISTS emprunt (id INT AUTO_INCREMENT PRIMARY KEY, membre_id INT NOT NULL, exemplaire_id INT NOT NULL, date_emprunt DATE NOT NULL, date_retour_prevue DATE NOT NULL, date_retour_effective DATE NULL, est_clos BOOLEAN NOT NULL DEFAULT FALSE, FOREIGN KEY (membre_id) REFERENCES membre(id) ON DELETE CASCADE, FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id) ON DELETE CASCADE);",
                "ALTER TABLE emprunt ADD CONSTRAINT IF NOT EXISTS unique_exemplaire_non_clos UNIQUE (exemplaire_id, est_clos);",
                "CREATE TABLE IF NOT EXISTS amende (id INT AUTO_INCREMENT PRIMARY KEY, emprunt_id INT NOT NULL UNIQUE, jours_retard INT NOT NULL, montant DECIMAL(10,2) NOT NULL, est_payee BOOLEAN NOT NULL DEFAULT FALSE, date_paiement DATE NULL, FOREIGN KEY (emprunt_id) REFERENCES emprunt(id) ON DELETE CASCADE);",
                "CREATE TABLE IF NOT EXISTS caisse (id INT PRIMARY KEY DEFAULT 1, solde DECIMAL(15,2) NOT NULL DEFAULT 0.00);",
                "INSERT IGNORE INTO caisse (id, solde) VALUES (1, 0.00);",
                "CREATE TABLE IF NOT EXISTS capteur (id INT AUTO_INCREMENT PRIMARY KEY, emplacement VARCHAR(255) NOT NULL, temperature DECIMAL(5,2) NOT NULL DEFAULT 0.00, humidite DECIMAL(5,2) NOT NULL DEFAULT 0.00, derniere_lecture DATETIME NULL, actif BOOLEAN NOT NULL DEFAULT TRUE);",
                "CREATE TABLE IF NOT EXISTS vehicule (id INT AUTO_INCREMENT PRIMARY KEY, immatriculation VARCHAR(20) NOT NULL UNIQUE, modele VARCHAR(100) NOT NULL, etat ENUM('DISPONIBLE', 'EN_LIVRAISON', 'EN_MAINTENANCE') NOT NULL DEFAULT 'DISPONIBLE', date_derniere_revision DATE NULL, annexe_actuelle VARCHAR(100) NOT NULL);",
                "CREATE INDEX IF NOT EXISTS idx_exemplaire_livre ON exemplaire(livre_id);",
                "CREATE INDEX IF NOT EXISTS idx_emprunt_membre ON emprunt(membre_id);",
                "CREATE INDEX IF NOT EXISTS idx_emprunt_exemplaire ON emprunt(exemplaire_id);",
                "CREATE INDEX IF NOT EXISTS idx_amende_emprunt ON amende(emprunt_id);"
            };

            for (String sql : schemaStatements) {
                stmt.execute(sql);
            }
            System.out.println("[DB] Base de données MySQL initialisée avec succès.");
        }
    }

    /**
     * Ferme la connexion à la base de données.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("[DB] Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
}