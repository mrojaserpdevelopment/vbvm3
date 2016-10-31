package com.erpdevelopment.vbvm.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.LessonsAdapter;
import com.erpdevelopment.vbvm.adapter.LessonsCompleteAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.service.DownloadServiceTest;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.utils.BitmapManager;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LessonsFragment extends Fragment {

    private View rootView;
    private TextView tvLessonCount;
    private TextView tvLessonCountComplete;
    private ImageView imgStudy;
    private ImageView imgStudyBg;
    private ListView lvLessons;
    private ListView lvLessonsComplete;
    private LessonsAdapter adapterLessons;
    private LessonsCompleteAdapter adapterLessonsComplete;
    private List<Lesson> listLessons;
    private List<Lesson> listLessonsComplete;
    private Study mStudy;
    private ImageLoader imageLoader;
    private Activity activity;
    private FileCache fileCache;
    public Intent intentDownloadAll;

    public LessonsFragment() {}

    public static LessonsFragment newInstance(int index) {
        LessonsFragment f = new LessonsFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        imageLoader = new ImageLoader(getActivity());
        activity = getActivity();
        fileCache = new FileCache(MainActivity.mainCtx);
//        activity.registerReceiver(receiverDownloadProgress, new IntentFilter(DownloadServiceTest.NOTIFICATION_PROGRESS));
//        activity.registerReceiver(receiverDownloadComplete, new IntentFilter(DownloadServiceTest.NOTIFICATION_COMPLETE));
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.registerReceiver(receiverDownloadProgress, new IntentFilter(DownloadServiceTest.NOTIFICATION_PROGRESS));
        activity.registerReceiver(receiverDownloadComplete, new IntentFilter(DownloadServiceTest.NOTIFICATION_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(receiverDownloadProgress);
        activity.unregisterReceiver(receiverDownloadComplete);
    }

    private BroadcastReceiver receiverDownloadProgress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intentDownloadAll) {
            Bundle bundle = intentDownloadAll.getExtras();
            if (bundle != null) {
                String idLesson = bundle.getString("idLesson");
                int downloadProgress = bundle.getInt("downloadProgress");
                String downloadType = bundle.getString("downloadType");
                List<Lesson> listLessons = mStudy.getLessons();
                System.out.println("LessonsFragment.onReceive: " + idLesson + " - " + downloadProgress);
                for (int i=0; i<listLessons.size(); i++) {
                    if (listLessons.get(i).getIdProperty().equals(idLesson)) {
                        switch (downloadType) {
                            case "audio":
                                listLessons.get(i).setDownloadProgressAudio(downloadProgress);
                                listLessons.get(i).setDownloadStatusAudio(2);
                                break;
                            case "teacher":
                                listLessons.get(i).setDownloadProgressTeacher(downloadProgress);
                                listLessons.get(i).setDownloadStatusTeacherAid(2);
                                break;
                            case "transcript":
                                listLessons.get(i).setDownloadProgressTranscript(downloadProgress);
                                listLessons.get(i).setDownloadStatusTranscript(2);
                                break;
                        }
                    }
                }
                adapterLessons.setLessonListItems(listLessons);
            }
        }
    };

    private BroadcastReceiver receiverDownloadComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            boolean oneDownloadComplete = false; //At least one download has finished successfully
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadServiceTest.RESULT);
                String fileName = bundle.getString(DownloadServiceTest.FILENAME);
                String idLesson = bundle.getString("idLesson");
                String downloadType = bundle.getString("downloadType");
                List<Lesson> listLessonsUpdated = mStudy.getLessons();
                int downloadStatus = 0;
                if (resultCode == getActivity().RESULT_OK) {
                    System.out.println("Downloaded: " + fileName);
//                    Toast.makeText(getActivity(), "Downloaded: " + fileName, Toast.LENGTH_LONG).show();
                    oneDownloadComplete = true;
                    downloadStatus = 1;
                } else {
                }
                for (int i=0; i<listLessonsUpdated.size(); i++) {
                    if (listLessonsUpdated.get(i).getIdProperty().equals(idLesson)) {
                        switch (downloadType) {
                            case "audio":
                                listLessonsUpdated.get(i).setDownloadStatusAudio(downloadStatus);
                                listLessonsUpdated.get(i).setDownloadProgressAudio(0);
                                break;
                            case "teacher":
                                listLessonsUpdated.get(i).setDownloadStatusTeacherAid(downloadStatus);
                                listLessonsUpdated.get(i).setDownloadProgressTeacher(0);
                                break;
                            case "transcript":
                                listLessonsUpdated.get(i).setDownloadStatusTranscript(downloadStatus);
                                listLessonsUpdated.get(i).setDownloadProgressTranscript(0);
                                break;
                        }
                        break;
                    }
                }
                adapterLessons.setLessonListItems(listLessonsUpdated);
                DownloadServiceTest.decrementCount();
                if (oneDownloadComplete) {
                    if (DownloadServiceTest.countDownloads == 0){
                        // All lessons have been downloaded
                        DownloadServiceTest.downloading = false;
//                        new asyncGetStudyLessons().execute(mStudy);
                    }
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStudy = getArguments().getParcelable("study");
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();
        lvLessons = (ListView) rootView.findViewById(R.id.lv_lessons);
        lvLessonsComplete = (ListView) rootView.findViewById(R.id.lv_lessons_complete);
        imgStudy = (ImageView) rootView.findViewById(R.id.img_study);
        imgStudyBg = (ImageView) rootView.findViewById(R.id.img_study_bg);
        tvLessonCount = (TextView) rootView.findViewById(R.id.tv_lesson_count);
        tvLessonCountComplete = (TextView) rootView.findViewById(R.id.tv_lesson_count_complete);
        listLessons = new ArrayList<>();
        listLessonsComplete = new ArrayList<>();
        adapterLessons = new LessonsAdapter(getActivity(), listLessons, rootView);
        adapterLessonsComplete = new LessonsCompleteAdapter(getActivity(), listLessonsComplete, rootView);
        lvLessons.setAdapter(adapterLessons);
        lvLessonsComplete.setAdapter(adapterLessonsComplete);
//        imageLoader.DisplayImage(mStudy.getThumbnailSource(), imgStudy);
        imageLoader.DisplayImage(mStudy.getThumbnailSource(),imgStudy,200,false);
        imageLoader.DisplayImage(mStudy.getThumbnailSource(),imgStudyBg,100,false);
        new asyncGetStudyLessons().execute(mStudy);

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv1 = (ExpandableTextView) activity.findViewById(R.id.expand_text_view);

        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv1.setText(mStudy.getStudiesDescription());
    }

    private class asyncGetStudyLessons extends AsyncTask<Study, String, String > {

        private ProgressDialog pDialog;
        private List<Lesson> list = new ArrayList<Lesson>();

        private List<Lesson> listNew = new ArrayList<Lesson>();
        private List<Lesson> listComplete = new ArrayList<Lesson>();

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Study... params) {
            WebServiceCall.lessonsInDB = MainActivity.settings.getBoolean("lessonsInDB", false);
            list = DBHandleLessons.getLessons(params[0].getIdProperty());
            if ( (list.size() == 0) || ( !WebServiceCall.lessonsInDB ) ) {
                if ( !CheckConnectivity.isOnline(getActivity())) {
                    CheckConnectivity.showMessage(getActivity());
                } else {
                    list = new WebServiceCall().getStudyLessons(params[0]); //get and save lessons to DB
                    Log.i("LessonsFragment", "Update DB with data from Webservice");
                }
            }
//            mStudy.setLessons(list);
            for (Lesson lesson : list) {
                if ( lesson.getState().equals("complete") )
                    listComplete.add(lesson);
                else
                    listNew.add(lesson);
            }
            mStudy.setLessons(listNew);
            mStudy.setLessonsComplete(listComplete);

            resetDownloadingState();
            return null;
        }

        protected void onPostExecute(String result) {
           pDialog.dismiss();
           tvLessonCount.setText(String.valueOf(mStudy.getLessons().size()));
           tvLessonCountComplete.setText(String.valueOf(mStudy.getLessonsComplete().size()));
           adapterLessons.setLessonListItems(mStudy.getLessons());
           adapterLessonsComplete.setLessonsCompleteItems(mStudy.getLessonsComplete());
        }
    }

    private void markAllLessons(String state) {
        List<Lesson> lessons = new ArrayList<Lesson>();
        if (state.equals("complete")) {
            for (Lesson lesson : mStudy.getLessons()) {
                DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
                lesson.setCurrentPosition(0);
                lesson.setState(state);
                lessons.add(lesson);
            }
            mStudy.getLessons().clear();
            mStudy.getLessonsComplete().clear();
            mStudy.getLessonsComplete().addAll(lessons);
        } else {
            for (Lesson lesson : mStudy.getLessonsComplete()) {
                DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
                lesson.setCurrentPosition(0);
                lesson.setState(state);
                lessons.add(lesson);
            }
            mStudy.getLessonsComplete().clear();
            mStudy.getLessons().clear();
            mStudy.getLessons().addAll(lessons);
        }
        adapterLessons.setLessonListItems(mStudy.getLessons());
        adapterLessonsComplete.setLessonsCompleteItems(mStudy.getLessonsComplete());
        lessons.clear();
    }

