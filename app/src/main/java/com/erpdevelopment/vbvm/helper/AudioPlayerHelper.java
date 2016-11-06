package com.erpdevelopment.vbvm.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
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

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.FontManager;
import com.erpdevelopment.vbvm.utils.IMediaPlayerServiceClient;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;
import com.roughike.bottombar.BottomBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

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
    private TextView tvPlayerLessonTitleMini;
    private TextView tvPlayerLessonDescriptionMini;

//    private TextView tvPlayerSpeed;
    private ImageButton btnBackward;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private TextView tvIconVolumeDown;
    private SeekBar sbVolumeControl;
    private TextView tvIconVolumeUp;

    //    private ImageButton tvIconPlayMini;
//    private TextView tvIconPlayMini;
    private ImageButton btnPlayMini;
    private ImageButton btnSlideUp;

    private RelativeLayout viewMiniPlayer;

    private ActionBar actionBar;
    private BottomBar bottomBar;
    private SlidingUpPanelLayout slidingLayout;
    private RelativeLayout rlAudioPlayerSlide;

    private AudioPlayerService mService;
    private int seekForwardTime = 30000; // 15 sec
    private int seekBackwardTime = 30000; // 15 sec

//    private int currentSongIndex = 0;
    private String thumbnailSource;
    private String description;
    private String title;
    private int size = 0;
    private ImageLoader2 imageLoader;
    private Utilities utils = new Utilities();

    private Lesson mLesson;

    private AudioManager audioManager = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            mService = ((AudioPlayerService.LocalBinder) binder).getService();
//            if ( mService.isPlayingLesson() ) {
//                if ( AudioPlayerService.created ) {
                    btnPlay.setImageResource(R.drawable.media_pause);
                    System.out.println("mp is playing lesson");
//                }
//            }
            //send this instance to the service, so it can make callbacks on this instance as a client
            mService.setClient(AudioPlayerHelper.this);
//            mService.playAudio(currentSongIndex);
            mService.playAudio(mLesson);
            setSlidingPanelStateExpanded();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public void setLessonToPlay(Lesson lesson) {
        mLesson = lesson;
        thumbnailSource = lesson.getStudyThumbnailSource();
        description = lesson.getLessonsDescription();
        title = lesson.getTitle();
        size = lesson.getStudyLessonsSize();
//        currentSongIndex = extras.getInt("position");
//        thumbnailSource = extras.getString("thumbnailSource");
//        description = extras.getString("description");
//        title = extras.getString("title");
//        size = extras.getInt("size");
    }

    public void initContext(Activity activity, View rootView) {
        this.activity = activity;
        tvIconSlideDown = (TextView) this.activity.findViewById(R.id.tv_icon_slide_down);
        imgPlayerStudy = (ImageView) this.activity.findViewById(R.id.img_player_study);
        sbAudioPlayer = (SeekBar) this.activity.findViewById(R.id.sb_audio_player);
        tvCurrentDuration = (TextView) this.activity.findViewById(R.id.tv_current_duration);
        tvTotalDuration = (TextView) this.activity.findViewById(R.id.tv_total_duration);
        tvPlayerLessonTitle = (TextView) this.activity.findViewById(R.id.tv_player_lesson_title);
        tvPlayerLessonDescription = (TextView) this.activity.findViewById(R.id.tv_player_lesson_description);
        btnBackward = (ImageButton) this.activity.findViewById(R.id.btn_backward);
        btnPlay = (ImageButton) this.activity.findViewById(R.id.btn_play);
        btnForward = (ImageButton) this.activity.findViewById(R.id.btn_forward);
        btnPlayMini = (ImageButton) this.activity.findViewById(R.id.btn_play_mini);
//        tvIconPlayMini = (TextView) this.activity.findViewById(R.id.tv_icon_play_mini);
        btnSlideUp = (ImageButton) this.activity.findViewById(R.id.btn_slide_up);
        actionBar = ((AppCompatActivity) this.activity).getSupportActionBar();

        RelativeLayout rootViewMain = (RelativeLayout) ((rootView.getParent()).getParent()).getParent();
        bottomBar = (BottomBar) rootViewMain.findViewById(R.id.bottomBar);
        slidingLayout = (SlidingUpPanelLayout)rootViewMain.findViewById(R.id.sliding_layout);
        imageLoader = new ImageLoader2(this.activity);

        viewMiniPlayer = (RelativeLayout) rootViewMain.findViewById(R.id.view_mini_player);
        rlAudioPlayerSlide = (RelativeLayout) rootViewMain.findViewById(R.id.rl_audio_player_slide);
        tvPlayerLessonTitleMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_title_mini);
        tvPlayerLessonDescriptionMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_description_mini);

        tvIconVolumeDown = (TextView) this.activity.findViewById(R.id.tv_icon_volume_down);
        tvIconVolumeUp = (TextView) this.activity.findViewById(R.id.tv_icon_volume_up);
        sbVolumeControl = (SeekBar) this.activity.findViewById(R.id.sb_volume_control);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        setVolumeControl();

