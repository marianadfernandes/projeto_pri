import java.io.*;
import java.util.*;

public class DocumentManager {

    public String readFile(String filepath) {
        String text = "";
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                text = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return text;
    }

    public HashMap<Integer, String> attributeFileId(ArrayList<String> files ){
        HashMap<Integer, String> filesIds = new HashMap<>();
        for (int i=0; i<files.size(); i++){
            filesIds.put(i+1, files.get(i));
        }
        for (Integer key : filesIds.keySet())
            System.out.println(key + " - "+ filesIds.get(key));
        return filesIds;
    }

    public HashMap<Map.Entry<Integer, String>, String> filesIdsAndText(HashMap<Integer,String> filesIds, ArrayList<String> listText){
        HashMap<Map.Entry<Integer, String>, String> filesMap = new HashMap<>();
        for (Map.Entry<Integer,String> entry : filesIds.entrySet()){
            filesMap.put(entry,listText.get(entry.getKey()-1));
        }
        for (Map.Entry<Map.Entry<Integer, String>, String> entry : filesMap.entrySet()) {
            System.out.println(entry.getKey().getKey() + " - " + entry.getKey().getValue() + " - " + entry.getValue());
        }
        return filesMap;
    }

    public String[] splitData(String text) {
        return text.split("(?!')\\W+");
    }

    public ArrayList<String> processText(String [] splitText) {
        ArrayList<String> terms = new ArrayList<>();
        for (String token : splitText) {
            String term = token.toLowerCase();
            terms.add(term);
        }
        return terms;
    }

    public HashMap<Integer, ArrayList<String>> createDirectIndex(HashMap<Map.Entry<Integer, String>, String> filesMap) {
        HashMap<Integer, ArrayList<String>> directIdx = new HashMap<>();

        for (Map.Entry<Map.Entry<Integer, String>, String> entry : filesMap.entrySet()) {
            String text = entry.getValue();
            String [] splitText = splitData(text);
            ArrayList<String> terms = processText(splitText);
            directIdx.put(entry.getKey().getKey(), terms);
        }

        for (Map.Entry<Integer, ArrayList<String>> entry : directIdx.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        return directIdx;
    }

    public SortedMap<String, ArrayList<Integer>> createInvertedIndex(HashMap<Integer, ArrayList<String>> directIdx) {
        SortedMap<String, ArrayList<Integer>> invertedIndex = new TreeMap<>();
        for (Integer key : directIdx.keySet()) {
            for (String value : directIdx.get(key)) {
                ArrayList<Integer> postingList = invertedIndex.getOrDefault(value, new ArrayList<>());
                if (!postingList.contains(key)) {
                    postingList.add(key);
                }
                invertedIndex.put(value, postingList);
            }
        }
        printInvertedIndex(invertedIndex);
        return invertedIndex;
    }

    public void printInvertedIndex(SortedMap<String, ArrayList<Integer>> invertedIndex){
        System.out.printf("%-10s | %-14s | %-30s %n", "TERM", "DOC. FREQUENCY", "POSTING LIST");
        for (String key : invertedIndex.keySet()) {
            System.out.printf("%-10s | %-14s | %-30s %n", key, invertedIndex.get(key).size(), invertedIndex.get(key));
        }
    }

    public boolean searchDocID(HashMap<Map.Entry<Integer, String>, String> filesMap, Integer docid, HashMap<Integer, ArrayList<String>> directIdx) {
        for (Map.Entry<Map.Entry<Integer, String>, String> entry : filesMap.entrySet()) {
            if (entry.getKey().getKey().equals(docid)) {
                System.out.println("Caminho para o ficheiro: " + entry.getKey().getValue());
                System.out.println("Termos do documento " + docid + ": " + directIdx.get(docid));
                return true;
            }
        }
        System.out.println("Não contém a chave pretendida");
        return false;
    }

    public void searchTerm(SortedMap<String, ArrayList<Integer>> invertedIndex, String term) {
        if (invertedIndex.containsKey(term)) {
            System.out.println(term + " - Doc Frequency: " + invertedIndex.get(term).size() + " - Posting List: " + invertedIndex.get(term));
        } else {
            System.out.println("Não contém a chave pretendida");
        }
    }

    public void dumpData(SortedMap<String, ArrayList<Integer>> invertedIndex, HashMap<Integer, String> filesIds) {
        try {
            FileWriter myWriter = new FileWriter("invertedIndex.txt");
            PrintWriter print_line = new PrintWriter(myWriter);
            print_line.printf("%-10s | %-50s %n", "DOC. ID", "DOC. PATH");
            for (Map.Entry<Integer, String> entry : filesIds.entrySet()) {
                print_line.printf("%-10s | %-50s %n", entry.getKey(), entry.getValue());
            }
            print_line.printf("%n%-15s | %-14s | %-30s %n", "TERM", "DOC. FREQUENCY", "POSTING LIST");
            for (String key : invertedIndex.keySet()) {
                print_line.printf("%-15s | %-14s | %-30s %n", key, invertedIndex.get(key).size(), invertedIndex.get(key));
            }
            myWriter.close();
            System.out.println("\nEscrita no ficheiro concluída.");
        } catch (IOException e) {
            System.out.println("Erro na escrita.");
            e.printStackTrace();
        }
    }
}

