package graphics.epi.db.model;

/**
 * Created by coreywalsh on 4/6/14.
 */
public class Note {
    int id;
    String path;
    String class;
    String subject;
    String plaintext;
    String keywords;
    String name;
    String created_at;
    String written_at;

    // constructors
    public Note() {
    }

    public Note(String note, int status) {
        this.note = note;
        this.status = status;
    }

    public Note(int id, String note, int status) {
        this.id = id;
        this.note = note;
        this.status = status;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setClass(String class) {
        this.class = class;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public void setPlainText(String plaintext) {
        this.plaintext = plaintext;
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCreatedAt(String created_at){
        this.created_at = created_at;
    }
    public void setWrittenAt(String written_at){
        this.written_at = written_at;
    }

    // getters
    public long getId() {
        return this.id;
    }
    public String getPath() {
        return this.path;
    }
    public String getClass() {
        return this.class;
    }
    public String getSubject() {
        return this.subject;
    }
    public String getPlainText() {
        return this.plaintext;
    }
    public String getKeywords() {
        return this.keywords;
    }
    public String getName() {
        return this.name;
    }
    public String getCreatedAt() {
        return this.created_at;
    }
    public String getWrittenAt() {
        return this.written_at;
    }
}
