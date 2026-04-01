package Modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un membre inscrit à la bibliothèque.
 * Un membre peut emprunter des livres dans la limite autorisée.
 */
public class Membre {
    //  Constante 

    /** Nombre maximum d'emprunts simultanés autorisés par membre. */
    public static final int MAX_EMPRUNTS = 3;

    //  Attributs 

    private int        id;
    private String     nom;
    private String     prenom;
    private String     email;
    private String     telephone;
    private LocalDate  dateInscription;

    /**
     * Historique des IDs d'emprunts du membre.
     * La liste des emprunts actifs est gérée par la classe Emprunt (Étudiant 2).
     * Ici on stocke juste les IDs pour référence.
     */
    private List<Integer> idsEmprunts;

    //  Constructeurs 

    /**
     * Constructeur pour un nouveau membre (date d'inscription = aujourd'hui).
     */
    public Membre(String nom, String prenom, String email, String telephone) {
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setTelephone(telephone);
        this.dateInscription = LocalDate.now();
        this.idsEmprunts     = new ArrayList<>();
        this.id              = 0;
    }

    /**
     * Constructeur utilisé lors de la récupération depuis la base de données.
     */
    public Membre(int id, String nom, String prenom, String email,
                  String telephone, LocalDate dateInscription) {
        this(nom, prenom, email, telephone);
        this.id              = id;
        this.dateInscription = dateInscription;
    }

    //  Getters 

    public int        getId()              { return id; }
    public String     getNom()             { return nom; }
    public String     getPrenom()          { return prenom; }
    public String     getEmail()           { return email; }
    public String     getTelephone()       { return telephone; }
    public LocalDate  getDateInscription() { return dateInscription; }
    public List<Integer> getIdsEmprunts()  { return new ArrayList<>(idsEmprunts); }

    /** Nombre d'emprunts actifs (à mettre à jour depuis la classe Emprunt). */
    private int nombreEmpruntsActifs = 0;

    public int getNombreEmpruntsActifs() { return nombreEmpruntsActifs; }

    //  Setters avec validation 

    public void setId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("L'identifiant ne peut pas être négatif.");
        this.id = id;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        this.nom = nom.trim();
    }

    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty())
            throw new IllegalArgumentException("Le prénom ne peut pas être vide.");
        this.prenom = prenom.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Email invalide : " + email);
        this.email = email.toLowerCase().trim();
    }

    public void setTelephone(String telephone) {
        if (telephone == null)
            throw new IllegalArgumentException("Le téléphone ne peut pas être null.");
        String clean = telephone.replaceAll("[\\s\\-\\.]", "");
        if (!clean.matches("\\+?\\d{8,15}"))
            throw new IllegalArgumentException("Numéro de téléphone invalide : " + telephone);
        this.telephone = clean;
    }

    //  Méthodes métier 

    /**
     * Vérifie si le membre peut encore emprunter un livre.
     */
    public boolean peutEmprunter() {
        return nombreEmpruntsActifs < MAX_EMPRUNTS;
    }

    /**
     * Incrémente le compteur d'emprunts actifs (appelé par la classe Emprunt).
     */
    public void incrementerEmprunts() {
        if (!peutEmprunter())
            throw new IllegalStateException(
                "Le membre " + getNomComplet() +
                " a déjà atteint la limite de " + MAX_EMPRUNTS + " emprunts."
            );
        nombreEmpruntsActifs++;
    }

    /**
     * Décrémente le compteur d'emprunts actifs (appelé lors d'un retour).
     */
    public void decrementerEmprunts() {
        if (nombreEmpruntsActifs <= 0)
            throw new IllegalStateException("Le compteur d'emprunts est déjà à 0.");
        nombreEmpruntsActifs--;
    }

    /**
     * Ajoute un ID d'emprunt à l'historique.
     */
    public void ajouterIdEmprunt(int idEmprunt) {
        idsEmprunts.add(idEmprunt);
    }

    /**
     * Retourne le nom complet (prénom + nom).
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    //  Méthodes utilitaires 

    @Override
    public String toString() {
        return String.format(
            "[Membre #%d] %s %s | Email: %s | Tél: %s | Inscrit le: %s | Emprunts actifs: %d/%d",
            id, prenom, nom, email, telephone, dateInscription, nombreEmpruntsActifs, MAX_EMPRUNTS
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Membre)) return false;
        Membre autre = (Membre) obj;
        return this.email.equals(autre.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
