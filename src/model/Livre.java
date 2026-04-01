package Modele;
/**
 * Représente un livre dans le catalogue de la bibliothèque.
 * Un livre est une notice bibliographique (titre, auteur, ISBN...).
 * Les exemplaires physiques sont gérés par la classe Exemplaire.
 */
public class Livre {
    
    //  Attributs

    private int    id;       // identifiant en base de données (0 = pas encore enregistré)
    private String titre;
    private String auteur;
    private String isbn;     // format attendu : 13 chiffres
    private String genre;    // ex: "Roman", "Science-fiction", "Histoire"
    private int    anneePublication;

    //  Constructeurs 

    /**
     * Constructeur principal utilisé pour créer un nouveau livre (pas encore en BDD).
     */
    public Livre(String titre, String auteur, String isbn, String genre, int anneePublication) {
        setTitre(titre);
        setAuteur(auteur);
        setIsbn(isbn);
        setGenre(genre);
        setAnneePublication(anneePublication);
        this.id = 0;
    }

    /**
     * Constructeur utilisé lors de la récupération depuis la base de données.
     */
    public Livre(int id, String titre, String auteur, String isbn, String genre, int anneePublication) {
        this(titre, auteur, isbn, genre, anneePublication);
        this.id = id;
    }

    //  Getters

    public int    getId()               { return id; }
    public String getTitre()            { return titre; }
    public String getAuteur()           { return auteur; }
    public String getIsbn()             { return isbn; }
    public String getGenre()            { return genre; }
    public int    getAnneePublication() { return anneePublication; }

    //  Setters avec validation

    public void setId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("L'identifiant ne peut pas être négatif.");
        this.id = id;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.trim().isEmpty())
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        this.titre = titre.trim();
    }

    public void setAuteur(String auteur) {
        if (auteur == null || auteur.trim().isEmpty())
            throw new IllegalArgumentException("L'auteur ne peut pas être vide.");
        this.auteur = auteur.trim();
    }

    public void setIsbn(String isbn) {
        if (isbn == null)
            throw new IllegalArgumentException("L'ISBN ne peut pas être null.");
        // On supprime les tirets et espaces éventuels avant validation
        String clean = isbn.replaceAll("[\\s\\-]", "");
        if (!clean.matches("\\d{13}"))
            throw new IllegalArgumentException("L'ISBN doit contenir exactement 13 chiffres. Reçu : " + isbn);
        this.isbn = clean;
    }

    public void setGenre(String genre) {
        if (genre == null || genre.trim().isEmpty())
            throw new IllegalArgumentException("Le genre ne peut pas être vide.");
        this.genre = genre.trim();
    }

    public void setAnneePublication(int annee) {
        if (annee < 1450 || annee > 2100)
            throw new IllegalArgumentException(
                "Année de publication invalide : " + annee +
                ". Doit être comprise entre 1450 et 2100."
            );
        this.anneePublication = annee;
    }

    //  Méthodes utilitaires 

    /**
     * Affichage lisible du livre.
     */
    @Override
    public String toString() {
        return String.format(
            "[Livre #%d] \"%s\" — %s | ISBN: %s | Genre: %s | Année: %d",
            id, titre, auteur, isbn, genre, anneePublication
        );
    }

    /**
     * Deux livres sont identiques s'ils ont le même ISBN.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Livre)) return false;
        Livre autre = (Livre) obj;
        return this.isbn.equals(autre.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}
