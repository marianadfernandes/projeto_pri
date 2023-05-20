package indexer;

import java.io.File;
import java.util.*;

public class FilesRetriever {

    public static ArrayList<String> listFilesForFolder(String path) {

        File folder = new File(path);
        ArrayList<String> files = new ArrayList<>();

        for (File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().substring(fileEntry.getName().lastIndexOf(".")).equals(".txt") || fileEntry.getName().substring(fileEntry.getName().lastIndexOf(".")).equals(".xml")) {
                    if (path.substring(path.length() - 1).equals("/")) {
                        files.add(path + fileEntry.getName());
                    } else {
                        files.add(path + "/" + fileEntry.getName());
                    }
                }
            }
        }
        return files;
    }

    public static ArrayList<String> listTextFromFiles(ArrayList<String> files){

        ArrayList<String> listText = new ArrayList<>();
        DocumentManager doc = new DocumentManager();

        for ( String file : files ){
            String text = doc.readFile(file);

            // processar tags do xml
            text = text.replaceAll("<\\/?.*?>", "");

            listText.add(text);
        }

        for (String text : listText) {
            System.out.println("Conteúdo: " + text);
        }
        return listText;
    }
}