package us.semanter.app.vision.task;

import us.semanter.app.vision.util.VisionResult;

public interface TaskListener {
    public void onTaskCompleted(Runnable task, VisionResult result);
}
