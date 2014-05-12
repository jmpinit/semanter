package us.semanter.app.vision.task;

import android.content.Context;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

import us.semanter.app.vision.TaskNode;
import us.semanter.app.vision.VisionUtil;

public class Thumbnailer extends TaskNode {
    public final static String TASK_NAME = "thumbnail";

    public Thumbnailer(Context ctx, TaskNode parent) {
        super(ctx, parent);
    }
    public Thumbnailer(Context ctx, TaskNode parent, TaskNode task) { super(ctx, parent, task); }
    public Thumbnailer(Context ctx, TaskNode parent, List<TaskNode> children) {
        super(ctx, parent, children);
    }

    @Override
    public void operateOn(String parentID, String sourcePath) {
        Mat source = VisionUtil.matFromFile(sourcePath);

        Mat thumbnail = new Mat(96, 96, CvType.CV_8UC3);
        Imgproc.resize(source, thumbnail, new Size(thumbnail.width(), thumbnail.height()));

        // save in result tree
        saveResult(new File(sourcePath), parentID, thumbnail);

        dispatch(sourcePath);
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }
}
