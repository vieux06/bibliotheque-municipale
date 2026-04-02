package model;

import java.time.LocalDate;

/**
 * Représente un véhicule utilisé pour les livraisons d'ouvrages entre annexes.
 */
public class Vehicule {
    
    //  Énumération des états 

    public enum Etat {
        DISPONIBLE,    // prêt à partir en livraison
        EN_LIVRAISON,  // actuellement en déplacement
        EN_MAINTENANCE // en révision, indisponible
    }

    //  Attributs 

    private int       id;
    private String    immatriculation;   // ex: "DK-1234-AB"
    private String    modele;            // ex: "Renault Kangoo"
    private Etat      etat;
    private LocalDate dateDerniereRevision;
    private String    annexeActuelle;    // nom de l'annexe où se trouve le véhicule

    //  Constructeurs 

    public Vehicule(String immatriculation, String modele, String annexeActuelle) {
        setImmatriculation(immatriculation);
        setModele(modele);
        setAnnexeActuelle(annexeActuelle);
        this.etat                  = Etat.DISPONIBLE;
        this.dateDerniereRevision  = null;
        this.id                    = 0;
    }

    public Vehicule(int id, String immatriculation, String modele,
                    Etat etat, LocalDate dateDerniereRevision, String annexeActuelle) {
        this(immatriculation, modele, annexeActuelle);
        this.id                   = id;
        this.etat                 = etat;
        this.dateDerniereRevision = dateDerniereRevision;
    }

    //  Getters 

    public int       getId()                    { return id; }
    public String    getImmatriculation()       { return immatriculation; }
    public String    getModele()                { return modele; }
    public Etat      getEtat()                  { return etat; }
    public LocalDate getDateDerniereRevision()  { return dateDerniereRevision; }
    public String    getAnnexeActuelle()        { return annexeActuelle; }

    //  Setters avec validation 

    public void setId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("L'identifiant ne peut pas être négatif.");
        this.id = id;
    }

    public void setImmatriculation(String immatriculation) {
        if (immatriculation == null || immatriculation.trim().isEmpty())
            throw new IllegalArgumentException("L'immatriculation ne peut pas être vide.");
        this.immatriculation = immatriculation.trim().toUpperCase();
    }

    public void setModele(String modele) {
        if (modele == null || modele.trim().isEmpty())
            throw new IllegalArgumentException("Le modèle ne peut pas être vide.");
        this.modele = modele.trim();
    }

    public void setEtat(Etat etat) {
        if (etat == null)
            throw new IllegalArgumentException("L'état ne peut pas être null.");
        this.etat = etat;
    }

    public void setAnnexeActuelle(String annexe) {
        if (annexe == null || annexe.trim().isEmpty())
            throw new IllegalArgumentException("Le nom de l'annexe ne peut pas être vide.");
        this.annexeActuelle = annexe.trim();
    }

    public void setDateDerniereRevision(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La date de révision ne peut pas être dans le futur.");
        this.dateDerniereRevision = date;
    }

    //  Méthodes métier 

    /**
     * Lance une livraison vers une annexe destination.
     */
    public void partirEnLivraison(String annexeDestination) {
        if (this.etat != Etat.DISPONIBLE)
            throw new IllegalStateException(
                "Le véhicule " + immatriculation +
                " n'est pas disponible (état: " + etat + ")."
            );
        if (annexeDestination == null || annexeDestination.trim().isEmpty())
            throw new IllegalArgumentException("La destination ne peut pas être vide.");

        System.out.println("Véhicule " + immatriculation + " en route vers : " + annexeDestination);
        this.etat = Etat.EN_LIVRAISON;
        this.annexeActuelle = annexeDestination;
    }

    /**
     * Confirme l'arrivée à l'annexe destination.
     */
    public void confirmerArrivee() {
        if (this.etat != Etat.EN_LIVRAISON)
            throw new IllegalStateException(
                "Le véhicule " + immatriculation + " n'est pas en livraison."
            );
        this.etat = Etat.DISPONIBLE;
        System.out.println("Véhicule " + immatriculation + " arrivé à : " + annexeActuelle);
    }

    /**
     * Met le véhicule en maintenance.
     */
    public void mettreEnMaintenance() {
        if (this.etat == Etat.EN_LIVRAISON)
            throw new IllegalStateException(
                "Impossible de mettre en maintenance un véhicule en livraison."
            );
        this.etat = Etat.EN_MAINTENANCE;
    }

    /**
     * Termine la maintenance et enregistre la date de révision.
     */
    public void terminerMaintenance() {
        this.etat                 = Etat.DISPONIBLE;
        this.dateDerniereRevision = LocalDate.now();
        System.out.println("Maintenance terminée pour " + immatriculation +
                           " le " + dateDerniereRevision);
    }

    //  Méthodes utilitaires 

    @Override
    public String toString() {
        String revision = (dateDerniereRevision != null)
            ? dateDerniereRevision.toString() : "jamais";
        return String.format(
            "[Vehicule #%d] %s (%s) | État: %s | Annexe: %s | Dernière révision: %s",
            id, immatriculation, modele, etat, annexeActuelle, revision
        );
    }
}
