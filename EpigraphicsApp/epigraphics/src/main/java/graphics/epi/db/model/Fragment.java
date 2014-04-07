package graphics.epi.db.model;

/**
 * Created by coreywalsh on 4/6/14.
 */
public class Fragment {
    public Fragment() {
    }

    public Fragment(String path) {
        this.path = path;
    }

    public Fragment(int id, String path) {
        this.id = id;
        this.path = path;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }
    public void setPath(String path) {
        this.path = path;
    }

    // getters
    public long getId() {
        return this.id;
    }
    public String getPath() {
        return this.path;
    }
}
