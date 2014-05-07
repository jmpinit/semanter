package us.semanter.app.vision.task;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import us.semanter.app.vision.TaskNode;
import us.semanter.app.vision.VisionUtil;
import us.semanter.app.vision.util.Polygon;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static org.opencv.imgproc.Imgproc.approxPolyDP;
import static org.opencv.imgproc.Imgproc.arcLength;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.isContourConvex;
import static org.opencv.imgproc.Imgproc.medianBlur;
import static org.opencv.imgproc.Imgproc.threshold;

public class Flattener extends TaskNode {
    private static final String TASK_NAME = "flatten";

    // operation parameters (potentially subject to correction)
    // TODO allow corrections to be specified
    private static final int SQUARE_SIZE = 25000; // FIXME train on this somehow
    private static final double THRESHOLD_COS = 0.05; // FIXME train on this somehow

    public Flattener() {
        super();
    }

    public Flattener(List<TaskNode> children) {
        super(children);
    }

    public void operateOn(String sourcePath) {
        Mat source = VisionUtil.matFromFile(sourcePath);

        // find paper
        List<Polygon> squares = findSquares(source);

        if(squares.size() == 0) {
            // make no change
            VisionUtil.saveMat(source, bmpConfig, getResultPath(sourcePath).toString());
        } else {
            // get largest and assume it is the notes
            Polygon notePage = Polygon.largest(squares);

            Log.d("Flattener", notePage.toString());

            // calculate deskew transform
            final double paperWidth = 8.5 * 50;
            final double paperHeight = 11 * 50;

            Mat sourcePerspective = notePage.toMatOfPoint2f();
            Mat destinationPerspective = new MatOfPoint2f(
                    new Point(paperWidth, 0),
                    new Point(paperWidth, paperHeight),
                    new Point(0, paperHeight),
                    new Point(0, 0)
            );

            Mat transformation = Imgproc.getPerspectiveTransform(sourcePerspective, destinationPerspective);

            // apply transform
            Mat flattened = new Mat();
            Imgproc.warpPerspective(source, flattened, transformation, new Size(source.width(), source.height()));

            // crop to just note
            Rect noteRegion = new Rect(0, 0, (int) paperWidth, (int) paperHeight);
            Mat croppedRef = new Mat(flattened, noteRegion);

            // save result
            VisionUtil.saveMat(croppedRef, bmpConfig, getResultPath(sourcePath).toString());

            Log.d("Flattener", "Saved result to " + getResultPath(sourcePath));
        }

        dispatch(getResultPath(sourcePath).toString());
    }

    private List<Polygon> findSquares(Mat image) {
        ArrayList<Polygon> squares;

        Mat bwImage = new Mat();
        cvtColor(image, bwImage, COLOR_RGB2GRAY);

        Mat blurred = new Mat();
        medianBlur(image, blurred, 9);

        int width = blurred.width();
        int height = blurred.height();
        int depth = blurred.depth();

        Mat gray0 = new Mat(width, height, depth);
        blurred.copyTo(gray0);

        squares = new ArrayList<Polygon>();

        // find squares in every color plane of the image
        for (int c = 0; c < 3; c++) {
            Core.mixChannels(Arrays.asList(blurred), Arrays.asList(new Mat[]{gray0}), new MatOfInt(c, 0));

            // try several threshold levels
            int thresholdLevel = 8;
            for(int l = 0; l < thresholdLevel; l++) {
                // use canny instead of 0 threshold level
                // canny helps catch squares with gradient shading
                Mat gray = new Mat();

                if(l == 0) {
                    Canny(gray0, gray, 10.0, 20.0, 3, false);
                    Mat kernel = new Mat(11, 11, CvType.CV_8UC1, new Scalar(1));
                    dilate(gray, gray, kernel);
                } else {
                    Mat thresh = new Mat(gray0.rows(), gray0.cols(), gray0.type());
                    threshold(gray0, thresh, ((double)l)/thresholdLevel*255, 128, THRESH_BINARY_INV);
                    cvtColor(thresh, gray, COLOR_BGR2GRAY);
                }

                // find contours and store them in a list
                List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                findContours(gray, contours, new Mat(), RETR_LIST, CHAIN_APPROX_SIMPLE);

                // test contours
                for (MatOfPoint contour : contours) {
                    // approximate contour with accuracy proportional to the contour perimeter
                    MatOfPoint2f thisContour = new MatOfPoint2f(contour.toArray());
                    double arcLength = 0.02 * arcLength(thisContour, true);
                    MatOfPoint2f approx = new MatOfPoint2f();
                    approxPolyDP(thisContour, approx, arcLength, true);

                    double area = contourArea(approx);
                    boolean isConvex = isContourConvex(new MatOfPoint(approx.toArray()));

                    if (approx.rows() == 4 && Math.abs(area) > SQUARE_SIZE && isConvex) {
                        double maxCosine = 0;

                        Point[] approxArray = approx.toArray();
                        for (int j = 2; j < 5; j++) {
                            double cosine = Math.abs(angle(approxArray[j % 4], approxArray[j - 2], approxArray[j - 1]));
                            maxCosine = Math.max(maxCosine, cosine);
                        }

                        if (maxCosine > THRESHOLD_COS) {
                            squares.add(new Polygon(approxArray));
                            Log.d("Flattener", "area = " + area);
                        }
                    }
                }
            }
        }

        return squares;
    }

    public String getTaskName() {
        return TASK_NAME;
    }

    private double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;

        return (dx1*dx2 + dy1*dy2)/Math.sqrt(Math.abs((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10));
    }
}
