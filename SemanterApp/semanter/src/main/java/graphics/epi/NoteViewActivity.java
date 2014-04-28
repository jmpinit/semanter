package graphics.epi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class NoteViewActivity extends ActionBarActivity {
    private static final String TAG = "NoteViewActivity";

    private ImageView noteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_view);

        noteView = (ImageView)findViewById(R.id.note_content);

        // load image to display
        Bundle b = getIntent().getExtras();
        Uri imageURI = b.getParcelable("image_path");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap noteImage = BitmapFactory.decodeFile(imageURI.getPath(), options);
        noteView.setImageBitmap(noteImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.noteview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_edit:
                // TODO implement opening of metadata editing
                Toast failureMessage = new Toast(this);
                failureMessage.setDuration(Toast.LENGTH_SHORT);
                failureMessage.setText("Unimplemented");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean launchEdit(View button) {
        Log.d("launchEdit", "Launching Edit View...");




        return  true;
    }
}
