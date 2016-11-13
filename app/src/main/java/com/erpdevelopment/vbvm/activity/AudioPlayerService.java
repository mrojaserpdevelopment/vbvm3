package com.erpdevelopment.vbvm.activity;

import java.io.IOException;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.BitmapManager2;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.IMediaPlayerServiceClient;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AudioPlayerService extends Service implements OnPreparedListener{

	public final IBinder localBinder = new LocalBinder();
    private MediaPlayer mp = new MediaPlayer();
    public static boolean created = false;
	public String currentLesson = "";
	public static long currentPositionInTrack = 0;
	public static long savedOldPositionInTrack = 0;	
	private Handler handlerUI = new Handler();
	private String totalDurationLabel;
	private String currentDurationLabel;
	private Utilities utils = new Utilities();
	private int progress;
	private String currentTitle;
	private String currentDescription;
	private IMediaPlayerServiceClient mClient;
	public static boolean stopped = false;
    private static int classID = 579; // just a number for startForeground notification
    private static boolean isLessonComplete = false;
    public static boolean playAfterStop = false;
	private boolean paused = false;

	public static final String NOTIFICATION_AUDIO_PROGRESS = "notification_audio_progress";
	public static final String NOTIFICATION_AUDIO_COMPLETE = "notification_audio_complete";
    
	@Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }
 
    @Override
    public void onCreate() {
		LocalBroadcastManager.getInstance(this).registerReceiver(startTrackingTouchReceiver,
	            new IntentFilter("StartTrackingTouch"));
		LocalBroadcastManager.getInstance(this).registerReceiver(stopTrackingTouchReceiver,
		            new IntentFilter("StopTrackingTouch"));
		//Register the phone call listener
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);    	
    	if(mgr != null) {
    	    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    	}    	
    }    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return Service.START_REDELIVER_INTENT;
    }
    
	public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
	
	private void sendMessage() {
	  Intent intent = new Intent(NOTIFICATION_AUDIO_PROGRESS);
	  intent.putExtra("totalDurationLabel", totalDurationLabel);
	  intent.putExtra("currentDurationLabel", currentDurationLabel);
	  intent.putExtra("progress", progress);
	  intent.putExtra("currentDescription", currentDescription);
	  intent.putExtra("currentTitle", currentTitle);
	  intent.putExtra("isLessonComplete", isLessonComplete);
	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void sendMessageComplete() {
		Intent intent = new Intent(NOTIFICATION_AUDIO_COMPLETE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
		 
	private BroadcastReceiver startTrackingTouchReceiver = new BroadcastReceiver() {
	   @Override
	   public void onReceive(Context context, Intent intent) {
			handlerUI.removeCallbacks(mUpdateTimeTask);
	   }
	};

	private BroadcastReceiver stopTrackingTouchReceiver = new BroadcastReceiver() {
	   @Override
	   public void onReceive(Context context, Intent intent) {
		   handlerUI.removeCallbacks(mUpdateTimeTask);
		   int currentPosition = intent.getIntExtra("currentPosition", -1);
		   mp.seekTo(currentPosition);
		   // update timer progress again
		   updateProgressBar();
	   }
	};
		 	
	public void  removeHandlerCallbacks(){
		 handlerUI.removeCallbacks(mUpdateTimeTask);
	 }

	public void playAudio(Lesson lesson){
		stopped = false;
		paused = false;
		String filename = BitmapManager2.getFileNameFromUrl(lesson.getAudioSource());
		// Play lesson
		try {
			currentLesson = lesson.getAudioSource();
			currentDescription = lesson.getLessonsDescription();
			currentTitle = lesson.getTitle();
			FilesManager.lastLessonId = lesson.getIdProperty();
			MainActivity.settings.edit().putString("currentLessonId", FilesManager.lastLessonId).commit();
			if(!created){
				created = true;
				mClient.onInitializePlayerStart("Connecting...");
				handlerUI.removeCallbacks(mUpdateTimeTask);
				resetMediaPlayer();
				int downloadStatus = DBHandleLessons.getLessonById(lesson.getIdProperty()).getDownloadStatusAudio();
				// Check if lesson is downloaded
				if ( downloadStatus == 1) {
					mp.setDataSource(FileCache.cacheDirAudio.getAbsolutePath() + "/" + filename);
					mp.setOnPreparedListener(this);
					mp.prepareAsync();
					isLessonComplete = false;
					FilesManager.lastLessonId = "";
					mp.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer arg0) {
							System.out.println("AudioPlayerService.onCompletion");
//							DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "complete");
//							FilesManager.lastLessonId = "";
							isLessonComplete = true;
							stopLesson();
							sendMessageComplete();
						}
					});
				}
			}
		} catch (IllegalArgumentException e) {
			Log.e("IllegalArgumentExcept", "error setting data source 1");
		} catch (IllegalStateException e) {
			Log.e("IllegalStateException", "error setting data source 2");
		} catch (IOException e) {
			Log.e("IOException", "error setting data source 3");
			if ( currentLesson.trim().equals("") ) {
				currentDescription = lesson.getLessonsDescription();
				currentTitle = lesson.getTitle();
				sendMessageEmptyLesson();
			}
		}
	}
	
	private void sendMessageEmptyLesson() {
		totalDurationLabel = "TBD";
	    currentDurationLabel = "0:00";
	    progress = (int)(utils.getProgressPercentage(0, 0));
	    FilesManager.totalDuration = 0;
	    currentPositionInTrack = 0; //To access from Bible Study state of old lesson playing
	    sendMessage();
	}
	
	public void playAudio(){
		stopped = false;
		startMediaPlayer();
	}
	
	public void pauseLesson() {
		pauseMediaPlayer();
		paused = true;
    }

	public boolean isPaused() {
		return paused;
	}
	
	public void stopLesson() {
		stopped = true;
		stopMediaPlayer();
    }
	
	public boolean isPlayingLesson(){
		return mp.isPlaying();
	}

	public boolean isStopped(){
		return stopped;
	}

	public boolean isCreated(){
		return created;  
	}
	
	public void setCreated(boolean c){
		created = c;
	}
	
	public int getDuration() {
		return mp.getDuration();
	}
	
	public int getCurrentPosition() {		
		return mp.getCurrentPosition();
	}
	
	public void setSeekToPosition(long currentPosition) {
		mp.seekTo((int)currentPosition);
	}
	
	public MediaPlayer getMediaPlayer() {
		return mp;
	}
	
	public void updateProgressBar() {
        handlerUI.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
	   public void run() {
		   long totalDuration = mp.getDuration();
		   long currentDuration = mp.getCurrentPosition();
		   totalDurationLabel = utils.milliSecondsToTimer(totalDuration);
		   currentDurationLabel = utils.milliSecondsToTimer(currentDuration);
		   progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
		   FilesManager.totalDuration = totalDuration;
		   currentPositionInTrack = currentDuration; //To access from Bible Study state of old lesson playing
		   sendMessage();
		   //Save current state for restore when app is forced to close
		   DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, currentPositionInTrack);        
		   Editor e = MainActivity.settings.edit();
		   e.putLong("currentPositionInTrack", currentPositionInTrack);
		   e.commit();
		   // Running this thread after 100 milliseconds
	       handlerUI.postDelayed(this, 100);
	   }
	};	
	
	public void onPrepared(MediaPlayer mp) {
		mClient.onInitializePlayerSuccess();
//		if ( playAfterStop ) {
			startMediaPlayer();
			playAfterStop = false;
//		}
		setSeekToPosition(savedOldPositionInTrack);
		updateProgressBar();
	};

	/**
     * Starts the contained StatefulMediaPlayer and foregrounds the service to support
     * persisted background playback.
     */
    public void startMediaPlayer() {
		//Playing audio forever even after clearing memory
		Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        final NotificationCompat.Builder builder = new Builder(this);
        builder.setContentTitle("Verse By Verse Ministry");
        builder.setContentText("Now Playing...");
        builder.setSmallIcon(R.drawable.app_logo_24);
        builder.setContentIntent(pi);
        Notification notification = builder.build();
        mp.start();
        startForeground(classID, notification);
    }
    
    /**
     * Stops the contained StatefulMediaPlayer.
     */
    public void stopMediaPlayer() {
    	created = false;
    	handlerUI.removeCallbacks(mUpdateTimeTask);
        stopForeground(true);
        mp.stop();        
    }
 
    public void resetMediaPlayer() {
        stopForeground(true);
        mp.reset();
    }
    
    public void pauseMediaPlayer() {
        mp.pause();
        stopForeground(true);
    }
    
    /**
     * Sets the client using this service.
     * @param client The client of this service, which implements the IMediaPlayerServiceClient interface
     */
    public void setClient(IMediaPlayerServiceClient client) {
       this.mClient = client;
    }
    
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
	        	
    	@Override
	    public void onCallStateChanged(int state, String incomingNumber) {
	        if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
	            //Incoming call: Pause audio
	        	if(isPlayingLesson()){
					if(isCreated())
						pauseLesson();
				}
	        } else if(state == TelephonyManager.CALL_STATE_IDLE) {
	            //Not in call: Play audio
	        }
//			else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
//	            //A call is dialing, active or on hold
//	        }
	        super.onCallStateChanged(state, incomingNumber);
	    }
	};
	
	public void onDestroy() {
		//Unregister phone call listener
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();		
	};
}
