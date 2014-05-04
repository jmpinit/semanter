package us.semanter.app.model;

/**
 * Immutable
 */
public class Tag {
    private String value;

    public Tag(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Tag)) return false;
        return value.equals(((Tag) other).value);
    }
}
