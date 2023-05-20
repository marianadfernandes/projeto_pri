package query_module;

import objects.InvertedIndex;
import objects.IdsMap;
import java.io.*;
import java.nio.file.Paths;

public class Load {

    public InvertedIndex loadInvertedIndexFromFile (){
        String folderPath = "output"; // Specify the folder path where the file is located

        try {
            String filePath = Paths.get(folderPath, "invertedIndex_Serialized.ser").toString();
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            InvertedIndex invertedIndex = (InvertedIndex) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Inverted Index (Deserialized) carregado do ficheiro: " + filePath);
            return invertedIndex;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IdsMap loadIdsMapFromFile (){
        String folderPath = "output"; // Specify the folder path where the file is located

        try {
            String filePath = Paths.get(folderPath, "idsMap_Serialized.ser").toString();
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            IdsMap idsMap = (IdsMap) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Ids Map (Deserialized) carregado do ficheiro: " + filePath);
            return idsMap;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}