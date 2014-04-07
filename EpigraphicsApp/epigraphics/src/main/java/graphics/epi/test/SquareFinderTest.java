package graphics.epi.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.List;
import java.util.concurrent.RunnableFuture;

import graphics.epi.R;
import graphics.epi.utils.Geometry;
import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionExecutor;
import graphics.epi.vision.VisionListener;
import graphics.epi.vision.analyze.SquareFinder;

public class SquareFinderTest extends AndroidTestCase implements VisionListener {
    private final static String TAG = "SquareFinderTest"; // FIXME from class name

    boolean openCVLoaded;
    VisionExecutor executor;
    List<Geometry.Quad> squares = null;

    @Override
    public void setUp() {
        openCVLoaded = false;
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, mContext, mLoaderCallback);
        while(!openCVLoaded) {}

        executor = new VisionExecutor();
    }

    public void testSquareFinder() {
        BitmapFactory bmpFactory = new BitmapFactory();
        Bitmap image = bmpFactory.decodeResource(mContext.getResources(), R.drawable.phone_bad);

        assertNotNull(image);

        RunnableFuture task = new SquareFinder(this, image); // FIXME load image from resources
        executor.execute(task);

        while(squares == null) {}

        Log.d(TAG, "found " + squares.size() + " squares.");
        for(Geometry.Quad square: squares) {
            Log.d(TAG, square.toString());
        }
    }

    @Override
    public void OnVisionActionComplete(VisionAction action) {
        try {
            Bundle result = (Bundle)action.get();
            squares = result.getParcelableArrayList("squares");
        } catch(Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mContext) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    openCVLoaded = true;
                    Log.d(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
