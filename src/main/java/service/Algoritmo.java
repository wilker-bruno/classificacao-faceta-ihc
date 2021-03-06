package service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;
import model.Dicionario;
import model.Postagem;
import model.PredominanteUX;
import model.PredominanteUsabilidade;
import model.enums.FacetaUX;
import model.enums.FacetaUsabilidade;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Getter
public class Algoritmo {
    private Integer contadorEmojis = 0;
    private final Dicionario dicionario = new Dicionario();
    private final Map<String, Integer> emojis = new TreeMap<>();

    public void analisarEmojisNasPostagens(String path) throws FileNotFoundException {
        CsvToBean<Postagem> postagens = new CsvToBeanBuilder<Postagem>(new FileReader(path))
                .withType(Postagem.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        for (Postagem postagem : postagens) {
            List<String> emojisExtraidos = EmojiParser.extractEmojis(postagem.getSentenca());
            contadorEmojis += emojisExtraidos.size();

            for (String emoji : emojisExtraidos) {
                emojis.merge(emoji, 1, Integer::sum);
            }
        }
    }

    public void mostrarEmojisMaisFrequentes() {
        Stream<Map.Entry<String, Integer>> sorted =
                emojis.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));

        System.out.println("\n\n ------------------ EMOJIS DA BASE ------------------");
        System.out.println("TOTAL DE EMOJIS = " + contadorEmojis);
        System.out.println("TOTAL DE EMOJIS ÙNICOS = " + emojis.keySet().size());

        AtomicInteger count = new AtomicInteger();
        sorted.forEach(element -> {
            count.addAndGet(element.getValue());
            System.out.print(element + " ");
        });

