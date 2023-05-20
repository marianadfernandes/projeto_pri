package query_module;

import objects.InvertedIndex;
import objects.IdsMap;

import java.util.*;

public class Search {
    QueryProcess queryProcess = new QueryProcess();

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

        System.out.printf("AND result - The intersection is: %s\n", intersectDocs);
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

        System.out.printf("OR result - The union is: %s\n",unitedDocs);
        return unitedDocs;
    }


    // -------------------- NOT de 1 termo (pela posting list)--------------------
    public ArrayList<Integer> searchNegation(ArrayList<Integer> postingList1, IdsMap filesIds){
        ArrayList<Integer> negatedDocs = new ArrayList<>(filesIds.getFilesIds().keySet());
        negatedDocs.removeAll(postingList1);

        System.out.printf("NOT result - The negation is: %s\n", negatedDocs);
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

        System.out.printf("ANDNOT result - The negative intersection is: %s\n", andNotDocs);
        return andNotDocs;
    }



    //--------------------PRIORITY FUNCTIONS--------------------
    public void querySolver(String query, IdsMap idsMap, InvertedIndex invertedIndex) {
        query = queryProcess.processQueryTermsInPostLists(query, invertedIndex);

        String result_posting = null;
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);

            // verifica se a query tem expressões entre parêntesis, guardando essas posições caso existam
            // nomeadamente os parentesis aberto e fechado correspondentes ao mesmo par
            if (c == '(') {
                stack.push(i);
            } else if (c == ')') {
                if (!stack.isEmpty()) {
                    int pos_initial = stack.pop();
                    // com as posições guardadas define-se uma query intermédia a resolver
                    String middle_query = query.substring(pos_initial + 1, i);
                    System.out.println("\nMiddle query to solve: " + middle_query);

                    // a essa query intermédia é aplicada a função dos operadores, com as prioridades lá definidas
                    result_posting = logicOperators(middle_query, idsMap);

                    // na query original, a parte correspondente à query intermédia resolvida é substituída pela posting list que resulta dessa mesma resolução
                    query = query.substring(0, pos_initial) + result_posting + query.substring(i + 1);
                    i = pos_initial + result_posting.length() - 1;

                    System.out.println("\nModified query: " + query);
                }
            }
        }

        result_posting = logicOperators(query, idsMap);

        if (result_posting != null) {
            System.out.println("\nFINAL Result: " + result_posting);
        }
    }


    // função que formata uma query intermédia de modo a estar no formato certo para entrar nas funções de lógica
    public LinkedHashMap<String, ArrayList<Integer>> semiQueryTransformation(String oper, ArrayList<Integer> index_spaces, String query){
        String semi_query = null;

        //para juntar as duas posting-lists para ser possível retorná-las juntas; é linkedhashmap para respeitar a ordem com que são inseridas chaves
        LinkedHashMap<String , ArrayList<Integer>> postingListsMap = new LinkedHashMap<>();


        // se o operador for NOT, só deve ser tido em conta o conteúdo a seguir (1 termo)
        if(oper == " NOT "){
            Integer pos_initial = query.indexOf(oper) + 1;
            Integer pos_final = index_spaces.get(index_spaces.indexOf(query.indexOf(oper) + oper.length() - 1) + 1);
            semi_query = query.substring(pos_initial, pos_final);
        }
        // se for qualquer um dos outros, a operação é entre 2 termos portanto é tido em conta o que está antes e depois do mesmo
        else {
            Integer pos_initial = index_spaces.get(index_spaces.indexOf(query.indexOf(oper)) - 1) + 1;
            Integer pos_final = index_spaces.get(index_spaces.indexOf(query.indexOf(oper) + oper.length() - 1) + 1);
            semi_query = query.substring(pos_initial, pos_final);
            //System.out.println("semi query:"+semi_query);
        }

        // se o operador não for NOT, há a conversão de 2 postingLists em String para Array
        if(oper != " NOT "){
            String posting1 = semi_query.substring(semi_query.indexOf("[") + 1, semi_query.indexOf("]"));

            // no caso da PL ser vazia, coloca um elemento vazio no mapa de PLs, de modo a ser possível o seu uso nas operações lógicas
            if (posting1.equals("")) {
                ArrayList<Integer> emptyPL = new ArrayList<>();
                postingListsMap.put("posting1", emptyPL);
            }
            // caso tenha valores, é feita uma conversão da lista(String) em array list, que é o que entra nas funções lógicas e é inserida essa chave no mapa
            else {
                String[] posting1_middle = posting1.split(",");
                ArrayList<Integer> posting1_list = new ArrayList<>();
                for (String number : posting1_middle) {
                    int num = Integer.parseInt(number);
                    posting1_list.add(num);
                }
                postingListsMap.put("posting1", posting1_list);
            }
        }

        // se for NOT é convertida apenas uma PL; se não for terá então 2 PLs no mapa
        String posting2 = semi_query.split(oper.substring(1))[1].substring(semi_query.split(oper.substring(1))[1].indexOf("[") + 1, semi_query.split(oper.substring(1))[1].indexOf("]"));

        // no caso da PL ser vazia, coloca um elemento vazio no mapa de PLs, de modo a ser possível o seu uso nas operações lógicas
        if (posting2.equals("")) {
            ArrayList<Integer> emptyPL = new ArrayList<>();
            postingListsMap.put("posting2", emptyPL);
        }
        // caso tenha valores, é feita uma conversão da lista(String) em array list, que é o que entra nas funções lógicas e é inserida essa chave no mapa
        else {
            String[] posting2_middle = posting2.split(",");
            ArrayList<Integer> posting2_list = new ArrayList<>();
            for (String number : posting2_middle) {
                int num = Integer.parseInt(number);
                posting2_list.add(num);
            }
            postingListsMap.put("posting2", posting2_list);
        }

        // um dos elementos no mapa é a semi_query, pois é necessária para a função de resolução da query, para substituir na query original a semi_query pela respetiva PL resultante
        ArrayList<Integer> flag = new ArrayList<>();
        postingListsMap.put(semi_query, flag);

        return postingListsMap;
    }


    // função para distinguir operadores, definir prioridades e aplicar a respetiva função lógica
    // ordem de prioridade: parentesis, ANDNOT, NOT, AND, OR
    public String logicOperators (String query, IdsMap idsMap) {
        // Variáveis
        LinkedHashMap<String, ArrayList<Integer>> postingListsMap = new LinkedHashMap<>();
        ArrayList<Integer> result_posting = new ArrayList<>();
        String result_string = new String();
        String logicOpers[] = new String[]{" ANDNOT ", " NOT ", " AND ", " OR "};
        ArrayList<Integer> index_spaces = new ArrayList<>();
        String oper = "";

        //Loop para resolver todos os operadores
        while(oper != null) {

            //------------- parte de código que verifica se a query tem um espaço no início e no fim, para ficar de acordo com o restante código
            if (query.charAt(query.length() - 1) != ' ') {
                query = query + ' ';
            }
            if (query.charAt(0) != ' ') {
                query = ' ' + query;
            }

            // eliminação dos espaços entre os doc ids das posting lists, para não serem contabilizados no cálculo dos espaços, necessário ao resto do código
            query = query.replaceAll(", ", ",");
            //6System.out.println("\nRemaining query to solve:" + query);

            index_spaces.removeAll(index_spaces);
            int index = query.indexOf(" ");
            while (index != -1) {
                index_spaces.add(index);
                index = query.indexOf(" ", index + 1);
            }
            //-----------------------------------------------------------------------------------------------------------------------------------------------


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


    //-------------------- Query SOLVER quase original mas que tem erros como:
    // - não continua a resolver a query quando deixa de ter parentesis
    // - quando tem dois pares de parentesis tipo (...(...)), eliminava parentesis do fim o que não permitia a resolução completa
    public void querySolver1(String query, IdsMap idsMap, InvertedIndex invertedIndex) {
        query = queryProcess.processQueryTermsInPostLists(query, invertedIndex);

        String result_posting = null;
        Integer pos_initial = null;
        Integer pos_final = null;
        char[] query_char = query.toCharArray();


        // verifica se a query tem expressões entre parêntesis, guardando essas posições caso existam
        if (query.contains("(")) {
            while (true) {
                if (!query.contains("(")) {
                    break;
                }
                for (int i = 0; i < query.length(); i++) {
                    if (query.charAt(i) == '(') {
                        pos_initial = i;
                    } else if (query.charAt(i) == ')') {
                        pos_final = i;
                        break;
                    }
                }
                // com as posições que guardou define uma query intermédia a resolver
                String middle_query = query.substring(pos_initial + 1, pos_final);
                System.out.println("Middle query to solve: " + middle_query);

                // a essa query intermédia é aplicada a função dos operadores, com as prioridades lá definidas
                result_posting = logicOperators(middle_query, idsMap);

                /*if (result_posting == null) {
                    System.out.println("\nResult is null!");
                    break;
                }*/

                // na query original, a parte correspondente à query intermédia resolvida é substituída pela posting list que resulta dessa mesma resolução
                System.out.println("query original:"+query);
                query = query.substring(0, pos_initial) + result_posting + query.substring(pos_final + 1);
                System.out.println("query alterada:"+query);
            }
        }
        // quando a query não tem parêntesis, é aplicada diretamente a função dos operadores
        else {
            result_posting = logicOperators(query, idsMap);
        }

        if (result_posting != null) {
            System.out.println("\nFINAL Result: " + result_posting);
        }
    }
}
