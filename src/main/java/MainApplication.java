import service.Algoritmo;

public class MainApplication {
    public static void main(String[] args) throws Exception {
        Algoritmo algoritmo = new Algoritmo();
        String VALIDACAO_1 = "bases/combinacao-1/BaseValidacao.csv";
        String TREINAMENTO_1 = "bases/combinacao-1/BaseTreinamento.csv";
        String VALIDACAO_2 = "bases/combinacao-2/BaseValidacao.csv";
        String TREINAMENTO_2 = "bases/combinacao-2/BaseTreinamento.csv";
        String VALIDACAO_3 = "bases/combinacao-3/BaseValidacao.csv";
        String TREINAMENTO_3 = "bases/combinacao-3/BaseTreinamento.csv";

        algoritmo.analisarEmojisNasPostagens(TREINAMENTO_3);
        algoritmo.mostrarEmojisMaisFrequentes();

        algoritmo.construirDicionario(TREINAMENTO_3);
        algoritmo.mostrarEmojisPorFacetaUX();
        algoritmo.mostrarEmojisPorFacetaUsabilidade();

//        algoritmo.classificarPostagens(VALIDACAO_2);

//        System.out.println("Dicionario UX: " + algoritmo.getDicionario().getUx());
//        System.out.println("Dicionario Usabilidade: " + algoritmo.getDicionario().getUsabilidade());
    }
}
