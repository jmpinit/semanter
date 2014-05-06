package us.semanter.app.vision;

import org.json.JSONException;
import org.json.JSONObject;

import us.semanter.app.vision.result.FlattenerResult;
import us.semanter.app.vision.util.VisionResult;

public class VisionResultFactory {
    public static VisionResult fromJSON(JSONObject json) throws JSONException {
        String taskName = json.getString("task_name");

        if(taskName.equals(FlattenerResult.TASK_NAME)) {
            return (VisionResult)(new FlattenerResult(json));
        } else {
            throw new JSONException("Unrecognized task name.");
        }
    }
}