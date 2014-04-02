package graphics.epi.vision;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;

/**
 * Handles long term scheduling of background computer vision processing
 */
public class VisionExecutor implements Executor {
    final static String TAG = "VisionExecutor";

    final Queue tasks = new ArrayDeque();
    Runnable active;

    public synchronized void execute(final Runnable r) {
        Thread executingThread = new Thread(r);
        executingThread.start();

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

    protected synchronized void scheduleNext() {
        if((active = (Runnable)tasks.poll()) != null) {
            execute(active);
        }
    }
}
