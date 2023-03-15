import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Document {

    int id;
    static int idCounter = 0;
    String text;
    static HashMap<Integer, String[]> directIndex = new HashMap<Integer, String[]>();
    Map<String, ArrayList<Object>> invertedIndex = new HashMap<>();


    public Document() {
        setId(++idCounter);
    }


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

    public void addEntry(String[] splitText) {
        directIndex.put(id ,splitText);
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
                postingList.add(key);
                invertedIndex.put(value, postingList);
            }
        }
    }

    public void printDirectIndex(){
        for (int key : directIndex.keySet()) {
            System.out.println(key + " - " + Arrays.toString(directIndex.get(key)));
        }
    }

    public void printInvertedIndex(){
        System.out.println("Hello");
        for (String key : invertedIndex.keySet()) {
            System.out.println(key + " - " + invertedIndex.get(key));
        }
    }
}

