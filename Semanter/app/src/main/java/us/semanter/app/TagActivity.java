package us.semanter.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteListAdapter;
import us.semanter.app.model.Tag;
import us.semanter.app.ui.TagSorterView;
import us.semanter.app.ui.TagView;

public class TagActivity extends ActionBarActivity {
    private GridView gridView;
    private NoteListAdapter adapter;
    private TagView tags;
    private TagSorterView sorter;

    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        /*Intent intent = getIntent();
        Note[] noteArray = intent.getParcelableArrayExtra("notes");

        if(noteArray != null) {
            notes = new ArrayList<Note>();
            for (Object note : noteArray)
                notes.add((Note) note);
        } else {
            finish();
        }*/

        gridView = (GridView)findViewById(R.id.activity_tag_notes);
        tags = (TagView)findViewById(R.id.activity_tag_tags);
        sorter = (TagSorterView)findViewById(R.id.activity_tag_sorter);

        adapter = new NoteListAdapter(this, R.layout.thumbnail_note, notes);
        adapter.notifyDataSetChanged();

        // FIXME test
        sorter.addTags(new ArrayList<Tag>(Arrays.asList(new Tag[] { new Tag("hello"), new Tag("world"), new Tag("alice")})));
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
