package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import model.enums.FacetaUX;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PredominanteUX {
    private FacetaUX faceta;
    private Integer peso = 0;
}
