package fr.octocorn.elasticspringboot.infrastructure.datagen;

/**
 * Référentiel des secteurs d'activité utilisé pour la génération de données.
 */
public enum Sectors {

    IT          ("Informatique"),
    HEALTH      ("Santé"),
    FINANCE     ("Finance & Comptabilité"),
    COMMERCE    ("Commerce & Vente"),
    MARKETING   ("Marketing & Communication"),
    HR          ("Ressources Humaines"),
    LEGAL       ("Juridique"),
    BTP         ("BTP & Architecture"),
    LOGISTICS   ("Logistique & Transport"),
    EDUCATION   ("Éducation & Formation"),
    INDUSTRY    ("Industrie & Production"),
    HOSPITALITY ("Restauration & Hôtellerie");

    private final String label;

    Sectors(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

