package query_module;

import objects.*;

import java.util.*;

public class Search {

    //Processamento da Query: transformação direta de termos nas respetivas postings lists
    //para depois serem trabalhadas as postings lists nos operadores, independentemente de qual seja o primeiro


/*    public void processQuery(String query, objects.InvertedIndex invertedIndex, objects.IdsMap filesIds) {
        if (query.matches("NOT .*")) {
            String term = query.replaceAll("NOT ", "");
            searchNegation(term, invertedIndex, filesIds);
        } else if (query.matches(".* AND (?!NOT).*")) {
            String [] terms = query.split(" AND ");
            //searchIntersections(new ArrayList<>(Arrays.asList(terms)), invertedIndex);
            searchIntersection(terms[0], terms[1], invertedIndex);
        } else if (query.matches(".* OR .*")) {
            String [] terms = query.split(" OR ");
            if (terms.length == 2) {
                searchUnion(terms[0], terms[1], invertedIndex);
            }
        } else if (query.matches(".* AND\\s?NOT .*")) {
            String [] terms = query.split(" AND\s?NOT ");
            searchANDNOT(terms[0], terms[1], invertedIndex);
        }

    }*/

    public void searchTerm(String key, InvertedIndex invertedIndex){
        if (invertedIndex.getInvertedIndex().containsKey(key)){
            System.out.println(key + ": " + invertedIndex.getInvertedIndex().get(key));
        }
        else{
            System.out.println("Element not found.");
        }
    }

    //-------------------- AND entre 2 termos (pelas posting lists) --------------------
    public ArrayList<Integer> searchIntersection(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2){
        ArrayList<Integer> intersectDocs = new ArrayList<>();
        int i = 0;
        int j = 0;

        while ( postingList1.size() != i && postingList2.size() != j){
            if ( postingList1.get(i) == postingList2.get(j)){
                intersectDocs.add(postingList1.get(i));
                i+=1;
                j+=1;
            }
            else{
                if(postingList1.get(i) < postingList2.get(j)){
                    i+=1;
                }
                else{
                    j+=1;
                }
            }
        }

        System.out.printf("\nThe intersection is: %s", intersectDocs);
        return intersectDocs;
    }


    //-------------------- OR entre 2 termos (pelas posting lists)--------------------
    public ArrayList<Integer> searchUnion(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2){
        ArrayList<Integer> unitedDocs = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (postingList1.size() != i && postingList2.size() != j){
            if ( postingList1.get(i) == postingList2.get(j)){
                unitedDocs.add((Integer) postingList1.get(i));
                i += 1;
                j += 1;
            }
            else{
                if(postingList1.get(i) < postingList2.get(j)){
                    unitedDocs.add((Integer) postingList1.get(i));
                    i+=1;
                }
                else{
                    unitedDocs.add((Integer) postingList2.get(j));
                    j+=1;
                }
            }
        }

        while (i < postingList1.size()) {
            unitedDocs.add(postingList1.get(i));
            i++;
        }

        while (j < postingList2.size()) {
            unitedDocs.add(postingList2.get(j));
            j++;
        }

        System.out.printf("\nThe union is: %s",unitedDocs);
        return unitedDocs;
    }


    // -------------------- NOT de 1 termo (pela posting list)--------------------
    public ArrayList<Integer> searchNegation(ArrayList<Integer> postingList1, IdsMap filesIds){
        ArrayList<Integer> negatedDocs = new ArrayList<>(filesIds.getFilesIds().keySet());
        negatedDocs.removeAll(postingList1);

        System.out.printf("\nThe negation is: %s", negatedDocs);
        return negatedDocs;
    }


    //-------------------- AND NOT entre 2 termos (pelas posting lists)--------------------
    public ArrayList<Integer> searchANDNOT(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2){
        ArrayList<Integer> andNotDocs = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (postingList1.size() != i && postingList2.size() != j){
            if ( postingList1.get(i) == postingList2.get(j)){
                i += 1;
                j += 1;
            }
            else{
                //if( (int)values.get(i) < (int)values1.get(j)){
                if( postingList1.get(i) < postingList2.get(j)){
                    andNotDocs.add(postingList1.get(i));
                    i+=1;
                }
                else{
                    j+=1;
                }
            }
        }

        while (i < postingList1.size()) {
            andNotDocs.add(postingList1.get(i));
            i++;
        }

        System.out.printf("\nThe negative intersection is: %s", andNotDocs);
        return andNotDocs;
    }



