package us.semanter.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.semanter.app.vision.util.JSONable;

/**
 * Immutable in-memory representation of note
 */
public class Note implements JSONable {
    public final static String FIELD_NAME = "name";
    public final static String FIELD_DATE = "date";
    public final static String FIELD_THUMBNAIL = "thumbnail";
    public final static String FIELD_LAST = "last";
    public final static String FIELD_TAGS = "tags";
    public final static String FIELD_RESULT = "result";

    public final static String RESULT_LAST = "last";
    public final static String RESULT_THUMBNAIL = "thumb";

    private final String name;
    private final Date date;
    private File thumbnail;
    private File last;
    private final Set<Tag>  tags;
    private Result result;

    // TODO constants for JSON fields

    public Note(String name, Date date, Set<Tag> tags) {
        this.name = name;
        this.date = new Date(date.getTime());
        this.thumbnail = null;
        this.last = null;
        this.tags = new HashSet<Tag>(tags);
        this.result = null;
    }

    public Note(String name, Date date, List<Tag> tags) {
        this.name = name;
        this.date = new Date(date.getTime());
        this.thumbnail = null;
        this.last = null;
        this.tags = new HashSet<Tag>(tags);
        this.result = null;
    }

    public Note(String name, Date date) {
        this(name, date, new HashSet<Tag>());
    }

    public Note setThumbnail(File thumbnail) {
        Note newNote = new Note(this.name, this.date, this.tags);
        newNote.thumbnail = thumbnail;
        newNote.last = last;
        newNote.result = result;
        return newNote;
    }

    public Note setLast(File last) {
        Note newNote = new Note(this.name, this.date, this.tags);
        newNote.thumbnail = thumbnail;
        newNote.last = last;
        newNote.result = result;
        return newNote;
    }

    public Note addTag(Tag tag) {
        Note newNote = new Note(this.name, this.date, this.tags);
        newNote.tags.add(tag);
        newNote.thumbnail = thumbnail;
        newNote.last = last;
        newNote.result = result;
        return newNote;
    }

    public Note removeTag(Tag tag) {
        Note newNote = new Note(this.name, this.date, this.tags);
        newNote.tags.remove(tag);
        newNote.thumbnail = thumbnail;
        newNote.last = last;
        newNote.result = result;
        return newNote;
    }

    public Note addResult(String parentName, Result res) {
        Note newNote = new Note(this.name, this.date, this.tags);
        newNote.thumbnail = thumbnail;
        newNote.last = last;

        if(result == null)
            newNote.result = res;
        else
            newNote.result = result.add(parentName, res);

        return newNote;
    }

    public Result getResult(String parentID, String taskName) {
        if(result == null)
            return null;
        else
            return result.get(parentID, taskName);
    }

    public String getName() { return name; }
    public Date getDate() {
        return new Date(date.getTime());
    }
    public File getThumbnail() { return thumbnail; }
    public File getLast() { return last; }

    public Set<Tag> getTags() {
        return new HashSet<Tag>(tags);
    }

    public static class Result implements JSONable {
        private final static String FIELD_NAME = "name";
        private final static String FIELD_TASK = "task";
        private final static String FIELD_IMAGE = "image";
        private final static String FIELD_CHILDREN = "children";

        private final String uid;
        private final String task;
        private final File image;
        private final List<Result> children;

        public Result(String uid, String taskName, File image, List<Result> children) {
            this.uid = uid;
            this.task = taskName;
            this.image = new File(image.toString());

            if (children == null)
                this.children = null;
            else
                this.children = new ArrayList<Result>(children);
        }

        public Result(JSONObject json) throws JSONException {
            this.uid = json.getString(FIELD_NAME);
            this.task = json.getString(FIELD_TASK);
            this.image = new File(json.getString(FIELD_IMAGE));

            JSONArray childrenJSON = json.getJSONArray(FIELD_CHILDREN);
            if(childrenJSON.length() == 0) {
                this.children = null;
            } else {
                this.children = new ArrayList<Result>();
                for (int i = 0; i < childrenJSON.length(); i++)
                    this.children.add(new Result(childrenJSON.getJSONObject(i)));
            }
        }

        public Result get(String parentId, String taskName) {
            if(uid.equals(parentId)) {
                for(Result child: children) {
                    if(child.getTask().equals(taskName))
                        return child;
                }
            } else {
                for(Result child: children) {
                    Result res = child.get(parentId, taskName);
                    if(res != null)
                        return res;
                }
            }

            return null;
        }

        public Result add(String parentID, Result res) {
            if(uid.equals(parentID)) {
                List<Result> newChildren;

                if(children == null)
                    newChildren = new ArrayList<Result>();
                else
                    newChildren = new ArrayList<Result>(children);

                newChildren.add(res);
                return new Result(uid, task, image, newChildren);
            } else {
                if(children == null) {
                    return null;
                } else {
                    for(Result child: children) {
                        Result newResult = child.add(parentID, res);
                        if(newResult != null)
                            return newResult;
                    }
                }
            }

            return null;
        }

        public String getUID() { return uid; }
        public String getTask() { return task; }
        public List<Result> getChildren() { return new ArrayList<Result>(children); }

        @Override
        public JSONObject toJSON() throws JSONException {
            JSONObject json = new JSONObject();

            json.put(FIELD_NAME, uid);
            json.put(FIELD_TASK, task);
            json.put(FIELD_IMAGE, image.toString());

            JSONArray childrenJSON = new JSONArray();
            if(children != null) {
                for (Result child : children)
                    childrenJSON.put(child.toJSON());
            }

            json.put(FIELD_CHILDREN, childrenJSON);

            return json;
        }
    }

    /*
    JSONable
     */

    public Note(JSONObject json) throws JSONException {
        String name = json.getString(FIELD_NAME);

        Date date = new Date(json.getLong(FIELD_DATE));

        String thumbnailPath = json.getString(FIELD_THUMBNAIL);
        File thumbnail;
        if(thumbnailPath.equals(""))
            thumbnail = null;
        else
            thumbnail = new File(thumbnailPath);

        String lastPath = json.getString(FIELD_LAST);
        File last;
        if(lastPath.equals(""))
            last = null;
        else
            last = new File(lastPath);

        Set<Tag> tags = new HashSet<Tag>();
        JSONObject tagJSON = json.getJSONObject(FIELD_TAGS);
        for(int i=0; tagJSON.has(""+i); i++)
            tags.add(new Tag(tagJSON.getString(""+i)));

        JSONObject resultsJSON = json.getJSONObject(FIELD_RESULT);

        Result result;
        if(resultsJSON.length() == 0)
            result = null;
        else
            result = new Result(resultsJSON);

        this.name = name;
        this.date = date;
        this.thumbnail = thumbnail;
        this.last = last;
        this.tags = tags;
        this.result = result;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(FIELD_NAME, name);
        json.put(FIELD_DATE, date.getTime());
        if(thumbnail == null)
            json.put(FIELD_THUMBNAIL, "");
        else
            json.put(FIELD_THUMBNAIL, thumbnail.getPath());

        if(last == null)
            json.put(FIELD_LAST, "");
        else
            json.put(FIELD_LAST, last.getPath());

        JSONObject tagJSON = new JSONObject();
        int i = 0;
        for(Tag t: tags) {
            tagJSON.put("" + i++, t.getValue());
        }

        json.put(FIELD_TAGS, tagJSON);
        if(result == null)
            json.put(FIELD_RESULT, new JSONObject());
        else
            json.put(FIELD_RESULT, result.toJSON());

        return json;
    }

    @Override
    public String toString() {
        try {
            return toJSON().toString(4);
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
