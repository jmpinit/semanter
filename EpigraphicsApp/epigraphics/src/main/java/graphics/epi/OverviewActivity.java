package graphics.epi;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RunnableFuture;

import graphics.epi.filesystemtree.Folder;
import graphics.epi.filesystemtree.Items;
import graphics.epi.utils.Geometry;
import graphics.epi.vision.VisionAction;
import graphics.epi.vision.analyze.SquareFinder;
import graphics.epi.vision.operations.OpDummyLong;
import graphics.epi.vision.VisionExecutor;
import graphics.epi.vision.VisionListener;
import graphics.epi.vision.operations.OpThreshold;
import graphics.epi.vision.operations.VisionOp;

public class OverviewActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FileSystemFragment.FileSystemCallbacks, VisionListener {

    static final String TAG = "epigraphics";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        /*ArrayList<String> demoArray = new ArrayList<String>();

        demoArray.add("/file1");
        demoArray.add("/18.06/file2");
        demoArray.add("/18.06/file3");
        demoArray.add("/18.100c/file4");
        demoArray.add("/18.100c/file5");
        demoArray.add("/18.100c/day1/file8");
        demoArray.add("/18.100c/day2/file9");
        demoArray.add("/18.100c/day2/hour5/file10");
        demoArray.add("/file6");*/

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        /*fragmentManager.beginTransaction()
                .replace(R.id.container, FileSystemFragment.newInstance(new Folder(null, "/", demoArray))).commit();*/
    }

    @Override
    public void fileSystemInteraction(Items next) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (next.isFile()) {
            //TODO: handle file
            System.out.println(next.toString());
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FileSystemFragment.newInstance((Folder) next))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.overview, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private View rootView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_overview, container, false);

            // display which section this is
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            // FIXME
            final Button button = (Button)rootView.findViewById(R.id.btn_pick);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final int SELECT_PHOTO = 100;

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            ((OverviewActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER)
            );
        }

        public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
            final int REQ_CODE_PICK_IMAGE = 100;

            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

            switch(requestCode) {
                case REQ_CODE_PICK_IMAGE:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = rootView.getContext().getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null
                        );
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

                        // display raw image
                        ImageView rawView = (ImageView)rootView.findViewById(R.id.img_raw);
                        rawView.setImageBitmap(yourSelectedImage);

                        // launch async vision task
                        Log.d(TAG, "launching vision task");
                        Executor executor = new VisionExecutor();
                        RunnableFuture task = new SquareFinder((VisionListener)this.getActivity(), yourSelectedImage);
                        executor.execute(task);
                        Log.d(TAG, "launched vision task");
                    }
            }
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        // http://stackoverflow.com/a/6147919
        Log.d(TAG, "Activity sees result.");
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void OnVisionActionComplete(VisionAction op) {
        // TODO result dispatcher
        try {
            final Bundle results = (Bundle) op.get();

            List<Geometry.Quad> squares = results.getParcelableArrayList("squares");
            Log.d(TAG, "found " + squares.size() + " squares.");
            for(Geometry.Quad quad: squares) {
                Log.d(TAG, quad.toString());
            }

            // display result
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView resultView = (ImageView) findViewById(R.id.img_raw); // FIXME should be img_result
                    resultView.setImageBitmap(resultImage);
                }
            });*/
        } catch(InterruptedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch(ExecutionException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}