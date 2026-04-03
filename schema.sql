-- Schéma SQL pour la base de données de la bibliothèque municipale (SQLite)
-- Généré à partir des classes Java (model/ et service/)

-- Table pour les livres
CREATE TABLE livre (
    id INTEGER PRIMARY KEY,
    titre TEXT NOT NULL,
    auteur TEXT NOT NULL,
    isbn TEXT NOT NULL UNIQUE,
    genre TEXT NOT NULL,
    annee_publication INTEGER NOT NULL
);

-- Table pour les membres
CREATE TABLE membre (
    id INTEGER PRIMARY KEY,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    telephone TEXT NOT NULL,
    date_inscription TEXT NOT NULL -- Format YYYY-MM-DD
);

-- Table pour les exemplaires (liée à livre)
CREATE TABLE exemplaire (
    id INTEGER PRIMARY KEY,
    livre_id INTEGER NOT NULL,
    etat TEXT NOT NULL DEFAULT 'DISPONIBLE', -- 'DISPONIBLE', 'EMPRUNTE', 'ABIME', 'PERDU'
    numero_rayon INTEGER NOT NULL,
    FOREIGN KEY (livre_id) REFERENCES livre(id) ON DELETE CASCADE
);

-- Table pour les emprunts (liée à membre et exemplaire)
CREATE TABLE emprunt (
    id INTEGER PRIMARY KEY,
    membre_id INTEGER NOT NULL,
    exemplaire_id INTEGER NOT NULL,
    date_emprunt TEXT NOT NULL, -- Format YYYY-MM-DD
    date_retour_prevue TEXT NOT NULL,
    date_retour_effective TEXT NULL,
    est_clos INTEGER NOT NULL DEFAULT 0, -- 0=false, 1=true
    FOREIGN KEY (membre_id) REFERENCES membre(id) ON DELETE CASCADE,
    FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id) ON DELETE CASCADE
);

-- Contrainte unique sur exemplaire_id pour éviter emprunts multiples (seulement si non clos)
CREATE UNIQUE INDEX idx_emprunt_exemplaire_unique ON emprunt(exemplaire_id) WHERE est_clos = 0;

-- Table pour les amendes (liée à emprunt)
CREATE TABLE amende (
    id INTEGER PRIMARY KEY,
    emprunt_id INTEGER NOT NULL UNIQUE,
    jours_retard INTEGER NOT NULL,
    montant REAL NOT NULL,
    est_payee INTEGER NOT NULL DEFAULT 0,
    date_paiement TEXT NULL,
    FOREIGN KEY (emprunt_id) REFERENCES emprunt(id) ON DELETE CASCADE
);

-- Table pour la caisse (singleton, une seule ligne)
CREATE TABLE caisse (
    id INTEGER PRIMARY KEY CHECK (id = 1), -- Force une seule ligne
    solde REAL NOT NULL DEFAULT 0.0
);

-- Insertion de la ligne unique pour la caisse
INSERT OR IGNORE INTO caisse (id, solde) VALUES (1, 0.0);

-- Table pour les capteurs de surveillance
CREATE TABLE capteur (
    id INTEGER PRIMARY KEY,
    emplacement TEXT NOT NULL,
    temperature REAL NOT NULL DEFAULT 0.0,
    humidite REAL NOT NULL DEFAULT 0.0,
    derniere_lecture TEXT NULL, -- Format YYYY-MM-DD HH:MM:SS
    actif INTEGER NOT NULL DEFAULT 1 -- 0=false, 1=true
);

-- Table pour les véhicules de livraison
CREATE TABLE vehicule (
    id INTEGER PRIMARY KEY,
    immatriculation TEXT NOT NULL UNIQUE,
    modele TEXT NOT NULL,
    etat TEXT NOT NULL DEFAULT 'DISPONIBLE', -- 'DISPONIBLE', 'EN_LIVRAISON', 'EN_MAINTENANCE'
    date_derniere_revision TEXT NULL,
    annexe_actuelle TEXT NOT NULL
);

-- Index pour optimiser les requêtes
CREATE INDEX idx_exemplaire_livre ON exemplaire(livre_id);
CREATE INDEX idx_emprunt_membre ON emprunt(membre_id);
CREATE INDEX idx_emprunt_exemplaire ON emprunt(exemplaire_id);
CREATE INDEX idx_amende_emprunt ON amende(emprunt_id);