//    private void markAllLessonsComplete() {
//        List<Lesson> lessons = new ArrayList<Lesson>();
//        for (Lesson lesson : mStudy.getLessons()){
//            DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
//            lesson.setCurrentPosition(0);
//            lesson.setState(state);
//            lessons.add(lesson);
//        }
//        adapterLessons.setLessonListItems(lessons);
//    }
//
//    private void markAllLessonsIncomplete() {
//        List<Lesson> lessons = new ArrayList<Lesson>();
//        for (Lesson lesson : mStudy.getLessons()){
//            DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
//            lesson.setCurrentPosition(0);
//            lesson.setState(state);
//            lessons.add(lesson);
//        }
//        adapterLessons.setLessonListItems(lessons);
//    }

    private void downloadAllLessons() {
        System.out.println("DownloadServiceTest.downloading: " + DownloadServiceTest.downloading);
        System.out.println("DownloadService.countDownloads: " + DownloadServiceTest.countDownloads);
//        System.out.println("DownloadService.downloading: " + DownloadService.downloading);
//        if ( !DownloadServiceTest.downloading && !DownloadService.downloading) {
        if ( !DownloadServiceTest.downloading && DownloadService.countDownloads == 0) {
            for (int i = 0; i < mStudy.getLessons().size(); i++) {
                System.out.println("LessonsFragment.downloadAllLessons 2 ");
                Lesson lesson = mStudy.getLessons().get(i);
                //Download if lesson is not downloaded or is downloading
                if ( !lesson.getAudioSource().equals("") && (lesson.getDownloadStatusAudio()!=1) ) {
                    System.out.println("Downloading audio: " + lesson.getAudioSource());
                    startDownload(lesson.getIdProperty(),lesson.getAudioSource(),"audio");
                }
                if ( !lesson.getTeacherAid().equals("") && (lesson.getDownloadStatusTeacherAid()!=1) ) {
                    System.out.println("Downloading teacher: " + lesson.getTeacherAid());
                    startDownload(lesson.getIdProperty(),lesson.getTeacherAid(),"teacher");
                }
                if ( !lesson.getTranscript().equals("") && (lesson.getDownloadStatusTranscript()!=1) ) {
                    System.out.println("Downloading transcript: " + lesson.getTranscript());
                    startDownload(lesson.getIdProperty(),lesson.getTranscript(),"transcript");
                }
            }
        }
    }

    private void startDownload(String idLesson, String url, String downloadType) {
        intentDownloadAll = new Intent(getActivity(), DownloadServiceTest.class);
        intentDownloadAll.putExtra("idLesson", idLesson);
        intentDownloadAll.putExtra("url", url);
        intentDownloadAll.putExtra("downloadType", downloadType);
        getActivity().startService(intentDownloadAll);
        adapterLessons.setIntentServiceDownloadAll(intentDownloadAll);
        DownloadServiceTest.incrementCount();
    }

    private void deleteAllLessons(List<Lesson> lessons) {
        if ( !DownloadServiceTest.downloading ) {
            int count = 0;
            for (int i=0; i<lessons.size(); i++){
                Lesson lesson = lessons.get(i);
                String urlAudio = lesson.getAudioSource();
                String urlTranscript = lesson.getTranscript();
                String urlTeacherAid = lesson.getTeacherAid();
                String idLesson = lesson.getIdProperty();
                if ( !urlAudio.equals("") && lesson.getDownloadStatusAudio()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(urlAudio)).delete();
                    lesson.setDownloadStatusAudio(0);
                    count++;
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, "audio");
                }
                if ( !urlTranscript.equals("") && lesson.getDownloadStatusTranscript()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(urlTranscript)).delete();
                    lesson.setDownloadStatusTranscript(0);
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, "transcript");
                }
                if ( !urlTeacherAid.equals("") && lesson.getDownloadStatusTeacherAid()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(urlTeacherAid)).delete();
                    lesson.setDownloadStatusTeacherAid(0);
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, "teacher");
                }
            }
            Toast.makeText(activity, count + " lesson(s) deleted", Toast.LENGTH_LONG).show();
            adapterLessons.setLessonListItems(lessons);
        } else {
            Toast.makeText(activity, "Download all is in progress...", Toast.LENGTH_LONG).show();
        }
    }


    private void resetDownloadingState() {
        if ( !DownloadService.downloading && !DownloadServiceTest.downloading){
            // if not downloading study, check and reset state for each lesson
            for (Lesson lesson: mStudy.getLessons()){
                if ( lesson.getDownloadStatusAudio() == 2 )
                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0, "audio");
                if ( lesson.getDownloadStatusTeacherAid() == 2 )
                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0, "teacher");
                if ( lesson.getDownloadStatusTranscript() == 2 )
                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0, "transcript");
            }
            List<Lesson> list = DBHandleLessons.getLessons(mStudy.getIdProperty());
            mStudy.setLessons(list);
            fileCache.clearTempFolder();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        activity.unregisterReceiver(receiverDownloadProgress);
