package com.erpdevelopment.vbvm.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.adapter.LessonsAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.service.DownloadAllService;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.utils.BitmapManager2;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LessonsFragment extends Fragment {

    private View rootView;
    private ImageView imgStudy;
    private ImageView imgStudyBg;
    private ListView lvLessons;
    private LessonsAdapter adapterLessons;
    private List<Lesson> listLessons;
    private List<Lesson> listLessonsComplete;
    private Study mStudy;
    private ImageButton btnPlay;
    private ImageButton btnPlayMini;
//    private TextView tvIconPlayMini;

    //    private ImageLoader imageLoader;
    private ImageLoader2 imageLoader2;
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
        System.out.println("LessonsFragment.onCreate...");
        setHasOptionsMenu(true);
        activity = getActivity();
        imageLoader2 = new ImageLoader2(activity);
        fileCache = new FileCache(MainActivity.mainCtx);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStudy = getArguments().getParcelable("study");
        return inflater.inflate(R.layout.fragment_lessons2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();

        lvLessons = (ListView) rootView.findViewById(R.id.lv_lessons);
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.fragment_lessons3, lvLessons, false);
        lvLessons.addHeaderView(header);
        listLessons = new ArrayList<>();
        adapterLessons = new LessonsAdapter(getActivity(), listLessons, rootView);
        lvLessons.setAdapter(adapterLessons);

        btnPlay = (ImageButton) this.activity.findViewById(R.id.btn_play);
//        tvIconPlayMini = (TextView) activity.findViewById(R.id.tv_icon_play_mini);
        btnPlayMini = (ImageButton) activity.findViewById(R.id.btn_play_mini);

        imgStudy = (ImageView) rootView.findViewById(R.id.img_study);
        imgStudyBg = (ImageView) rootView.findViewById(R.id.img_study_bg);

//        listLessons = new ArrayList<>();
//        listLessonsComplete = new ArrayList<>();
//        adapterLessons = new LessonsAdapter(getActivity(), listLessons, rootView);
//        lvLessons.setAdapter(adapterLessons);

        Picasso.with(activity)
                .load(mStudy.getThumbnailSource())
                .resize(300,300)
                .centerCrop()
                .into(imgStudy);

        Picasso.with(activity)
                .load(mStudy.getThumbnailSource())
                .resize(600,600)
                .centerCrop()
                .into(imgStudyBg);

        //Show description of study
        ExpandableTextView expTv1 = (ExpandableTextView) activity.findViewById(R.id.expand_text_view);
        expTv1.setText(mStudy.getStudiesDescription());

        new asyncGetStudyLessons().execute(mStudy);
    }

    private class asyncGetStudyLessons extends AsyncTask<Study, String, String > {

//        private ProgressDialog pDialog;
//        private List<Lesson> list = new ArrayList<Lesson>();

        private List<Lesson> listIncomplete = new ArrayList<Lesson>();
        private List<Lesson> listComplete = new ArrayList<Lesson>();

        protected void onPreExecute() {
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        protected String doInBackground(Study... params) {
            WebServiceCall.lessonsInDB = MainActivity.settings.getBoolean("lessonsInDB", false);
            listLessons = DBHandleLessons.getLessons(params[0].getIdProperty());
            if ( (listLessons.size() == 0) || ( !WebServiceCall.lessonsInDB ) ) {
                if ( !CheckConnectivity.isOnline(getActivity())) {
                    CheckConnectivity.showMessage(getActivity());
                } else {
                    listLessons = new WebServiceCall().getStudyLessons(params[0]); //get and save lessons to DB
                    Log.i("LessonsFragment", "Update DB with data from Webservice");
                }
            }
            resetDownloadingState(listLessons);
            return null;
        }

        protected void onPostExecute(String result) {
//           pDialog.dismiss();

            for (Lesson lesson : listLessons) {
                if ( lesson.getState().equals("complete") )
                    listComplete.add(lesson);
                else
                    listIncomplete.add(lesson);
            }
            listLessons.clear();
            if (listIncomplete.size()>0) {
                System.out.println("Incomplete > 0");
                Lesson lSectionIncomplete = new Lesson();
                lSectionIncomplete.setSection(true);
                lSectionIncomplete.setSectionCompleted(false);
                listLessons.add(lSectionIncomplete);
                listLessons.addAll(listIncomplete);
            }
            if (listComplete.size()>0) {
                System.out.println("complete > 0");
                Lesson lSectionComplete = new Lesson();
                lSectionComplete.setSection(true);
                lSectionComplete.setSectionCompleted(true);
                listLessons.add(lSectionComplete);
                listLessons.addAll(listComplete);
            }
            mStudy.setLessons(listLessons);
            adapterLessons.setSizeListComplete(listComplete.size());
            adapterLessons.setSizeListIncomplete(listIncomplete.size());
            adapterLessons.setLessonListItems(mStudy.getLessons());
        }
    }

    private void markAllLessons(String state) {
        List<Lesson> lessons = new ArrayList<Lesson>();
        for (Lesson lesson : mStudy.getLessons()) {
            DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
            lesson.setCurrentPosition(0);
            lesson.setState(state);
            lessons.add(lesson);
        }
        new asyncGetStudyLessons().execute(mStudy);
    }

    private void downloadAllLessons() {
        System.out.println("DownloadAllService.IS_SERVICE_RUNNING: " + DownloadAllService.downloading);
        System.out.println("DownloadService.countDownloads: " + DownloadAllService.countDownloads);
        if ( !DownloadAllService.downloading && DownloadService.countDownloads == 0) {
            for (int i = 0; i < listLessons.size(); i++) {
                System.out.println("LessonsFragment.downloadAllLessons");
                Lesson lesson = listLessons.get(i);
                //Download if lesson is not downloaded or is IS_SERVICE_RUNNING
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
        intentDownloadAll = new Intent(getActivity(), DownloadAllService.class);
        intentDownloadAll.putExtra("idLesson", idLesson);
        intentDownloadAll.putExtra("url", url);
        intentDownloadAll.putExtra("downloadType", downloadType);
        getActivity().startService(intentDownloadAll);
        adapterLessons.setIntentServiceDownloadAll(intentDownloadAll);
        DownloadAllService.incrementCount();
    }

    private void deleteAllLessons() {
        listLessons = DBHandleLessons.getLessons(mStudy.getIdProperty());
        if ( !DownloadAllService.downloading ) {
            int count = 0;
            for (int i=0; i<listLessons.size(); i++){
                Lesson lesson = listLessons.get(i);
                String urlAudio = lesson.getAudioSource();
                String urlTranscript = lesson.getTranscript();
                String urlTeacherAid = lesson.getTeacherAid();
                String idLesson = lesson.getIdProperty();
                if ( !urlAudio.equals("") && lesson.getDownloadStatusAudio()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager2.getFileNameFromUrl(urlAudio)).delete();
                    lesson.setDownloadStatusAudio(0);
                    count++;
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, "audio");
                }
                if ( !urlTranscript.equals("") && lesson.getDownloadStatusTranscript()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager2.getFileNameFromUrl(urlTranscript)).delete();
                    lesson.setDownloadStatusTranscript(0);
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, "transcript");
                }
                if ( !urlTeacherAid.equals("") && lesson.getDownloadStatusTeacherAid()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager2.getFileNameFromUrl(urlTeacherAid)).delete();
                    lesson.setDownloadStatusTeacherAid(0);
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, "teacher");
                }
                listLessons.set(i,lesson);
            }
            Toast.makeText(activity, count + " lesson(s) deleted", Toast.LENGTH_LONG).show();
            adapterLessons.setLessonListItems(listLessons);
        } else {
            Toast.makeText(activity, "Stop download first", Toast.LENGTH_LONG).show();
        }
    }


    private void resetDownloadingState(List<Lesson> lessons) {
        if ( !DownloadService.IS_SERVICE_RUNNING && !DownloadAllService.downloading){
            // if not IS_SERVICE_RUNNING study, check and reset state for each lesson
            for (Lesson lesson: lessons){
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
                deleteAllLessons();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverDownloadProgress, new IntentFilter(DownloadService.NOTIFICATION_DOWNLOAD_PROGRESS));
        activity.registerReceiver(receiverDownloadAllProgress, new IntentFilter(DownloadAllService.NOTIFICATION_DOWNLOAD_ALL_PROGRESS));
        activity.registerReceiver(receiverDownloadAllComplete, new IntentFilter(DownloadAllService.NOTIFICATION_DOWNLOAD_ALL_COMPLETE));
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverAudioComplete, new IntentFilter(AudioPlayerService.NOTIFICATION_AUDIO_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverDownloadProgress);
        activity.unregisterReceiver(receiverDownloadAllProgress);
        activity.unregisterReceiver(receiverDownloadAllComplete);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverAudioComplete);
    }
    private BroadcastReceiver receiverDownloadProgress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int downloadStatus = intent.getIntExtra("downloadStatus", 0);
