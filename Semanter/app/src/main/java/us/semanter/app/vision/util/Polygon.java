package us.semanter.app.vision.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
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
public class Polygon implements JSONable {
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

    public Polygon(JSONObject json) {
        try {
            points = new ArrayList<Point>();

            for(int i = 0; json.has(""+i); i++) {
                JSONObject ptJSON = json.getJSONObject("" + i);
                points.add(new Point(ptJSON.getDouble("x"), ptJSON.getDouble("y")));
            }
        } catch (JSONException e) {
            Log.e(Polygon.class.getCanonicalName(), "Failed to construct because of JSONException. " + e.getMessage());
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        for(int i=0; i < points.size(); i++) {
            Point pt = points.get(i);

            JSONObject ptJson = new JSONObject();
            ptJson.put("x", pt.x);
            ptJson.put("y", pt.y);

            json.put(""+i, ptJson);
        }

        return json;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Polygon)) return false;

        Polygon otherPolygon = (Polygon)other;

        if(points.size() != otherPolygon.points.size()) return false;
        for(int i=0; i < points.size(); i++) {
            if(points.get(i).x != otherPolygon.points.get(i).x || points.get(i).y != otherPolygon.points.get(i).y)
                return false;
        }

        return true;
    }
}
