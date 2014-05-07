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
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteGridAdapter;
import us.semanter.app.vision.VisionService;

public class SearchActivity extends ActionBarActivity {
    private GridView noteList;
    private NoteGridAdapter noteGridAdapter;

    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
    }

    @Override
    public void onStart() {
        super.onStart();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("SearchActivity", "I see an update of " + intent.getStringExtra(VisionService.EXTRA_SOURCE));
            }
        }, new IntentFilter(VisionService.ACTION_UPDATE));
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

        notes.add(newNote);
        noteGridAdapter.notifyDataSetChanged();

        // start processing
        VisionService.startActionImport(this, filePath);
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
