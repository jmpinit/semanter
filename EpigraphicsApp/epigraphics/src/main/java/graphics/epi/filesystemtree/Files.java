package graphics.epi.filesystemtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Files implements Items, Serializable {
    String name;

    Files(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Items> getItems() {
        List<Items> one = new ArrayList<Items>();
        one.add(this);
        return one;
    }

    public boolean isFile() {
        return true;
    }
}
