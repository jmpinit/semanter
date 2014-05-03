package us.semanter.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import us.semanter.app.R;
import us.semanter.app.model.Tag;

public class TagView extends LinearLayout implements TagEditor.TagListener {
    private Context context;
    private LinearLayout tagLayout;
    private TagEditor editorView;

    private Set<Tag> tags;

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        // UI

        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tag, this, true);

        tagLayout = (LinearLayout)findViewById(R.id.tag_selector_scroll_layout);
        editorView = (TagEditor)findViewById(R.id.tag_editor_text);
        editorView.registerListener(this);

        // Data

        tags = new HashSet<Tag>();
    }

    public TagView(Context context) {
        this(context, null);
    }

    private void addTag(Tag t) throws TagExistsException {
        // ui
        LayoutInflater inflater = LayoutInflater.from(context);
        Button button = (Button) inflater.inflate(R.layout.tag, null, false);

        button.setText(t.getValue());
        addView(button, 0);

        // data
        if(!tags.add(t))
            throw new TagExistsException();
    }

    public void onNewTag(Tag newTag) {
        Log.d("TagView", newTag.getValue());

        try {
            addTag(newTag);
        } catch(TagExistsException e) {
            Toast.makeText(context, "tag already added", Toast.LENGTH_SHORT);
        }
    }

    public Set getTags() {
        return tags;
    }

    static class TagExistsException extends Exception {
        public TagExistsException() {
            super();
        }
    }
}
