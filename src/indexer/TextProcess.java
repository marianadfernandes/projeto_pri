package indexer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public class TextProcess {
    //---------------------- PROCESSAMENTO DOS TOKENS ----------------------
    public ArrayList<String> readStopWords(String... filepaths) {
        ArrayList<String> stopWords = new ArrayList<>();

        for (String filepath : filepaths) {
            try {
                List<String> lines = Files.readAllLines(Path.of(filepath), StandardCharsets.UTF_8);
                for (String line : lines) {
                    String stopWord = line.trim();
                    if (!stopWord.isEmpty()) {
                        stopWords.add(stopWord);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + filepath);
                e.printStackTrace();
            }
        }
        return stopWords;
    }

    // obtenção dos tokens (texto separado por palavras singulares, começando por se colocar todo o texto em letra minúscula
    // e depois eliminando-se a pontuação e números, acabando com a normalização de todas as sequências de espaços num só)
    public String[] splitData(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[\\.+,:_;!\\?\\(\\)\\/\"“’\\[\\]{}]", " ");
        text = text.replaceAll("\\d+", " ");
        text = text.replaceAll("\\s+", " ");
        return text.split("\\s");
    }


    // função para normalizar os tokens em termos
    // o processamento feito é: remoção de acentos e carateres especiais; remoção de stop words portuguesas e inglesas
    public ArrayList<String> processTokens(String[] splitText) {
        ArrayList<String> terms = new ArrayList<>();
        ArrayList<String> stopWords = readStopWords("stop_words_files/stop_words_english.txt", "stop_words_files/stop_words_portuguese.txt");

        for (String token : splitText) {
            if (stopWords.contains(token)) {
                continue;
            }
            //remover acentos
            String normalizedString = Normalizer.normalize(token, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");

            //remover carateres especiais
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
            normalizedString = pattern.matcher(normalizedString).replaceAll("");

            //se o resultado de eliminar carateres especiais ainda for uma string com conteúdo, adiciona à lista de termos
            if (!Objects.equals(normalizedString, "")) {
                terms.add(normalizedString);
            }
        }

        //terms = lemmatizeTokens(terms);
        return terms;
    }

    // colocar termos nos seus formatos raíz
    public ArrayList<String> lemmatizeTokens(ArrayList<String> tokens) {
        ArrayList<String> lemmas = new ArrayList<>();

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        for (String token : tokens) {
            // Create an empty Annotation just with the given text
            Annotation document = new Annotation(token);

            // Run all the selected pipeline on this text
            pipeline.annotate(document);

            // Get the lemmas from the annotated document
            List<CoreLabel> tokenAnnotations = document.get(CoreAnnotations.TokensAnnotation.class);
            String lemma = tokenAnnotations.get(0).get(CoreAnnotations.LemmaAnnotation.class);
            lemmas.add(lemma);
        }
        return lemmas;
    }
    //------------------------------------------------------------------
}
