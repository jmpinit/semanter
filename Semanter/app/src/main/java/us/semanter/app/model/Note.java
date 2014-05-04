package us.semanter.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable in-memory representation of note
 */
public class Note implements Parcelable {
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

    /**
     * Constructor for Parcelable
     */
    private Note(Parcel in) {
        date = new Date(in.readLong());
        progress = in.readInt();

        String[] tagNames = new String[in.readInt()];
        in.readStringArray(tagNames);

        tags = new HashSet<Tag>();
        for(String tagName: tagNames)
            tags.add(new Tag(tagName));
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

    /*
    Parcelable
    format:
    1) (long) Unix time stamp
    2) (int) progress
    3) (int) number of tags
    4..n) (
     */

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(date.getTime());
        out.writeInt(progress);

        out.writeInt(tags.size());
        String[] tagNames = new String[tags.size()];
        { int i = 0; for(Tag t: tags)
            tagNames[i++] = t.getValue(); }
        out.writeStringArray(tagNames);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