//        activity.unregisterReceiver(receiverDownloadComplete);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment_lessons, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_mark_all_complete:
                markAllLessons("complete");
                return true;
            case R.id.action_mark_all_incomplete:
                markAllLessons("new");
                return true;
            case R.id.action_download_all:
                downloadAllLessons();
                return true;
            case R.id.action_delete_all_files:
                deleteAllLessons(mStudy.getLessons());
                deleteAllLessons(mStudy.getLessonsComplete());
                return true;
//            case android.R.id.home:
//                LessonsFragment fragmentLessons = LessonsFragment.newInstance(0);
//                StudiesFragment fragmentStudies = StudiesFragment.newInstance(0);
//
//                FragmentTransaction ft =((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();
//                if (fragmentStudies.isAdded()) { // if the fragment is already in container
//                    System.out.println("step 1...");
//                    ft.show(fragmentStudies);
//                    ft.hide(fragmentLessons);
//                }
//        } else { // fragment needs to be added to frame container
//            ft.add(R.id.frame_container, fragmentStudies, "Studies");
//        }
                // Hide fragment B
//        if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
//        // Hide fragment C
//        if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
//        if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
//        if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
                // Commit changes
//                ft.commit();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backToStudies() {
        LessonsFragment fragmentLessons = LessonsFragment.newInstance(0);
        StudiesFragment fragmentStudies = StudiesFragment.newInstance(0);
        FragmentTransaction ft = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();

        if (fragmentStudies.isAdded()) { // if the fragment is already in container
            System.out.println("LessonsFragment.backToStudies 1");
            ft.show(fragmentStudies);
        } else { // fragment needs to be added to frame container
            System.out.println("LessonsFragment.backToStudies 2");
            ft.add(R.id.frame_container, fragmentStudies, "Studies");
        }
        System.out.println("LessonsFragment.backToStudies 3");
        if (fragmentStudies.isAdded()) {
            System.out.println("LessonsFragment.backToStudies 4");
            ft.show(fragmentStudies);
            ft.hide(fragmentLessons);
        }

//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(study.getTitle());
//
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("study", study);
//        fragmentLessons.getArguments().putAll(bundle);

//        ft.detach(fragmentLessons);
//        ft.attach(fragmentLessons);

        ft.commit();
    }

    private ActionBar actionBar;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        System.out.println("LessonsFragment.onHiddenChanged: " + hidden);
//        if (hidden) {
//            actionBar.setHomeButtonEnabled(false); // disable the button
//            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
//            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.show();
//        } else {
////            actionBar.setHomeButtonEnabled(false); // disable the button
//            actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
////            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
////            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setTitle(mStudy.getTitle());
//            actionBar.show();
//        }
    }
}