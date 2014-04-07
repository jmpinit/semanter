package graphics.epi.db.model;

public class Note {
    int id;
    String path;
    String classname;
    String subject;
    String plaintext;
    String keywords;
    String name;
    String created_at;
    String written_at;
    // constructors
    public Note() {
    }

    public Note(String path, String classname, String subject, String plaintext, String keywords, String name, String created_at, String written_at) {
        this.path = path;
        this.classname = classname;
        this.subject = subject;
        this.plaintext = plaintext;
        this.keywords = keywords;
        this.name = name;
        this.created_at = created_at;
        this.written_at = written_at;
    }

    public Note(int id, String path, String classname, String subject, String plaintext, String keywords, String name, String created_at, String written_at) {
        this.id = id;
        this.path = path;
        this.classname = classname;
        this.subject = subject;
        this.plaintext = plaintext;
        this.keywords = keywords;
        this.name = name;
        this.created_at = created_at;
        this.written_at = written_at;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setClassName(String classname) {
        this.classname = classname;
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
    public String getClassName() {
        return this.classname;
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