package Modele;

import java.time.LocalDateTime;

/**
 * Représente un capteur de surveillance des conditions de conservation.
 * Surveille la température et l'humidité dans les salles de stockage.
 */
public class Capteur {
    //  Constantes (seuils recommandés pour la conservation des livres) 

    public static final double TEMP_MIN  =  15.0; // °C
    public static final double TEMP_MAX  =  22.0; // °C
    public static final double HUMI_MIN  =  40.0; // %
    public static final double HUMI_MAX  =  60.0; // %

    //  Attributs 

    private int           id;
    private String        emplacement;         // ex: "Salle A - Rayon 3"
    private double        temperature;         // en °C
    private double        humidite;            // en %
    private LocalDateTime derniereLecture;
    private boolean       actif;

    //  Constructeurs 

    public Capteur(String emplacement) {
        setEmplacement(emplacement);
        this.temperature    = 0.0;
        this.humidite       = 0.0;
        this.derniereLecture = null;
        this.actif          = true;
        this.id             = 0;
    }

    public Capteur(int id, String emplacement, double temperature,
                   double humidite, LocalDateTime derniereLecture, boolean actif) {
        this(emplacement);
        this.id             = id;
        this.temperature    = temperature;
        this.humidite       = humidite;
        this.derniereLecture = derniereLecture;
        this.actif          = actif;
    }

    //  Getters 

    public int           getId()             { return id; }
    public String        getEmplacement()    { return emplacement; }
    public double        getTemperature()    { return temperature; }
    public double        getHumidite()       { return humidite; }
    public LocalDateTime getDerniereLecture(){ return derniereLecture; }
    public boolean       isActif()           { return actif; }

    //  Setters avec validation 

    public void setId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("L'identifiant ne peut pas être négatif.");
        this.id = id;
    }

    public void setEmplacement(String emplacement) {
        if (emplacement == null || emplacement.trim().isEmpty())
            throw new IllegalArgumentException("L'emplacement ne peut pas être vide.");
        this.emplacement = emplacement.trim();
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    //  Méthodes métier 

    /**
     * Enregistre une nouvelle mesure de température et d'humidité.
     * Met à jour automatiquement la date/heure de lecture.
     */
    public void enregistrerMesure(double temperature, double humidite) {
        if (!actif)
            throw new IllegalStateException(
                "Le capteur '" + emplacement + "' est désactivé."
            );
        if (temperature < -50 || temperature > 100)
            throw new IllegalArgumentException(
                "Température hors plage physique : " + temperature + " °C"
            );
        if (humidite < 0 || humidite > 100)
            throw new IllegalArgumentException(
                "Humidité hors plage valide (0-100%) : " + humidite + " %"
            );

        this.temperature     = temperature;
        this.humidite        = humidite;
        this.derniereLecture = LocalDateTime.now();

        // Alerte immédiate si hors seuils
        if (!conditionsOk()) {
            System.out.println("⚠ ALERTE CAPTEUR [" + emplacement + "] : " + getEtatConditions());
        }
    }

    /**
     * Vérifie si les conditions sont dans les seuils recommandés.
     */
    public boolean conditionsOk() {
        return temperature >= TEMP_MIN && temperature <= TEMP_MAX
            && humidite    >= HUMI_MIN && humidite    <= HUMI_MAX;
    }

    /**
     * Retourne un message décrivant l'état des conditions.
     */
    public String getEtatConditions() {
        StringBuilder sb = new StringBuilder();

        if (temperature < TEMP_MIN)
            sb.append("Température trop basse (").append(temperature).append("°C < ").append(TEMP_MIN).append("°C). ");
        else if (temperature > TEMP_MAX)
            sb.append("Température trop élevée (").append(temperature).append("°C > ").append(TEMP_MAX).append("°C). ");

        if (humidite < HUMI_MIN)
            sb.append("Humidité trop basse (").append(humidite).append("% < ").append(HUMI_MIN).append("%). ");
        else if (humidite > HUMI_MAX)
            sb.append("Humidité trop élevée (").append(humidite).append("% > ").append(HUMI_MAX).append("%). ");

        return sb.length() == 0 ? "Conditions normales." : sb.toString().trim();
    }

    //  Méthodes utilitaires 

    @Override
    public String toString() {
        String lecture = (derniereLecture != null) ? derniereLecture.toString() : "aucune lecture";
        return String.format(
            "[Capteur #%d] %s | Temp: %.1f°C | Hum: %.1f%% | %s | Dernière lecture: %s",
            id, emplacement, temperature, humidite,
            conditionsOk() ? "OK" : "ALERTE",
            lecture
        );
    }
}
