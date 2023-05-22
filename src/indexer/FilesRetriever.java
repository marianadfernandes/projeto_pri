package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FilesRetriever {

    public ArrayList<String> listFilesForFolder(String path) {

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

    public String readFile(String filepath) {
        StringBuilder text = new StringBuilder();
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj, StandardCharsets.UTF_8.name());
            while (myReader.hasNextLine()) {
                text.append(myReader.nextLine()).append("\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return text.toString();
    }

    public ArrayList<String> listTextFromFiles(ArrayList<String> files){

        ArrayList<String> listText = new ArrayList<>();

        for ( String file : files ){
            String text = readFile(file);

            // processar tags do xml
            text = text.replaceAll("<\\/?.*?>", " ");

            listText.add(text);
        }

        /*for (String text : listText) {
            System.out.println("Conteúdo: " + text);
        }*/
        return listText;
    }
}