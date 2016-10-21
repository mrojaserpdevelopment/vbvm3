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
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.LessonListAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.service.DownloadServiceTest;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;
import com.roughike.bottombar.BottomBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class LessonsFragment extends Fragment {

    private View rootView;
    private SlidingUpPanelLayout slidingLayout;
    private ActionBar actionBar;
    private BottomBar bottomBar;
    private RelativeLayout viewMiniPlayer;
    private RelativeLayout rootViewMain;
//    private LinearLayout llDragView;
//    private TextView tvSlide;
    private TextView tvLessonCount;
    private ImageButton btnPlay;
    private ImageButton btnSlideUp;
    private ImageButton btnSlideDown;
    private ImageView imgStudy;
    private ListView lvLessons;
    private LessonListAdapter adapter;
    private List<Lesson> listLessons;
    private Study mStudy;
    private ImageLoader imageLoader;
    private Activity activity;
    private ArrayList<Lesson> listTempLesson = new ArrayList<Lesson>();
    private RelativeLayout rlAudioPlayerSlide;
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

    public Intent getIntentDownloadAll() {
        return intentDownloadAll;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        imageLoader = new ImageLoader(getActivity());
        activity = getActivity();
        fileCache = new FileCache(MainActivity.mainCtx);
        activity.registerReceiver(receiverDownloadProgress, new IntentFilter(DownloadServiceTest.NOTIFICATION_PROGRESS));
        activity.registerReceiver(receiverDownloadComplete, new IntentFilter(DownloadServiceTest.NOTIFICATION_COMPLETE));
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
                adapter.setLessonListItems(listLessons);
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
                    Toast.makeText(getActivity(), "Downloaded: " + fileName, Toast.LENGTH_LONG).show();
                    oneDownloadComplete = true;
                    downloadStatus = 1;
//                    new asyncGetStudyLessons().execute(mStudy);
//                    List<Lesson> listLessonsUpdated = mStudy.getLessons();
//                    for (int i=0; i<listLessonsUpdated.size(); i++) {
//                        if (listLessonsUpdated.get(i).getIdProperty().equals(idLesson)) {
//                            switch (downloadType) {
//                                case "audio":
//                                    listLessonsUpdated.get(i).setDownloadStatusAudio(1);
//                                    listLessonsUpdated.get(i).setDownloadProgressAudio(0);
//                                    break;
//                                case "teacher":
//                                    listLessonsUpdated.get(i).setDownloadStatusTeacherAid(1);
//                                    listLessonsUpdated.get(i).setDownloadProgressTeacher(0);
//                                    break;
//                                case "transcript":
//                                    listLessonsUpdated.get(i).setDownloadStatusTranscript(1);
//                                    listLessonsUpdated.get(i).setDownloadProgressTranscript(0);
//                                    break;
//                            }
//                            break;
//                        }
//                    }
//                    adapter.setLessonListItems(listLessons);
                } else {
                    System.out.println("Download canceled: " + fileName);
//                    Toast.makeText(getActivity(), "Download canceled: " + fileName, Toast.LENGTH_LONG).show();
//                    for (int i=0; i<listLessonsUpdated.size(); i++) {
//                        if (listLessonsUpdated.get(i).getIdProperty().equals(idLesson)) {
//                            switch (downloadType) {
//                                case "audio":
//                                    listLessonsUpdated.get(i).setDownloadStatusAudio(0);
////                                    listLessonsUpdated.get(i).setDownloadProgressAudio(0);
//                                    break;
//                                case "teacher":
//                                    listLessonsUpdated.get(i).setDownloadStatusTeacherAid(0);
////                                    listLessonsUpdated.get(i).setDownloadProgressTeacher(0);
//                                    break;
//                                case "transcript":
//                                    listLessonsUpdated.get(i).setDownloadStatusTranscript(0);
////                                    listLessonsUpdated.get(i).setDownloadProgressTranscript(0);
//                                    break;
//                            }
//                            break;
//                        }
//                    }
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
                adapter.setLessonListItems(listLessonsUpdated);
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
//        rootViewMain = (RelativeLayout) (rootView.getParent()).getParent();
//        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        bottomBar = (BottomBar) rootViewMain.findViewById(R.id.bottomBar);
//        slidingLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);
////        llDragView = (LinearLayout) rootView.findViewById(R.id.dragView);
//        btnPlay = (ImageButton) rootView.findViewById(R.id.btn_play_mini);
////        tvSlide = (TextView) rootView.findViewById(R.id.tv_title_chapter);
//        btnSlideUp = (ImageButton) rootView.findViewById(R.id.btn_slide_up);
//        btnSlideDown = (ImageButton) rootView.findViewById(R.id.btn_slide_down);
//        viewMiniPlayer = (RelativeLayout) rootView.findViewById(R.id.view_mini_player);
        lvLessons = (ListView) rootView.findViewById(R.id.lv_lessons);
        imgStudy = (ImageView) rootView.findViewById(R.id.img_study);
        tvLessonCount = (TextView) rootView.findViewById(R.id.tv_lesson_count);
//        rlAudioPlayerSlide = (RelativeLayout) rootView.findViewById(R.id.rl_audio_player_slide);

        listLessons = new ArrayList<>();
        adapter = new LessonListAdapter(getActivity(), listLessons, rootView);
        lvLessons.setAdapter(adapter);

        imageLoader.DisplayImage(mStudy.getThumbnailSource(), imgStudy);

//        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlayTest);
//        btnSlideUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                System.out.println("slide has been clicked");
////                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
////                actionBar.hide();
////                bottomBar.setVisibility(View.GONE);
////                viewMiniPlayer.setVisibility(View.GONE);
////                btnSlideDown.setVisibility(View.VISIBLE);
//                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//            }
//        });
//
//        btnSlideDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
////                actionBar.show();
////                viewMiniPlayer.setVisibility(View.VISIBLE);
//                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
////                btnSlideDown.setVisibility(View.GONE);
////                bottomBar.setVisibility(View.VISIBLE);
//            }
//        });
//
//        slidingLayout.addPanelSlideListener(onSlideListener());
        new asyncGetStudyLessons().execute(mStudy);
//        adapter.setLessonListItems(mStudy.getLessons());
    }

//    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
//        return new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View view, float v) {
//                System.out.println("sliding...");
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                System.out.println("State has changed: " + newState.toString());
//                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    System.out.println("slide up has been clicked");
////                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//                    actionBar.hide();
//                    bottomBar.setVisibility(View.GONE);
//                    viewMiniPlayer.setVisibility(View.GONE);
//                    btnSlideDown.setVisibility(View.VISIBLE);
//                    rlAudioPlayerSlide.setVisibility(View.VISIBLE);
////                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//                }
//                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                    System.out.println("slide down " +
//                            "has been clicked");
////                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//                    actionBar.show();
////                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                    viewMiniPlayer.setVisibility(View.VISIBLE);
//                    btnSlideDown.setVisibility(View.GONE);
//                    rlAudioPlayerSlide.setVisibility(View.GONE);
//                    bottomBar.setVisibility(View.VISIBLE);
//                }
//            }
//
//        };
//    }

    private class asyncGetStudyLessons extends AsyncTask<Study, String, String > {

        private ProgressDialog pDialog;

        private List<Lesson> list = new ArrayList<Lesson>();

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Study... params) {
//            Study study = params[0];
            //Save state flag for sync Webservice/DB
            WebServiceCall.lessonsInDB = MainActivity.settings.getBoolean("lessonsInDB", false);
            list = DBHandleLessons.getLessons(params[0].getIdProperty());
            //Check whether this lessons are in DB or synchronization is ON
            if ( (list.size() == 0) || ( !WebServiceCall.lessonsInDB ) ) {
                if ( !CheckConnectivity.isOnline(getActivity())) {
                    CheckConnectivity.showMessage(getActivity());
                } else {
                    list = new WebServiceCall().getStudyLessons(params[0]); //get and save lessons to DB
                    Log.i("LessonsFragment", "Update DB with data from Webservice");
                }
            }
//            study.setLessons(list);
            mStudy.setLessons(list);
            resetDownloadingState();
//            if (list != null)
//                return "1";
//            else
//                return "";
            return null;
        }

        protected void onPostExecute(String result) {
           pDialog.dismiss();
//            if (result != "")
                adapter.setLessonListItems(mStudy.getLessons());
//            else
//                Log.e("onPostExecute=", "List is null");
        }
    }

