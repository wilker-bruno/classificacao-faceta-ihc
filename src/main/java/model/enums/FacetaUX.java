package model.enums;

public enum FacetaUX {
    SATISFACAO("Satisfação"),
    AFETO("Afeto"),
    CONFIANCA("Confiança"),
    ESTETICA("Estética"),
    FRUSTRACAO("Frustração"),
    MOTIVACAO("Motivação"),
    SUPORTE("Suporte");

    private final String name;

    FacetaUX(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
