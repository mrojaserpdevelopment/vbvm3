package com.erpdevelopment.vbvm.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.LessonListAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.FilesManager;
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
    private LinearLayout llDragView;
    private TextView tvSlide;
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

    public LessonsFragment() {
        // Required empty public constructor
    }

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
        imageLoader = new ImageLoader(getActivity());
    }

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
        rootViewMain = (RelativeLayout) (rootView.getParent()).getParent();
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        bottomBar = (BottomBar) rootViewMain.findViewById(R.id.bottomBar);
        slidingLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);
        llDragView = (LinearLayout) rootView.findViewById(R.id.dragView);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        tvSlide = (TextView) rootView.findViewById(R.id.tvTitleChapter);
        btnSlideUp = (ImageButton) rootView.findViewById(R.id.btnSlideUp);
        btnSlideDown = (ImageButton) rootView.findViewById(R.id.btnSlideDown);
        viewMiniPlayer = (RelativeLayout) rootView.findViewById(R.id.viewMiniPlayer);
        lvLessons = (ListView) rootView.findViewById(R.id.lv_lessons);
        imgStudy = (ImageView) rootView.findViewById(R.id.img_study);
        tvLessonCount = (TextView) rootView.findViewById(R.id.tv_lesson_count);

        listLessons = new ArrayList<>();
        adapter = new LessonListAdapter(getActivity(), listLessons);
        lvLessons.setAdapter(adapter);

        imageLoader.DisplayImage(mStudy.getThumbnailSource(), imgStudy);

//        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlayTest);
        btnSlideUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("slide has been clicked");
//                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//                actionBar.hide();
//                bottomBar.setVisibility(View.GONE);
//                viewMiniPlayer.setVisibility(View.GONE);
//                btnSlideDown.setVisibility(View.VISIBLE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        btnSlideDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//                actionBar.show();
//                viewMiniPlayer.setVisibility(View.VISIBLE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                btnSlideDown.setVisibility(View.GONE);
//                bottomBar.setVisibility(View.VISIBLE);
            }
        });

        slidingLayout.addPanelSlideListener(onSlideListener());
        new asyncGetStudyLessons().execute(mStudy);
//        adapter.setStudyDetailsListItems(mStudy.getLessons());
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                System.out.println("sliding...");
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                System.out.println("State has changed: " + newState.toString());
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    System.out.println("slide up has been clicked");
//                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.hide();
                    bottomBar.setVisibility(View.GONE);
                    viewMiniPlayer.setVisibility(View.GONE);
                    btnSlideDown.setVisibility(View.VISIBLE);
//                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    System.out.println("slide down " +
                            "has been clicked");
