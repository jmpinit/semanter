package us.semanter.app.vision;

import java.util.HashMap;
import java.util.Map;

import us.semanter.app.ui.VisionView;
import us.semanter.app.ui.review.FlattenerView;

public class VisionPipeline {
    public static enum Task {
        FLATTEN("flatten", 0),
        NORMALIZE("normalize", 1),
        SEGMENT("segment", 2),
        WRITING_RECOGNIZE("writing_recognize", 3);

        private String name;
        private int position;

        Task(String name, int position) {
            this.name = name;
            this.position = position;
        }

        public String getName() { return name; }
        public int getPosition() { return position; }
    }

    public static final Map<Integer, Task> TASK_POSITION = new HashMap<Integer, Task>();
    {
        TASK_POSITION.put(0, Task.FLATTEN);
        TASK_POSITION.put(1, Task.NORMALIZE);
        TASK_POSITION.put(2, Task.SEGMENT);
        TASK_POSITION.put(3,  Task.WRITING_RECOGNIZE);
    };

    public static final Map<String, Class<? extends VisionView>> REVIEWERS = new HashMap<String, Class<? extends VisionView>>();
    {
        REVIEWERS.put(Task.FLATTEN.getName(), FlattenerView.class);
    }

    public static Class<? extends VisionView> reviewerFromName(String name) {
        return REVIEWERS.get(name);
    }
}
