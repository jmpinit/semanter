package us.semanter.app.vision.util;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Immutable
 */
public class Polygon {
    private List<Point> points;

    public Polygon(Point[] points) {
        this.points = new ArrayList(Arrays.asList(points));
    }

    public Polygon(List<Point> points) {
        this.points = new ArrayList<Point>(points);
    }

    public List<Point> getPoints() {
        return points;
    }

    public MatOfPoint toMatOfPoint() {
        return new MatOfPoint(points.toArray(new Point[0])); // FIXME don't leak rep
    }

    public MatOfPoint2f toMatOfPoint2f() {
        return new MatOfPoint2f(points.toArray(new Point[0])); // FIXME don't leak rep
    }

    public static Polygon largest(List<Polygon> polygons) {
        Polygon biggest = null;

        double biggestArea = 0;
        for(Polygon poly: polygons) {
            double area = Imgproc.contourArea(poly.toMatOfPoint());

            if(area > biggestArea) {
                biggest = poly;
                biggestArea = area;
            }
        }

        return biggest;
    }
}
