import db.DatabaseManager;
import model.Livre;
import model.Exemplaire;
import model.Membre;
import model.Capteur;
import model.Vehicule;
import model.Stock;
import service.Emprunt;
import service.Caisse;
import service.Amende;

import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("  BIBLIOTHÈQUE MUNICIPALE - Scénario Complet Réaliste");
        System.out.println("========================================================\n");

        try {
            // Initialisation DB
            DatabaseManager.getConnection();
            System.out.println(" Base de données initialisée.\n");

            // Initialisation du Stock
            Stock stock = new Stock();

            // ============ SECTION 1 : CRÉATION ENTITÉS ============
            System.out.println("┌─ SECTION 1 : Création des entités");
            System.out.println("│");

            // Création livres
            Livre livre1 = new Livre("Une si longue lettre", "Mariama Ba", "9782723604306", "Roman", 1979);
            livre1.save();
            System.out.println("│ [Livre 1] " + livre1.getTitre());

            Livre livre2 = new Livre("L'enfant nègre", "Camara Laye", "9782070367283", "Autobiographie", 1953);
            livre2.save();
            System.out.println("│ [Livre 2] " + livre2.getTitre());

            // Création exemplaires et ajout au stock
            Exemplaire exemplaire1 = new Exemplaire(livre1, 10);
            exemplaire1.save();
            stock.ajouterExemplaire(exemplaire1);
            System.out.println("│ [Exemplaire 1] Rayon 10");

            Exemplaire exemplaire2 = new Exemplaire(livre1, 11);
            exemplaire2.save();
            stock.ajouterExemplaire(exemplaire2);
            System.out.println("│ [Exemplaire 2] Rayon 11");

            Exemplaire exemplaire3 = new Exemplaire(livre2, 5);
            exemplaire3.save();
            stock.ajouterExemplaire(exemplaire3);
            System.out.println("│ [Exemplaire 3] Rayon 5");

            // Création membres
            Membre membre1 = new Membre("Leye", "Vieux Dame", "vieuxdameleye96@gmail.com", "+221 77 913 00 57");
            membre1.save();
            System.out.println("│ [Membre 1] " + membre1.getPrenom() + " " + membre1.getNom());

            Membre membre2 = new Membre("Diouf", "Lamine", "laminediouf@mail.com", "+221 76 123 45 67");
            membre2.save();
            System.out.println("│ [Membre 2] " + membre2.getPrenom() + " " + membre2.getNom());

            // Création capteur et véhicule
            Capteur capteur = new Capteur("Salle A - Rayon 3");
            capteur.enregistrerMesure(20.5, 45.0);
            capteur.save();
            System.out.println("│ [Capteur] " + capteur);

            // Simuler alerte de conservation hors seuil
            System.out.println("\n--- Alerte conservation ---");
            capteur.enregistrerMesure(20.5, 72.0); // Humidité trop élevée
            capteur.save();
            System.out.println("│ ALERTE : " + capteur.getEtatConditions() + "\n");

            Vehicule vehicule = new Vehicule("DK-1234-AB", "Renault Kangoo", "Annexe Centre");
            vehicule.save();
            System.out.println("│ [Véhicule] " + vehicule + "\n");

            // Simulation de livraison entre annexes
            System.out.println("\n--- Livraison entre annexes ---");
            vehicule.partirEnLivraison("Annexe Sud");
            System.out.println("│ Véhicule en livraison vers Annexe Sud");
            vehicule.confirmerArrivee();
            vehicule.save();
            System.out.println("│ Véhicule revenu disponible : " + vehicule.getEtat() + " à " + vehicule.getAnnexeActuelle() + "\n");

            // ============ SECTION 2 : STOCK & RECHERCHE ============
            System.out.println("┌─ SECTION 2 : Gestion du Stock");
            System.out.println("│");
            stock.afficherStock();

            System.out.println("│ Exemplaires disponibles pour \"" + livre1.getTitre() + "\" : "
                    + stock.compterDisponibles(livre1) + "\n");

            // ============ SECTION 3 : SCÉNARIO EMPRUNT NORMAL ============
            System.out.println("┌─ SECTION 3 : Scénario d'emprunt normal");
            System.out.println("│");

            System.out.println("--- État initial du système ---");
            afficherEtatSysteme();

            // Emprunt via Stock
            Exemplaire exEmprunt = stock.trouverExemplaireDisponible(livre1);
            if (exEmprunt != null) {
                Emprunt emprunt = new Emprunt(0, membre1, exEmprunt);
                emprunt.save();
                System.out.println("│  Emprunt créé");
                System.out.println("│   - Membre : " + membre1.getPrenom());
                System.out.println("│   - Livre : " + exEmprunt.getLivre().getTitre());
                System.out.println("│   - Disponible ? " + exEmprunt.isDisponible() + "\n");

                System.out.println("--- État après emprunt ---");
                afficherEtatSysteme();

                // Retour immédiat
                Amende amende = emprunt.retourner();
                emprunt.save();
                if (amende != null) {
                    amende.save();
                    System.out.println("[ALERTE] Amende générée : " + amende.getMontant() + " FCFA");
                } else {
                    System.out.println("│  Retour sans retard");
                }
                System.out.println("│   - Disponible après retour ? " + exEmprunt.isDisponible() + "\n");

                System.out.println("--- État après retour ---");
                afficherEtatSysteme();

                // Boucle de test : emprunt en retard pour générer une amende et mettre à jour la caisse
                System.out.println("\n--- Scénario d'emprunt en retard (génération d'amende) ---");
                Exemplaire exRetard = stock.trouverExemplaireDisponible(livre2);
                if (exRetard != null) {
                    Emprunt empruntRetard = new Emprunt(
                            0,
                            membre2,
                            exRetard,
                            LocalDate.now().minusDays(20),
                            LocalDate.now().minusDays(6)
                    );
                    empruntRetard.save();
                    System.out.println("│ ✓ Emprunt en retard créé pour " + membre2.getPrenom());

                    Amende amendeRetard = empruntRetard.retourner();
                    empruntRetard.save();
                    if (amendeRetard != null) {
                        amendeRetard.save();
                        Caisse.getInstance().enregistrerAmende(amendeRetard);
                        Caisse.getInstance().payerAmende(amendeRetard);
                        Caisse.getInstance().saveSolde();
                        System.out.println("│ ✓ Amende payée : " + amendeRetard.getMontant() + " FCFA");
                    }

                    System.out.println("--- État après emprunt en retard ---");
                    afficherEtatSysteme();
                }
            }

            // ============ SECTION 4 : TEST CAS D'ERREUR ============
            System.out.println("\n┌─ SECTION 4 : Tests des validations et cas d'erreur");
            System.out.println("│");

            // Test 1 : ISBN invalide
            System.out.println("│ TEST 1 : Création avec ISBN invalide");
            try {
                new Livre("Titre", "Auteur", "12345", "Genre", 2020);
                System.out.println("│  ERREUR : Aurait dû lever une exception");
            } catch (IllegalArgumentException e) {
                System.out.println("│  Exception capturée : " + e.getMessage());
            }

            // Test 2 : Année invalide
            System.out.println("│\n│ TEST 2 : Création avec année invalide");
            try {
                new Livre("Titre", "Auteur", "9782723604306", "Genre", 2500);
                System.out.println("│  ERREUR : Aurait dû lever une exception");
            } catch (IllegalArgumentException e) {
                System.out.println("│  Exception capturée : " + e.getMessage());
            }

            // Test 3 : Création avec champ vide
            System.out.println("│\n│ TEST 3 : Création membre avec email vide");
            try {
                new Membre("Nom", "Prenom", "", "+221");
                System.out.println("│  ERREUR : Aurait dû lever une exception");
            } catch (IllegalArgumentException e) {
                System.out.println("│  Exception capturée : " + e.getMessage());
            }

            // Test 4 : Payement d'amende null
            System.out.println("│\n│ TEST 4 : Paiement d'amende null");
            try {
                Caisse.getInstance().payerAmende(null);
                System.out.println("│  ERREUR : Aurait dû lever une exception");
            } catch (IllegalArgumentException e) {
                System.out.println("│  Exception capturée : " + e.getMessage());
            }

            // Test 5 : Retrait exemplaire du stock
            System.out.println("│\n│ TEST 5 : Retrait exemplaire du stock");
            Exemplaire exRetire = stock.trouverExemplaireDisponible(livre2);
            if (exRetire != null) {
                int idEx = exRetire.getId();
                boolean retire = stock.retirerExemplaire(idEx);
                System.out.println("│  Exemplaire retiré : " + retire);
                System.out.println("│   Stock après retrait : " + stock.getExemplaires().size() + " exemplaire(s)");
            }

            // Test 6 : Recherche exemplaire pour livre inexistant (via stock)
            System.out.println("│\n│ TEST 6 : Recherche exemplaire pour nouveau livre");
            Livre titreMystere = new Livre("Titre Mystère", "Auteur Secret", "1111111111111", "Mystère", 2000);
            Exemplaire resultat = stock.trouverExemplaireDisponible(titreMystere);
            System.out.println("│  Exemplaire trouvé : " + (resultat != null ? resultat.getId() : "Aucun"));

            System.out.println("│\n");

            // ============ SECTION 5 : AFFICHAGE FINAL ============
            System.out.println("┌─ SECTION 5 : État final du système");
            System.out.println("│");
            afficherEtatSysteme();

            // Fermeture DB
            DatabaseManager.closeConnection();
            System.out.println("│  Connexion BD fermée\n");

        } catch (SQLException e) {
            System.err.println("[ERREUR BD] " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("========================================================");
        System.out.println("  Fin du scénario");
        System.out.println("========================================================");
    }

    private static void afficherEtatSysteme() throws SQLException {
        System.out.println("│ Livres en DB         : " + compterEntites("livre"));
        System.out.println("│ Exemplaires en DB    : " + compterEntites("exemplaire"));
        System.out.println("│ Membres en DB        : " + compterEntites("membre"));
        System.out.println("│ Emprunts en DB       : " + compterEntites("emprunt"));
        System.out.println("│ Amendes en DB        : " + compterEntites("amende"));
        System.out.println("│ Capteurs en DB       : " + compterEntites("capteur"));
        System.out.println("│ Véhicules en DB      : " + compterEntites("vehicule"));

        Caisse.getInstance().loadSolde();
        System.out.print("│ ");
        Caisse.getInstance().afficherEtat();
        System.out.println("│");
    }

    private static int compterEntites(String table) throws SQLException {
        var conn = DatabaseManager.getConnection();
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        stmt.close();
        return count;
    }
}
