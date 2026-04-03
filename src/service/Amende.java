package service;

import db.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;

/**
 * Représente une amende générée suite à un retard de retour d'emprunt.
 * Une amende peut être payée (via la Caisse) ou rester impayée.
 */
public class Amende {

    private int id;
    private Emprunt emprunt;
    private long joursRetard;
    private double montant;
    private boolean estPayee;
    private LocalDate datePaiement; // null si pas encore payée

    /**
     * Constructeur appelé par Emprunt.retourner() quand il y a un retard.
     */
    public Amende(Emprunt emprunt, long joursRetard, double montant) {
        if (emprunt == null) {
            throw new IllegalArgumentException("L'emprunt associé ne peut pas être null.");
        }
        if (joursRetard <= 0) {
            throw new IllegalArgumentException("Le nombre de jours de retard doit être positif.");
        }
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant de l'amende doit être positif.");
        }

        this.emprunt = emprunt;
        this.joursRetard = joursRetard;
        this.montant = montant;
        this.estPayee = false;
        this.datePaiement = null;
    }

    /**
     * Marque cette amende comme payée (appelé par la Caisse).
     * Ne peut être appelé qu'une seule fois.
     */
    void marquerPayee() {
        if (estPayee) {
            throw new IllegalStateException("Cette amende est déjà payée.");
        }
        this.estPayee = true;
        this.datePaiement = LocalDate.now();
        System.out.println("[AMENDE] Amende de " + montant + " FCFA payée le " + datePaiement
                + " pour " + emprunt.getMembre().getNom() + ".");
    }

    @Override
    public String toString() {
        return "Amende{"
                + "membre=" + emprunt.getMembre().getNom()
                + ", joursRetard=" + joursRetard
                + ", montant=" + montant + " FCFA"
                + ", statut=" + (estPayee ? "Payée le " + datePaiement : "Impayée")
                + "}";
    }

    // ── Getters ──

    public Emprunt getEmprunt() { return emprunt; }

    public long getJoursRetard() { return joursRetard; }

    public double getMontant() { return montant; }

    public boolean isEstPayee() { return estPayee; }

    public LocalDate getDatePaiement() { return datePaiement; }

    // ── Méthodes CRUD ──────────────────────────────────────────────────────────

    /**
     * Sauvegarde l'amende en base de données.
     */
    public void save() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        if (id == 0) {
            // Insert
            String sql = "INSERT INTO amende (emprunt_id, jours_retard, montant, est_payee, date_paiement) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, emprunt.getId());
                stmt.setLong(2, joursRetard);
                stmt.setDouble(3, montant);
                stmt.setInt(4, estPayee ? 1 : 0);
                stmt.setString(5, datePaiement != null ? datePaiement.toString() : null);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } else {
            // Update
            String sql = "UPDATE amende SET emprunt_id=?, jours_retard=?, montant=?, est_payee=?, date_paiement=? WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, emprunt.getId());
                stmt.setLong(2, joursRetard);
                stmt.setDouble(3, montant);
                stmt.setInt(4, estPayee ? 1 : 0);
                stmt.setString(5, datePaiement != null ? datePaiement.toString() : null);
                stmt.setInt(6, id);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Trouve une amende par son ID.
     */
    public static Amende findById(int id) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT * FROM amende WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Emprunt emprunt = Emprunt.findById(rs.getInt("emprunt_id"));
                Amende amende = new Amende(
                    emprunt,
                    rs.getLong("jours_retard"),
                    rs.getDouble("montant")
                );
                amende.id = rs.getInt("id");
                amende.estPayee = rs.getInt("est_payee") == 1;
                if (rs.getString("date_paiement") != null) {
                    amende.datePaiement = LocalDate.parse(rs.getString("date_paiement"));
                }
                return amende;
            }
        }
        return null;
    }

    /**
     * Supprime l'amende de la base de données.
     */
    public void delete() throws SQLException {
        if (id == 0) return;
        Connection conn = DatabaseManager.getConnection();
        String sql = "DELETE FROM amende WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
