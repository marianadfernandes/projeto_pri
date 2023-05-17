package query_module;

import indexer.IdsMap;
import indexer.InvertedIndex;

import java.util.*;

public class Search {

    public void processQuery(String query, InvertedIndex invertedIndex, IdsMap filesIds) {
        if (query.contains("NOT")) {
            String term = query.replaceAll("NOT ", "");
            searchNegation(term, invertedIndex, filesIds);
        } else if (query.contains("AND")) {
            String [] terms = query.split(" AND ");
            //searchIntersections(new ArrayList<>(Arrays.asList(terms)), invertedIndex);
            searchIntersection(terms[0], terms[1], invertedIndex);
        } else if (query.contains("OR")) {
            String [] terms = query.split(" OR ");
            if (terms.length == 2) {
                searchUnion(terms[0], terms[1], invertedIndex);
            }
        }

    }

    public void searchTerm(String key, InvertedIndex invertedIndex){
        if (invertedIndex.getInvertedIndex().containsKey(key)){
            System.out.println(key + ": " + invertedIndex.getInvertedIndex().get(key));
        }
        else{
            System.out.println("Element not found.");
        }
    }
    public ArrayList<Integer> searchIntersection(String key, String key1, InvertedIndex invertedIndex){
        ArrayList<Integer> intersectDocs = new ArrayList<>();
        if(invertedIndex.getInvertedIndex().containsKey(key)){
            ArrayList values = invertedIndex.getInvertedIndex().get(key);
            ArrayList values1 = invertedIndex.getInvertedIndex().get(key1);
//            System.out.println(values);
//            System.out.println(values1);

            int i = 0;
            int j = 0;
            while (values.size() != i && values1.size() != j){
                if ( values.get(i) == values1.get(j)){
                    intersectDocs.add((Integer) values.get(i));
                    i+=1;
                    j+=1;
                }
                else{
                    if( (int)values.get(i) < (int)values1.get(j)){
                        i+=1;
                    }
                    else{
                        j+=1;
                    }
                }
            }
        }
        System.out.printf("The intersection between \"%s\" and \"%s\" is: %s", key, key1, intersectDocs);
        return intersectDocs;
    }

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

    public ArrayList<Integer> searchUnion(String key, String key1, InvertedIndex invertedIndex){
        ArrayList<Integer> unitedDocs = new ArrayList<>();
        if(invertedIndex.getInvertedIndex().containsKey(key)){
            ArrayList values = invertedIndex.getInvertedIndex().get(key);
            ArrayList values1 = invertedIndex.getInvertedIndex().get(key1);
//            System.out.println(values);
//            System.out.println(values1);

            int i = 0;
            int j = 0;
            while (values.size() != i && values1.size() != j){
                if ( values.get(i) == values1.get(j)){
                    unitedDocs.add((Integer) values.get(i));
                    i += 1;
                    j += 1;
                }
                else{
                    if( (int)values.get(i) < (int)values1.get(j)){
                        unitedDocs.add((Integer) values.get(i));
                        i+=1;
                    }
                    else{
                        unitedDocs.add((Integer) values1.get(j));
                        j+=1;
                    }
                }
            }

            while (i < values.size()) {
                unitedDocs.add((Integer) values.get(i));
                i++;
            }

            while (j < values1.size()) {
                unitedDocs.add((Integer) values1.get(j));
                j++;
            }
        }
        System.out.printf("The union between \"%s\" and \"%s\" is: %s", key, key1, unitedDocs);
        return unitedDocs;
    }

    public Set<Integer> searchNegation(String key, InvertedIndex invertedIndex, IdsMap filesIds){
        Set<Integer> negatedDocs = new HashSet<>(filesIds.getFilesIds().keySet());
        //System.out.println(negatedDocs);
        if(invertedIndex.getInvertedIndex().containsKey(key)){
            ArrayList<Integer> values = invertedIndex.getInvertedIndex().get(key);
//            System.out.println(values);

            negatedDocs.removeAll(values);
            //System.out.println(negatedDocs);


        }
        System.out.printf("The negation for \"%s\" is: %s", key, negatedDocs);
        return negatedDocs;
    }
}
