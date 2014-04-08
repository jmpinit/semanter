package graphics.epi.vision;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Handles long term scheduling of background computer vision processing
 */
public class VisionExecutor implements Executor {
    final static String TAG = "VisionExecutor";

    private Map<Runnable, Object> jobs;

    public VisionExecutor() {
        jobs = new HashMap<Runnable, Object>();
    }

    public synchronized void execute(final Runnable r) {
        Thread executingThread = new Thread(r);
        executingThread.start();
    }

    public synchronized void execute(Object client, final Runnable r) {
        Thread executingThread = new Thread(r);
        executingThread.start();

        jobs.put(r, client);

        // TODO intelligent scheduling
        /*tasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });

        if(active == null) {
            scheduleNext();
        }*/
    }

    public Object getClient(VisionAction action) {
        return jobs.get(action);
    }
}
