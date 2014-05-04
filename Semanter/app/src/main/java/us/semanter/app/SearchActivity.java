package us.semanter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteListAdapter;
import us.semanter.app.model.Tag;

public class SearchActivity extends ActionBarActivity {
    private GridView noteList;
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // FIXME test
        final Tag[] testTags = new Tag[] {
                new Tag("geometry"),
                new Tag("project"),
                new Tag("receipt"),
                new Tag("whiteboard"),
                new Tag("printed"),
                new Tag("blog")
        };

        List<Note> notes = new ArrayList<Note>();
        for(int i=0; i < Math.random()*512 + 16; i++) {
            List<Tag> tags = new Vector<Tag>();
            for(int j=0; j < 4; j++)
                tags.add(testTags[(int)(Math.random()*testTags.length)]);

            Note newNote = new Note(new Date(), tags);
            for(int j=0; j < (int)(Math.random()*4); j++)
                newNote = newNote.nextTask();

            notes.add(newNote);
        }

        noteList = (GridView)findViewById(R.id.search_note_grid);
        noteListAdapter = new NoteListAdapter(this, R.layout.thumbnail_note, notes);
        noteList.setAdapter(noteListAdapter);

        final Intent reviewIntent = new Intent(this, ReviewActivity.class);
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                reviewIntent.putExtra(getString(R.string.param_note), (Note)noteListAdapter.getItem(position));
                startActivity(reviewIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
