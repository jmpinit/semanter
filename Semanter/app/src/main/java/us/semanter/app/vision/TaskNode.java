package us.semanter.app.vision;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteFactory;

/**
 * Defines an Immutable node in a tree of computer vision operations. The output of one node is the input of its children.
 * When a node finishes its operation on an image it updates the note that the image belongs to with its metadata
 * and notifies its listeners.
 */
public abstract class TaskNode {
    protected static final Bitmap.Config bmpConfig = Bitmap.Config.ARGB_8888;

    private Context context;
    private final List<NodeListener> listeners;
    private final TaskNode parent;
    private final List<TaskNode> children;

    private boolean used; // flag for immutability after first usage

    // identifier for this specific node.
    // important for referencing corrected tasks
    private String uid;

    private boolean hashCached;
    private int hash;

    public TaskNode(Context ctx, TaskNode parent, List<TaskNode> children) {
        this.context = ctx;
        this.parent = parent;
        this.listeners = new Vector<NodeListener>();
        this.children = new ArrayList<TaskNode>(children);

        hashCached = false;

        used = false;

        // must be called after adding children
        uid = generateUID();
    }

    public TaskNode(Context ctx, TaskNode parent, TaskNode task) {
        this(ctx, parent, Arrays.asList(new TaskNode[]{task}));
    }

    public TaskNode(Context ctx, TaskNode parent) {
        this(ctx, parent, new ArrayList<TaskNode>(0));
    }

    public void addChild(TaskNode child) {
        if(used)
            throw new RuntimeException("Children cannot be added to TaskNode after it's first used, to preserve immutability.");
        else
            this.children.add(child);
    }

    public void addChildren(List<TaskNode> children) {
        if(used)
            throw new RuntimeException("Children cannot be added to TaskNode after it's first used, to preserve immutability.");
        else
            this.children.addAll(children);    }

    /**
     * Take an image, operate on it, and then pass the result to the children of the node.
     * @param sourcePath path of the image to operate on. It is assumed that the parent directory is for the note
     *                   the image is attached to.
     */
    public abstract void operateOn(String parentID, String sourcePath);

    public void saveResult(File source, String parentID, Mat result) {
        File resultPath = getResultPath(source.toString());
        VisionUtil.saveMat(result, bmpConfig, resultPath.getPath());

        Log.d("Vision", getUID() + " operated and produced " + resultPath + " after " + parentID);

        Note note = NoteFactory.noteFromPath(source.getPath());
        Note moddedNote = note.addResult(parentID, new Note.Result(getUID(), getTaskName(), resultPath, null));
        NoteFactory.saveMeta(context, moddedNote);
    }

    protected void dispatch(String outputPath) {
        used = true;
        dispatchEvents(getTaskName(), NoteFactory.getNotePath(outputPath));
        dispatchTasks(outputPath);
    }

    /**
     * @param notePath the directory of the note that was updated.
     */
    private void dispatchEvents(String taskName, String notePath) {
        for(NodeListener listener: listeners) {
            listener.onTaskCompleted(taskName, notePath);
        }
    }

    /**
     * @param outputPath the path of the image output of this node's operation.
     */
    private void dispatchTasks(final String outputPath) {
        Log.d("TaskNode", "Dispatching tasks.");
        for(final TaskNode child: children) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    child.operateOn(uid, outputPath);
                }
            })).start();
        }
    }

    // add listener to this node. notified on op completion.
    // note on immutability: listeners are not used for anything.
    // information flows only from a node to a listener, so they should be safe for immutability
    public void registerListener(NodeListener listener) {
        listeners.add(listener);
    }

    // add listener to every node downstream
    // note on immutability: listeners are not used for anything.
    // information flows only from a node to a listener, so they should be safe for immutability
    public void registerListenerForAll(NodeListener listener) {
        registerListener(listener);
        for(TaskNode child: children)
            child.registerListenerForAll(listener);
    }

    // must be called after the node is constructed, because it depends
    // on the structure of the tree.
    private String generateUID() {
        return getTaskName() + "-" + hashCode();
    }

    /*
    Getters
     */

    public abstract String getTaskName();

    public String getUID() {
        return uid;
    }

    public File getResultPath(String sourcePath) {
        File sourceFile = new File(sourcePath);
        File noteFolder = new File(sourceFile.getParent());

        File result = new File(noteFolder + "/" + noteFolder.getName() + "-" + getUID() + ".png");

        return result;
    }

    @Override
    public int hashCode() {
        if(this.hashCached) {
            return hash;
        } else {
            int hash = getTaskName().hashCode();

            if(parent != null)
                hash ^= parent.hashCode();

            // cache the result (safe because the tree is immutable)
            this.hash = hash;

            return hash;
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TaskNode)) return false;
        return ((TaskNode)other).hashCode() == this.hashCode();
    }

    public interface NodeListener {
        /**
         * @param changePath the filesystem location of the note that was updated
         */
        public void onTaskCompleted(String taskName, String changePath);
    }
}