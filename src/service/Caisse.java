package service;

import db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente la caisse de la bibliothèque.
 * Gère le solde et le paiement des amendes.
 * Il n'existe qu'une seule caisse (patron Singleton).
 */
public class Caisse {

    private static Caisse instance;

    private double solde;
    private List<Amende> amendesEnAttente;
    private List<Amende> amendesPayees;

    /**
     * Constructeur privé (Singleton).
     */
    private Caisse() {
        this.solde = 0.0;
        this.amendesEnAttente = new ArrayList<>();
        this.amendesPayees = new ArrayList<>();
    }

    /**
     * Retourne l'unique instance de la Caisse.
     */
    public static Caisse getInstance() {
        if (instance == null) {
            instance = new Caisse();
        }
        return instance;
    }

    /**
     * Enregistre une nouvelle amende à encaisser.
     *
     * @param amende l'amende générée par un retard
     */
    public void enregistrerAmende(Amende amende) {
        if (amende == null) {
            throw new IllegalArgumentException("L'amende ne peut pas être null.");
        }
        if (amende.isEstPayee()) {
            throw new IllegalStateException("Cette amende est déjà payée, impossible de la ré-enregistrer.");
        }
        amendesEnAttente.add(amende);
        System.out.println("[CAISSE] Amende enregistrée : " + amende.getMontant()
                + " FCFA pour " + amende.getEmprunt().getMembre().getNom() + ".");
    }

    /**
     * Encaisse le paiement d'une amende.
     * Le montant est ajouté au solde de la caisse.
     *
     * @param amende l'amende à payer
     */
    public void payerAmende(Amende amende) {
        if (amende == null) {
            throw new IllegalArgumentException("L'amende ne peut pas être null.");
        }
        if (amende.isEstPayee()) {
            throw new IllegalStateException("Cette amende est déjà payée.");
        }
        if (!amendesEnAttente.contains(amende)) {
            throw new IllegalStateException("Cette amende n'est pas enregistrée dans la caisse.");
        }

        amende.marquerPayee();
        amendesEnAttente.remove(amende);
        amendesPayees.add(amende);
        this.solde += amende.getMontant();

        System.out.println("[CAISSE] Encaissement de " + amende.getMontant()
                + " FCFA. Nouveau solde : " + solde + " FCFA.");
    }

    /**
     * Effectue un retrait de la caisse (ex : dépenses de fonctionnement).
     *
     * @param montant montant à retirer
     * @param motif   raison du retrait (pour traçabilité)
     */
    public void retirer(double montant, String motif) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif.");
        }
        if (montant > solde) {
            throw new IllegalStateException("Solde insuffisant. Solde actuel : " + solde
                    + " FCFA, retrait demandé : " + montant + " FCFA.");
        }
        this.solde -= montant;
        System.out.println("[CAISSE] Retrait de " + montant + " FCFA (" + motif
                + "). Nouveau solde : " + solde + " FCFA.");
    }

    /**
     * Affiche un résumé de l'état de la caisse.
     */
    public void afficherEtat() {
        System.out.println("══════════════════════════════════════");
        System.out.println("  ÉTAT DE LA CAISSE");
        System.out.println("  Solde actuel       : " + solde + " FCFA");
        System.out.println("  Amendes en attente : " + amendesEnAttente.size());
        System.out.println("  Amendes encaissées : " + amendesPayees.size());
        double totalEnAttente = amendesEnAttente.stream()
                .mapToDouble(Amende::getMontant).sum();
        System.out.println("  Montant à encaisser: " + totalEnAttente + " FCFA");
        System.out.println("══════════════════════════════════════");
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public double getSolde() { return solde; }

    public List<Amende> getAmendesEnAttente() {
        return new ArrayList<>(amendesEnAttente); // copie défensive
    }

    public List<Amende> getAmendesPayees() {
        return new ArrayList<>(amendesPayees); // copie défensive
    }

    // ── Méthodes DB ──────────────────────────────────────────────────────────

    /**
     * Charge le solde depuis la base de données.
     */
    public void loadSolde() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT solde FROM caisse WHERE id = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.solde = rs.getDouble("solde");
            }
        }
    }

    /**
     * Sauvegarde le solde en base de données.
     */
    public void saveSolde() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "UPDATE caisse SET solde = ? WHERE id = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, solde);
            stmt.executeUpdate();
        }
    }
}
