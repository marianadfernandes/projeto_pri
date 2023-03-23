import java.io.File;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class FilesRetriever {

    static int id;
    static SortedMap<Integer, String> filesIndex = new TreeMap<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ArrayList<String> listFilesForFolder(String path) {

        File folder = new File(path);
        ArrayList<String> files = new ArrayList<>();

        for (File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().substring(fileEntry.getName().lastIndexOf(".")).equals(".txt")){
                    if( path.substring(path.length()-1).equals("/")){
                        files.add(path + fileEntry.getName());
                    }
                    else {
                        files.add(path + "/" + fileEntry.getName());
                    }
                }
            }
        }
        return files;
    }

    public static SortedMap<Integer, String> filesMap(ArrayList<String> files) {
        for (String file : files) {
            id += 1;
            filesIndex.put(id, file);
        }
        return filesIndex;
    }

    public static ArrayList<String> listTextFromFiles(ArrayList<String> files){

        ArrayList<String> listText = new ArrayList<>();
        String text;
        DocumentManager doc = new DocumentManager();

        for ( String file : files ){
            text = doc.readFile(file);
            listText.add(text);
        }
        return listText;
    }

    public static void searchFileName(Integer docid) {
        if (filesIndex.containsKey(docid)) {
            System.out.println("Nome do ficheiro: " + filesIndex.get(docid));
        } else {
            System.out.println("Não contém a chave pretendida");
        }
    }
}
