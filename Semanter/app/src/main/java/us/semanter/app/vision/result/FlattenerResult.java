package us.semanter.app.vision.result;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import us.semanter.app.vision.util.JSONable;
import us.semanter.app.vision.util.VisionResult;

/**
 * Immutable results of flattening
 */
public class FlattenerResult implements VisionResult, JSONable {
    public static final String TASK_NAME = "flatten";
    private static final String KEY_PRIOR = "prior";
    private static final String KEY_OUTLINES = "outlines";

    private Uri prior;
    private List<OutlineGuess> outlines;

    public FlattenerResult(Uri prior, List<OutlineGuess> outlines) {
        this.prior = prior;
        this.outlines = new ArrayList<OutlineGuess>(outlines);
    }

    public String getTaskName() {
        return TASK_NAME;
    }

    public Uri getPrior() {
        return prior;
    }

    public List<OutlineGuess> getOutlines() {
        return new ArrayList<OutlineGuess>(outlines);
    }

    /**
     * JSON
     */

    public FlattenerResult(JSONObject json) {
        try {
            prior = Uri.parse(json.getString(KEY_PRIOR));

            outlines = new ArrayList<OutlineGuess>();
            JSONObject outlinesJSON = json.getJSONObject(KEY_OUTLINES);

            for(int i = 0; outlinesJSON.has(""+i); i++) {
                outlines.add(new OutlineGuess(outlinesJSON.getJSONObject("" + i)));
            }
        } catch(JSONException e) {
            Log.e("Flattener", "Failed to construct because of JSONException. " + e.getMessage());
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject outlinesJSON = new JSONObject();

        for(int i = 0; i < outlines.size(); i++) {
            OutlineGuess outline = outlines.get(i);
            outlinesJSON.put("" + i, outline.toJSON());
        }

        JSONObject json = new JSONObject();
        json.put("prior", prior.toString());
        json.put("outlines", outlinesJSON);

        return json;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof FlattenerResult)) return false;

        FlattenerResult otherFlattenerResult = (FlattenerResult)other;
        if(!prior.getPath().equals(otherFlattenerResult.prior.getPath())) return false;
        if(!outlines.equals(otherFlattenerResult.outlines)) return false;

        return true;
    }

    /**
     * Constructor for Parcelable
     */
    private FlattenerResult(Parcel in) {
        try {
            JSONObject json = new JSONObject(in.readString());

            prior = Uri.parse(json.getString(KEY_PRIOR));

            outlines = new ArrayList<OutlineGuess>();
            JSONObject outlinesJSON = json.getJSONObject(KEY_OUTLINES);

            for(int i = 0; outlinesJSON.has(""+i); i++) {
                outlines.add(new OutlineGuess(outlinesJSON.getJSONObject("" + i)));
            }
        } catch(JSONException e) {
            Log.e("Flattener", "Failed to construct because of JSONException. " + e.getMessage());
        }
    }

        /*
        Parcelable
        format:
        1) (String) prior
        2) (int) size of confidenceMap
        3..) (int, float) index of polygon,
        3) (int) number of tags
        4..n) (String) tag
        n..) (Bundle) result
         */

    @Override
    public void writeToParcel(Parcel out, int flags) {
        try {
            out.writeString(toJSON().toString());
        } catch(JSONException e) {
            Log.e(FlattenerResult.class.getCanonicalName(), "Failed to write to parcel because of JSONException. " + e.getMessage());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<FlattenerResult> CREATOR = new Parcelable.Creator<FlattenerResult>() {
        public FlattenerResult createFromParcel(Parcel in) {
            return new FlattenerResult(in);
        }
        public FlattenerResult[] newArray(int size) {
            return new FlattenerResult[size];
        }
    };
}
