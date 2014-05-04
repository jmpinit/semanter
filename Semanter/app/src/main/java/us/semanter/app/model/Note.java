package us.semanter.app.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable in-memory representation of note
 */
public class Note {
    private final Date date;
    private final Set<Tag> tags;
    private int progress;

    public Note(Date date, Set<Tag> tags) {
        this.date = new Date(date.getTime());
        this.tags = new HashSet<Tag>(tags);
        this.progress = 0;
    }

    public Note(Date date, List<Tag> tags) {
        this.date = new Date(date.getTime());
        this.tags = new HashSet<Tag>(tags);
        this.progress = 0;
    }

    public Note nextTask() {
        Note newNote = new Note(this.date, this.tags);
        newNote.progress = progress + 1;
        return newNote;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public Set<Tag> getTags() {
        return new HashSet<Tag>(tags);
    }

    public int getProgress() {
        return progress;
    }
}
