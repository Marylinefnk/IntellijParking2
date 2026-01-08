package esiag.back.models;

public enum TypePlace {

    STANDARD("Standard"),
    PMR("pmr"),
    ELECTRIQUE("Électrique"),
    MOTO("Moto"),
    FAMILIALE("Familiale");

    private final String label;

    TypePlace(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

