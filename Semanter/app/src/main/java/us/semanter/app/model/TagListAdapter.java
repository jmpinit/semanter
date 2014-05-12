package us.semanter.app.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import us.semanter.app.R;
import us.semanter.app.ui.TagView;

public class TagListAdapter extends ArrayAdapter<NoteModifier> {
    private int resource;

    public TagListAdapter(Context c, int resource, List<NoteModifier> modifiers) {
        super(c, resource, modifiers);
        this.resource = resource;
    }

    @Override
    public android.view.View getView(final int position, android.view.View convertView, ViewGroup parent) {
        ViewGroup tagRow;

        NoteModifier modifier = (NoteModifier)getItem(position);
        Note note = modifier.getNote();

        // create
        LayoutInflater inflater;
        inflater = LayoutInflater.from(getContext());
        tagRow = (ViewGroup)inflater.inflate(resource, null);

        if(note != null) {
            // add data
            // TODO short date formatting
            DateFormat dateFormat = new SimpleDateFormat("E");

            ((TextView) tagRow.findViewById(R.id.thumbnail_note_date)).setText(dateFormat.format(note.getDate()));
            TagView tags = ((TagView) tagRow.findViewById(R.id.row_tag_tags));
            tags.registerListener(modifier.listener);

            for (Tag tag : note.getTags()) {
                try {
                    tags.addTag(tag);
                } catch (Exception e) {
                    Log.e("TagListAdapter", "Couldn't add tag - already exists.");
                    e.printStackTrace();
                }
            }

            // TODO task numbers as enum
            ImageView thumbnailImage = (ImageView)tagRow.findViewById(R.id.thumbnail_note_image);

            File thumbnail = note.getThumbnail();
            if(thumbnail == null)
                thumbnailImage.setImageResource(R.drawable.ic_progress_1);
            else
                thumbnailImage.setImageURI(Uri.parse(thumbnail.getPath()));
        }

        return tagRow;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
        /*if (position < getCount()) {
            return getItem(position).getNote().getResultCount() > 0;
        } else {
            return false;
        }*/
    }
}
