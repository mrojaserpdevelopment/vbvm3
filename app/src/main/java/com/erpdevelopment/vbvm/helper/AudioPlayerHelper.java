package com.erpdevelopment.vbvm.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.FontManager;
import com.erpdevelopment.vbvm.utils.IMediaPlayerServiceClient;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;
import com.roughike.bottombar.BottomBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class AudioPlayerHelper implements SeekBar.OnSeekBarChangeListener, IMediaPlayerServiceClient {
    private Activity activity;
//    private ImageButton btnSlideDown;
    private TextView tvIconSlideDown;
    private ImageView imgPlayerStudy;
    private SeekBar sbAudioPlayer;
    private TextView tvCurrentDuration;
    private TextView tvTotalDuration;
    private TextView tvPlayerLessonTitle;
    private TextView tvPlayerLessonDescription;
    private TextView tvPlayerSpeed;
    private ImageButton btnBackward;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private TextView tvIconVolumeDown;
    private SeekBar sbVolumeControl;
    private TextView tvIconVolumeUp;
    private ImageButton btnPlayMini;
    private TextView tvLessonTitleMini;
    private TextView tvLessonDescriptionMini;
    private ImageButton btnSlideUp;

    private RelativeLayout viewMiniPlayer;

    private ActionBar actionBar;
    private BottomBar bottomBar;
    private SlidingUpPanelLayout slidingLayout;
    private RelativeLayout rlAudioPlayerSlide;

    private AudioPlayerService mService;
    private int seekForwardTime = 30000; // 15 sec
    private int seekBackwardTime = 30000; // 15 sec

    private int currentSongIndex = 0;
    private String thumbnailSource;
    private String description;
    private String title;
    private int size = 0;
    private ImageLoader imageLoader;
    private Utilities utils = new Utilities();

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            mService = ((AudioPlayerService.LocalBinder) binder).getService();

            System.out.println("onServiceConnected...");
//            if ( mService.isPlayingLesson() ) {
//                if ( AudioPlayerService.created ) {
                    btnPlay.setImageResource(R.drawable.media_pause);
                    System.out.println("mp is playing lesson");
//                }
//            }
            //send this instance to the service, so it can make callbacks on this instance as a client
            mService.setClient(AudioPlayerHelper.this);
            mService.playAudio(currentSongIndex);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public AudioPlayerHelper(Activity activity) {
        this.activity = activity;
    }

    public void setBundleExtras(Bundle extras) {
        currentSongIndex = extras.getInt("position");
        thumbnailSource = extras.getString("thumbnailSource");
        description = extras.getString("description");
        title = extras.getString("title");
        size = extras.getInt("size");
    }

    public void initContext(View rootView) {
//        btnSlideDown = (ImageButton) activity.findViewById(R.id.btn_slide_down);
        tvIconSlideDown = (TextView) activity.findViewById(R.id.tv_icon_slide_down);
        imgPlayerStudy = (ImageView) activity.findViewById(R.id.img_player_study);
        sbAudioPlayer = (SeekBar) activity.findViewById(R.id.sb_audio_player);
        tvCurrentDuration = (TextView) activity.findViewById(R.id.tv_current_duration);
        tvTotalDuration = (TextView) activity.findViewById(R.id.tv_total_duration);
        tvPlayerLessonTitle = (TextView) activity.findViewById(R.id.tv_player_lesson_title);
        tvPlayerLessonDescription = (TextView) activity.findViewById(R.id.tv_player_lesson_description);
        tvPlayerSpeed = (TextView) activity.findViewById(R.id.tv_player_speed);
        btnBackward = (ImageButton) activity.findViewById(R.id.btn_backward);
        btnPlay = (ImageButton) activity.findViewById(R.id.btn_play);
        btnForward = (ImageButton) activity.findViewById(R.id.btn_forward);
        tvIconVolumeDown = (TextView) activity.findViewById(R.id.tv_icon_volume_down);
        tvIconVolumeUp = (TextView) activity.findViewById(R.id.tv_icon_volume_up);
        sbVolumeControl = (SeekBar) activity.findViewById(R.id.sb_volume_control);
        btnPlayMini = (ImageButton) activity.findViewById(R.id.btn_play_mini);
        tvLessonTitleMini = (TextView) activity.findViewById(R.id.tv_lesson_title_mini);
        tvLessonDescriptionMini = (TextView) activity.findViewById(R.id.tv_lesson_description_mini);
        btnSlideUp = (ImageButton) activity.findViewById(R.id.btn_slide_up);
        viewMiniPlayer = (RelativeLayout) rootView.findViewById(R.id.view_mini_player);
        actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        rlAudioPlayerSlide = (RelativeLayout) rootView.findViewById(R.id.rl_audio_player_slide);
        RelativeLayout rootViewMain = (RelativeLayout) (rootView.getParent()).getParent();
        bottomBar = (BottomBar) rootViewMain.findViewById(R.id.bottomBar);
        slidingLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);
        imageLoader = new ImageLoader(activity);
        imageLoader.DisplayImage(thumbnailSource,imgPlayerStudy,300);

        tvIconVolumeDown.setTypeface(FontManager.getTypeface(activity,FontManager.FONTAWESOME));
        tvIconVolumeUp.setTypeface(FontManager.getTypeface(activity,FontManager.FONTAWESOME));
        tvIconSlideDown.setTypeface(FontManager.getTypeface(activity,FontManager.FONTAWESOME));

        btnSlideUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        tvIconSlideDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        slidingLayout.addPanelSlideListener(onSlideListener());
//        adapter.setLessonListItems(mStudy.getLessons());

//        btnPlayMini = (ImageButton) rootView.findViewById(R.id.btn_play_mini);
//        tvSlide = (TextView) rootView.findViewById(R.id.tv_title_chapter);
//        btnSlideUp = (ImageButton) rootView.findViewById(R.id.btn_slide_up);
//        btnSlideDown = (ImageButton) rootView.findViewById(R.id.btn_slide_down);
//        viewMiniPlayer = (RelativeLayout) rootView.findViewById(R.id.view_mini_player);

        sbAudioPlayer.setOnSeekBarChangeListener(this); // Register listener for updating progress bar when playing audio

//        Bundle b = getIntent().getExtras();
//        currentSongIndex = extras.getInt("position");
//        thumbnailSource = extras.getString("thumbnailSource");
//        description = extras.getString("description");
//        title = extras.getString("title");
//        size = extras.getInt("size");
//        readSource = b.getString("readSource");
//        presentSource = b.getString("presentSource");
//        handoutSource = b.getString("handoutSource");

//        if ( !(readSource.equals("")) && readSource != null )
//            llImageRead.setVisibility(View.VISIBLE);
//        if ( !(presentSource.equals("")) && presentSource != null )
//            llImagePresent.setVisibility(View.VISIBLE);
//        if ( !(handoutSource.equals("")) && handoutSource != null )
//            llImageHandout.setVisibility(View.VISIBLE);

//        imageLoader.DisplayImage(thumbnailSource, imgPlayerStudy);
//
//        tvPlayerLessonDescription.setText(description);
//        tvPlayerLessonTitle.setText(title);

        bindToService();

        if ( AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().equals("") ) {
            btnPlay.setEnabled(false);
            btnBackward.setEnabled(false);
            btnForward.setEnabled(false);
        } else {
            btnPlay.setEnabled(true);
            btnBackward.setEnabled(true);
            btnForward.setEnabled(true);
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sbAudioPlayer.setEnabled(true);
                if(mService.isPlayingLesson()){
                    if(mService.isCreated()){
                        mService.pauseLesson();
                        btnPlay.setImageResource(R.drawable.media_play);
                    }
                }else{
                    if(mService.isCreated()){
                        mService.playAudio();
                    }else{
                        if (mService.isStopped())
                            AudioPlayerService.playAfterStop = true;
                        mService.playAudio(currentSongIndex);
                        AudioPlayerService.currentPositionInTrack = 0;
                    }
                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "partial");
                    FilesManager.lastLessonId = AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty();
                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "playing");
                    btnPlay.setImageResource(R.drawable.media_pause);
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mService.isPlayingLesson()){
                    mService.removeHandlerCallbacks();
                    int currentPosition = mService.getCurrentPosition();
                    int totalDuration = mService.getDuration();
                    // check if seekForward time is lesser than audio duration
                    if(currentPosition + seekForwardTime <= totalDuration){
                        sendStopTrackingTouch(currentPosition + seekForwardTime);
                    }else{
                        sendStopTrackingTouch(totalDuration);
                    }
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mService.isPlayingLesson()){
                    mService.removeHandlerCallbacks();
                    int currentPosition = mService.getCurrentPosition();
                    // check if seekBackward time is greater than 0 sec
                    if(currentPosition - seekBackwardTime >= 0){
                        sendStopTrackingTouch(currentPosition - seekBackwardTime);
                    }else{
                        sendStopTrackingTouch(0);
                    }
                }
            }
        });

        LocalBroadcastManager.getInstance(activity).registerReceiver(progressReceiver,
                new IntentFilter("UpdateProgress"));
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                System.out.println("sliding...");
            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    actionBar.hide();
                    bottomBar.setVisibility(View.GONE);
                    viewMiniPlayer.setVisibility(View.GONE);
