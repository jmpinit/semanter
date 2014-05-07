package us.semanter.app.vision;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;

import us.semanter.app.vision.task.Flattener;
import us.semanter.app.vision.task.Normalizer;

/**
 * Processes images through a vision pipeline, saves the results, and notifies bound listeners.
 */
public class VisionService extends IntentService {
    private static final String ACTION_IMPORT = "us.semanter.app.vision.action.IMPORT";
    private static final String ACTION_CORRECT = "us.semanter.app.vision.action.CORRECT";

    public static final String ACTION_UPDATE = "us.semanter.app.vision.action.UPDATE";

    public static final String EXTRA_SOURCE = "us.semanter.app.vision.extra.PHOTO";
    private static final String EXTRA_NOTE = "us.semanter.app.vision.extra.NOTE";
    private static final String EXTRA_PIPE_NAME = "us.semanter.app.vision.extra.PIPE_NAME";
    private static final String EXTRA_ALTERNATIVE = "us.semanter.app.vision.extra.ALTERNATIVE";

    private TaskNode rootOperation;

    public VisionService() {
        super("VisionService");

        // construct the initial operation tree carried out by the service.
        TaskNode normalize = new Normalizer();
        TaskNode flatten = new Flattener(Arrays.asList(new TaskNode[] {normalize}));

        rootOperation = flatten;

        rootOperation.registerListenerForAll(new TaskNode.NodeListener() {
            @Override
            public void onTaskCompleted(String changePath) {
                Log.d("VisionService", "Finished modifying " + changePath + " succesfully.");

                Intent changeNotification = new Intent();
                changeNotification.setAction(ACTION_UPDATE);
                changeNotification.putExtra(EXTRA_SOURCE, changePath);
                sendBroadcast(changeNotification);
            }
        });
    }

    /**
     * Imports photo into Vision system by throwing it into the root Pipe.
     * @param sourcePath path to photo to import
     */
    public static void startActionImport(Context context, String sourcePath) {
        Intent intent = new Intent(context, VisionService.class);
        intent.setAction(ACTION_IMPORT);
        intent.putExtra(EXTRA_SOURCE, sourcePath);
        context.startService(intent);
    }

    /**
     * Propagates correction to a task parameter through the vision network.
     */
    public static void startActionCorrect(Context context, String sourcePath, String pipeID, String alternative) {
        Intent intent = new Intent(context, VisionService.class);
        intent.setAction(ACTION_CORRECT);
        intent.putExtra(EXTRA_SOURCE, sourcePath);
        intent.putExtra(EXTRA_PIPE_NAME, pipeID);
        intent.putExtra(EXTRA_ALTERNATIVE, alternative);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d("VisionService", "" + action);
            if (ACTION_IMPORT.equals(action)) {
                final String sourcePath = intent.getStringExtra(EXTRA_SOURCE);
                handleActionImport(sourcePath);
            } else if (ACTION_CORRECT.equals(action)) {
                final String sourcePath = intent.getStringExtra(EXTRA_SOURCE);
                final String pipeName = intent.getStringExtra(EXTRA_PIPE_NAME);
                final String alternative = intent.getStringExtra(EXTRA_ALTERNATIVE);
                handleActionCorrect(sourcePath, pipeName, alternative);
            }
        }
    }

    private void handleActionImport(final String sourcePath) {
        rootOperation.operateOn(sourcePath);
    }

    private void handleActionCorrect(String sourcePath, String pipeName, String alternative) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void onDestroy() {
        Log.d("VisionService", "onDestroy()");
    }
}
