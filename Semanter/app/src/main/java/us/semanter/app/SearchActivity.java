package us.semanter.app;

import android.content.Intent;
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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteListAdapter;
import us.semanter.app.vision.task.Flattener;
import us.semanter.app.vision.task.TaskListener;
import us.semanter.app.vision.util.VisionResult;

public class SearchActivity extends ActionBarActivity implements TaskListener {
    private GridView noteList;
    private NoteListAdapter noteListAdapter;
    private Intent reviewIntent;

    private List<Note> notes;
    private Map<Runnable, Note> taskMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        notes = new ArrayList<Note>();
        taskMap = new HashMap<Runnable, Note>();

        noteList = (GridView)findViewById(R.id.search_note_grid);
        noteListAdapter = new NoteListAdapter(this, R.layout.thumbnail_note, notes);
        noteList.setAdapter(noteListAdapter);

        reviewIntent = new Intent(this, ReviewActivity.class);
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                reviewIntent.putExtra(getString(R.string.param_note), (Note)noteListAdapter.getItem(position));
                startActivity(reviewIntent);
            }
        });
    }

    @Override
    public void onTaskCompleted(final Runnable task, final VisionResult result) {
        runOnUiThread(new Runnable() {
            public void run() {
                Note note = taskMap.get(task);
                taskMap.remove(task);

                notes.remove(note);
                notes.add(note.progress(result));
                noteListAdapter.notifyDataSetChanged();
            }
        });
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

        // TODO use EXIF for date
        Note newNote = new Note(new Date());

        Flattener flattener = new Flattener(Uri.parse(filePath));
        flattener.registerListener(this);

        taskMap.put(flattener, newNote);
        notes.add(newNote);
        noteListAdapter.notifyDataSetChanged();

        // start processing
        (new Thread(flattener)).start();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("SearchActivity", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
