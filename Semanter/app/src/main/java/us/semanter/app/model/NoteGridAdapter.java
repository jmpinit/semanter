package us.semanter.app.model;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import us.semanter.app.R;

public class NoteGridAdapter extends BaseAdapter {
    private Context mContext;
    private int resource;

    private List<Note> notes;

    public NoteGridAdapter(Context c, int resource, List<Note> notes) {
        mContext = c;
        this.resource = resource;

        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        inflater = LayoutInflater.from(mContext);
        ViewGroup thumbnailView = (ViewGroup) inflater.inflate(resource, null);

        // create
        Note note = (Note)getItem(position);

        if(note != null) {
            // adjust for GridView
            thumbnailView.setLayoutParams(new GridView.LayoutParams(85, 85));
            thumbnailView.setPadding(8, 8, 8, 8);

            // add data
            // TODO short date formatting
            DateFormat dateFormat = new SimpleDateFormat("E");

            ((TextView) thumbnailView.findViewById(R.id.thumbnail_note_date)).setText(dateFormat.format(note.getDate()));

            // TODO task numbers as enum
            ImageView thumbnailImage = (ImageView) thumbnailView.findViewById(R.id.thumbnail_note_image);

            File thumbnail = note.getThumbnail();
            if (thumbnail == null) {
                thumbnailImage.setImageResource(R.drawable.ic_progress_1);
            } else {
                thumbnailImage.setImageURI(Uri.parse(thumbnail.getPath()));
            }
        } else {
            thumbnailView.setVisibility(View.GONE);
        }

        return thumbnailView;
    }

    @Override
    public boolean  areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        /*if(position < notes.size()) {
            return notes.get(position).getResultCount() > 0;
        } else {
            return false;
        }*/
        return true;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < notes.size())
            return notes.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
