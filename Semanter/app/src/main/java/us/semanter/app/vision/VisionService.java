package us.semanter.app.vision;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;

import us.semanter.app.model.NoteFactory;
import us.semanter.app.vision.task.Flattener;
import us.semanter.app.vision.task.Normalizer;
import us.semanter.app.vision.task.Thumbnailer;

/**
 * Processes images through a vision pipeline, saves the results, and notifies bound listeners.
 */
public class VisionService extends IntentService {
    private static final String ACTION_IMPORT = "us.semanter.app.vision.action.IMPORT";
    private static final String ACTION_CORRECT = "us.semanter.app.vision.action.CORRECT";

    public static final String ACTION_UPDATE = "us.semanter.app.vision.action.UPDATE";
    public static final String ACTION_THUMBNAIL = "us.semanter.app.vision.action.THUMBNAIL";

    public static final String EXTRA_CHANGE_PATH = "us.semanter.app.vision.extra.PHOTO";
    public static final String EXTRA_THUMBNAIL = "us.semanter.app.vision.extra.THUMBNAIL";
    public static final String EXTRA_NOTE_NAME = "us.semanter.app.vision.extra.NOTE_NAME";
    public static final String EXTRA_PIPE_NAME = "us.semanter.app.vision.extra.PIPE_NAME";
    private static final String EXTRA_ALTERNATIVE = "us.semanter.app.vision.extra.ALTERNATIVE";

    private TaskNode rootOperation;
    private boolean cvReady;

    public VisionService() {
        super("VisionService");
        cvReady = false;
    }

    /**
     * Imports photo into Vision system by throwing it into the root Pipe.
     * @param sourcePath path to photo to import
     */
    public static void startActionImport(Context context, String sourcePath) {
        Intent intent = new Intent(context, VisionService.class);
        intent.setAction(ACTION_IMPORT);
        intent.putExtra(EXTRA_CHANGE_PATH, sourcePath);
        context.startService(intent);
    }

    /**
     * Propagates correction to a task parameter through the vision network.
     */
    public static void startActionCorrect(Context context, String sourcePath, String pipeID, String alternative) {
        Intent intent = new Intent(context, VisionService.class);
        intent.setAction(ACTION_CORRECT);
        intent.putExtra(EXTRA_CHANGE_PATH, sourcePath);
        intent.putExtra(EXTRA_PIPE_NAME, pipeID);
        intent.putExtra(EXTRA_ALTERNATIVE, alternative);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d("VisionService", "action is " + action);
            if (ACTION_IMPORT.equals(action)) {
                final String sourcePath = intent.getStringExtra(EXTRA_CHANGE_PATH);
                handleActionImport(sourcePath);
            } else if (ACTION_CORRECT.equals(action)) {
                final String sourcePath = intent.getStringExtra(EXTRA_CHANGE_PATH);
                final String pipeName = intent.getStringExtra(EXTRA_PIPE_NAME);
                final String alternative = intent.getStringExtra(EXTRA_ALTERNATIVE);
                handleActionCorrect(sourcePath, pipeName, alternative);
            }
        }
    }

    private void handleActionImport(final String sourcePath) {
        while(!cvReady) {}
        Log.d("VisionService", "Importing " + sourcePath);
        rootOperation.operateOn(null, sourcePath);
    }

    private void handleActionCorrect(String sourcePath, String pipeName, String alternative) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, openCVLoaderCallback);
    }

    @Override
    public void onDestroy() {
        Log.d("VisionService", "onDestroy()");
    }

    private BaseLoaderCallback openCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("VisionService", "OpenCV loaded successfully");

                    // construct the initial operations carried out by the service
                    Context ctx = getApplicationContext();

                    TaskNode flatten = new Flattener(getApplicationContext(), null);
                    TaskNode flattenThumbnail = new Thumbnailer(ctx, flatten);

                    TaskNode normalize = new Normalizer(ctx, flatten);
                    TaskNode normalizeThumbnail = new Thumbnailer(ctx, normalize);

                    // connect operations
                    flatten.addChild(normalize);
                    flatten.addChild(flattenThumbnail);

                    normalize.addChild(normalizeThumbnail);

                    rootOperation = flatten;

                    TaskNode.NodeListener changeListener = new TaskNode.NodeListener() {
                        @Override
                        public void onTaskCompleted(String taskName, String changePath) {
                            Log.d("VisionService", "Finished modifying " + changePath + " successfully.");

                            Intent changeNotification = new Intent();
                            changeNotification.setAction(ACTION_UPDATE);
                            changeNotification.putExtra(EXTRA_CHANGE_PATH, changePath);
                            changeNotification.putExtra(EXTRA_PIPE_NAME, taskName);
                            changeNotification.putExtra(EXTRA_NOTE_NAME, NoteFactory.getNoteName(getApplicationContext(), new File(changePath)));
                            sendBroadcast(changeNotification);
                        }
                    };

                    flatten.registerListener(changeListener);
                    normalize.registerListener(changeListener);

                    TaskNode.NodeListener thumbnailListener = new TaskNode.NodeListener() {
                        @Override
                        public void onTaskCompleted(String taskName, String changePath) {
                            Log.d("VisionService", "Sending thumbnail " + changePath);
                            Intent changeNotification = new Intent();
                            changeNotification.setAction(ACTION_THUMBNAIL);
                            changeNotification.putExtra(EXTRA_THUMBNAIL, changePath);
                            changeNotification.putExtra(EXTRA_NOTE_NAME, NoteFactory.getNoteName(getApplicationContext(), new File(changePath)));
                            sendBroadcast(changeNotification);
                        }
                    };

                    flattenThumbnail.registerListener(thumbnailListener);
                    normalizeThumbnail.registerListener(thumbnailListener);

                    cvReady = true;
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
