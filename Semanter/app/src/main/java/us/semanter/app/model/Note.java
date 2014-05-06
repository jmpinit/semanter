package us.semanter.app.model;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import us.semanter.app.vision.VisionResultFactory;
import us.semanter.app.vision.util.JSONable;
import us.semanter.app.vision.util.VisionResult;

/**
 * Immutable in-memory representation of note
 */
public class Note implements JSONable {
    private final Date date;
    private final Set<Tag> tags;
    private Bundle results;
    private int resultCount;

    public Note(Date date, Set<Tag> tags) {
        this.date = new Date(date.getTime());
        this.tags = new HashSet<Tag>(tags);
        this.results = new Bundle();
        this.resultCount = 0;
    }

    public Note(Date date, List<Tag> tags) {
        this.date = new Date(date.getTime());
        this.tags = new HashSet<Tag>(tags);
        this.results = new Bundle();
        this.resultCount = 0;
    }

    public Note(Date date) {
        this(date, new HashSet<Tag>());
    }

    public Note addTag(Tag tag) {
        Note newNote = new Note(this.date, this.tags);
        this.tags.add(tag);
        newNote.results = results;
        return newNote;
    }

    public Note addResult(VisionResult result) {
        try {
            Note newNote = new Note(this.date, this.tags);
            newNote.results = results;
            newNote.results.putString(result.getTaskName(), result.toJSON().toString());
            newNote.resultCount = resultCount + 1;
            return newNote;
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("Note", "Couldn't add result because of JSON exception.");
        }

        return null;
    }

    public int getResultCount() {
        return resultCount;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public Set<Tag> getTags() {
        return new HashSet<Tag>(tags);
    }

    public VisionResult getResult(String name) {
        try {
            return VisionResultFactory.fromJSON(new JSONObject(results.getString(name)));
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("Note", "Couldn't get result because of JSONException.");
        }

        return null;
    }

    /*
    JSONable
     */

    public Note(JSONObject json) throws JSONException {
        Note newNote = null;

        Date date = new Date(json.getLong("date"));
        int resultCount = json.getInt("result_count");

        Set<Tag> tags = new HashSet<Tag>();
        JSONObject tagJSON = json.getJSONObject("tags");
        for(int i=0; tagJSON.has(""+i); i++)
            tags.add(new Tag(tagJSON.getString(""+i)));

        Bundle results = new Bundle();
        JSONObject resultsJSON = json.getJSONObject("results");
        Iterator<String> resultItr = resultsJSON.keys();
        while(resultItr.hasNext()) {
            String taskName = resultItr.next();
            results.putString(taskName, resultsJSON.getString(taskName));
        }

        this.date = date;
        this.resultCount = resultCount;
        this.tags = tags;
        this.results = results;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("date", date.getTime());
        json.put("result_count", resultCount);

        JSONObject tagJSON = new JSONObject();
        int i = 0;
        for(Tag t: tags)
            tagJSON.put("" + i++, t.getValue());

        json.put("tags", tagJSON);

        JSONObject resultJSON = new JSONObject();
        for(String resName: results.keySet()) {
            resultJSON.put(resName, results.get(resName));
        }

        json.put("results", resultJSON);

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
