package us.semanter.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.semanter.app.R;
import us.semanter.app.model.Tag;
import us.semanter.app.model.TagListener;

public class TagView extends LinearLayout implements TagEditor.Listener {
    private Context context;
    private LinearLayout tagLayout;
    private TagEditor editorView;

    private Set<Tag> tags;
    private Map<Tag, Button> tagButtons;

    private List<TagListener> listeners;

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);

        listeners = new ArrayList<TagListener>();

        this.context = context;

        // UI

        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(inflater.inflate(R.layout.view_tag, this, false));

        tagLayout = (LinearLayout)findViewById(R.id.tag_selector_scroll_layout);
        editorView = (TagEditor)findViewById(R.id.tag_editor_text);
        editorView.registerListener(this);

        // Data

        tags = new HashSet<Tag>();
        tagButtons = new HashMap<Tag, Button>();
    }

    public TagView(Context context) {
        this(context, null);
    }

    public void addTag(final Tag tag) throws TagExistsException {
        if(tags.add(tag)) {
            LayoutInflater inflater = LayoutInflater.from(context);
            Button button = (Button) inflater.inflate(R.layout.tag, null, false);

            button.setText(tag.getValue());
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    removeTag(tag);
                }
            });

            tagLayout.addView(button, 0);
            tagButtons.put(tag, button);
        } else {
            throw new TagExistsException();
        }
    }

    private synchronized void removeTag(Tag tag) {
        tags.remove(tag);
        tagLayout.removeView(tagButtons.get(tag));
        dispatchRemove(tag);
    }

    public void onNewTag(Tag newTag) {
        try {
            addTag(newTag);
            dispatchNew(newTag);
        } catch(TagExistsException e) {
            Toast.makeText(context, "tag already added", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchNew(Tag tag) {
        for(TagListener listener: listeners) {
            listener.onNewTag(tag);
        }
    }

    private void dispatchRemove(Tag tag) {
        for(TagListener listener: listeners) {
            listener.onRemoveTag(tag);
        }
    }

    public void registerListener(TagListener listener) {
        listeners.add(listener);
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
