package model.enums;

public enum FacetaUsabilidade {
    EFICACIA("Eficácia"),
    EFICIENCIA("Eficiência"),
    SATISFACAO("Satisfação"),
    SEGURANCA("Segurança"),
    UTILIDADE("Utilidade"),
    MEMORABILIDADE("Memorabilidade"),
    APRENDIZADO("Aprendizado");

    private final String name;

    FacetaUsabilidade(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
