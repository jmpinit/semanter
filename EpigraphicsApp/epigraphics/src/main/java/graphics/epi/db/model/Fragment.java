package graphics.epi.db.model;

public class Fragment {
    int id;
    String path;
    String created_at;
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
    public void setCreatedAt(String created_at){
        this.created_at = created_at;
    }

    // getters
    public long getId() {
        return this.id;
    }
    public String getPath() {
        return this.path;
    }
    public String getCreatedAt() {
        return this.created_at;
    }

}