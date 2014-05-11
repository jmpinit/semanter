package us.semanter.app.vision;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import us.semanter.app.model.NoteFactory;

/**
 * Defines an Immutable node in a tree of computer vision operations. The output of one node is the input of its children.
 * When a node finishes its operation on an image it updates the note that the image belongs to with its metadata
 * and notifies its listeners.
 */
public abstract class TaskNode {
    protected static final Bitmap.Config bmpConfig = Bitmap.Config.ARGB_8888;

    private final List<NodeListener> listeners;
    private final List<TaskNode> children;

    // identifier for this specific node.
    // important for referencing corrected tasks
    private String uid;

    private boolean hashCached;
    private int hash;

    public TaskNode(List<TaskNode> children) {
        this.listeners = new Vector<NodeListener>();
        this.children = new ArrayList<TaskNode>(children);

        hashCached = false;

        // must be called after adding children
        uid = generateUID();
    }

    public TaskNode() {
        this(new ArrayList<TaskNode>(0));
    }

    /**
     * Take an image, operate on it, and then pass the result to the children of the node.
     * @param sourcePath path of the image to operate on. It is assumed that the parent directory is for the note
     *                   the image is attached to.
     */
    public abstract void operateOn(String sourcePath);

    protected void dispatch(String outputPath) {
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
    private void dispatchTasks(String outputPath) {
        Log.d("TaskNode", "Dispatching tasks.");
        for(TaskNode child: children)
            child.operateOn(outputPath);
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
            for (TaskNode child : children)
                hash ^= child.hashCode();

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