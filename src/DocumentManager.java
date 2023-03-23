import java.io.*;
import java.util.*;

public class DocumentManager {

    int id;
    static int idCounter = 0;
    String text;
    static HashMap<Integer, ArrayList<String>> directIndex = new HashMap<Integer, ArrayList<String>>();
    SortedMap<String, ArrayList<Object>> invertedIndex = new TreeMap<>();


    /*public DocumentManager() {
        setId(++idCounter);
    }*/


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String readFile(String filepath) {
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

    public void addEntry(Integer docid, ArrayList<String> splitText) {
        directIndex.put(docid ,splitText);
    }

    /*
    public void invertEntry() {
        ArrayList<Integer> postingList = new ArrayList<>();
        for (Integer key : directIndex.keySet()) {
            for (String value : directIndex.get(key)) {
                if (directIndex.get(key). && !postingList.contains(key)) {
                    postingList.add(key);
                }
                invertedIndex.put(value, postingList);
            }
        }
    }*/

    public void invertEntry() {
        for (Integer key : directIndex.keySet()) {
            for (String value : directIndex.get(key)) {
                ArrayList<Object> postingList = invertedIndex.getOrDefault(value, new ArrayList<>());
                if (!postingList.contains(key)) {
                    postingList.add(key);
                }
                invertedIndex.put(value, postingList);
            }
        }
    }

    public void printDirectIndex(){
        for (int key : directIndex.keySet()) {
            System.out.println(key + " - " + directIndex.get(key));
        }
    }

    public void printInvertedIndex(){
        System.out.printf("%-10s | %-14s | %-30s %n", "TERM", "DOC. FREQUENCY", "POSTING LIST");
        for (String key : invertedIndex.keySet()) {
            System.out.printf("%-10s | %-14s | %-30s %n", key, invertedIndex.get(key).size(), invertedIndex.get(key));
        }
    }

    public void searchDocID(Integer docid) {
        if (directIndex.containsKey(docid)) {
            System.out.println("Termos do documento " + docid + ": " + directIndex.get(docid));
        } else {
            System.out.println("Não contém a chave pretendida");
        }
    }

    public void searchTerm(String term) {
        if (invertedIndex.containsKey(term)) {
            System.out.println(term + " - Doc Frequency: " + invertedIndex.get(term).size() + " - Posting List: " + invertedIndex.get(term));
        } else {
            System.out.println("Não contém a chave pretendida");
        }
    }

    public void dumpData() {
        try {
            FileWriter myWriter = new FileWriter("invertedIndex.txt");
            PrintWriter print_line = new PrintWriter(myWriter);
            print_line.printf("%-10s | %-14s | %-30s %n", "TERM", "DOC. FREQUENCY", "POSTING LIST");
            for (String key : invertedIndex.keySet()) {
                print_line.printf("%-10s | %-14s | %-30s %n", key, invertedIndex.get(key).size(), invertedIndex.get(key));
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

