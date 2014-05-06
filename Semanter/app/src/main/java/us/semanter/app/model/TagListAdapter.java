package us.semanter.app.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import us.semanter.app.R;
import us.semanter.app.ui.TagView;

public class TagListAdapter extends ArrayAdapter<Note> {
    private int resource;

    public TagListAdapter(Context c, int resource, List<Note> notes) {
        super(c, resource, notes);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup tagRow;

        Note note = (Note)getItem(position);

        // create
        LayoutInflater inflater;
        inflater = LayoutInflater.from(getContext());
        tagRow = (ViewGroup)inflater.inflate(resource, null);

        // add data
        // TODO short date formatting
        DateFormat dateFormat = new SimpleDateFormat("E");

        ((TextView)tagRow.findViewById(R.id.thumbnail_note_date)).setText(dateFormat.format(note.getDate()));
        TagView tags = ((TagView)tagRow.findViewById(R.id.row_tag_tags));

        for(Tag tag: note.getTags()) {
            try {
                tags.addTag(tag);
            } catch(Exception e) {
                Log.e("TagListAdapter", "Couldn't add tag - already exists.");
                e.printStackTrace();
            }
        }

        // TODO task numbers as enum
        ImageView progressImage = (ImageView)tagRow.findViewById(R.id.thumbnail_note_progress);
        switch(note.getResultCount()) {
            case 0:
                progressImage.setImageResource(R.drawable.ic_progress_1);
                break;
            case 1:
                progressImage.setImageResource(R.drawable.ic_progress_2);
                break;
            case 2:
                progressImage.setImageResource(R.drawable.ic_progress_3);
                break;
            case 3:
                progressImage.setImageResource(R.drawable.ic_progress_4);
                break;
            case 4:
                ((ViewSwitcher)tagRow.findViewById(R.id.thumbnail_note_switcher)).showNext();
                break;
        }

        return tagRow;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position < getCount()) {
            return getItem(position).getResultCount() > 0;
        } else {
            return false;
        }
    }
}
