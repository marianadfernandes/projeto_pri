package objects;

import java.io.Serializable;
import java.util.HashMap;

public class IdsMap implements Serializable {
    Integer docId;
    String docPath;

    HashMap<Integer, String> filesIds = new HashMap<>();

    public HashMap<Integer, String> getFilesIds() {
        return filesIds;
    }

    public void setFilesIds(HashMap<Integer, String> filesIds) {
        this.filesIds = filesIds;
    }

    public void addEntry(Integer id, String path) {
        this.docId = id;
        this.docPath = path;
        this.filesIds.put(docId, docPath);
    }

}
