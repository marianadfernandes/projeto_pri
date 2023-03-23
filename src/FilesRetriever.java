import java.io.File;
import java.util.ArrayList;

public class FilesRetriever {
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
}
