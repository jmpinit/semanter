package graphics.epi;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

import graphics.epi.filesystemtree.Folder;
import graphics.epi.filesystemtree.Items;

public class FileSystemFragment extends Fragment implements AbsListView.OnItemClickListener {
    private Folder fol;
    private static final String ARG_FOLDER = "folder";

    private FileSystemCallbacks mListener;

    private AbsListView fileListView;       // visible list
    private ListAdapter fileListAdapter;    // fills list

    public static FileSystemFragment newInstance(Folder fol) {
        FileSystemFragment fragment = new FileSystemFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_FOLDER, fol);
        fragment.setArguments(args);

        return fragment;
    }

    public FileSystemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fol = (Folder)getArguments().getSerializable(ARG_FOLDER);
        }

        if(fol == null) {
            fol = new Folder(null, "/", new ArrayList<String>());
        }

        fileListAdapter = new ArrayAdapter<Items>(getActivity(), R.layout.arrayadaptertext, fol.getItems());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        fileListView = (AbsListView) view.findViewById(R.id.list_file);
        ((AdapterView<ListAdapter>) fileListView).setAdapter(fileListAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        fileListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FileSystemCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FileSystemCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.fileSystemInteraction(fol.get(position));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public static interface FileSystemCallbacks {
        public void fileSystemInteraction(Items next);
    }
}