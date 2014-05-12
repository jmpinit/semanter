package us.semanter.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteFactory;
import us.semanter.app.model.NoteGridAdapter;
import us.semanter.app.model.Tag;
import us.semanter.app.model.TagListener;
import us.semanter.app.ui.TagView;
import us.semanter.app.vision.VisionService;

public class SearchActivity extends ActionBarActivity {
    private GridView noteList;
    private NoteGridAdapter noteGridAdapter;
    private TagView tags;

    private List<Note> notes;
    private List<Note> notesInList;

    private final IntentFilter visionFilter = new IntentFilter(VisionService.ACTION_UPDATE);
    private BroadcastReceiver visionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        notesInList = new ArrayList<Note>();

        tags = (TagView)findViewById(R.id.search_tags);
        tags.registerListener(new TagListener() {
            @Override
            public void onNewTag(Tag tag) { filterNotes(); }

            @Override
            public void onRemoveTag(Tag tag) { filterNotes(); }
        });

        noteList = (GridView)findViewById(R.id.search_note_grid);
        noteGridAdapter = new NoteGridAdapter(this, R.layout.thumbnail_note, notesInList);
        noteList.setAdapter(noteGridAdapter);

        loadNotes();

        final Intent reviewIntent = new Intent(this, ReviewActivity.class);
        final Context ctx = this;
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String notePath = reviewIntent.getStringExtra(VisionService.EXTRA_SOURCE);
                Intent reviewIntent = new Intent(ctx, ReviewActivity.class);
                reviewIntent.putExtra("note", notePath);
                startActivity(reviewIntent);
            }
        });

        visionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("SearchActivity", "I see an update of " + intent.getStringExtra(VisionService.EXTRA_SOURCE));
                loadNotes();
            }
        };

        startService(new Intent(this, VisionService.class));
        registerReceiver(visionReceiver, visionFilter);
    }

    private void filterNotes() {
        Set<Tag> filterTags = tags.getTags();

        for (Note note : notes) {
            boolean visible = true;

            Set<Tag> noteTags = note.getTags();
            for (Tag tag : filterTags) {
                if (!noteTags.contains(tag)) {
                    visible = false;
                    break;
                }
            }

            if (visible) {
                if (!notesInList.contains(note))
                    notesInList.add(note);
            } else {
                notesInList.remove(note);
            }
        }

        noteGridAdapter.notifyDataSetChanged();
    }

    private void loadNotes() {
        NoteFactory.initStorage(this);

        // TODO only on change reload all notes

        if(notes == null)
            notes = new ArrayList<Note>();
        else
            notes.clear();

        //notes = NoteFactory.getAllNotes(getExternalFilesDir(null).getPath() + "/notes");
        notes = NoteFactory.getAllNotes(this);

        notesInList.clear();
        notesInList.addAll(notes);
        filterNotes();
        Log.d("SearchActivity", "Loaded notes.");
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            unregisterReceiver(visionReceiver);
        } catch(IllegalArgumentException e) {} // already unregistered
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            unregisterReceiver(visionReceiver);
        } catch(IllegalArgumentException e) {} // already unregistered
    }

    @Override
    public void onStart() {
        super.onStart();
        loadNotes();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        registerReceiver(visionReceiver, visionFilter);
        loadNotes();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode != RESULT_OK) return;

        switch(requestCode) {
            case 100: importPhoto(imageReturnedIntent); break;
        }
    }

    public void importPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 100);
    }

    public void importPhoto(Intent imageIntent) {
        Uri imageURI = imageIntent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(imageURI, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        // create note out of image
        try {
            String noteSourcePath = NoteFactory.createNewNote(this, filePath).toString();
            Note newNote = NoteFactory.noteFromPath(noteSourcePath);

            notes.add(newNote);
            noteGridAdapter.notifyDataSetChanged();

            // start processing
            VisionService.startActionImport(this, noteSourcePath);
        } catch(IOException e) {
            e.printStackTrace();
            Log.e("SearchActivity", "Couldn't create directory to store note.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                importPhoto();
                break;
            case R.id.action_tag:
                Intent tagIntent = new Intent(this, TagActivity.class);
                startActivity(tagIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
