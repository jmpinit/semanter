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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteFactory;
import us.semanter.app.model.NoteGridAdapter;
import us.semanter.app.vision.VisionService;
import us.semanter.app.vision.task.Normalizer;

public class SearchActivity extends ActionBarActivity {
    private GridView noteList;
    private NoteGridAdapter noteGridAdapter;

    private List<Note> notes;

    private final IntentFilter visionFilter = new IntentFilter(VisionService.ACTION_UPDATE);
    private final BroadcastReceiver visionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("SearchActivity", "I see an update of " + intent.getStringExtra(VisionService.EXTRA_SOURCE));

            // FIXME testing
            String taskName = intent.getStringExtra(VisionService.EXTRA_PIPE_NAME);
            if(taskName.equals(Normalizer.TASK_NAME)) {
                Intent imgIntent = new Intent();
                imgIntent.setAction(Intent.ACTION_VIEW);
                imgIntent.setDataAndType(Uri.parse("file://" + NoteFactory.getSourcePath(intent.getStringExtra(VisionService.EXTRA_SOURCE))), "image/*");
                startActivity(imgIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        notes = NoteFactory.getAllNotes(getExternalFilesDir(null).getPath() + "/notes");

        if(notes == null)
            notes = new ArrayList<Note>();

        noteList = (GridView)findViewById(R.id.search_note_grid);
        noteGridAdapter = new NoteGridAdapter(this, R.layout.thumbnail_note, notes);
        noteList.setAdapter(noteGridAdapter);

        final Intent reviewIntent = new Intent(this, ReviewActivity.class);
        final Context activityContext = this;
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                /*try {
                    reviewIntent.putExtra(getString(R.string.param_note), ((Note) noteGridAdapter.getItem(position)).toJSON().toString());
                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.e("SearchActivity", "Couldn't send note to ReviewActivity because of JSONException.");
                }
                reviewIntent.putExtra("task", VisionPipeline.Task.FLATTEN.getName());
                startActivity(reviewIntent);*/
            }
        });

        startService(new Intent(this, VisionService.class));
        registerReceiver(visionReceiver, visionFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            unregisterReceiver(visionReceiver);
        } catch(IllegalArgumentException e) {} // already unregistered
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            unregisterReceiver(visionReceiver);
        } catch(IllegalArgumentException e) {} // already unregistered
    }

    @Override
    public void onRestart() {
        super.onRestart();
        registerReceiver(visionReceiver, visionFilter);
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
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_import:
                importPhoto();
                break;
            case R.id.action_tag:
                Intent tagIntent = new Intent(this, TagActivity.class);
                String[] noteJSON = new String[notes.size()];

                try {
                    for (int i = 0; i < notes.size(); i++)
                        noteJSON[i] = notes.get(i).toJSON().toString();

                    tagIntent.putExtra(getString(R.string.param_notes), noteJSON);
                    startActivity(tagIntent);
                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.e("SearchActivity", "Couldn't send note because of JSONException.");
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
