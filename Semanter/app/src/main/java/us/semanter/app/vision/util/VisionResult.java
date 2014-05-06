package us.semanter.app.vision.util;

import android.net.Uri;

public interface VisionResult extends JSONable {
    public Uri getPrior();
    public String getTaskName();
}