//                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.show();
//                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    viewMiniPlayer.setVisibility(View.VISIBLE);
                    btnSlideDown.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.VISIBLE);
                }
            }

        };
    }

    private class asyncGetStudyLessons extends AsyncTask<Study, String, String > {

        private ProgressDialog pDialog;
        private Study study;
        private List<Lesson> list = new ArrayList<Lesson>();

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Study... params) {
            study = params[0];
            //Save state flag for sync Webservice/DB
            WebServiceCall.lessonsInDB = MainActivity.settings.getBoolean("lessonsInDB", false);
            list = DBHandleLessons.getLessons(study.getIdProperty());
            //Check whether this lessons are in DB or synchronization is ON
            if ( (list.size() == 0) || ( !WebServiceCall.lessonsInDB ) ) {
                if ( !CheckConnectivity.isOnline(getActivity())) {
                    CheckConnectivity.showMessage(getActivity());
                } else {
                    list = new WebServiceCall().getStudyLessons(study); //get and save lessons to DB
                    Log.i("LessonsFragment", "Update DB with data from Webservice");
                }
            }
            study.setLessons(list);
            mStudy.setLessons(list);

//            resetDownloadingState();

            if (list != null)
                return "1";
            else
                return "";
        }

        protected void onPostExecute(String result) {

            pDialog.dismiss();//ocultamos progess dialog.

            if (result != ""){
//                adapter.setStudyDetailsListItems(mStudy.getLessons());
                adapter.setStudyDetailsListItems(mStudy.getLessons());
                //lvLessons.setAdapter(adapter);
                tvLessonCount.setText(String.valueOf(mStudy.getLessons().size()));
                lvLessons.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parentView, View view,
                                            int position, long id) {

//                        Lesson lesson = (Lesson) parentView.getItemAtPosition(position);
//                        if ( !CheckConnectivity.isOnline(BibleStudyLessonsActivity.this) && lesson.getDownloadStatus() != 1 ) {
//                            CheckConnectivity.showMessage(BibleStudyLessonsActivity.this);
//                        } else {
//                            adapter.setSelectedPosition(position);
//
//                            //Storing Data using SharedPreferences
//                            SharedPreferences.Editor edit = MainActivity.settings.edit();
//                            edit.remove("posLesson");
//                            edit.remove("studyTitle");
//                            edit.putInt("posLesson", position);
//                            edit.putString("studyTitle", study.getTitle());
//                            edit.commit();
//
//                            // save index and top position
//                            int index = lvLessons.getFirstVisiblePosition();
//                            edit.remove("indexLesson").commit();
//                            edit.putInt("indexLesson", index).commit();
//
//                            lesson.setStudyThumbnailSource(study.getThumbnailSource());
//                            lesson.setStudyLessonsSize(study.getLessons().size());
//
//                            if (!(lesson.getIdProperty().equals(FilesManager.lastLessonId))){
//                                AudioPlayerService.created = false;
//                                //save current/old position in track before updating to new position
//                                if (!FilesManager.lastLessonId.equals("")) {
//                                    System.out.println("old lesson Id is: " + FilesManager.lastLessonId);
//                                    DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, (int)AudioPlayerService.currentPositionInTrack);
//                                    System.out.println("position saved is: " + AudioPlayerService.currentPositionInTrack);
//                                }
//                                Lesson oldLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
////								if ( !oldLesson.getState().equals("complete") )
//                                if ( oldLesson.getState().equals("playing") )
//                                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
//                                //Update position in track with new selected lesson's
//                                Lesson newLesson = DBHandleLessons.getLessonById(lesson.getIdProperty());
////								DBHandleLessons.updateLessonState(newLesson.getIdProperty(), newLesson.getCurrentPosition(), "playing");
//                                AudioPlayerService.currentPositionInTrack = newLesson.getCurrentPosition();
//                                AudioPlayerService.savedOldPositionInTrack = AudioPlayerService.currentPositionInTrack;
//                                System.out.println("position restored is: " + AudioPlayerService.currentPositionInTrack);
//                            }
//
//                            FilesManager.positionLessonInList = position;
//                            FilesManager.lastLessonId = lesson.getIdProperty();
//
//                            Lesson les = null;
//                            for (int i=0; i<study.getLessons().size(); i++){
//                                les = new Lesson();
//                                les.setIdProperty(study.getLessons().get(i).getIdProperty());
//                                les.setAudioSource(study.getLessons().get(i).getAudioSource());
//                                les.setTitle(study.getLessons().get(i).getTitle());
//                                les.setLessonsDescription(study.getLessons().get(i).getLessonsDescription());
//                                les.setStudyThumbnailSource(study.getThumbnailSource());
//                                les.setStudyLessonsSize(study.getLessons().size());
//                                les.setTranscript(study.getLessons().get(i).getTranscript());
//                                les.setStudentAid(study.getLessons().get(i).getStudentAid());
//                                les.setTeacherAid(study.getLessons().get(i).getTeacherAid());
//                                listTempLesson.add(les);
//                            }
//
//                            AudioPlayerService.listTempLesson2 = listTempLesson;
//
//                            Intent i = new Intent(BibleStudyLessonsActivity.this, AudioControllerActivity.class);
////			                Intent i = getIntent();
//                            i.putExtra("position", position);
//                            i.putExtra("lessonIdProperty", lesson.getIdProperty());
//                            i.putExtra("thumbnailSource", study.getThumbnailSource());
//                            i.putExtra("description", study.getLessons().get(position).getLessonsDescription());
//                            i.putExtra("title", study.getLessons().get(position).getTitle());
//                            i.putExtra("size", study.getLessons().size());
//                            i.putExtra("readSource", study.getLessons().get(position).getTranscript());
//                            i.putExtra("presentSource", study.getLessons().get(position).getStudentAid());
//                            i.putExtra("handoutSource", study.getLessons().get(position).getTeacherAid());
//                            i.putExtra("study", mStudy);
////							setResult(RESULT_OK, i);
////							finish();
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //This will clear all the activities on top of AudioControllerActivity
//                            startActivity(i);
////							finish();
//                        }
                    }
                });
            }else{
                Log.e("onPostExecute=", "List is null");
            }
        }
    }

//    private void deleteAllLessons() {
//
//        if ( !DownloadService.downloading ) {
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


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
//        new asyncGetStudyLessons().execute(mStudy);
//        adapter.setStudyDetailsListItems(mStudy.getLessons());
//        if ( DownloadService.downloading ){
//            tvDownloading.setText("Downloading all: " + DownloadService.downloadAllTitle);
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
}