import java.util.ArrayList;
import java.util.Scanner;

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

        DocumentManager doc = new DocumentManager();
        DocumentManager doc2 = new DocumentManager();

        String text = doc.readFile("input/teste.txt");
        String text2 = doc2.readFile("input/teste2.txt");

        String [] splitText = doc.splitData(text);
        String [] splitText2 = doc2.splitData(text2);

        ArrayList<String> terms = doc.processText(splitText);
        ArrayList<String> terms2 = doc2.processText(splitText2);

        doc.addEntry(terms);
        doc2.addEntry(terms2);

        doc.invertEntry();
        doc2.invertEntry();

        doc.printDirectIndex();

        doc.printInvertedIndex();

        doc.dumpData();

        Scanner myObj = new Scanner(System.in);
        System.out.println("Introduza o doc id a procurar:");
        Integer docid = parseInt(myObj.nextLine());
        doc.searchDocID(docid);

        System.out.println("Introduza o termo a procurar:");
        String term = myObj.nextLine().toLowerCase();
        doc.searchTerm(term);
    }
}