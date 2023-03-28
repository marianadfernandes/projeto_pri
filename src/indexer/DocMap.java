package indexer;

import java.util.HashMap;
import java.util.Map;

public class DocMap {
    String texto;
    Map.Entry<Integer, String> entry;

    HashMap<Map.Entry<Integer, String>, String> filesMap = new HashMap<>();

    public HashMap<Map.Entry<Integer, String>, String> getFilesMap() {
        return filesMap;
    }

    public void setFilesMap(HashMap<Map.Entry<Integer, String>, String> filesMap) {
        this.filesMap = filesMap;
    }

    public void addEntry(Map.Entry<Integer, String> entry, String text) {
        this.entry = entry;
        this.texto = text;
        this.filesMap.put(entry, texto);
    }
}
