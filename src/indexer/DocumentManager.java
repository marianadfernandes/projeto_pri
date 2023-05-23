package indexer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import objects.InvertedIndex;
import objects.IdsMap;
import objects.DocMap;

public class DocumentManager {

    TextProcess textProcess = new TextProcess();

    public IdsMap attributeFileId(ArrayList<String> files ){
        IdsMap filesIds = new IdsMap();
        for (int i=0; i<files.size(); i++) {
            filesIds.addEntry(i + 1, files.get(i));
        }
        for (Integer key : filesIds.getFilesIds().keySet())
            System.out.println(key + " - "+ filesIds.getFilesIds().get(key));
        return filesIds;
    }

    public DocMap filesIdsAndText(IdsMap filesIds, ArrayList<String> listText){
        DocMap filesMap = new DocMap();
        for (Map.Entry<Integer,String> entry : filesIds.getFilesIds().entrySet()){
            filesMap.addEntry(entry, listText.get(entry.getKey()-1));
        }
        /*for (Map.Entry<Map.Entry<Integer, String>, String> entry : filesMap.getFilesMap().entrySet()) {
            System.out.println(entry.getKey().getKey() + " - " + entry.getKey().getValue() + " - " + entry.getValue());
        }*/
        return filesMap;
    }


    public HashMap<Integer, ArrayList<String>> createDirectIndex(DocMap filesMap) {
        HashMap<Integer, ArrayList<String>> directIdx = new HashMap<>();

        ArrayList<String> terms = null;
        for (Map.Entry<Map.Entry<Integer, String>, String> entry : filesMap.getFilesMap().entrySet()) {
            String text = entry.getValue();
            String[] splitText = textProcess.splitData(text);
            terms = textProcess.processTokens(splitText);
            directIdx.put(entry.getKey().getKey(), terms);
        }
        System.out.println("termos processados: " + terms);

        for (Map.Entry<Integer, ArrayList<String>> entry : directIdx.entrySet()) {
            if (entry.getKey() == 37) {
                System.out.println(entry.getKey() + " - número de tokens " + entry.getValue().size());
/*                for (String value : entry.getValue()) {
                    if (value.equals("a")) {
                        System.out.println((entry.getValue().get(4127)));
                        System.out.println((entry.getValue().indexOf("a")));
                        System.out.println((entry.getValue().get(4129)));
                    }
                }*/
            }
            else {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }

        }
        return directIdx;
    }

    public InvertedIndex createInvertedIndex(HashMap<Integer, ArrayList<String>> directIdx) {
        InvertedIndex invertedIndex = new InvertedIndex();
        for (Integer key : directIdx.keySet()) {
            for (String value : directIdx.get(key)) {
                ArrayList<Integer> postingList = invertedIndex.getInvertedIndex().getOrDefault(value, new ArrayList<>());
                if (!postingList.contains(key)) {
                    postingList.add(key);
                }
                invertedIndex.addEntry(value, postingList);
            }
        }
        printInvertedIndex(invertedIndex);
        return invertedIndex;
    }

    public void printInvertedIndex(InvertedIndex invertedIndex){
        System.out.printf("%-20s | %-14s | %-30s %n", "TERM", "DOC. FREQUENCY", "POSTING LIST");
        for (String key : invertedIndex.getInvertedIndex().keySet()) {
            System.out.printf("%-20s | %-14s | %-30s %n", key, invertedIndex.getInvertedIndex().get(key).size(), invertedIndex.getInvertedIndex().get(key));
        }
    }

    public boolean searchDocID(DocMap filesMap, Integer docid, HashMap<Integer, ArrayList<String>> directIdx) {
        for (Map.Entry<Map.Entry<Integer, String>, String> entry : filesMap.getFilesMap().entrySet()) {
            if (entry.getKey().getKey().equals(docid)) {
                System.out.println("Caminho para o ficheiro: " + entry.getKey().getValue());
                System.out.println("Tokens do documento " + docid + ": " + directIdx.get(docid));
                return true;
            }
        }
        System.out.println("Não contém a chave pretendida");
        return false;
    }

    public void searchTerm(InvertedIndex invertedIndex, String term) {
        if (invertedIndex.getInvertedIndex().containsKey(term)) {
            System.out.println(term + " - Doc Frequency: " + invertedIndex.getInvertedIndex().get(term).size() + " - Posting List: " + invertedIndex.getInvertedIndex().get(term));
        } else {
            System.out.println("Não contém a chave pretendida");
        }
    }

    public void dumpData(InvertedIndex invertedIndex, IdsMap filesIds) {
        String folderPath = "output"; // Specify the folder path where you want to store the file

        try {
            // Create the folder if it doesn't exist
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String filePath = Paths.get(folderPath, "invertedIndex.txt").toString();
            FileWriter myWriter = new FileWriter(filePath);
            PrintWriter print_line = new PrintWriter(myWriter);

            print_line.printf("%-10s | %-50s %n", "DOC. ID", "DOC. PATH");
            for (Map.Entry<Integer, String> entry : filesIds.getFilesIds().entrySet()) {
                print_line.printf("%-10s | %-50s %n", entry.getKey(), entry.getValue());
            }
            print_line.printf("%n%-20s | %-14s | %-30s %n", "TERM", "DOC. FREQUENCY", "POSTING LIST");
            for (String key : invertedIndex.getInvertedIndex().keySet()) {
                print_line.printf("%-20s | %-14s | %-30s %n", key, invertedIndex.getInvertedIndex().get(key).size(), invertedIndex.getInvertedIndex().get(key));
            }
            myWriter.close();
            System.out.println("\nEscrita no ficheiro concluída.");
        } catch (IOException e) {
            System.out.println("Erro na escrita.");
            e.printStackTrace();
        }
    }

    public void saveIndexToFile (InvertedIndex invertedIndex) {
        String folderPath = "output"; // Specify the folder path where you want to save the file

        try {
            String filePath = Paths.get(folderPath, "invertedIndex_Serialized.ser").toString();
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(invertedIndex);
            oos.close();
            fos.close();
            System.out.println("\nInverted Index (Serialized) guardado no ficheiro: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDocMapToFile (IdsMap idsMap) {
        String folderPath = "output"; // Specify the folder path where you want to save the file

        try {
            String filePath = Paths.get(folderPath, "idsMap_Serialized.ser").toString();
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idsMap);
            oos.close();
            fos.close();
            System.out.println("Ids Map (Serialized) guardado no ficheiro: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}