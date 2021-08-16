package model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postagem {
    @CsvBindByName(column = "Postagem")
    private String sentenca;
    @CsvBindByName(column = "UX (Av1)")
    private String facetaUX;
    @CsvBindByName(column = "Usabilidade (Av1)")
    private String facetaUsabilidade;

    private String facetaUxAtribuida;
    private String facetaUsabilidadeAtribuida;
}
