package us.semanter.app.model;

import android.util.Log;

/**
 * Updates a reference to a note on modification events.
 */
public class NoteModifier {
    private Holder<Note> noteHolder;

    public NoteModifier(Note note) {
        this.noteHolder = new Holder<Note>(note);
    }

    public Note getNote() {
        return noteHolder.getValue();
    }

    public final TagListener listener = new TagListener() {
        @Override
        public void onNewTag(Tag tag) {
            Log.d("NoteModifier", "new tag.");
            Log.d("NoteModifier", noteHolder.getValue().getTags().toString());
            noteHolder.setValue(noteHolder.getValue().addTag(tag));
            Log.d("NoteModifier", noteHolder.getValue().getTags().toString());
        }

        @Override
        public void onRemoveTag(Tag tag) {
            noteHolder.setValue(noteHolder.getValue().removeTag(tag));
        }
    };
}
