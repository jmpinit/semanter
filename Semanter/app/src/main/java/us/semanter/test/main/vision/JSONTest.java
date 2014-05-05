package us.semanter.test.main.vision;

import android.net.Uri;
import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import us.semanter.app.vision.result.FlattenerResult;
import us.semanter.app.vision.result.OutlineGuess;
import us.semanter.app.vision.util.Polygon;

/**
 * Test the JSON conversion of JSONable objects
 */
public class JSONTest extends AndroidTestCase {
    public void flattenResultJSONTest() {
        Uri testUri = Uri.parse("/test/some/file/path");
        List<OutlineGuess> testOutlines = randomOutlines();

        FlattenerResult result = new FlattenerResult(testUri, testOutlines);

        String json = "";
        try {
            json = result.toJSON().toString();
            FlattenerResult resultFromJSON = new FlattenerResult(new JSONObject(json));
            assertTrue(result.equals(resultFromJSON));
        } catch(JSONException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void outlineGuessTest() {
        OutlineGuess guess = new OutlineGuess(randomPolygon(), (float)Math.random());

        String json = "";
        try {
            json = guess.toJSON().toString();
            OutlineGuess guessFromJSON = new OutlineGuess(new JSONObject(json));
            assertTrue(guess.equals(guessFromJSON));
        } catch(JSONException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void polygonJSONTest() {

    }

    private Polygon randomPolygon() {
        return new Polygon(randomPoints());
    }

    private OutlineGuess randomOutline() {
        return new OutlineGuess(randomPolygon(), (float)Math.random());
    }

    private List<OutlineGuess> randomOutlines() {
        Random generator = new Random();
        int length = generator.nextInt();

        List<OutlineGuess> outlines = new ArrayList<OutlineGuess>();
        for(int i = 0; i < length; i++) {
            outlines.add(randomOutline());
        }

        return outlines;
    }

    private List<Polygon> randomPolygons() {
        Random generator = new Random();
        int length = generator.nextInt();

        List<Polygon> polygons = new ArrayList<Polygon>();
        for(int i = 0; i < length; i++) {
            polygons.add(randomPolygon());
        }

        return polygons;
    }

    private List<Point> randomPoints() {
        Random generator = new Random();
        int length = generator.nextInt();

        List<Point> points = new ArrayList<Point>();
        for(int i = 0; i < length; i++) {
            points.add(new Point(generator.nextDouble(), generator.nextDouble()));
        }

        return points;
    }
}
