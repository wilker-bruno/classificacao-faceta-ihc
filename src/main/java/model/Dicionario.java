package model;

import lombok.Getter;
import lombok.ToString;
import model.enums.FacetaUX;
import model.enums.FacetaUsabilidade;

import java.util.HashMap;

@Getter
@ToString
public class Dicionario {
    private final HashMap<FacetaUX, HashMap<String, Integer>> ux = new HashMap<>();
    private final HashMap<FacetaUsabilidade, HashMap<String, Integer>> usabilidade = new HashMap<>();

    public Dicionario() {
        initUX();
        initUsabilidade();
    }

    private void initUX() {
        for (FacetaUX faceta : FacetaUX.values()) {
            ux.put(faceta, new HashMap<>());
        }
    }

    private void initUsabilidade() {
        for (FacetaUsabilidade faceta : FacetaUsabilidade.values()) {
            usabilidade.put(faceta, new HashMap<>());
        }
    }
}
