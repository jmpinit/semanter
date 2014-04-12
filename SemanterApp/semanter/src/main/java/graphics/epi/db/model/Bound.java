package graphics.epi.db.model;

public class Bound {
    int id;
    int left;
    int right;
    int top;
    int bottom;
    String created_at;
    public Bound() {
    }

    public Bound(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public Bound(int id, int left, int right, int top, int bottom) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }
    public void setLeft(int left) {
        this.left = left;
    }
    public void setRight(int right) {
        this.right = right;
    }
    public void setTop(int top) {
        this.top = top;
    }
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
    public void setCreatedAt(String created_at){
        this.created_at = created_at;
    }

    // getters
    public long getId() {
        return this.id;
    }
    public int getLeft() {
        return this.left;
    }
    public int getRight() {
        return this.right;
    }
    public int getTop() {
        return this.top;
    }
    public int getBottom() {
        return this.bottom;
    }
    public String getCreatedAt() {
        return this.created_at;
    }

}