//        BitmapManager2.bitmapWidth = BitmapManager2.bitmapHeight = 600;
//        ImageLoader2.useCache = false;
//        imageLoader.DisplayImage(thumbnailSource,imgPlayerStudy);

        Picasso.with(activity)
                .load(thumbnailSource)
                .resize(600,600)
                .centerCrop()
                .into(imgPlayerStudy);

        tvIconVolumeDown.setTypeface(FontManager.getTypeface(this.activity,FontManager.FONTAWESOME));
        tvIconVolumeUp.setTypeface(FontManager.getTypeface(this.activity,FontManager.FONTAWESOME));
        tvIconSlideDown.setTypeface(FontManager.getTypeface(this.activity,FontManager.FONTAWESOME));

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
        sbAudioPlayer.setOnSeekBarChangeListener(this); // Register listener for updating progress bar when playing audio

        tvPlayerLessonTitle.setText(title);
        tvPlayerLessonDescription.setText(description);
        tvPlayerLessonTitleMini.setText(title);
        tvPlayerLessonDescriptionMini.setText(description);

        MainActivity.settings.edit()
                .putString("lessonTitle",title)
                .putString("lessonDescription",description)
                .commit();

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

//        if ( AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().equals("") ) {
        if ( mLesson.getAudioSource().equals("") ) {
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
                        btnPlayMini.setImageResource(R.drawable.icon_mini_player2);
//                        tvIconPlayMini.setText(activity.getResources().getString(R.string.fa_icon_play_mini));
                    }
                }else{
                    if(mService.isCreated()){
                        mService.playAudio();
                    }else{
                        if (mService.isStopped())
                            AudioPlayerService.playAfterStop = true;
//                        mService.playAudio(currentSongIndex);
                        mService.playAudio(mLesson);
                        AudioPlayerService.currentPositionInTrack = 0;
                    }
                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "partial");
                    FilesManager.lastLessonId = mLesson.getIdProperty();
                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "playing");
                    btnPlay.setImageResource(R.drawable.media_pause);
                    btnPlayMini.setImageResource(R.drawable.media_pause);
//                    tvIconPlayMini.setText(activity.getResources().getString(R.string.fa_icon_pause));
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
                new IntentFilter(AudioPlayerService.NOTIFICATION_AUDIO_PROGRESS));
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    actionBar.hide();
                    bottomBar.setVisibility(View.GONE);
                    viewMiniPlayer.setVisibility(View.GONE);
                    slidingLayout.setShadowHeight(4);
                    slidingLayout.setPanelHeight(activity.getResources().getDimensionPixelSize(R.dimen.sliding_panel_height));
//                    btnSlideDown.setVisibility(View.VISIBLE);
//                    tvIconSlideDown.setVisibility(View.VISIBLE);
                    rlAudioPlayerSlide.setVisibility(View.VISIBLE);
                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    actionBar.show();
                    viewMiniPlayer.setVisibility(View.VISIBLE);
//                    btnSlideDown.setVisibility(View.GONE);
//                    tvIconSlideDown.setVisibility(View.GONE);
                    rlAudioPlayerSlide.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.VISIBLE);
                    if (mService.isPlayingLesson()) {
                        System.out.println("AudioPlayerHelper.onPanelStateChanged: collapsed playing...");
                        slidingLayout.setShadowHeight(4);
                        slidingLayout.setPanelHeight(activity.getResources().getDimensionPixelSize(R.dimen.sliding_panel_height));
                    }
                }
            }
        };
    }

    public void setSlidingPanelStateExpanded() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
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
            if ("com.erpdevelopment.vbvm.activity.AudioPlayerService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", -1);
            tvCurrentDuration.setText(intent.getStringExtra("currentDurationLabel"));
            tvTotalDuration.setText(intent.getStringExtra("totalDurationLabel"));
            sbAudioPlayer.setProgress(progress);
            boolean isLessonComplete = intent.getBooleanExtra("isLessonComplete", false);
            if ( isLessonComplete )
                btnPlay.setImageResource(R.drawable.media_play);
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

    public void unregisterReceiverProgress(Activity a) {
//        if ( progressReceiver != null )
//            activity.unregisterReceiver(progressReceiver);
        if ( progressReceiver != null )
            LocalBroadcastManager.getInstance(a).unregisterReceiver(progressReceiver);
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

    public AudioPlayerService getInstanceAudioPlayerService() {
        return mService;
    }

    private void setVolumeControl()
    {
        sbVolumeControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolumeControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        sbVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0){}

            @Override
            public void onStartTrackingTouch(SeekBar arg0){}

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        });
    }
}
