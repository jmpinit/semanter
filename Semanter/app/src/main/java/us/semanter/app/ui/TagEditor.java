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
    List<TagListener> listeners;

    public TagEditor(Context context, AttributeSet attrs) {
        super(context, attrs);

        listeners = new Vector<TagListener>();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            dispatchAll(allToTags());
            setText("");
        } else if(keyCode == KeyEvent.KEYCODE_SPACE) {
            Log.d("TagEditor", getSelectionStart() + ", " + getText().length());
            if(getSelectionStart() == getText().length() - 1) { // at end of field?
                dispatchAll(allToTags());
                setText("");
            }
        }

        return false;
    }

    private List<Tag> allToTags() {
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

    private void dispatch(Tag tag) {
        Log.d("TagEditor", tag.getValue());
        for(TagListener listener: listeners) {
            listener.onNewTag(tag);
        }
    }

    private void dispatchAll(List<Tag> tags) {
        for(Tag tag: tags) dispatch(tag);
    }

    public void registerListener(TagListener listener) {
        listeners.add(listener);
    }

    public interface TagListener {
        public void onNewTag(Tag newTag);
    }
}
