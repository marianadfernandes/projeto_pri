package query_module;

import indexer.InvertedIndex;
import java.io.*;

public class Load {

    public InvertedIndex loadInvertedIndexFromFile (){
        try {
            FileInputStream fis = new FileInputStream("invertedIndex_Serialized.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            InvertedIndex invertedIndex = (InvertedIndex) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Inverted Index (Deserialized) carregado do ficheiro.");
            return invertedIndex;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}