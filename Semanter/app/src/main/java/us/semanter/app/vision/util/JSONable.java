package us.semanter.app.vision.util;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONable {
    public JSONObject toJSON() throws JSONException;
}
