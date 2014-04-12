package graphics.epi.vision.operations;

/**
 * Dummy vision task that takes a long time
 * to test background processing
 */

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import graphics.epi.vision.VisionListener;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

public class OpDummyLong extends VisionOp {
    private static final String TAG = OpDummyLong.class.getCanonicalName();
    private static final int timeLength = 20000; // how long the task will take in millis

    public OpDummyLong(VisionListener caller, Bitmap source) {
        super(caller, source);
    }

    public void run() {
        // image into OpenCV
        Mat imageMat = new Mat();
        bitmapToMat(source, imageMat);
        pause(timeLength/4);
        if(cancelled) return;

        // grayscale image
        Mat grayMat = new Mat();
        cvtColor(imageMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        pause(timeLength/4);
        if(cancelled) return;

        // threshold image
        Mat threshMat = new Mat();
        threshold(grayMat, threshMat, 128, 255, Imgproc.THRESH_BINARY);
        pause(timeLength/4);
        if(cancelled) return;

        // image out of OpenCV
        result = Bitmap.createBitmap(source);
        matToBitmap(threshMat, result);
        pause(timeLength/4);

        finish();
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
