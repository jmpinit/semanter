package graphics.epi.filesystemtree;

import java.util.List;

public class Parent implements Items {
    private Folder fol;

    Parent(Folder fol) {
        this.fol = fol;
    }

    @Override
    public String toString() {
        return "Go Back";
    }

    public List<Items> getItems() {
        return null;
    }

    public boolean isFile() {
        return false;
    }
}