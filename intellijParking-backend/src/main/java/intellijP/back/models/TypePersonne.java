package intellijP.back.models;

public enum TypePersonne {

    SUPERVISEUR("Superviseur"),
    ABONNE("Abonné"),
    VISITEUR("Visiteur");

    private final String label;

    TypePersonne(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
