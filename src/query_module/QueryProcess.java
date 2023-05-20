package query_module;

import objects.InvertedIndex;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class QueryProcess {

    public ArrayList<Integer> searchTerm(String key, InvertedIndex invertedIndex){
        if (invertedIndex.getInvertedIndex().containsKey(key)){
            System.out.println(key + ": " + invertedIndex.getInvertedIndex().get(key));
            return invertedIndex.getInvertedIndex().get(key);
        }
        else{
            System.out.println("Element not found.");
        }
        return null;
    }


    //Normalização de termo para ficar com o mesmo aspeto que no índice
    //remover acentos, remover carateres especiais, ou seja, todos os carateres que não são letras de A-Z nem números
    public java.lang.String normalizeTerm(String input) {
        //remover acentos
        String normalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        //remover carateres especiais
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        normalizedString = pattern.matcher(normalizedString).replaceAll("");

        //colocar em minusculas
        normalizedString = normalizedString.toLowerCase();

        return normalizedString;
    }


    //Processamento da Query: transformação direta de termos nas respetivas postings lists
    //para depois serem trabalhadas as postings lists nos operadores, independentemente de qual seja o primeiro
    public java.lang.String processQueryTermsInPostLists(String query, InvertedIndex invertedIndex) {
        String new_query = query.replaceAll("[\\(\\)]", "");
        String[] terms = new_query.split("\s");
        System.out.println("Query terms: " + Arrays.toString(terms));

        for (String term : terms) {
            if (!Objects.equals(term, "OR") && !Objects.equals(term, "AND") && !Objects.equals(term, "NOT") && !Objects.equals(term, "ANDNOT")) {
                System.out.println("\nTerm: " + term);
                //normalização do termo para ficar coincidente com o aspeto dos termos no índice invertido
                String normTerm = normalizeTerm(term);

                //aplicar função searchTerm a cada termo que não é operador e guardar a respetiva posting list numa variável
                ArrayList<Integer> postingList = searchTerm(normTerm, invertedIndex);

                //se o termo for encontrado no índice
                if (postingList != null) {
                    //fazer replace na query do termo term pela posting list resultante da aplicação da função anterior
                    query = query.replace(term, postingList.toString());
                }
                else {
                    query = query.replace(term, "[]");
                }
            }
        }
        System.out.println("\nQuery with posting lists: " + query);
        return query;
    }


    public ArrayList<ArrayList<Integer>> processStringPLToArray(String postingLists) {
        // Split the string while preserving the brackets
        String[] parts = postingLists.split("\\s+(?=[\\[])");

        // Parse each part into ArrayLists of integers
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (String part : parts) {
            String[] elements = part.replaceAll("[\\[\\]]", "").split(",\\s*");
            ArrayList<Integer> array = new ArrayList<>();
            for (String element : elements) {
                array.add(Integer.parseInt(element));
            }
            result.add(array);
        }
        return result;
    }
}
