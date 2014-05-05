package us.semanter.app.vision.result;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import us.semanter.app.vision.util.JSONable;
import us.semanter.app.vision.util.Polygon;

public class OutlineGuess implements JSONable {
    private Polygon polygon;
    private float confidence;

    public OutlineGuess(Polygon poly, float confidence) {
        this.polygon = poly;
        this.confidence = confidence;
    }

    public OutlineGuess(JSONObject json) {
        try {
            JSONObject polygonJSON = json.getJSONObject("polygon");
            polygon = new Polygon(polygonJSON);
            confidence = (float)json.getDouble("confidence");
        } catch (JSONException e) {
            Log.e(OutlineGuess.class.getCanonicalName(), "Failed to construct because of JSONException. " + e.getMessage());
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("polygon", polygon.toJSON());
        json.put("confidence", confidence);

        return json;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public float getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof OutlineGuess)) return false;

        OutlineGuess otherOutlineGuess = (OutlineGuess)other;

        if(!polygon.equals(otherOutlineGuess.polygon)) return false;
        if(confidence != otherOutlineGuess.confidence) return false;

        return true;
    }
}
