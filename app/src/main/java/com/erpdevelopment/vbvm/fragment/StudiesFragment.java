package com.erpdevelopment.vbvm.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.SettingsActivity;
import com.erpdevelopment.vbvm.adapter.StudiesAdapter;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.Constants;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;
import com.erpdevelopment.vbvm.view.ExpandableHeightGridview;

public class StudiesFragment extends Fragment {

    private View rootView;
    private ScrollView scroll;
    private ExpandableHeightGridview gvStudiesNew;
    private ExpandableHeightGridview gvStudiesOld;
    private ExpandableHeightGridview gvStudiesSingle;
//    private ImageLoader imageLoader;
    private ImageLoader2 imageLoader2;
    private StudiesAdapter adapterStudiesNew;
    private StudiesAdapter adapterStudiesOld;
    private StudiesAdapter adapterStudiesSingle;
    private ActionBar actionBar;
    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private OnStudyItemSelectedListener listener;

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
    public interface OnStudyItemSelectedListener {
        // This can be any number of events to be sent to the activity
        void onStudyItemSelected(Study study);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStudyItemSelectedListener) {
            listener = (OnStudyItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement StudiesFragment.OnStudyItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
//        imageLoader = new ImageLoader(getActivity());
        imageLoader2 = new ImageLoader2(getActivity());
        setAdapterStudiesFragment();
    }

    private void setAdapterStudiesFragment() {
//        adapterStudiesNew = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeNew,imageLoader);
        adapterStudiesNew = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeNew,imageLoader2);
        gvStudiesNew.setAdapter(adapterStudiesNew);
        gvStudiesNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Study study = (Study) parent.getItemAtPosition(position);
                //fire the event when the user selects a study in the fragment
                listener.onStudyItemSelected(study);
            }
        });
//        adapterStudiesOld = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeOld,imageLoader);
        adapterStudiesOld = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeOld,imageLoader2);
        gvStudiesOld.setAdapter(adapterStudiesOld);
        gvStudiesOld.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Study study = (Study) parent.getItemAtPosition(position);
                //fire the event when the user selects a study in the fragment
                listener.onStudyItemSelected(study);
            }
        });
//        adapterStudiesSingle = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeSingle,imageLoader);
        adapterStudiesSingle = new StudiesAdapter(getActivity(), FilesManager.listStudiesTypeSingle,imageLoader2);
        gvStudiesSingle.setAdapter(adapterStudiesSingle);
        gvStudiesSingle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Study study = (Study) parent.getItemAtPosition(position);
                //fire the event when the user selects a study in the fragment
                listener.onStudyItemSelected(study);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FilesManager.lastLessonId = MainActivity.settings.getString("currentLessonId", "");
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
        System.out.println("StudiesFragment.onOptionsItemSelected");
        String url = "";
        switch (item.getItemId()) {
            case R.id.action_about:
                url = Constants.URL_VBVMI.ABOUT;
                break;
            case R.id.action_events:
                url = Constants.URL_VBVMI.EVENTS;
                break;
            case R.id.action_contact:
                url = Constants.URL_VBVMI.CONTACT;
                break;
            case R.id.action_donate:
                url = Constants.URL_VBVMI.DONATE;
                break;
            case R.id.action_config:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                return true;
            default:
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        return true;
    }

}
