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
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionExecutor;
import graphics.epi.vision.VisionListener;
import graphics.epi.vision.operations.OpDeskew;

import static org.opencv.android.Utils.bitmapToMat;


public class DemoActivity extends ActionBarActivity implements VisionListener {
    private static final String TAG = "DemoActivity";

    final int REQ_CODE_PICK_IMAGE = 100;

    // data
    private VisionExecutor cvExecutor;
    private List<Note> notes;

    // views
    private ListView noteList;
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        cvExecutor = new VisionExecutor();

        notes = new ArrayList<Note>();

        noteList = (ListView)findViewById(R.id.list_note);
        noteListAdapter = new NoteListAdapter(this, R.layout.demo_row, notes);
        noteList.setAdapter(noteListAdapter);

        final Context context = this;
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        });
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
                rawView = inflater.inflate(R.layout.demo_row, null);
            }

            Note note = getItem(position);

            if(note != null) {
                ImageView thumbView = (ImageView) rawView.findViewById(R.id.note_thumbnail);
                ProgressBar progress = (ProgressBar) rawView.findViewById(R.id.demo_progress);

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
        notes.add(newNote);
        noteListAdapter.notifyDataSetChanged();
    }

    class Note {
        private String name;
        private Uri source, processed;
        private VisionAction processing;
        public Bitmap thumbnail;

        Note(String name, Uri source, VisionAction processing) {
            this.name = name;
            this.source = source;
            this.processing = processing;
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
