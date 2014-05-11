package us.semanter.app.model;

public class FilterableNote {
    private boolean visible;
    private Note note;

    public FilterableNote(Note note) {
        this.visible = true;
        this.note = note;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() { return visible; }
    public Note getNote() { return note; }
}
