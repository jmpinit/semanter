package us.semanter.test.main.vision;

import android.net.Uri;
import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import us.semanter.app.vision.result.FlattenerResult;
import us.semanter.app.vision.result.OutlineGuess;
import us.semanter.app.vision.util.Polygon;

/**
 * Test the JSON conversion of JSONable objects
 */
public class JSONTest extends AndroidTestCase {
    private final static int NUM_ITEMS = 4;

    public void testFlattenResultJSON() {
        Uri testUri = Uri.parse("/test/some/file/path");
        List<OutlineGuess> testOutlines = Utils.randomOutlines(NUM_ITEMS);

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

    public void testOutlineGuessJSON() {
        OutlineGuess guess = new OutlineGuess(Utils.randomPolygon(NUM_ITEMS), (float)Math.random());

        String json = "";
        try {
            json = guess.toJSON().toString();
            OutlineGuess guessFromJSON = new OutlineGuess(new JSONObject(json));
            assertTrue(guess.equals(guessFromJSON));
        } catch(JSONException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testPolygonJSON() {
        Polygon polygon = Utils.randomPolygon(NUM_ITEMS);

        String json = "";
        try {
            json = polygon.toJSON().toString();
            Polygon polygonFromJSON = new Polygon(new JSONObject(json));
            assertTrue("\n" + json + "\n" + polygonFromJSON.toJSON().toString(), polygon.equals(polygonFromJSON));
        } catch(JSONException e) {
            assertTrue(e.getMessage(), false);
        }
    }
}
