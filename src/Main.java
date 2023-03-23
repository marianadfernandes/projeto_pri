import java.util.ArrayList;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.Integer.parseInt;

public class Main {



    public static void main(String[] args) {

        String path = "./input/";
        ArrayList<String> files = new ArrayList<>();
        ArrayList<String> listText = new ArrayList<>();

        files = FilesRetriever.listFilesForFolder(path);
        System.out.println(files);
        listText = FilesRetriever.listTextFromFiles(files);
        System.out.println(listText);

        SortedMap<Integer,String> fileIndex = new TreeMap<>();
        fileIndex = FilesRetriever.filesMap(files);
        for (Integer key : fileIndex.keySet())
            System.out.println(key + " - "+ fileIndex.get(key));

        DocumentManager doc = new DocumentManager();
        // DocumentManager doc2 = new DocumentManager();

        for (Integer key : fileIndex.keySet()) {
            String text = doc.readFile(fileIndex.get(key));
            String [] splitText = doc.splitData(text);
            ArrayList<String> terms = doc.processText(splitText);
            doc.addEntry(key, terms);
            doc.invertEntry();
        }
        /*String text2 = doc2.readFile("input/teste2.txt");

        String [] splitText = doc.splitData(text);
        String [] splitText2 = doc2.splitData(text2);

        ArrayList<String> terms = doc.processText(splitText);
        ArrayList<String> terms2 = doc2.processText(splitText2);

        doc.addEntry(terms);
        doc2.addEntry(terms2);

        doc.invertEntry();
        doc2.invertEntry(); */

        doc.printDirectIndex();

        doc.printInvertedIndex();

        doc.dumpData();

        Scanner myObj = new Scanner(System.in);
        System.out.println("Introduza o doc id a procurar:");
        Integer docid = parseInt(myObj.nextLine());
        doc.searchDocID(docid);
        FilesRetriever.searchFileName(docid);

        System.out.println("Introduza o termo a procurar:");
        String term = myObj.nextLine().toLowerCase();
        doc.searchTerm(term);
    }
}