    //--------------------PRIORITY FUNCTIONS--------------------

    //Função que resolve a query completa
    public String querySolver (String query, IdsMap idsMap) {
        String result_posting = new String();
        Integer pos_initial = null;
        Integer pos_final = null;
        char[] query_char = query.toCharArray();

        if(query.contains("(")) {
            while (true) {
                //System.out.println("\nquery: " + query);
                if (!query.contains("(")) {
                    break;
                }
                for (int i = 0; i < query.length(); i++) {
                    if (query.charAt(i) == "(".charAt(0)) {
                        pos_initial = i;
                    } else if (query.charAt(i) == ")".charAt(0)) {
                        pos_final = i;
                        break;
                    }
                }
                String middle_query = query.substring(pos_initial + 1, pos_final);
                System.out.println("\ninter result:" + middle_query);

                result_posting = logicOperators(middle_query, idsMap);
                // ir buscar resultado da operação acima e substituir na query *middle_query*
                if (result_posting == null) {
                    System.out.println("\nResult is null!");
                    break;
                }
                // nao esquecer que resultado pode ser nulo
                query = query.substring(0, pos_initial) + result_posting + query.substring(pos_final + 1);
            }
        }
        else{
            result_posting = logicOperators(query, idsMap);
        }
        if(result_posting != null){
            System.out.println("\nresult:"+ result_posting);
        }
        return result_posting;
    }

    //Logic Operators function
    public String logicOperators (String query, IdsMap idsMap) {
        //Variaveis
        LinkedHashMap<String, ArrayList<Integer>> postingListsMap = new LinkedHashMap<>();
        ArrayList<Integer> result_posting = new ArrayList<>();
        String result_string = new String();
        String logicOpers[] = new String[]{" ANDNOT ", " NOT ", " AND ", " OR "};
        ArrayList<Integer> index_spaces = new ArrayList<>();
        String oper = "";

        //Loop para resolver todos os operadores
        while(oper != null) {

            //!!!!!!!!!!!!!!!!!! Função dos espaços - não está a funcionar direito! Coloca espaços de todas as vezes, mesmo quando já tem
            System.out.println("\nquery11:" + query);
            if(query.substring(query.length()-1,query.length()) != " "){
                query = query+" ";
            }
            else if(query.substring(0,1) != " "){
                query = " "+query;
            }
            System.out.println("\nquery1:" + query);
            index_spaces.removeAll(index_spaces);
            int index = query.indexOf(" ");
            while (index != -1) {
                index_spaces.add(index);
                index = query.indexOf(" ", index + 1);
            }
            // System.out.println(index_spaces);
            // System.out.println("\noper:"+oper);

            //OPER = ANDNOT
            if (query.contains(logicOpers[0])) {
                oper = logicOpers[0];
                postingListsMap = semiQueryTransformation(oper, index_spaces, query);

                result_posting = searchANDNOT(postingListsMap.get("posting1"), postingListsMap.get("posting2"));
                result_string = result_posting.toString().replaceAll("\\s+", "");
                query = query.replace(postingListsMap.keySet().toArray()[2].toString(), result_string);
            }

            //OPER = NOT
            else if (!query.contains(logicOpers[0]) && query.contains(logicOpers[1])) {
                oper = logicOpers[1];
                postingListsMap = semiQueryTransformation(oper, index_spaces, query);

                result_posting = searchNegation(postingListsMap.get("posting2"), idsMap);
                result_string = result_posting.toString().replaceAll("\\s+", "");
                query = query.replace(postingListsMap.keySet().toArray()[1].toString(), result_string);
            }

            //OPER = AND
            else if (!query.contains(logicOpers[0]) && !query.contains(logicOpers[1]) && query.contains(logicOpers[2])) {
                oper = logicOpers[2];
                postingListsMap = semiQueryTransformation(oper, index_spaces, query);

                result_posting = searchIntersection(postingListsMap.get("posting1"), postingListsMap.get("posting2"));
                result_string = result_posting.toString().replaceAll("\\s+", "");
                query = query.replace(postingListsMap.keySet().toArray()[2].toString(), result_string);
            }

            //OPER = OR
            else if (!query.contains(logicOpers[0]) && !query.contains(logicOpers[1]) && !query.contains(logicOpers[2]) && query.contains(logicOpers[3])) {
                oper = logicOpers[3];
                postingListsMap = semiQueryTransformation(oper, index_spaces, query);

                result_posting = searchUnion(postingListsMap.get("posting1"), postingListsMap.get("posting2"));
                result_string = result_posting.toString().replaceAll("\\s+", "");
                query = query.replace(postingListsMap.keySet().toArray()[2].toString(), result_string);
            }

            //Caso a query já não contenha operadores, serve para quebrar o while, pois significa que já se chegou ao resultado final
            else if(!query.contains(logicOpers[0]) && !query.contains(logicOpers[1]) && !query.contains(logicOpers[2]) && !query.contains(logicOpers[3])){
                oper = null;
            }

            else {
                System.out.println("\nNo logic operator found!");
                return null;
            }
        }

        return result_string;
    }