//    private void deleteAllLessons() {
//
//        if ( !DownloadServiceTest.downloading ) {
//            boolean deleted = false;
//            int count = 0;
//            for (int i=0; i<mStudy.getLessons().size(); i++){
//
//                String audioUrl = mStudy.getLessons().get(i).getAudioSource();
//                String pdfUrl1 = mStudy.getLessons().get(i).getTranscript();
//                String pdfUrl2 = mStudy.getLessons().get(i).getTeacherAid();
//                String pdfUrl3 = mStudy.getLessons().get(i).getStudentAid();
//                int status = mStudy.getLessons().get(i).getDownloadStatus();
//                String idLesson = mStudy.getLessons().get(i).getIdProperty();
//                if ( status==1 || status==2 ){
//                    if (!audioUrl.equals(""))
//                        new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(audioUrl)).delete();
//                    if (!pdfUrl1.equals(""))
//                        new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(pdfUrl1)).delete();
//                    if (!pdfUrl2.equals(""))
//                        new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(pdfUrl2)).delete();
//                    if (!pdfUrl3.equals(""))
//                        new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(pdfUrl3)).delete();
//                    DBHandleLessons.updateLessonDownloadStatus(idLesson, 0);
//                    count++;
//                    deleted = true;
//                }
//            }
//            if (deleted) {
//                Toast.makeText(this, count + " lesson(s) deleted", Toast.LENGTH_LONG).show();
//                new asyncGetStudyLessons().execute(mStudy);
//            } else {
//                Toast.makeText(this, "No lesson(s) to delete", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(BibleStudyLessonsActivity.this,
//                    "Please wait. A download is in progress...",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    private void markAllLessons(String state) {
        List<Lesson> lessons = new ArrayList<Lesson>();
        for (Lesson lesson : mStudy.getLessons()){
            DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
            lesson.setCurrentPosition(0);
            lesson.setState(state);
            lessons.add(lesson);
        }
        adapter.setLessonListItems(lessons);
    }

    private void downloadAllLessons() {
        if ( !DownloadServiceTest.downloading && !DownloadService.downloading) {
            for (int i = 0; i < mStudy.getLessons().size(); i++) {
                Lesson lesson = mStudy.getLessons().get(i);
                //Download if lesson is not downloaded or is downloading
                if ( !lesson.getAudioSource().isEmpty() && (lesson.getDownloadStatusAudio()!=1) ) {
                    System.out.println("Downloading audio: " + lesson.getAudioSource());
//                    TextView tvPlayMini = (TextView) rootView.findViewById(R.id.tv_icon_play_mini);
//                    tvPlayMini.setText(activity.getResources().getString(R.string.fa_icon_stop));
//                    tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                    startDownload(lesson.getIdProperty(),lesson.getAudioSource(),"audio");
                }
                if ( !lesson.getTeacherAid().isEmpty() && (lesson.getDownloadStatusTeacherAid()!=1) ) {
                    System.out.println("Downloading teacher: " + lesson.getTeacherAid());
                    startDownload(lesson.getIdProperty(),lesson.getTeacherAid(),"teacher");
                }
                if ( !lesson.getTranscript().isEmpty() && (lesson.getDownloadStatusTranscript()!=1) ) {
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
        adapter.setIntentServiceDownloadAll(intentDownloadAll);
        DownloadServiceTest.incrementCount();
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
//        new asyncGetStudyLessons().execute(mStudy);
//        adapter.setLessonListItems(mStudy.getLessons());
//        if ( DownloadServiceTest.downloading ){
//            tvDownloading.setText("Downloading all: " + DownloadServiceTest.downloadAllTitle);
//            tvDownloading.setVisibility(View.VISIBLE);
//            tvTitle.setVisibility(View.GONE);
//            imgMenuBarOptions.setVisibility(View.GONE);
//        } else {
//            // if not downloading study, check and reset state for each lesson
//            for (Lesson lesson: mStudy.getLessons()){
//                if ( lesson.getDownloadStatus() == 2 )
//                    DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0);
//            }
//            fileCache.clearTempFolder();
//            tvDownloading.setVisibility(View.GONE);
//            tvTitle.setVisibility(View.VISIBLE);
//            imgMenuBarOptions.setVisibility(View.VISIBLE);
//        }
        super.onResume();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}