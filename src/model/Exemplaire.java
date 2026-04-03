package model;

import db.DatabaseManager;
import java.sql.*;

/**
 * Représente un exemplaire physique d'un livre.
 * Un même livre (même ISBN) peut avoir plusieurs exemplaires en stock.
 * C'est l'exemplaire qui est emprunté, pas le livre.
 */
public class Exemplaire {
    //  Énumération des états possibles 

    public enum Etat {
        DISPONIBLE,    // en rayon, peut être emprunté
        EMPRUNTE,      // actuellement chez un membre
        ABIME,         // endommagé, nécessite une vérification
        PERDU          // signalé perdu
    }

    //  Attributs 

    private int   id;
    private Livre livre;       // le livre auquel cet exemplaire appartient
    private Etat  etat;
    private int   numeroRayon; // emplacement physique dans la bibliothèque

    //  Constructeurs  

    /**
     * Constructeur pour un nouvel exemplaire (état DISPONIBLE par défaut).
     */
    public Exemplaire(Livre livre, int numeroRayon) {
        setLivre(livre);
        setNumeroRayon(numeroRayon);
        this.etat = Etat.DISPONIBLE;
        this.id   = 0;
    }

    /**
     * Constructeur utilisé lors de la récupération depuis la base de données.
     */
    public Exemplaire(int id, Livre livre, Etat etat, int numeroRayon) {
        this(livre, numeroRayon);
        this.id   = id;
        this.etat = etat;
    }

    //  Getters 

    public int   getId()          { return id; }
    public Livre getLivre()       { return livre; }
    public Etat  getEtat()        { return etat; }
    public int   getNumeroRayon() { return numeroRayon; }

    //  Setters avec validation 

    public void setId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("L'identifiant ne peut pas être négatif.");
        this.id = id;
    }

    public void setLivre(Livre livre) {
        if (livre == null)
            throw new IllegalArgumentException("Un exemplaire doit être associé à un livre.");
        this.livre = livre;
    }

    public void setEtat(Etat etat) {
        if (etat == null)
            throw new IllegalArgumentException("L'état ne peut pas être null.");
        this.etat = etat;
    }

    public void setNumeroRayon(int numeroRayon) {
        if (numeroRayon < 0)
            throw new IllegalArgumentException("Le numéro de rayon ne peut pas être négatif.");
        this.numeroRayon = numeroRayon;
    }

    //  Méthodes métier

    /**
     * Indique si cet exemplaire peut être emprunté.
     */
    public boolean estDisponible() {
        return this.etat == Etat.DISPONIBLE;
    }

    /**
     * Alias pour compatibilité avec la classe Emprunt.
     */
    public boolean isDisponible() {
        return estDisponible();
    }

    /**
     * Modifie la disponibilité de l'exemplaire.
     */
    public void setDisponible(boolean disponible) {
        if (disponible) {
            marquerDisponible();
        } else {
            marquerEmprunte();
        }
    }

    /**
     * Marque l'exemplaire comme emprunté.
     * Lève une exception si l'exemplaire n'est pas disponible.
     */
    public void marquerEmprunte() {
        if (!estDisponible())
            throw new IllegalStateException(
                "Impossible d'emprunter l'exemplaire #" + id +
                " : état actuel = " + etat
            );
        this.etat = Etat.EMPRUNTE;
    }

    /**
     * Remet l'exemplaire en rayon après retour.
     */
    public void marquerDisponible() {
        this.etat = Etat.DISPONIBLE;
    }

    //  Méthodes utilitaires 

    @Override
    public String toString() {
        return String.format(
            "[Exemplaire #%d] \"%s\" | État: %s | Rayon: %d",
            id, livre.getTitre(), etat, numeroRayon
        );
    }

    // ── Méthodes CRUD ──────────────────────────────────────────────────────────

    /**
     * Sauvegarde l'exemplaire en base de données.
     */
    public void save() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        if (id == 0) {
            // Insert
            String sql = "INSERT INTO exemplaire (livre_id, etat, numero_rayon) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, livre.getId());
                stmt.setString(2, etat.toString());
                stmt.setInt(3, numeroRayon);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } else {
            // Update
            String sql = "UPDATE exemplaire SET livre_id=?, etat=?, numero_rayon=? WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, livre.getId());
                stmt.setString(2, etat.toString());
                stmt.setInt(3, numeroRayon);
                stmt.setInt(4, id);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Trouve un exemplaire par son ID.
     */
    public static Exemplaire findById(int id) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT * FROM exemplaire WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Livre livre = Livre.findById(rs.getInt("livre_id"));
                return new Exemplaire(
                    rs.getInt("id"),
                    livre,
                    Etat.valueOf(rs.getString("etat")),
                    rs.getInt("numero_rayon")
                );
            }
        }
        return null;
    }

    /**
     * Supprime l'exemplaire de la base de données.
     */
    public void delete() throws SQLException {
        if (id == 0) return;
        Connection conn = DatabaseManager.getConnection();
        String sql = "DELETE FROM exemplaire WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
