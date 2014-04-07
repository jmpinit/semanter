package graphics.epi.db.model;

/**
 * Created by coreywalsh on 4/6/14.
 */
public class Bounds {
    public Bounds() {
    }

    public Bounds(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public Bounds(int id, int left, int right, int top, int bottom) {
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
}
