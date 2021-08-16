package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import model.enums.FacetaUX;
import model.enums.FacetaUsabilidade;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PredominanteUsabilidade {
    private FacetaUsabilidade faceta;
    private Integer peso = 0;
}
