package us.semanter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import us.semanter.app.model.Note;
import us.semanter.app.ui.VisionView;
import us.semanter.app.vision.VisionPipeline;

public class ReviewActivity extends ActionBarActivity {
    private Note noteToReview;
    private VisionView visionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        try {
            JSONObject noteJSON = new JSONObject(intent.getStringExtra(getString(R.string.param_note)));
            noteToReview = new Note(noteJSON);
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("ReviewActivity", "Couldn't construct note because of JSONException");
            finish();
        }

        String taskName = intent.getStringExtra("task");
        if(taskName.equals(VisionPipeline.Task.FLATTEN.getName())) {
            setContentView(R.layout.review_flatten);
            visionView = (VisionView)findViewById(R.id.flatten_reviewer);
            visionView.review(noteToReview.getResult(VisionPipeline.Task.FLATTEN.getName()));
        } else {
            Log.e("ReviewActivity", "unrecognized task.");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.review, menu);
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
