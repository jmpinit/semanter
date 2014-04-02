package graphics.epi.vision;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

public class OpThreshold implements RunnableFuture {
    private Bitmap source, result;
    private boolean finished, cancelled;

    public OpThreshold(Bitmap source) {
        this.source = source;
        finished = false;
        cancelled = false;
    }

    public void run() {
        Log.d("threshold", "running cv code");

        // image into OpenCV
        Mat imageMat = new Mat();
        bitmapToMat(source, imageMat);
        if(cancelled) return;

        // grayscale image
        Mat grayMat = new Mat();
        cvtColor(imageMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        if(cancelled) return;

        // threshold image
        Mat threshMat = new Mat();
        threshold(grayMat, threshMat, 128, 255, Imgproc.THRESH_BINARY);
        if(cancelled) return;

        // image out of OpenCV
        result = Bitmap.createBitmap(source);
        matToBitmap(threshMat, result);

        finished = true;

        Log.d("threshold", "done cv code");
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        cancelled = true;
        finished = true;

        return isCancelled();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return finished;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        while(result == null) { }
        Log.d("threshold", "gotten");
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long milliTimeout = unit.toMillis(timeout);
        long startTime = System.currentTimeMillis();

        // if not done, wait for a bit
        while(result != null) {
            if(System.currentTimeMillis() - startTime > milliTimeout) {
                throw new TimeoutException();
            }
        }

        return result;
    }
}
