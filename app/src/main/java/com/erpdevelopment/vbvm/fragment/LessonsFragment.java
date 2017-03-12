package com.erpdevelopment.vbvm.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.erpdevelopment.vbvm.activity.MainActivity;
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
import com.erpdevelopment.vbvm.utils.Constants;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;
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
    private Study mStudy;
    private ImageButton btnPlay;
    private ImageButton btnPlayMini;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        fileCache = new FileCache(MainActivity.mainCtx);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStudy = getArguments().getParcelable("study");
        return inflater.inflate(R.layout.fragment_lessons2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
        btnPlayMini = (ImageButton) activity.findViewById(R.id.btn_play_mini);

        imgStudy = (ImageView) rootView.findViewById(R.id.img_study);
        imgStudyBg = (ImageView) rootView.findViewById(R.id.img_study_bg);

        Utilities.loadStudyImages(activity, mStudy.getThumbnailSource(), 300, imgStudy);
        Utilities.loadStudyImages(activity, mStudy.getThumbnailSource(), 600, imgStudyBg);

        //Show description of study
        ExpandableTextView expTv1 = (ExpandableTextView) activity.findViewById(R.id.expand_text_view);
        expTv1.setText(mStudy.getStudiesDescription());

        new asyncGetStudyLessons().execute(mStudy);
    }

    private class asyncGetStudyLessons extends AsyncTask<Study, String, String > {

        private List<Lesson> listIncomplete = new ArrayList<Lesson>();
        private List<Lesson> listComplete = new ArrayList<Lesson>();

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
            for (Lesson lesson : listLessons) {
                if ( lesson.getState().equals(Constants.LESSON_STATE.COMPLETE) )
                    listComplete.add(lesson);
                else
                    listIncomplete.add(lesson);
            }
            listLessons.clear();
            if (listIncomplete.size()>0) {
                Lesson lSectionIncomplete = new Lesson();
                lSectionIncomplete.setSection(true);
                lSectionIncomplete.setSectionCompleted(false);
                listLessons.add(lSectionIncomplete);
                listLessons.addAll(listIncomplete);
            }
            if (listComplete.size()>0) {
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
        if ( !DownloadAllService.downloading && DownloadService.countDownloads == 0) {
            for (int i = 0; i < listLessons.size(); i++) {
                Lesson lesson = listLessons.get(i);
                if ( !lesson.getAudioSource().equals("") && (lesson.getDownloadStatusAudio()!=1) )
                    startDownload(lesson.getIdProperty(),lesson.getAudioSource(),Constants.LESSON_FILE.AUDIO);
                if ( !lesson.getTeacherAid().equals("") && (lesson.getDownloadStatusTeacherAid()!=1) )
                    startDownload(lesson.getIdProperty(),lesson.getTeacherAid(),Constants.LESSON_FILE.TEACHER);
                if ( !lesson.getTranscript().equals("") && (lesson.getDownloadStatusTranscript()!=1) )
                    startDownload(lesson.getIdProperty(),lesson.getTranscript(),Constants.LESSON_FILE.TRANSCRIPT);
            }
        }
    }

    private void startDownload(String idLesson, String url, String downloadType) {
        intentDownloadAll = new Intent(getActivity(), DownloadAllService.class);
        intentDownloadAll.putExtra("idLesson", idLesson);
        intentDownloadAll.putExtra("url", url);
        intentDownloadAll.putExtra("downloadType", downloadType);
        getActivity().startService(intentDownloadAll);
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
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, Constants.LESSON_FILE.AUDIO);
                }
                if ( !urlTranscript.equals("") && lesson.getDownloadStatusTranscript()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager2.getFileNameFromUrl(urlTranscript)).delete();
                    lesson.setDownloadStatusTranscript(0);
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, Constants.LESSON_FILE.TRANSCRIPT);
                }
                if ( !urlTeacherAid.equals("") && lesson.getDownloadStatusTeacherAid()==1 ) {
                    new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager2.getFileNameFromUrl(urlTeacherAid)).delete();
                    lesson.setDownloadStatusTeacherAid(0);
                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, Constants.LESSON_FILE.TEACHER);
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
                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0, Constants.LESSON_FILE.AUDIO);
                if ( lesson.getDownloadStatusTeacherAid() == 2 )
                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0, Constants.LESSON_FILE.TEACHER);
                if ( lesson.getDownloadStatusTranscript() == 2 )
                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0, Constants.LESSON_FILE.TRANSCRIPT);
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
                markAllLessons(Constants.LESSON_STATE.COMPLETE);
                return true;
            case R.id.action_mark_all_incomplete:
                markAllLessons(Constants.LESSON_STATE.NEW);
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
        System.out.println("LessonsFragment.onResume");
        super.onResume();
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverDownloadProgress, new IntentFilter(DownloadService.NOTIFICATION_DOWNLOAD_PROGRESS));
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverDownloadAllProgress, new IntentFilter(DownloadAllService.NOTIFICATION_DOWNLOAD_ALL_PROGRESS));
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverDownloadAllComplete, new IntentFilter(DownloadAllService.NOTIFICATION_DOWNLOAD_ALL_COMPLETE));
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverAudioComplete, new IntentFilter(AudioPlayerService.NOTIFICATION_AUDIO_COMPLETE));
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiverAudioProgress, new IntentFilter(AudioPlayerService.NOTIFICATION_AUDIO_PROGRESS));
    }

    @Override
    public void onPause() {
        System.out.println("LessonsFragment.onPause");
        super.onPause();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverDownloadProgress);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverDownloadAllProgress);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverDownloadAllComplete);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverAudioComplete);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiverAudioProgress);
    }
    private BroadcastReceiver receiverDownloadProgress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int downloadStatus = intent.getIntExtra("downloadStatus", 0);
            String messageError = intent.getStringExtra("messageError");
            Lesson lesson = intent.getParcelableExtra("lesson");
            for (int i = 0; i < listLessons.size(); i++) {
                if (lesson.getIdProperty().equals(listLessons.get(i).getIdProperty())) {
                    listLessons.set(i, lesson);
                    adapterLessons.notifyDataSetChanged();
                    break;
                }
            }
            if ( downloadStatus == 1 && !DownloadAllService.downloading && DownloadService.countDownloads == 0 )
                adapterLessons.startAudio(lesson);
            if (!messageError.equals(""))
                Toast.makeText(activity, messageError, Toast.LENGTH_SHORT).show();
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
                for (int i=0; i<listLessons.size(); i++) {
                    if (listLessons.get(i).getIdProperty().equals(idLesson)) {
                        switch (downloadType) {
                            case Constants.LESSON_FILE.AUDIO:
                                listLessons.get(i).setDownloadProgressAudio(downloadProgress);
                                listLessons.get(i).setDownloadStatusAudio(2);
                                break;
                            case Constants.LESSON_FILE.TEACHER:
                                listLessons.get(i).setDownloadProgressTeacher(downloadProgress);
                                listLessons.get(i).setDownloadStatusTeacherAid(2);
                                break;
                            case Constants.LESSON_FILE.TRANSCRIPT:
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
            boolean oneDownloadComplete = false;
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadAllService.RESULT);
                String idLesson = bundle.getString("idLesson");
                String downloadType = bundle.getString("downloadType");
                List<Lesson> listLessonsUpdated = mStudy.getLessons();
                int downloadStatus = 0;
                if (resultCode == getActivity().RESULT_OK) {
                    oneDownloadComplete = true;
                    downloadStatus = 1;
                }
                for (int i=0; i<listLessonsUpdated.size(); i++) {
                    if (listLessonsUpdated.get(i).getIdProperty().equals(idLesson)) {
                        switch (downloadType) {
                            case Constants.LESSON_FILE.AUDIO:
                                listLessonsUpdated.get(i).setDownloadStatusAudio(downloadStatus);
                                listLessonsUpdated.get(i).setDownloadProgressAudio(0);
                                break;
                            case Constants.LESSON_FILE.TEACHER:
                                listLessonsUpdated.get(i).setDownloadStatusTeacherAid(downloadStatus);
                                listLessonsUpdated.get(i).setDownloadProgressTeacher(0);
                                break;
                            case Constants.LESSON_FILE.TRANSCRIPT:
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
                    }
                }
            }
        }
    };

    private BroadcastReceiver receiverAudioComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btnPlay.setImageResource(R.drawable.media_play);
            btnPlayMini.setImageResource(R.drawable.icon_mini_play);
            if ( MainActivity.settings.getBoolean("switchAuto",true) ) {
                DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, Constants.LESSON_STATE.COMPLETE);
                new asyncGetStudyLessons().execute(mStudy);
            }
            FilesManager.lastLessonId = "";
        }
    };

    private BroadcastReceiver receiverAudioProgress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Lesson lesson = intent.getParcelableExtra("lessonPlaying");
            for (int i = 0; i < listLessons.size(); i++) {
                if (lesson.getIdProperty().equals(listLessons.get(i).getIdProperty())) {
//                    System.out.println("LessonsFragment.onReceive 1");
                    listLessons.set(i, lesson);
                    adapterLessons.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

}