package us.semanter.test.main.vision;

import android.net.Uri;
import android.test.AndroidTestCase;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import us.semanter.app.vision.result.FlattenerResult;
import us.semanter.app.vision.result.OutlineGuess;
import us.semanter.app.vision.util.Polygon;

public class EqualsTest extends AndroidTestCase {
    private final static int NUM_ITEMS = 32;

    public void testOutlineGuessEquals() {
        OutlineGuess a = Utils.randomOutline(NUM_ITEMS);
        OutlineGuess b = new OutlineGuess(a.getPolygon(), a.getConfidence());

        List<Point> cPoints = b.getPolygon().getPoints();
        cPoints.remove(0);

        OutlineGuess c = new OutlineGuess(new Polygon(cPoints), 0);

        // reflexivity
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));

        // symmetry
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        // inverse
        assertFalse(c.equals(a));
        assertFalse(c.equals(b));
    }

    public void testPolygonEquals() {
        Polygon a = Utils.randomPolygon(NUM_ITEMS);
        List<Point> aPoints = a.getPoints();

        List<Point> bPoints = new ArrayList<Point>();
        for(Point pt: aPoints)
            bPoints.add(pt.clone());

        Polygon b = new Polygon(bPoints);

        bPoints.remove(0);
        Polygon c = new Polygon(bPoints);

        // reflexivity
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));

        // symmetry
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        // inverse
        assertFalse(c.equals(a));
        assertFalse(c.equals(b));
    }

    public void testFlattenResultEquals() {
        List<OutlineGuess> outlines = Utils.randomOutlines(NUM_ITEMS);

        FlattenerResult a = new FlattenerResult(Uri.parse("/test/a/path/test.bmp"), outlines);
        FlattenerResult b = new FlattenerResult(Uri.parse("/test/a/path/test.bmp"), outlines);
        FlattenerResult c = new FlattenerResult(Uri.parse("/test/a/different/path/test.bmp"), outlines);

        outlines.remove(0);

        FlattenerResult d = new FlattenerResult(Uri.parse("/test/a/path/test.bmp"), outlines);

        // reflexivity
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));

        // symmetry
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        // inverse
        assertFalse(c.equals(a));
        assertFalse(c.equals(b));
        assertFalse(d.equals(a));
        assertFalse(d.equals(b));
    }
}