//            if (downloadStatus > 0) {
                Lesson lesson = intent.getParcelableExtra("lesson");
                System.out.println("status - progress: " + lesson.getDownloadStatusAudio() + " - " + lesson.getDownloadProgressAudio());
                for (int i=0; i<listLessons.size(); i++) {
                    if (lesson.getIdProperty().equals(listLessons.get(i).getIdProperty())) {
//                    if ( lesson.getIdProperty().equals(LessonsAdapter.mCurrentLesson) ) {
                        System.out.println("lesson: " + lesson.getIdProperty());
//                        List<Lesson> listLesson = intent.getParcelableArrayListExtra("listLessons");
////                        adapterLessons.setLessonListItems(listLesson);
                        listLessons.set(i,lesson);
//                        adapterLessons.setLessonListItems(listLesson);
                        adapterLessons.notifyDataSetChanged();
                        break;
                    }
                }
//            }
//            if ( DownloadService.IS_SERVICE_RUNNING && DownloadService.countDownloads==0 ){
//                System.out.println("Stopping download service...");
//                Intent service = new Intent(context, DownloadService.class);
//                service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//                DownloadService.IS_SERVICE_RUNNING = false;
//                context.startService(service);
//            }
        }
    };

    private BroadcastReceiver receiverDownloadAllProgress = new BroadcastReceiver() {
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

    private BroadcastReceiver receiverDownloadAllComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            boolean oneDownloadComplete = false; //At least one download has finished successfully
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadAllService.RESULT);
                String fileName = bundle.getString(DownloadAllService.FILENAME);
                String idLesson = bundle.getString("idLesson");
                String downloadType = bundle.getString("downloadType");
                List<Lesson> listLessonsUpdated = mStudy.getLessons();
                int downloadStatus = 0;
                if (resultCode == getActivity().RESULT_OK) {
                    System.out.println("Downloaded: " + fileName);
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
                DownloadAllService.decrementCount();
                if (oneDownloadComplete) {
                    if (DownloadAllService.countDownloads == 0){
                        // All lessons have been downloaded
                        DownloadAllService.downloading = false;
//                        new asyncGetStudyLessons().execute(mStudy);
                    }
                }
            }
        }
    };
    private BroadcastReceiver receiverAudioComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("LessonsFragment.onReceive: receiverAudioComplete");
            btnPlay.setImageResource(R.drawable.media_play);
//            tvIconPlayMini = (TextView) activity.findViewById(R.id.tv_icon_play_mini);
            btnPlayMini.setImageResource(R.drawable.icon_mini_player);
            if ( MainActivity.settings.getBoolean("switchAuto",true) ) {
                System.out.println("LessonsFragment.onReceive: receiverAudioComplete 2");
                DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "complete");
                FilesManager.lastLessonId = "";
                new asyncGetStudyLessons().execute(mStudy);
            }
        }
    };



}