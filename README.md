# bibliotheque-municipale
Projet Java — Système de Gestion d'une Bibliothèque Municipale

Pour compiler sur l'invite de commande (CMD), on se met sur le dossier bibliotheque-principale et on execute la commande: 

javac -cp "lib\mysql-connector-j-9.6.0.jar" -d out src\db\DatabaseManager.java src\model\Livre.java src\model\Membre.java src\model\Exemplaire.java src\model\Capteur.java src\model\Vehicule.java src\model\Stock.java src\service\Amende.java src\service\Caisse.java src\service\Emprunt.java src\Main.jav

puis la commande ci-apres pour executer: java -cp "out;lib\mysql-connector-j-9.6.0.jar" Main