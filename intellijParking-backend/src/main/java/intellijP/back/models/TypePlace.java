package intellijP.back.models;

public enum TypePlace {

    STANDARD("Standard"),
    NORMAL("Normal"),
    PMR("pmr"),
    ELECTRIQUE("Électrique"),
    MOTO("Moto"),
    FAMILIALE("Familiale"),
    NORMAL("Normal");
    private final String label;

    TypePlace(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

