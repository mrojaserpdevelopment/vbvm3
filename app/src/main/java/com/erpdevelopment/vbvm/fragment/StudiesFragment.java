package com.erpdevelopment.vbvm.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
        imageLoader = new ImageLoader(getActivity());
        setAdapterStudiesFragment();
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

        if ( (FilesManager.listStudiesTypeNew == null) || (FilesManager.listStudiesTypeNew.size() == 0) ) {
            DownloadJsonData.getInstance().asyncJsonGetStudies(getActivity(),
                    adapterStudiesNew, adapterStudiesOld, adapterStudiesSingle, rootView, scroll);
        }
    }

    // Play first lesson from Studies list
    private class asyncPlayLesson extends AsyncTask< Study, String, Lesson > {

        ProgressDialog pDialog;
        Study study;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Lesson doInBackground(Study... params) {
            study = params[0];
            AudioPlayerService.listTempLesson2 = (ArrayList<Lesson>) DBHandleLessons.getLessons(study.getIdProperty());
            Lesson lesson = AudioPlayerService.listTempLesson2.get(0);
            return lesson;
        }

        protected void onPostExecute(Lesson resultLesson) {
            if ( !(resultLesson.getIdProperty().equals(FilesManager.lastLessonId)) ) {
                AudioPlayerService.created = false;
                DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack);
                FilesManager.lastLessonId = resultLesson.getIdProperty();
                AudioPlayerService.savedOldPositionInTrack = resultLesson.getCurrentPosition();
            } else {
                if (!AudioPlayerService.created)
                    AudioPlayerService.savedOldPositionInTrack = MainActivity.settings.getLong("currentPositionInTrack", 0);
            }
            pDialog.dismiss();
            Intent i = new Intent(getActivity(), AudioControllerActivity.class);
            i.putExtra("position", resultLesson.getPositionInList());
            i.putExtra("thumbnailSource", resultLesson.getStudyThumbnailSource());
            i.putExtra("description", resultLesson.getLessonsDescription());
            i.putExtra("title", resultLesson.getTitle());
            i.putExtra("size", resultLesson.getStudyLessonsSize());
            i.putExtra("readSource", resultLesson.getTranscript());
            i.putExtra("presentSource", resultLesson.getStudentAid());
            i.putExtra("handoutSource", resultLesson.getTeacherAid());
            i.putExtra("study", study);
            startActivity(i);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
//        checkUserFirstVisit();
        FilesManager.lastLessonId = MainActivity.settings.getString("currentLessonId", "");
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
