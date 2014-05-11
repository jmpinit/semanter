package us.semanter.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import us.semanter.app.model.Tag;

public class TagEditor extends EditText {
    private List<Listener> listeners;

    public TagEditor(Context context, AttributeSet attrs) {
        super(context, attrs);

        listeners = new Vector<Listener>();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            dispatchNew(allToTags());
            setText("");
        } else if(keyCode == KeyEvent.KEYCODE_SPACE) {
            // FIXME (never fires)
            Log.d("TagEditor", getSelectionStart() + ", " + getText().length());
            if(getSelectionStart() == getText().length() - 1) { // at end of field?
                dispatchNew(allToTags());
                setText("");
            }
        }

        return false;
    }

    private List<Tag> allToTags() {
        // FIXME no whitespace tags

        String text = getText().toString();

        int numSpaces = countMatches(text, " ");
        ArrayList<Tag> tags = new ArrayList<Tag>(numSpaces+1);

        String[] parts = text.split(" ");
        for(String rawName: parts)
            tags.add(new Tag(rawName.trim()));

        return tags;
    }

    private int countMatches(String text, String match) {
        return text.length() - text.replace(match, "").length();
    }

    /**
     * Event Source
     */

    private void dispatchNew(Tag tag) {
        Log.d("TagEditor", tag.getValue());
        for(Listener listener: listeners) {
            listener.onNewTag(tag);
        }
    }

    private void dispatchNew(List<Tag> tags) {
        for(Tag tag: tags)
            dispatchNew(tag);
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public interface Listener {
        public void onNewTag(Tag newTag);
    }
}
