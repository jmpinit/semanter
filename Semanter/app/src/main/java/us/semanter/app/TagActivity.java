package us.semanter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import us.semanter.app.model.Note;
import us.semanter.app.model.TagListAdapter;

public class TagActivity extends ActionBarActivity {
    private ListView noteList;
    private TagListAdapter adapter;

    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        Intent intent = getIntent();
        String[] noteJSON = intent.getStringArrayExtra(getString(R.string.param_notes));

        try {
            notes = new ArrayList<Note>(noteJSON.length);
            for (String json : noteJSON)
                notes.add(new Note(new JSONObject(json)));
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("TagActivity", "Couldn't create note because of JSONException.");
            finish();
        }

        for(Note note: notes)
            Log.d("TagActivity", note.toString());

        noteList = (ListView)findViewById(R.id.tag_note_list);
        adapter = new TagListAdapter(this, R.layout.row_tag, notes);
        noteList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
