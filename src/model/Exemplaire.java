package model;

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

    public void setDisponible(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDisponible'");
    }
}