    //função que restringe o operador da query e transforma as posting lists de strings para listas
    //de modo a estarem no tipo certo para entrar nas funções de lógica
    public LinkedHashMap<String, ArrayList<Integer>> semiQueryTransformation(String oper, ArrayList<Integer> index_spaces, String query){
        String semi_query = "";
        LinkedHashMap<String , ArrayList<Integer>> postingListsMap = new LinkedHashMap<>(); //para juntar as duas posting-lists para ser possível retorná-las juntas
        // é linkedhashmap para respeitar a ordem com que são inseridas chaves

        if(oper == " NOT "){
            Integer pos_initial = query.indexOf(oper) + 1;
            Integer pos_final = index_spaces.get(index_spaces.indexOf(query.indexOf(oper) + oper.length() - 1) + 1);
            semi_query = query.substring(pos_initial, pos_final);
        }
        else {
            Integer pos_initial = index_spaces.get(index_spaces.indexOf(query.indexOf(oper)) - 1) + 1;
            Integer pos_final = index_spaces.get(index_spaces.indexOf(query.indexOf(oper) + oper.length() - 1) + 1);
            semi_query = query.substring(pos_initial, pos_final);
        }

        if(oper != " NOT "){
            String posting1 = semi_query.substring(semi_query.indexOf("[") + 1, semi_query.indexOf("]"));
            //System.out.println(posting1);
            String[] posting1_middle = posting1.split(",");
            ArrayList<Integer> posting1_list = new ArrayList<>();
            for (String number : posting1_middle) {
                int num = Integer.parseInt(number);
                posting1_list.add(num);
            }
            postingListsMap.put("posting1", posting1_list);
        }
        String posting2 = semi_query.split(oper.substring(1))[1].substring(semi_query.split(oper.substring(1))[1].indexOf("[") + 1, semi_query.split(oper.substring(1))[1].indexOf("]"));
        String[] posting2_middle = posting2.split(",");
        ArrayList<Integer> posting2_list = new ArrayList<>();
        ArrayList<Integer> flag = new ArrayList<>();
        for (String number : posting2_middle) {
            int num = Integer.parseInt(number);
            posting2_list.add(num);
        }
        postingListsMap.put("posting2", posting2_list);

        postingListsMap.put(semi_query, flag);

        return postingListsMap;
    }







    //-------------------- AND entre x termos: NÃO USADA --------------------
    public boolean searchIntersections(ArrayList<String> keys, InvertedIndex invertedIndex) {
        ArrayList<Integer> intersectDocs = new ArrayList<>();
        ArrayList<ArrayList<Integer>> values = new ArrayList<>();

        for (int i = 0; i < keys.size(); i++) {
            if (invertedIndex.getInvertedIndex().containsKey(keys.get(i))) {
                values.add(invertedIndex.getInvertedIndex().get(keys.get(i)));
            }
            else{
                System.out.printf("\nThe word \"%s\" dont exist in the docs.%n", keys.get(i));
                //intersectDocs.add(i);
                return false;
            }
        }

        for(int n=0; n<values.size(); n++) {
            if(n>=1){
                values.remove(0);
                values.get(0).clear();
                values.get(0).addAll(intersectDocs);
            }

            intersectDocs.clear();

            int i = 0;
            int j = 0;
            while (values.get(0).size() != i && values.get(1).size() != j) {
                if (Objects.equals(values.get(0).get(i), values.get(1).get(j))) {
                    intersectDocs.add((Integer) values.get(0).get(i));
                    i += 1;
                    j += 1;
                } else {
                    if ((int) values.get(0).get(i) < (int) values.get(1).get(j)) {
                        i += 1;
                    } else {
                        j += 1;
                    }
                }
            }
        }
        System.out.printf("\nThe instersection betwwen %s is: %s",keys,intersectDocs);
        return true;
    }
}
