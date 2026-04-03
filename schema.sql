-- Schéma SQL pour la base de données de la bibliothèque municipale (MySQL)
-- Généré à partir des classes Java (model/ et service/)

-- Table pour les livres
CREATE TABLE livre (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    genre VARCHAR(100) NOT NULL,
    annee_publication INT NOT NULL CHECK (annee_publication BETWEEN 1450 AND 2100)
);

-- Table pour les membres
CREATE TABLE membre (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20) NOT NULL,
    date_inscription DATE NOT NULL
);

-- Table pour les exemplaires (liée à livre)
CREATE TABLE exemplaire (
    id INT AUTO_INCREMENT PRIMARY KEY,
    livre_id INT NOT NULL,
    etat ENUM('DISPONIBLE', 'EMPRUNTE', 'ABIME', 'PERDU') NOT NULL DEFAULT 'DISPONIBLE',
    numero_rayon INT NOT NULL CHECK (numero_rayon >= 0),
    FOREIGN KEY (livre_id) REFERENCES livre(id) ON DELETE CASCADE
);

-- Table pour les emprunts (liée à membre et exemplaire)
CREATE TABLE emprunt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    membre_id INT NOT NULL,
    exemplaire_id INT NOT NULL,
    date_emprunt DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE NULL,
    est_clos BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (membre_id) REFERENCES membre(id) ON DELETE CASCADE,
    FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id) ON DELETE CASCADE
);

-- Trigger pour empêcher deux emprunts non clos du même exemplaire
DELIMITER $$
CREATE TRIGGER before_emprunt_insert
BEFORE INSERT ON emprunt
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM emprunt
        WHERE exemplaire_id = NEW.exemplaire_id
          AND est_clos = FALSE
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Impossible : exemplaire déjà emprunté et non clos';
    END IF;
END$$

CREATE TRIGGER before_emprunt_update
BEFORE UPDATE ON emprunt
FOR EACH ROW
BEGIN
    IF NEW.est_clos = FALSE THEN
        IF EXISTS (
            SELECT 1 FROM emprunt
            WHERE exemplaire_id = NEW.exemplaire_id
              AND est_clos = FALSE
              AND id <> NEW.id
        ) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Impossible : exemplaire déjà emprunté et non clos';
        END IF;
    END IF;
END$$
DELIMITER ;

-- Table pour les amendes (liée à emprunt)
CREATE TABLE amende (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emprunt_id INT NOT NULL UNIQUE, -- Une amende par emprunt (si retard)
    jours_retard INT NOT NULL CHECK (jours_retard > 0),
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    est_payee BOOLEAN NOT NULL DEFAULT FALSE,
    date_paiement DATE NULL,
    FOREIGN KEY (emprunt_id) REFERENCES emprunt(id) ON DELETE CASCADE
);

-- Table pour la caisse (singleton, une seule ligne)
CREATE TABLE caisse (
    id INT PRIMARY KEY DEFAULT 1 CHECK (id = 1), -- Force une seule ligne
    solde DECIMAL(15,2) NOT NULL DEFAULT 0.00
);

-- Insertion de la ligne unique pour la caisse
INSERT INTO caisse (id, solde) VALUES (1, 0.00) ON DUPLICATE KEY UPDATE solde = solde;

-- Table pour les capteurs de surveillance
CREATE TABLE capteur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emplacement VARCHAR(255) NOT NULL,
    temperature DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    humidite DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    derniere_lecture DATETIME NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table pour les véhicules de livraison
CREATE TABLE vehicule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    modele VARCHAR(100) NOT NULL,
    etat ENUM('DISPONIBLE', 'EN_LIVRAISON', 'EN_MAINTENANCE') NOT NULL DEFAULT 'DISPONIBLE',
    date_derniere_revision DATE NULL,
    annexe_actuelle VARCHAR(100) NOT NULL
);

-- Index pour optimiser les requêtes
CREATE INDEX idx_exemplaire_livre ON exemplaire(livre_id);
CREATE INDEX idx_emprunt_membre ON emprunt(membre_id);
CREATE INDEX idx_emprunt_exemplaire ON emprunt(exemplaire_id);
CREATE INDEX idx_amende_emprunt ON amende(emprunt_id);