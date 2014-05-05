package us.semanter.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import us.semanter.app.model.Note;
import us.semanter.app.ui.review.FlattenerView;
import us.semanter.app.vision.task.Flattener;

public class ReviewActivity extends ActionBarActivity {
    private Note noteToReview;
    private FlattenerView flattenerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        noteToReview = intent.getParcelableExtra(getString(R.string.param_note));

        flattenerView = (FlattenerView)findViewById(R.id.review_vision_view);

        Uri uri = Uri.parse(intent.getStringExtra("hack"));
        Flattener flattener = new Flattener(uri);
        flattener.run();

        flattenerView.review(flattener.getResult());

        /*if(noteToReview != null) {
            // TODO
        } else {
            finish();
        }*/
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
