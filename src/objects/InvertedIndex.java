package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class InvertedIndex implements Serializable {

    String term;
    ArrayList<Integer> docIds;

    SortedMap<String, ArrayList<Integer>> invertedIndex = new TreeMap<>();

    public SortedMap<String, ArrayList<Integer>> getInvertedIndex() {
        return invertedIndex;
    }

    public void setInvertedIndex(SortedMap<String, ArrayList<Integer>> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public void addEntry(String termo, ArrayList<Integer> docIds) {
        this.term = termo;
        this.docIds = docIds;
        this.invertedIndex.put(termo, docIds);
    }
}
