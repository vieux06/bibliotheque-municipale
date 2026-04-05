# Bibliothèque Municipale

Système de Gestion d'une Bibliothèque Municipale — Projet Java POO  
DST-INFO2 GLSI — École Supérieure Polytechnique (ESP), UCAD, Dakar

## Fonctionnalités

- Gestion du catalogue de livres et des exemplaires
- Inscription et gestion des membres (max 3 emprunts simultanés)
- Gestion des emprunts avec calcul automatique des amendes (100 FCFA/jour)
- Caisse centralisée (Singleton) pour le traitement des paiements
- Surveillance des conditions de conservation (température, humidité)
- Gestion d'un véhicule pour les livraisons entre annexes
- Persistance complète en base de données MySQL

## Prérequis

- JDK 21 LTS
- XAMPP avec MySQL démarré
- Base de données `bibliotheque` créée dans phpMyAdmin

## Compilation
```bash
javac -cp "lib\mysql-connector-j-9.6.0.jar" -d out src\db\DatabaseManager.java src\model\*.java src\service\*.java src\Main.java
```

## Exécution
```bash
java -cp "out;lib\mysql-connector-j-9.6.0.jar" Main
```

## Structure du projet

src/
├── db/DatabaseManager.java
├── model/Livre.java, Membre.java, Exemplaire.java, Stock.java, Capteur.java, Vehicule.java
├── service/Emprunt.java, Amende.java, Caisse.java
└── Main.java
lib/mysql-connector-j-9.6.0.jar

## Auteurs

- Vieux Dame LEYE
- Seydina Mouhamadou Lamine DIOUF