package graphics.epi.filesystemtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Folder implements Items, Serializable {
    Folder parent;
    String name;

    public List<Items> inside = new ArrayList<Items>();

    public Folder(Folder parent, String s) {
        this.parent = parent;
        name = s;
        inside.add(new Files("c.txt"));
    }

    public Folder(Folder parent, List<String> allFiles) {
        this.parent = parent;
        inside.add(new Files("a.txt"));
        inside.add(new Files("b.txt"));
        inside.add(new Folder(this, "b.txt"));
        name = "/";
    }

    public List<Items> getItems() {
        return inside;
    }

    @Override
    public String toString() {
        return "Folder " + name;
    }

    public Items get(int position) {
        return inside.get(position);
    }

    public boolean isFile() {
        return false;
    }
}

