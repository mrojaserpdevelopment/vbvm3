package com.erpdevelopment.vbvm.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioControllerActivity;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.adapter.StudiesAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;
import com.erpdevelopment.vbvm.view.ExpandableHeightGridview;

import java.util.ArrayList;

public class StudiesFragment extends Fragment {

    private View rootView;
    private ScrollView scroll;
    private ExpandableHeightGridview gvStudiesNew;
    private ExpandableHeightGridview gvStudiesOld;
    private ExpandableHeightGridview gvStudiesSingle;
    private ImageLoader imageLoader;
    private StudiesAdapter adapterStudiesNew;
    private StudiesAdapter adapterStudiesOld;
    private StudiesAdapter adapterStudiesSingle;
    private ActionBar actionBar;
    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private OnItemSelectedListener listener;

    public StudiesFragment() {
    }

    /**
     * Static factory method that takes an int parameter,
     * initializes the fragment's arguments, and returns the
     * new fragment to the client.
     */
    public static StudiesFragment newInstance(int index) {
        StudiesFragment f = new StudiesFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onStudyItemSelected(Study study);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement StudiesFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("StudiesFragment.onCreateView");
        return inflater.inflate(R.layout.fragment_studies, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();
        scroll = (ScrollView) rootView.findViewById(R.id.scrollViewStudies);
        gvStudiesNew = (ExpandableHeightGridview) rootView.findViewById(R.id.gvStudiesNew);
        gvStudiesOld = (ExpandableHeightGridview) rootView.findViewById(R.id.gvStudiesOld);
        gvStudiesSingle = (ExpandableHeightGridview) rootView.findViewById(R.id.gvStudiesSingle);
        gvStudiesNew.setFocusable(false);
        gvStudiesOld.setFocusable(false);
        gvStudiesSingle.setFocusable(false);
        imageLoader = new ImageLoader(getActivity());
        setAdapterStudiesFragment();
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.show();
        }
    }

    private void setAdapterStudiesFragment() {
        adapterStudiesNew = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeNew,imageLoader);
        gvStudiesNew.setAdapter(adapterStudiesNew);
        gvStudiesNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Study study = (Study) parent.getItemAtPosition(position);
                //fire the event when the user selects a study in the fragment
                listener.onStudyItemSelected(study);
            }
        });
        adapterStudiesOld = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeOld,imageLoader);
        gvStudiesOld.setAdapter(adapterStudiesOld);
        gvStudiesOld.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        adapterStudiesSingle = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeSingle,imageLoader);
        gvStudiesSingle.setAdapter(adapterStudiesSingle);
        gvStudiesSingle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

//        if ( (FilesManager.listStudiesTypeNew == null) || (FilesManager.listStudiesTypeNew.size() == 0) ) {
//            DownloadJsonData.getInstance().asyncJsonGetStudies(getActivity(),
//                    adapterStudiesNew, adapterStudiesOld, adapterStudiesSingle, rootView, scroll);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        checkUserFirstVisit();
        System.out.println("StudiesFragment.onResume");
        FilesManager.lastLessonId = MainActivity.settings.getString("currentLessonId", "");
        System.out.println("FilesManager.listStudiesTypeNew.size(): " + FilesManager.listStudiesTypeNew.size());
        if ( (FilesManager.listStudiesTypeNew == null) || (FilesManager.listStudiesTypeNew.size() == 0) ) {
            DownloadJsonData.getInstance().asyncJsonGetStudies(getActivity(),
                    adapterStudiesNew, adapterStudiesOld, adapterStudiesSingle, rootView, scroll);
        } else {
            TextView tvCountStudiesNew = (TextView) rootView.findViewById(R.id.tvCountStudiesNew);
            TextView tvCountStudiesOld = (TextView) rootView.findViewById(R.id.tvCountStudiesOld);
            TextView tvCountStudiesSingle = (TextView) rootView.findViewById(R.id.tvCountStudiesSingle);
            TextView tvStudiesNew = (TextView) rootView.findViewById(R.id.tvStudiesNew);
            TextView tvStudiesOld = (TextView) rootView.findViewById(R.id.tvStudiesOld);
            TextView tvStudiesSingle = (TextView) rootView.findViewById(R.id.tvStudiesSingle);

            Resources res = getActivity().getResources();
            String messageCountStudies = res.getString(R.string.message_count_studies, FilesManager.listStudiesTypeNew.size());
            tvCountStudiesNew.setText(messageCountStudies);
            messageCountStudies = res.getString(R.string.message_count_studies, FilesManager.listStudiesTypeOld.size());
            tvCountStudiesOld.setText(messageCountStudies);
            messageCountStudies = res.getString(R.string.message_count_studies, FilesManager.listStudiesTypeSingle.size());
            tvCountStudiesSingle.setText(messageCountStudies);

            tvStudiesNew.setVisibility(View.VISIBLE);
            tvStudiesOld.setVisibility(View.VISIBLE);
            tvStudiesSingle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment_studies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                return true;
            case R.id.action_events:
                return true;
            case R.id.action_contact:
                return true;
            case R.id.action_donate:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    /**
//     * CHECK LAST UPDATE TIME
//     */
//    private boolean checkUserFirstVisit(){
//        long lastUpdateTime = MainActivity.settings.getLong("lastUpdateKey", 0L);
//        long timeElapsed = System.currentTimeMillis() - lastUpdateTime;
//        // YOUR UPDATE FREQUENCY HERE
////			final long UPDATE_FREQ = 1000 * 60 * 60 * 12;
//        final long UPDATE_FREQ = 1000 * 60 * 60 * 1; //Every 1 hour
//        SharedPreferences.Editor e = MainActivity.settings.edit();
//        if (timeElapsed > UPDATE_FREQ) {
//            e.putBoolean("studiesInDB", false);
//            e.putBoolean("lessonsInDB", false);
//            e.putBoolean("articlesInDB", false);
//            e.putBoolean("postsInDB", false);
//            e.putBoolean("eventsInDB", false);
//            e.putBoolean("videosInDB", false);
//            Log.i("MainActivity info", "Update DB with data from Webservice");
//        }
//        // STORE LATEST UPDATE TIME
//        e.putLong("lastUpdateKey", System.currentTimeMillis());
//        e.commit();
//        return false;
//    }
}
