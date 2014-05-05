package us.semanter.app.vision.util;

import android.net.Uri;
import android.os.Parcelable;

public interface VisionResult extends Parcelable {
    public Uri getPrior();
    public String getTaskName();
}
