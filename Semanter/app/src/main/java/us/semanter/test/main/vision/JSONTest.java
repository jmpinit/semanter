package us.semanter.test.main.vision;

import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import us.semanter.app.vision.result.OutlineGuess;
import us.semanter.app.vision.util.Polygon;

/**
 * Test the JSON conversion of JSONable objects
 */
public class JSONTest extends AndroidTestCase {
    private final static int NUM_ITEMS = 4;

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
