package service;

import java.time.LocalDate;

/**
 * Représente une amende générée suite à un retard de retour d'emprunt.
 * Une amende peut être payée (via la Caisse) ou rester impayée.
 */
public class Amende {

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
}
