package graphics.epi.vision.operations;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import graphics.epi.utils.Geometry;
import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionListener;
import graphics.epi.vision.analyze.SquareFinder;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

public class OpDeskew extends VisionOp implements VisionListener {
    private final static String TAG = "OpDeskew"; // TODO from class

    private List<Geometry.Quad> squares;

    public OpDeskew(VisionListener caller, Bitmap source) {
        super(caller, source);
    }

    public void run() {
        // find paper
        (new Thread(new SquareFinder(this, source))).start();

        // image into OpenCV
        Mat imageMat = new Mat();
        bitmapToMat(source, imageMat);
        if(cancelled) return;

        // wait for squares data
        while(squares == null) { if(cancelled) return; }

        // TODO indicate failure, don't just return
        if(squares.size() == 0) {
            // did not find squares
            finish();
            return;
        }

        // get largest and assume it is the notes
        Geometry.Quad notePage = Geometry.Quad.largest(squares);

        // calculate deskew transform
        final double paperWidth = 8.5*50;
        final double paperHeight = 11*50;

        Mat sourcePerspective = notePage.toMatOfPoint2f();
        Mat destinationPerspective = new MatOfPoint2f(
            new Point(paperWidth, 0),
            new Point(paperWidth, paperHeight),
            new Point(0, paperHeight),
            new Point(0, 0)
        );

        Mat transformation = Imgproc.getPerspectiveTransform(sourcePerspective, destinationPerspective);

        // apply transform
        Mat deskewed = new Mat();
        Imgproc.warpPerspective(imageMat, deskewed, transformation, new Size(imageMat.width(), imageMat.height()));

        // image out of OpenCV
        result = Bitmap.createBitmap(source);
        matToBitmap(deskewed, result);

        finish();
    }

    @Override
    public void OnVisionActionComplete(VisionAction op) {
        try {
            squares = ((Bundle) op.get()).getParcelableArrayList("squares");
        } catch(Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
