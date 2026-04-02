package service;

import model.Exemplaire;
import model.Membre;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Représente un emprunt d'un exemplaire par un membre.
 * Un emprunt a une durée maximale de 14 jours.
 */
public class Emprunt {

    // Durée maximale d'un emprunt en jours
    public static final int DUREE_MAX_JOURS = 14;

    // Tarif de l'amende par jour de retard (en FCFA)
    public static final double TARIF_AMENDE_PAR_JOUR = 100.0;

    private int id;
    private Membre membre;
    private Exemplaire exemplaire;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective; // null si pas encore rendu
    private boolean estClos;

    /**
     * Constructeur : crée un nouvel emprunt à la date d'aujourd'hui.
     */
    public Emprunt(int id, Membre membre, Exemplaire exemplaire) {
        if (membre == null) {
            throw new IllegalArgumentException("Le membre ne peut pas être null.");
        }
        if (exemplaire == null) {
            throw new IllegalArgumentException("L'exemplaire ne peut pas être null.");
        }
        if (!exemplaire.isDisponible()) {
            throw new IllegalStateException("L'exemplaire " + exemplaire.getId()
                    + " n'est pas disponible pour un emprunt.");
        }

        this.id = id;
        this.membre = membre;
        this.exemplaire = exemplaire;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = this.dateEmprunt.plusDays(DUREE_MAX_JOURS);
        this.dateRetourEffective = null;
        this.estClos = false;

        // Marquer l'exemplaire comme indisponible
        exemplaire.setDisponible(false);
    }

    /**
     * Enregistre le retour de l'exemplaire emprunté.
     * Calcule automatiquement une amende si retard.
     *
     * @return une Amende si retard, null sinon
     */
    public Amende retourner() {
        if (estClos) {
            throw new IllegalStateException("Cet emprunt est déjà clos.");
        }

        this.dateRetourEffective = LocalDate.now();
        this.estClos = true;

        // Remettre l'exemplaire disponible
        exemplaire.setDisponible(true);

        // Calculer l'amende si retard
        long joursRetard = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourEffective);
        if (joursRetard > 0) {
            double montant = joursRetard * TARIF_AMENDE_PAR_JOUR;
            System.out.println("[EMPRUNT] Retard de " + joursRetard + " jour(s) pour "
                    + membre.getNom() + ". Amende : " + montant + " FCFA.");
            return new Amende(this, joursRetard, montant);
        }

        System.out.println("[EMPRUNT] Retour dans les délais pour " + membre.getNom() + ". Aucune amende.");
        return null;
    }

    /**
     * Indique si cet emprunt est en retard à la date d'aujourd'hui.
     */
    public boolean isEnRetard() {
        if (estClos) return false;
        return LocalDate.now().isAfter(dateRetourPrevue);
    }

    /**
     * Nombre de jours de retard actuels (0 si pas en retard).
     */
    public long getJoursRetardActuels() {
        if (!isEnRetard()) return 0;
        return ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
    }

    @Override
    public String toString() {
        return "Emprunt{id=" + id
                + ", membre=" + membre.getNom()
                + ", exemplaire=" + exemplaire.getId()
                + ", dateEmprunt=" + dateEmprunt
                + ", dateRetourPrevue=" + dateRetourPrevue
                + ", rendu=" + (estClos ? dateRetourEffective.toString() : "non rendu")
                + "}";
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getId() { return id; }

    public Membre getMembre() { return membre; }

    public Exemplaire getExemplaire() { return exemplaire; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }

    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }

    public LocalDate getDateRetourEffective() { return dateRetourEffective; }

    public boolean isEstClos() { return estClos; }
}