        System.out.println("-----------------> " + count);
    }

    public void mostrarEmojisPorFacetaUX() {
        System.out.print("\n\n ------------------ FACETAS DE UX ------------------");
        for (Map.Entry<FacetaUX, HashMap<String, Integer>> mapUX : dicionario.getUx().entrySet()) {
            Stream<Map.Entry<String, Integer>> sorted = mapUX.getValue().entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));

            System.out.println("\n" + mapUX.getKey().name());
            sorted.forEach(stringIntegerEntry -> System.out.print(stringIntegerEntry + " "));
        }

    }

    public void mostrarEmojisPorFacetaUsabilidade() {
        System.out.print("\n\n ------------------ FACETAS DE USABILIDADE ------------------");
        for (Map.Entry<FacetaUsabilidade, HashMap<String, Integer>> mapUsabilidade : dicionario.getUsabilidade().entrySet()) {
            Stream<Map.Entry<String, Integer>> sorted = mapUsabilidade.getValue().entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));

            System.out.println("\n" + mapUsabilidade.getKey().name());
            sorted.forEach(stringIntegerEntry -> System.out.print(stringIntegerEntry + " "));
        }

    }

    public void construirDicionario(String path) throws FileNotFoundException {
        CsvToBean<Postagem> postagens = new CsvToBeanBuilder<Postagem>(new FileReader(path))
                .withType(Postagem.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        for (Postagem postagem : postagens) {
            for (String emoji : EmojiParser.extractEmojis(postagem.getSentenca())) {
                atualizarDicionarioUX(postagem.getFacetaUX(), emoji);
                atualizarDicionarioUsabilidade(postagem.getFacetaUsabilidade(), emoji);
            }
        }
    }

    public void classificarPostagens(String path) throws IOException {
        int acertosUX = 0;
        int acertosUsabilidade = 0;
        int totalPostagens = 0;

        List<Postagem> postagemList = new ArrayList<>();
        CsvToBean<Postagem> postagens = new CsvToBeanBuilder<Postagem>(new FileReader(path))
                .withType(Postagem.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        for (Postagem postagem : postagens) {
            totalPostagens += 1;
            List<String> emojis = EmojiParser.extractEmojis(postagem.getSentenca());
            HashMap<FacetaUX, Integer> pesoUX = new HashMap<>();
            HashMap<FacetaUsabilidade, Integer> pesoUsabilidade = new HashMap<>();

            for (String emoji : emojis) {
                PredominanteUX predominanteUX = facetaUxPredominante(emoji);
                PredominanteUsabilidade predominanteUsabilidade = facetaUsabilidadePredominante(emoji);
                pesoUX.merge(predominanteUX.getFaceta(), 1, Integer::sum);
                pesoUsabilidade.merge(predominanteUsabilidade.getFaceta(), 1, Integer::sum);
            }

            Integer qntd = 0;
            FacetaUX ux = null;
            for (Map.Entry<FacetaUX, Integer> entry : pesoUX.entrySet()) {
                if (entry.getValue() > qntd) {
                    ux = entry.getKey();
                    qntd = entry.getValue();
                    postagem.setFacetaUxAtribuida(entry.getKey() != null ? entry.getKey().toString() : "null");
                }
            }

            if (ux != null && ux.equalsName(postagem.getFacetaUX())) {
                acertosUX += 1;
            }

            qntd = 0;
            FacetaUsabilidade usabilidade = null;
            for (Map.Entry<FacetaUsabilidade, Integer> entry : pesoUsabilidade.entrySet()) {
                if (entry.getValue() > qntd) {
                    usabilidade = entry.getKey();
                    qntd = entry.getValue();
                    postagem.setFacetaUsabilidadeAtribuida(entry.getKey() != null ? entry.getKey().toString() : "null");
                }
            }

            if (usabilidade != null && usabilidade.equalsName(postagem.getFacetaUsabilidade())) {
                acertosUsabilidade += 1;
            }
            postagemList.add(postagem);
        }

        System.out.println("ACERTO UX " + ((double) acertosUX / (double) totalPostagens));
        System.out.println("ACERTO Usabilidade " + ((double) acertosUsabilidade / (double) totalPostagens));
        gerarArquivoCSV(postagemList);
    }

    private PredominanteUsabilidade facetaUsabilidadePredominante(String emoji) {
        PredominanteUsabilidade facetaPredominante = new PredominanteUsabilidade();

        for (FacetaUsabilidade value : FacetaUsabilidade.values()) {
            Integer qntd = dicionario.getUsabilidade().get(value).get(emoji);

            if (qntd != null && qntd > facetaPredominante.getPeso()) {
                facetaPredominante.setFaceta(value);
                facetaPredominante.setPeso(qntd);
            }
        }

        return facetaPredominante;
    }

    private PredominanteUX facetaUxPredominante(String emoji) {
        PredominanteUX facetaPredominante = new PredominanteUX();

        for (FacetaUX value : FacetaUX.values()) {
            Integer qntd = dicionario.getUx().get(value).get(emoji);

            if (qntd != null && qntd > facetaPredominante.getPeso()) {
                facetaPredominante.setFaceta(value);
                facetaPredominante.setPeso(qntd);
            }
        }

        return facetaPredominante;
    }

    private void atualizarDicionarioUX(String facetaUX, String emoji) {
        switch (facetaUX) {
            case "Satisfação":
                dicionario.getUx().get(FacetaUX.SATISFACAO).merge(emoji, 1, Integer::sum);
                break;
            case "Afeto":
                dicionario.getUx().get(FacetaUX.AFETO).merge(emoji, 1, Integer::sum);
                break;
            case "Confiança":
                dicionario.getUx().get(FacetaUX.CONFIANCA).merge(emoji, 1, Integer::sum);
                break;
            case "Estética":
                dicionario.getUx().get(FacetaUX.ESTETICA).merge(emoji, 1, Integer::sum);
                break;
            case "Motivação":
                dicionario.getUx().get(FacetaUX.MOTIVACAO).merge(emoji, 1, Integer::sum);
                break;
            case "Suporte":
                dicionario.getUx().get(FacetaUX.SUPORTE).merge(emoji, 1, Integer::sum);
            case "Frustração":
                dicionario.getUx().get(FacetaUX.FRUSTRACAO).merge(emoji, 1, Integer::sum);
                break;
        }
    }

    private void atualizarDicionarioUsabilidade(String facetaUsabilidade, String emoji) {
        switch (facetaUsabilidade) {
            case "Eficácia":
                dicionario.getUsabilidade().get(FacetaUsabilidade.EFICACIA).merge(emoji, 1, Integer::sum);
                break;
            case "Eficiência":
                dicionario.getUsabilidade().get(FacetaUsabilidade.EFICIENCIA).merge(emoji, 1, Integer::sum);
                break;
            case "Satisfação":
                dicionario.getUsabilidade().get(FacetaUsabilidade.SATISFACAO).merge(emoji, 1, Integer::sum);
                break;
            case "Segurança":
                dicionario.getUsabilidade().get(FacetaUsabilidade.SEGURANCA).merge(emoji, 1, Integer::sum);
                break;
            case "Utilidade":
                dicionario.getUsabilidade().get(FacetaUsabilidade.UTILIDADE).merge(emoji, 1, Integer::sum);
                break;
            case "Memorabilidade":
                dicionario.getUsabilidade().get(FacetaUsabilidade.MEMORABILIDADE).merge(emoji, 1, Integer::sum);
                break;
            case "Aprendizado":
                dicionario.getUsabilidade().get(FacetaUsabilidade.APRENDIZADO).merge(emoji, 1, Integer::sum);
                break;
        }
    }

    private void gerarArquivoCSV(List<Postagem> postagens) throws IOException {
        BufferedWriter saidaCSV = new BufferedWriter(new FileWriter("saida/base-classificadas.csv"));
        saidaCSV.write("Postagem;UX (Av1);UX (Av2);Usabilidade (Av1);Usabilidade (Av2)");
        saidaCSV.newLine();

        for (Postagem postagem : postagens) {
            saidaCSV.write(postagem.getSentenca() + ";" +
                    postagem.getFacetaUX() + ";" + postagem.getFacetaUxAtribuida() + ";" +
                    postagem.getFacetaUsabilidade() + ";" + postagem.getFacetaUsabilidadeAtribuida());
            saidaCSV.newLine();
        }

        saidaCSV.close();
    }
}
