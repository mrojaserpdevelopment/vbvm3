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

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.FontManager;
import com.erpdevelopment.vbvm.utils.IMediaPlayerServiceClient;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.roughike.bottombar.BottomBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

public class AudioPlayerHelper implements SeekBar.OnSeekBarChangeListener, IMediaPlayerServiceClient {
    private Activity activity;
    private TextView tvIconSlideDown;
    private ImageView imgPlayerStudy;
    private SeekBar sbAudioPlayer;
    private TextView tvCurrentDuration;
    private TextView tvTotalDuration;
    private TextView tvPlayerLessonTitle;
    private TextView tvPlayerLessonDescription;
    private TextView tvPlayerLessonTitleMini;
    private TextView tvPlayerLessonDescriptionMini;
    private ImageButton btnBackward;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private TextView tvIconVolumeDown;
    private SeekBar sbVolumeControl;
    private TextView tvIconVolumeUp;

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

    private String thumbnailSource;
    private String description;
    private String title;
    private Utilities utils = new Utilities();
    private Lesson mLesson;
    private AudioManager audioManager = null;

    public static AudioPlayerHelper playerInstance;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            mService = ((AudioPlayerService.LocalBinder) binder).getService();
            btnPlay.setImageResource(R.drawable.media_pause);
            btnPlayMini.setImageResource(R.drawable.icon_media_pause_16);
            //send this instance to the service, so it can make callbacks on this instance as a client
            mService.setClient(AudioPlayerHelper.this);
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
    }

    public void initContext(Activity a, View rootView) {
        activity = a;
        tvIconSlideDown = (TextView) activity.findViewById(R.id.tv_icon_slide_down);
        imgPlayerStudy = (ImageView) activity.findViewById(R.id.img_player_study);
        sbAudioPlayer = (SeekBar) activity.findViewById(R.id.sb_audio_player);
        tvCurrentDuration = (TextView) activity.findViewById(R.id.tv_current_duration);
        tvTotalDuration = (TextView) activity.findViewById(R.id.tv_total_duration);
        tvPlayerLessonTitle = (TextView) activity.findViewById(R.id.tv_player_lesson_title);
        tvPlayerLessonDescription = (TextView) activity.findViewById(R.id.tv_player_lesson_description);
        btnBackward = (ImageButton) activity.findViewById(R.id.btn_backward);
        btnPlay = (ImageButton) activity.findViewById(R.id.btn_play);
        btnForward = (ImageButton) activity.findViewById(R.id.btn_forward);
        btnPlayMini = (ImageButton) activity.findViewById(R.id.btn_play_mini);
        btnSlideUp = (ImageButton) activity.findViewById(R.id.btn_slide_up);
        actionBar = ((AppCompatActivity) activity).getSupportActionBar();

        RelativeLayout rootViewMain = (RelativeLayout) ((rootView.getParent()).getParent()).getParent();
        bottomBar = (BottomBar) rootViewMain.findViewById(R.id.bottomBar);
        slidingLayout = (SlidingUpPanelLayout)rootViewMain.findViewById(R.id.sliding_layout);

        viewMiniPlayer = (RelativeLayout) rootViewMain.findViewById(R.id.view_mini_player);
        rlAudioPlayerSlide = (RelativeLayout) rootViewMain.findViewById(R.id.rl_audio_player_slide);
        tvPlayerLessonTitleMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_title_mini);
        tvPlayerLessonDescriptionMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_description_mini);

        tvIconVolumeDown = (TextView) activity.findViewById(R.id.tv_icon_volume_down);
        tvIconVolumeUp = (TextView) activity.findViewById(R.id.tv_icon_volume_up);
        sbVolumeControl = (SeekBar) activity.findViewById(R.id.sb_volume_control);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        setVolumeControl();

        Utilities.loadStudyImages(activity, thumbnailSource, 600, imgPlayerStudy);

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
                .apply();

        bindToService();

        if ( mLesson.getAudioSource().equals("") ) {
            btnPlay.setEnabled(false);
            btnPlayMini.setEnabled(false);
            btnBackward.setEnabled(false);
            btnForward.setEnabled(false);
        } else {
            btnPlay.setEnabled(true);
            btnPlayMini.setEnabled(true);
            btnBackward.setEnabled(true);
            btnForward.setEnabled(true);
        }

        btnPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.performClick();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sbAudioPlayer.setEnabled(true);
                if(mService.isPlayingLesson()){
                    if(mService.isCreated()){
                        mService.pauseLesson();
                        mLesson.setPlaying(false);
                        btnPlay.setImageResource(R.drawable.media_play);
                        btnPlayMini.setImageResource(R.drawable.icon_mini_play);
                    }
                }else{
                    if(mService.isCreated()){
                        mService.playAudio();
                        mLesson.setPlaying(true);
                    }else{
                        if (mService.isStopped())
                            AudioPlayerService.playAfterStop = true;
                        mService.playAudio(mLesson);
                        AudioPlayerService.currentPositionInTrack = 0;
                    }
                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "partial");
                    FilesManager.lastLessonId = mLesson.getIdProperty();
                    DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "playing");
                    btnPlay.setImageResource(R.drawable.media_pause);
                    btnPlayMini.setImageResource(R.drawable.icon_media_pause_16);
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
                    if(currentPosition - seekBackwardTime >= 0){
                        sendStopTrackingTouch(currentPosition - seekBackwardTime);
                    }else{
                        sendStopTrackingTouch(0);
                    }
                }
            }
        });

//        LocalBroadcastManager.getInstance(activity).registerReceiver(progressReceiver,
//                new IntentFilter(AudioPlayerService.NOTIFICATION_AUDIO_PROGRESS));
        registerProgressReceiver();
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
                    slidingLayout.setShadowHeight(activity.getResources().getDimensionPixelSize(R.dimen.sliding_panel_shadow_height));
                    slidingLayout.setPanelHeight(activity.getResources().getDimensionPixelSize(R.dimen.sliding_panel_height));
                    rlAudioPlayerSlide.setVisibility(View.VISIBLE);
                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    actionBar.show();
                    viewMiniPlayer.setVisibility(View.VISIBLE);
                    rlAudioPlayerSlide.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.VISIBLE);
                    if (mService.isPlayingLesson()) {
                        slidingLayout.setShadowHeight(activity.getResources().getDimensionPixelSize(R.dimen.sliding_panel_shadow_height));
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
        if (AudioPlayerServiceRunning()) {
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
            if ( isLessonComplete ) {
                btnPlay.setImageResource(R.drawable.media_play);
                btnPlayMini.setImageResource(R.drawable.icon_mini_play);
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        System.out.println("seekBar = [" + seekBar + "], progress = [" + progress + "], fromUser = [" + fromUser + "]");
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

    public void registerProgressReceiver() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(progressReceiver,
                new IntentFilter(AudioPlayerService.NOTIFICATION_AUDIO_PROGRESS));
    }

    public void unregisterProgressReceiver() {
        if (mConnection!=null && mService!=null && AudioPlayerService.mBoundService)
            activity.unbindService(mConnection);
        if ( progressReceiver != null )
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(progressReceiver);
    }

    @Override
    public void onInitializePlayerStart(String message) {
    }
    @Override
    public void onInitializePlayerSuccess() {
    }
    @Override
    public void onError() {
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
