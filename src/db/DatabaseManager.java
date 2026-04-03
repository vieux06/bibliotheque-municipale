package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire de base de données pour SQLite.
 * Fournit une connexion unique et initialise le schéma si nécessaire.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:bibliotheque.db";
    private static Connection connection;

    /**
     * Obtient la connexion à la base de données.
     * Initialise la connexion si elle n'existe pas.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
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
                "CREATE TABLE IF NOT EXISTS livre (id INTEGER PRIMARY KEY, titre TEXT NOT NULL, auteur TEXT NOT NULL, isbn TEXT NOT NULL UNIQUE, genre TEXT NOT NULL, annee_publication INTEGER NOT NULL);",
                "CREATE TABLE IF NOT EXISTS membre (id INTEGER PRIMARY KEY, nom TEXT NOT NULL, prenom TEXT NOT NULL, email TEXT NOT NULL UNIQUE, telephone TEXT NOT NULL, date_inscription TEXT NOT NULL);",
                "CREATE TABLE IF NOT EXISTS exemplaire (id INTEGER PRIMARY KEY, livre_id INTEGER NOT NULL, etat TEXT NOT NULL DEFAULT 'DISPONIBLE', numero_rayon INTEGER NOT NULL, FOREIGN KEY (livre_id) REFERENCES livre(id) ON DELETE CASCADE);",
                "CREATE TABLE IF NOT EXISTS emprunt (id INTEGER PRIMARY KEY, membre_id INTEGER NOT NULL, exemplaire_id INTEGER NOT NULL, date_emprunt TEXT NOT NULL, date_retour_prevue TEXT NOT NULL, date_retour_effective TEXT NULL, est_clos INTEGER NOT NULL DEFAULT 0, FOREIGN KEY (membre_id) REFERENCES membre(id) ON DELETE CASCADE, FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id) ON DELETE CASCADE);",
                "CREATE UNIQUE INDEX IF NOT EXISTS idx_emprunt_exemplaire_unique ON emprunt(exemplaire_id) WHERE est_clos = 0;",
                "CREATE TABLE IF NOT EXISTS amende (id INTEGER PRIMARY KEY, emprunt_id INTEGER NOT NULL UNIQUE, jours_retard INTEGER NOT NULL, montant REAL NOT NULL, est_payee INTEGER NOT NULL DEFAULT 0, date_paiement TEXT NULL, FOREIGN KEY (emprunt_id) REFERENCES emprunt(id) ON DELETE CASCADE);",
                "CREATE TABLE IF NOT EXISTS caisse (id INTEGER PRIMARY KEY CHECK (id = 1), solde REAL NOT NULL DEFAULT 0.0);",
                "INSERT OR IGNORE INTO caisse (id, solde) VALUES (1, 0.0);",
                "CREATE TABLE IF NOT EXISTS capteur (id INTEGER PRIMARY KEY, emplacement TEXT NOT NULL, temperature REAL NOT NULL DEFAULT 0.0, humidite REAL NOT NULL DEFAULT 0.0, derniere_lecture TEXT NULL, actif INTEGER NOT NULL DEFAULT 1);",
                "CREATE TABLE IF NOT EXISTS vehicule (id INTEGER PRIMARY KEY, immatriculation TEXT NOT NULL UNIQUE, modele TEXT NOT NULL, etat TEXT NOT NULL DEFAULT 'DISPONIBLE', date_derniere_revision TEXT NULL, annexe_actuelle TEXT NOT NULL);",
                "CREATE INDEX IF NOT EXISTS idx_exemplaire_livre ON exemplaire(livre_id);",
                "CREATE INDEX IF NOT EXISTS idx_emprunt_membre ON emprunt(membre_id);",
                "CREATE INDEX IF NOT EXISTS idx_emprunt_exemplaire ON emprunt(exemplaire_id);",
                "CREATE INDEX IF NOT EXISTS idx_amende_emprunt ON amende(emprunt_id);"
            };

            for (String sql : schemaStatements) {
                stmt.execute(sql);
            }
            System.out.println("[DB] Base de données initialisée avec succès.");
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