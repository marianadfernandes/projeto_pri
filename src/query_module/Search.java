package query_module;

import indexer.IdsMap;
import indexer.InvertedIndex;

import java.util.*;

public class Search {

    //Processamento da Query: transformação direta de termos nas respetivas postings lists
    //para depois serem trabalhadas as postings lists nos operadores, independentemente de qual seja o primeiro


/*    public void processQuery(String query, InvertedIndex invertedIndex, IdsMap filesIds) {
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

    //-------------------- AND entre 2 termos --------------------
    public ArrayList<Integer> searchIntersection(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2){
        ArrayList<Integer> intersectDocs = new ArrayList<>();
        //if(invertedIndex.getInvertedIndex().containsKey(key)){
            //ArrayList values = invertedIndex.getInvertedIndex().get(key);
            //ArrayList values1 = invertedIndex.getInvertedIndex().get(key1);
//            System.out.println(values);
//            System.out.println(values1);

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

        System.out.printf("The intersection between \"%s\" and \"%s\" is: %s", intersectDocs);
        return intersectDocs;
    }

    //-------------------- AND entre x termos --------------------
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

//        for (int i=0; i<intersectDocs.size();i++) {
//            keys.remove((int)intersectDocs.get(i));
//        }

        for(int n=0; n<values.size(); n++) {
            if(n>=1){
                values.remove(0);
                values.get(0).clear();
                values.get(0).addAll(intersectDocs);
            }
//            System.out.println("\n"+n);
//            System.out.println("intersectDocs: "+intersectDocs);
//            System.out.println("values: "+values);
            intersectDocs.clear();
//            System.out.println("intersectDocs: "+intersectDocs);
//            System.out.println("values1: "+values);
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

    //-------------------- OR entre 2 termos --------------------
    public ArrayList<Integer> searchUnion(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2){
        ArrayList<Integer> unitedDocs = new ArrayList<>();
        //if(invertedIndex.getInvertedIndex().containsKey(key)){
            //ArrayList values = invertedIndex.getInvertedIndex().get(key);
            //ArrayList values1 = invertedIndex.getInvertedIndex().get(key1);
//            System.out.println(values);
//            System.out.println(values1);

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

        System.out.printf("The union between \"%s\" and \"%s\" is: %s",unitedDocs);
        return unitedDocs;
    }

    // -------------------- NOT de 1 termo --------------------
    public Set<Integer> searchNegation(ArrayList<Integer> postingList1, IdsMap filesIds){
        Set<Integer> negatedDocs = new HashSet<>(filesIds.getFilesIds().keySet());
        //System.out.println(negatedDocs);
        //if(invertedIndex.getInvertedIndex().containsKey(key)){
            //ArrayList<Integer> values = invertedIndex.getInvertedIndex().get(key);
//            System.out.println(values);

            negatedDocs.removeAll(postingList1);
            //System.out.println(negatedDocs);

        System.out.printf("The negation for \"%s\" is: %s", negatedDocs);
        return negatedDocs;
    }

    //-------------------- AND NOT entre 2 termos --------------------
    public ArrayList<Integer> searchANDNOT(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2){

        ArrayList<Integer> andNotDocs = new ArrayList<>();
        //if(invertedIndex.getInvertedIndex().containsKey(key)){
            //ArrayList values = invertedIndex.getInvertedIndex().get(key);
            //ArrayList values1 = invertedIndex.getInvertedIndex().get(key1);
//            System.out.println(values);
//            System.out.println(values1);

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

        System.out.printf("The negative intersection between \"%s\" and \"%s\" is: %s", andNotDocs);
        return andNotDocs;
    }



    //--------------------PRIORITY FUNCTIONS--------------------

    //Prentheses function
    public ArrayList<Integer> parentheses (String query) {
        ArrayList<Integer> result_posting = new ArrayList<>();
        Integer pos_initial = null;
        Integer pos_final = null;
        char[] query_char = query.toCharArray();

        while(true) {
            System.out.println("query: "+ query);
            if (!query.contains("(")){
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
            String middle_query = query.substring(pos_initial+1,pos_final);
            System.out.println("inter result: " + middle_query);

            result_posting = logicOperators(middle_query);
            // ir buscar resultado da operação acima e substituir na query *middle_query*
            if (result_posting == null){
                System.out.println("Result is null!");
                break;
            }
            // nao esquecer que resultado pode ser nulo
            query = query.substring(0,pos_initial) + result_posting.toString() + query.substring(pos_final+1);

        }
        if(result_posting != null){
            System.out.println("result: "+ result_posting);
        }
        
        return result_posting;
    }

    //Logic Operators function
    public ArrayList<Integer> logicOperators (String query) {
        ArrayList<Integer> result_posting = new ArrayList<>();
        String logicOpers[] = new String[]{" ANDNOT ", " NOT ", " AND ", " OR "};
        ArrayList<Integer> index_spaces = new ArrayList<>();

        query = " "+query+" ";
        int index = query.indexOf(" ");
        while (index != -1) {
            index_spaces.add(index);
            index = query.indexOf(" ", index + 1);
        }
        // System.out.println(index_spaces);

        //Colocar em loop para resolver todos os operadores
        if(query.contains(logicOpers[0])){
            String oper = logicOpers[0];
            Integer pos_initial = index_spaces.get(index_spaces.indexOf(query.indexOf(oper))-1)+1;
            Integer pos_final =index_spaces.get(index_spaces.indexOf(query.indexOf(oper)+ oper.length()-1)+1);
            String semi_query = query.substring(pos_initial,pos_final);
            //usar função ANDNOT
            String posting1 = semi_query.substring(semi_query.indexOf("[")+1, semi_query.indexOf("]"));
            String posting2 =semi_query.split("ANDNOT ")[1].substring(semi_query.split("ANDNOT ")[1].indexOf("[")+1,semi_query.split("ANDNOT ")[1].indexOf("]"));
            String[] posting1_middle = posting1.split(",");
            String[] posting2_middle = posting2.split(",");
            ArrayList<Integer> posting1_list = new ArrayList<>();
            ArrayList<Integer> posting2_list = new ArrayList<>();
            for (String number : posting1_middle) {
                int num = Integer.parseInt(number);
                posting1_list.add(num);
            }
            for (String number : posting2_middle) {
                int num = Integer.parseInt(number);
                posting2_list.add(num);
            }
            //System.out.println(posting1_list);
            //System.out.println(posting2_list);

            result_posting = searchANDNOT(posting1_list,posting2_list);
        }
        else if(!query.contains(logicOpers[0]) && query.contains(logicOpers[1])){
            String oper = logicOpers[1];
            Integer pos_initial = query.indexOf(oper)+1;
            Integer pos_final =index_spaces.get(index_spaces.indexOf(query.indexOf(oper)+ oper.length()-1)+1);
            String semi_query = query.substring(pos_initial,pos_final);
            //usar função NOT
            String posting1 = semi_query.substring(semi_query.indexOf("[")+1, semi_query.indexOf("]"));
            String[] posting1_middle = posting1.split(",");
            ArrayList<Integer> posting1_list = new ArrayList<>();
            for (String number : posting1_middle) {
                int num = Integer.parseInt(number);
                posting1_list.add(num);
            }
            //result_posting = searchNegation(posting1_list);
        }
        else if(!query.contains(logicOpers[0]) && !query.contains(logicOpers[1]) && query.contains(logicOpers[2])){
            String oper = logicOpers[2];
            Integer pos_initial = index_spaces.get(index_spaces.indexOf(query.indexOf(oper))-1)+1;
            Integer pos_final =index_spaces.get(index_spaces.indexOf(query.indexOf(oper)+ oper.length()-1)+1);
            String semi_query = query.substring(pos_initial,pos_final);
            //usar função AND
            String posting1 = semi_query.substring(semi_query.indexOf("[")+1, semi_query.indexOf("]"));
            String posting2 =semi_query.split("AND ")[1].substring(semi_query.split("AND ")[1].indexOf("[")+1,semi_query.split("AND ")[1].indexOf("]"));
            String[] posting1_middle = posting1.split(",");
            String[] posting2_middle = posting2.split(",");
            ArrayList<Integer> posting1_list = new ArrayList<>();
            ArrayList<Integer> posting2_list = new ArrayList<>();
            for (String number : posting1_middle) {
                int num = Integer.parseInt(number);
                posting1_list.add(num);
            }
            for (String number : posting2_middle) {
                int num = Integer.parseInt(number);
                posting2_list.add(num);
            }
            result_posting = searchIntersection(posting1_list,posting2_list);
        }
        else if(!query.contains(logicOpers[0]) && !query.contains(logicOpers[1]) && !query.contains(logicOpers[2]) && query.contains(logicOpers[3])){
            String oper = logicOpers[3];
            Integer pos_initial = index_spaces.get(index_spaces.indexOf(query.indexOf(oper))-1)+1;
            Integer pos_final =index_spaces.get(index_spaces.indexOf(query.indexOf(oper)+ oper.length()-1)+1);
            String semi_query = query.substring(pos_initial,pos_final);
            //usar função OR
            String posting1 = semi_query.substring(semi_query.indexOf("[")+1, semi_query.indexOf("]"));
            String posting2 =semi_query.split("OR ")[1].substring(semi_query.split("OR ")[1].indexOf("[")+1,semi_query.split("OR ")[1].indexOf("]"));
            String[] posting1_middle = posting1.split(",");
            String[] posting2_middle = posting2.split(",");
            ArrayList<Integer> posting1_list = new ArrayList<>();
            ArrayList<Integer> posting2_list = new ArrayList<>();
            for (String number : posting1_middle) {
                int num = Integer.parseInt(number);
                posting1_list.add(num);
            }
            for (String number : posting2_middle) {
                int num = Integer.parseInt(number);
                posting2_list.add(num);
            }
            result_posting = searchUnion(posting1_list,posting2_list);
        }
        else{
            System.out.println("No logic operator found!");
            return null;
        }

        return result_posting;
    }

}
