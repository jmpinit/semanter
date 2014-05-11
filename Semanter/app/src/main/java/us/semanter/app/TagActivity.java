package us.semanter.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteFactory;
import us.semanter.app.model.NoteModifier;
import us.semanter.app.model.TagListAdapter;

public class TagActivity extends ActionBarActivity {
    private ListView noteList;
    private TagListAdapter adapter;

    private List<NoteModifier> noteModifiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        List<Note> notes = NoteFactory.getAllNotes(getExternalFilesDir(null).getPath() + "/notes");

        noteModifiers = new ArrayList<NoteModifier>();
        for(Note note: notes)
            noteModifiers.add(new NoteModifier(note));

        if(noteModifiers == null)
            noteModifiers = new ArrayList<NoteModifier>();

        Log.d("TagActivity", "There are " + noteModifiers.size() + " notes.");

        adapter = new TagListAdapter(this, R.layout.row_tag, noteModifiers);

        noteList = (ListView)findViewById(R.id.tag_note_list);
        noteList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void saveNotes() {
        // TODO only save if actually modified
        for(NoteModifier modifier: noteModifiers) {
            NoteFactory.saveMeta(this, modifier.getNote());
            Log.d("TagActivity", "The tags to save are " + modifier.getNote().getTags().toString());
        }
        Log.d("TagActivity", "Saved notes.");
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

    @Override
    protected void onPause() {
        super.onPause();
        saveNotes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveNotes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNotes();
    }
}
