package us.semanter.app.vision.task;

import org.opencv.core.Mat;

import java.util.List;

import us.semanter.app.vision.TaskNode;
import us.semanter.app.vision.VisionUtil;

public class Normalizer extends TaskNode {
    private static final String TASK_NAME = "normalize";

    public Normalizer() {
        super();
    }

    public Normalizer(List<TaskNode> children) {
        super(children);
    }

    // operation parameters (potentially subject to correction)
    // TODO

    public void operateOn(String sourcePath) {
        Mat source = VisionUtil.matFromFile(sourcePath);

        // TODO normalize image

        VisionUtil.saveMat(source, bmpConfig, getResultPath(sourcePath).toString());

        dispatch(sourcePath);
    }

    public String getTaskName() {
        return TASK_NAME;
    }
}
