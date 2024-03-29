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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.Base64;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import graphics.epi.filesystemtree.Folder;
import graphics.epi.filesystemtree.Items;
import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionExecutor;
import graphics.epi.vision.VisionListener;
import graphics.epi.vision.operations.OpDeskew;

import static org.opencv.android.Utils.bitmapToMat;

public class OverviewActivity extends FragmentActivity
        implements FileSystemFragment.FileSystemCallbacks, VisionListener {

    private static final String JSON_FILENAME = "notedata.json";
    static final String TAG = "semanter";

    // data
    private VisionExecutor cvExecutor;
    private List<Note> notes;

    // views
    private ListView noteList;
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragOp = fragmentManager.beginTransaction();
        fragOp.replace(R.id.container, FileSystemFragment.newInstance(new Folder(null, "/", demoArray))).commit();

        /*=================================
        =            New Stuff            =
        =================================*/
        cvExecutor = new VisionExecutor();

        notes = new ArrayList<Note>();


//        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Note selectedNote = (Note)noteList.getItemAtPosition(position);
//
//                if(selectedNote.isReady()) {
//                    Intent intent = new Intent(OverviewActivity.this, NoteViewActivity.class);
//
//                    Bundle b = new Bundle();
//                    b.putParcelable("image_path", selectedNote.getProcessed());
//                    intent.putExtras(b);
//
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast failureMessage = new Toast(context);
//                    failureMessage.setDuration(Toast.LENGTH_SHORT);
//                    failureMessage.setText("Processing not completed");
//                    failureMessage.show();
//                }
//            }
//
//        });
//
        
        /*-----  End of New Stuff  ------*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview, menu);
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

    public JSONArray getJsonArray(ArrayList<Note> notes) {
        JSONArray jsonArray = new JSONArray();

        for (Note note : notes) {
            jsonArray.put(note.toJson());
        }

        return jsonArray;
    }

    public void save(ArrayList<Note> notes) {
        File jsonFile = new File(getFilesDir(), JSON_FILENAME);
        JSONArray noteArray = getJsonArray(notes);

        //if(!jsonFile.exists()) {
            try {
                // create the file
                FileOutputStream jsonStream = new FileOutputStream(jsonFile);
                JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(jsonStream, "UTF-8"));

                jsonWriter.value(noteArray.toString());

                jsonWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
      //  }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode != RESULT_OK) return;

        switch(requestCode) {
            case 100: importPhoto(imageReturnedIntent); break;
        }
    }

    @Override
    public void fileSystemInteraction(Items next) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (next.isFile()) {
            //TODO: handle file
            System.out.println(next.toString());
            launchView(next.toString());
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FileSystemFragment.newInstance((Folder) next))
                    .commit();
        }
    }

    private void launchView(String name) {
        Log.d("launchView", "Launching View...");
        Log.d("Note Name", name);
    }

    /*
    Note List
     */
    class NoteListAdapter extends ArrayAdapter<Note> {
        public NoteListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public NoteListAdapter(Context context, int resource, List<Note> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View rawView, ViewGroup parent) {
            if(rawView == null) {
                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                rawView = inflater.inflate(R.layout.overview_row, null);
            }

            Note note = getItem(position);

            if(note != null) {
                ImageView thumbView = (ImageView) rawView.findViewById(R.id.note_thumbnail);
                ProgressBar progress = (ProgressBar) rawView.findViewById(R.id.overview_progress);

                thumbView.setImageBitmap(note.thumbnail);

                thumbView.setEnabled(note.isReady());
                if(note.isReady()) {
                    progress.setVisibility(View.GONE);
                }
            }

            return rawView;
        }
    }

    /*
    Imports
     */

    public void importPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 100);
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
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Note newNote = new Note(imageURI.toString(), imageURI, processing, date);

        // make thumbnail
        newNote.thumbnail = Bitmap.createScaledBitmap(selectedImage, (int)(0.80*noteList.getWidth()), 64, false);

        // start processing
        cvExecutor.execute(newNote, processing);

        // add to list of notes
        notes.add(newNote);
        noteListAdapter.notifyDataSetChanged();
    }

    class Note {
        private String name;
        private Uri source, processed;
        private VisionAction processing;
        private String date;
        public Bitmap thumbnail;

        Note(String name, Uri source, VisionAction processing, String date) {
            this.name = name;
            this.source = source;
            this.processing = processing;
            this.date = date;
        }

        public JSONObject toJson(){
            JSONObject object = new JSONObject();
            try {
                object.put("name", this.name);
                object.put("source", this.source);
                object.put("date", this.date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }


        public String getName() {
            return name;
        }

        public boolean isReady() {
            return processing.isDone();
        }

        public Uri getProcessed() {
            return processed;
        }

        public void processingFinished() {
            try {
                Bitmap result = (Bitmap) processing.get();

                // generate filename
                byte[] hashBytes = ByteBuffer.allocate(4).putInt(result.hashCode()).array();
                processed = Uri.parse("/sdcard/Download/" + Base64.encodeToString(hashBytes, Base64.URL_SAFE).trim() + ".png");

                // save to file
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(processed.getPath());
                    result.compress(Bitmap.CompressFormat.PNG, 90, out);
                    Log.d(TAG, "saved to " + processed);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } finally {
                    try{
                        out.close();
                    } catch(Throwable ignore) {}
                }

                double sx = noteList.getWidth();
                double sy = (noteList.getWidth() / result.getWidth()) * result.getHeight();
                interestingThumbnail(Bitmap.createScaledBitmap(result, (int)sx, (int)sy, false));
            } catch(Exception e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }

        private void interestingThumbnail(Bitmap image) {
            // image into OpenCV
            Mat imageMat = new Mat();
            bitmapToMat(image, imageMat);

            // find slice of interest
            /*Mat rowSums = new Mat(imageMat.height(), 1, CvType.CV_16U);
            for(int y = (int)(0.1*image.getHeight()); y < image.getHeight(); y++) {
                Mat row = new Mat(imageMat, new Rect(0, 0, imageMat.width(), imageMat.height()));
                rowSums.put(y, 1, (int)Core.sumElems(row).val[0]);
            }

            final int binSize = 32;
            double current = 0;
            int startPos = 0;
            for(int y = 0; y < rowSums.cols() - binSize; y++) {
                Mat binned = new Mat(rowSums, new Rect(y, 0, binSize, 1));
                double average = Core.sumElems(binned).val[0] / binSize;

                if(average > current)
                    startPos = y;
                current = average;
            }

            // ensure not out of bounds
            if(startPos > imageMat.height() - binSize) startPos = imageMat.height() - binSize;

            // get slice of interest
            Rect roi = new Rect(0, startPos, imageMat.width(), binSize);
            Mat croppedRef = new Mat(imageMat, roi);
            Mat croppedMat = new Mat(croppedRef.width(), croppedRef.height(), croppedRef.type());
            croppedRef.copyTo(croppedMat);*/

            // image out of OpenCV
            //thumbnail = Bitmap.createBitmap(roi.width, roi.height, image.getConfig());
            thumbnail = Bitmap.createBitmap(imageMat.width(), 64, image.getConfig());
            thumbnail.eraseColor(Color.argb(255, (int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
            //matToBitmap(croppedRef, thumbnail);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void OnVisionActionComplete(VisionAction op) {
        Log.d(TAG, "op completed");

        Note client = (Note)cvExecutor.getClient(op);
        client.processingFinished();

        final NoteListAdapter adapter = noteListAdapter;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

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