//                    btnSlideDown.setVisibility(View.VISIBLE);
//                    tvIconSlideDown.setVisibility(View.VISIBLE);
                    rlAudioPlayerSlide.setVisibility(View.VISIBLE);
                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    actionBar.show();
                    viewMiniPlayer.setVisibility(View.VISIBLE);
//                    btnSlideDown.setVisibility(View.GONE);
                    tvIconSlideDown.setVisibility(View.GONE);
//                    rlAudioPlayerSlide.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    /**
     * Binds to the instance of AudioPlayerService. If no instance of AudioPlayerService exists, it first starts
     * a new instance of the service.
     */
    public void bindToService() {
        Intent intent = new Intent(activity, AudioPlayerService.class);
//        intent.putExtra("currentSongIndex", currentSongIndex);

        if (AudioPlayerServiceRunning()) {
            // Bind to LocalService
            activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            activity.startService(intent);
            activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /** Determines if the AudioPlayerService is already running.
     * @return true if the service is running, false otherwise.
     */
    private boolean AudioPlayerServiceRunning() {
        ActivityManager manager = (ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.erpdevelopment.vbvm.activity.AudioPlayerService".equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String totalDurationLabel = intent.getStringExtra("totalDurationLabel");
            String currentDurationLabel = intent.getStringExtra("currentDurationLabel");
            int progress = intent.getIntExtra("progress", -1);
            tvCurrentDuration.setText(currentDurationLabel);
            tvTotalDuration.setText(totalDurationLabel);
            sbAudioPlayer.setProgress(progress);
            String currentTitleLabel = intent.getStringExtra("currentTitle");
            String currentDescriptionLabel = intent.getStringExtra("currentDescription");
            tvPlayerLessonTitle.setText(currentTitleLabel);
            tvPlayerLessonDescription.setText(currentDescriptionLabel);
            boolean isLessonComplete = intent.getBooleanExtra("isLessonComplete", false);
//            if ( isLessonComplete )
//                updateBibleStudyProgress();
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setProgress(progress);
    }
    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        sendStartTrackingTouch();
    }

    /**
     * When user stops moving the progress handler
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int totalDuration = (int) FilesManager.totalDuration;
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        sendStopTrackingTouch(currentPosition);
    }

    private void sendStartTrackingTouch() {
        Intent intent = new Intent("StartTrackingTouch");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    private void sendStopTrackingTouch(int currentPosition) {
        Intent intent = new Intent("StopTrackingTouch");
        intent.putExtra("currentPosition", currentPosition);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

//    @Override
//    protected void onDestroy() {
//        unbindService(this.mConnection);
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver);
//        super.onDestroy();
//    }
    @Override
    public void onInitializePlayerStart(String message) {
//        if ( !AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().trim().equals("") ) {
//            mProgressDialog = ProgressDialog.show(this, "", message, true);
//            mProgressDialog.getWindow().setGravity(Gravity.CENTER);
//            mProgressDialog.setCancelable(false);
//        }
    }
    @Override
    public void onInitializePlayerSuccess() {
//        if ( !AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().trim().equals("") )
//            mProgressDialog.dismiss();
    }
    @Override
    public void onError() {

    }

//    @Override
//    protected void onResume() {
//        study = (Study) getIntent().getExtras().getParcelable("study");
//        FilesManager.lastStudyTitle = study.getTitle();
//        SharedPreferences.Editor editor = MainActivity.settings.edit();
//        editor.remove("lastStudyTitle");
//        editor.putString("lastStudyTitle", FilesManager.lastStudyTitle);
//        editor.commit();
//        tvTitleStudy.setText(study.getTitle());
//        tvTypeStudy.setText(study.getType());
//        Utilities.setActionBar(this, study.getTitle());
//        updateBibleStudyProgress();
//        super.onResume();
//    }

}
