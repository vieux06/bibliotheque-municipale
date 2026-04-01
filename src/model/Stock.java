package Modele;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère le stock d'exemplaires de la bibliothèque.
 * Permet d'ajouter, rechercher et consulter les exemplaires disponibles.
 */
public class Stock {
    //  Attributs 

    private List<Exemplaire> exemplaires;

    //  Constructeur 

    public Stock() {
        this.exemplaires = new ArrayList<>();
    }

    //  Getter 

    public List<Exemplaire> getExemplaires() {
        return new ArrayList<>(exemplaires); // copie défensive
    }

    //  Méthodes métier 

    /**
     * Ajoute un exemplaire au stock.
     */
    public void ajouterExemplaire(Exemplaire exemplaire) {
        if (exemplaire == null)
            throw new IllegalArgumentException("L'exemplaire ne peut pas être null.");
        exemplaires.add(exemplaire);
        System.out.println("Stock mis à jour : " + exemplaire);
    }

    /**
     * Retire un exemplaire du stock (ex: perdu ou retiré définitivement).
     */
    public boolean retirerExemplaire(int idExemplaire) {
        return exemplaires.removeIf(e -> e.getId() == idExemplaire);
    }

    /**
     * Retourne le premier exemplaire disponible pour un livre donné.
     * Retourne null si aucun exemplaire n'est disponible.
     */
    public Exemplaire trouverExemplaireDisponible(Livre livre) {
        if (livre == null)
            throw new IllegalArgumentException("Le livre ne peut pas être null.");
        return exemplaires.stream()
            .filter(e -> e.getLivre().equals(livre) && e.estDisponible())
            .findFirst()
            .orElse(null);
    }

    /**
     * Retourne tous les exemplaires (disponibles ou non) pour un livre.
     */
    public List<Exemplaire> getExemplairesParLivre(Livre livre) {
        List<Exemplaire> resultat = new ArrayList<>();
        for (Exemplaire e : exemplaires) {
            if (e.getLivre().equals(livre)) {
                resultat.add(e);
            }
        }
        return resultat;
    }

    /**
     * Compte le nombre d'exemplaires disponibles pour un livre.
     */
    public int compterDisponibles(Livre livre) {
        return (int) exemplaires.stream()
            .filter(e -> e.getLivre().equals(livre) && e.estDisponible())
            .count();
    }

    /**
     * Affiche l'état complet du stock.
     */
    public void afficherStock() {
        System.out.println("=== ÉTAT DU STOCK (" + exemplaires.size() + " exemplaires) ===");
        if (exemplaires.isEmpty()) {
            System.out.println("  (stock vide)");
        } else {
            for (Exemplaire e : exemplaires) {
                System.out.println("  " + e);
            }
        }
        System.out.println("==========================================");
    }
}
