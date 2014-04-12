package graphics.epi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import graphics.epi.filesystemtree.Folder;
import graphics.epi.filesystemtree.Items;
import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionListener;

public class OverviewActivity extends FragmentActivity
        implements FileSystemFragment.FileSystemCallbacks, VisionListener {

    static final String TAG = "semanter";

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_import) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        // http://stackoverflow.com/a/6147919
        Log.d(TAG, "Activity sees result.");
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void OnVisionActionComplete(VisionAction op) {
        Log.d(TAG, "op completed");

        // TODO result dispatcher
        try {
            final Bitmap resultImage = (Bitmap) op.get();
            Log.d(TAG, "got result");

            // display result
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView resultView = (ImageView) findViewById(R.id.img_raw); // FIXME should be img_result
                    resultView.setImageBitmap(resultImage);
                }
            });*/
        } catch(InterruptedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch(ExecutionException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}