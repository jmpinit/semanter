package graphics.epi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import graphics.epi.filesystemtree.Folder;
import graphics.epi.filesystemtree.Items;
import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionExecutor;
import graphics.epi.vision.VisionListener;
import graphics.epi.vision.operations.OpDeskew;

import static org.opencv.android.Utils.bitmapToMat;

public class NavigatorActivity extends ActionBarActivity implements VisionListener, FileSystemFragment.FileSystemCallbacks {
    private static final String TAG = "NavigatorActivity";

    private static final String JSON_FILENAME = "notedata.json";

    final int REQ_CODE_PICK_IMAGE = 100;

    // data
    private VisionExecutor cvExecutor;
    private List<Note> notes;

    // data
    private File jsonFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        cvExecutor = new VisionExecutor();

        notes = new ArrayList<Note>();

        /*noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = (Note)noteList.getItemAtPosition(position);

                if(selectedNote.isReady()) {
                    Intent intent = new Intent(DemoActivity.this, NoteViewActivity.class);

                    Bundle b = new Bundle();
                    b.putParcelable("image_path", selectedNote.getProcessed());
                    intent.putExtras(b);

                    startActivity(intent);
                    finish();
                } else {
                    Toast failureMessage = new Toast(context);
                    failureMessage.setDuration(Toast.LENGTH_SHORT);
                    failureMessage.setText("Processing not completed");
                    failureMessage.show();
                }
            }
        });*/

        ArrayList<String> demoArray = new ArrayList<String>();

        demoArray.add("/file1");
        demoArray.add("/18.06/file2");
        demoArray.add("/18.06/file3");
        demoArray.add("/18.100c/file4");
        demoArray.add("/18.100c/file5");
        demoArray.add("/18.100c/day1/file8");
        demoArray.add("/18.100c/day2/file9");
        demoArray.add("/18.100c/day2/hour5/file10");
        demoArray.add("/file6");
        Log.d("array",demoArray.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragOp = fragmentManager.beginTransaction();
        fragOp.replace(R.id.container, FileSystemFragment.newInstance(new Folder(null, "/", demoArray))).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.demo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode != RESULT_OK) return;

        switch(requestCode) {
            case REQ_CODE_PICK_IMAGE: importPhoto(imageReturnedIntent); break;
        }
    }

    @Override
    public void fileSystemInteraction(Items next) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (next.isFile()) {
            //TODO: handle file
            System.out.println(next.toString());
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FileSystemFragment.newInstance((Folder) next))
                    .commit();
        }
    }

    /*
    Imports
     */

    public void importPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
    }

    public void importPhoto(Intent imageReturnedIntent) {
        Uri imageURI = imageReturnedIntent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(imageURI, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

        // create a new note
        VisionAction processing = new OpDeskew(this, selectedImage);
        Note newNote = new Note(imageURI.toString(), imageURI, processing);

        // make thumbnail
        newNote.thumbnail = Bitmap.createScaledBitmap(selectedImage, (int)(0.80*noteList.getWidth()), 64, false);

        // start processing
        cvExecutor.execute(newNote, processing);

        // add to list of notes
        //notes.add(newNote);
        //noteListAdapter.notifyDataSetChanged();
    }

    /*
    Computer Vision
     */

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void OnVisionActionComplete(VisionAction op) {
        Note client = (Note)cvExecutor.getClient(op);
        client.processingFinished();

        /*final NoteListAdapter adapter = noteListAdapter;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });*/

        // TODO result dispatcher
        /*try {
            final Bitmap resultImage = (Bitmap) op.get();
            Log.d(TAG, "got result");

            // display result
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView resultView = (ImageView) findViewById(R.id.img_raw); // FIXME should be img_result
                    resultView.setImageBitmap(resultImage);
                }
            });
        } catch(InterruptedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch(ExecutionException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }*/
    }
}
