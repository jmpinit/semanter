package us.semanter.test.main.vision;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import us.semanter.app.vision.result.OutlineGuess;
import us.semanter.app.vision.util.Polygon;

public class Utils {
    public static Polygon randomPolygon(int num) {
        return new Polygon(randomPoints(num));
    }

    public static OutlineGuess randomOutline(int num) {
        return new OutlineGuess(randomPolygon(num), (float)Math.random());
    }

    public static List<OutlineGuess> randomOutlines(int num) {
        Random generator = new Random();
        int length = 1 + generator.nextInt(num);

        List<OutlineGuess> outlines = new ArrayList<OutlineGuess>();
        for(int i = 0; i < length; i++) {
            outlines.add(randomOutline(num));
        }

        return outlines;
    }

    public static List<Polygon> randomPolygons(int num) {
        Random generator = new Random();
        int length = 1 + generator.nextInt(num);

        List<Polygon> polygons = new ArrayList<Polygon>();
        for(int i = 0; i < length; i++) {
            polygons.add(randomPolygon(num));
        }

        return polygons;
    }

    public static List<Point> randomPoints(int num) {
        Random generator = new Random();
        int length = 1 + generator.nextInt(num);

        List<Point> points = new ArrayList<Point>();
        for(int i = 0; i < length; i++) {
            points.add(new Point(generator.nextDouble(), generator.nextDouble()));
        }

        return points;
    }
}
