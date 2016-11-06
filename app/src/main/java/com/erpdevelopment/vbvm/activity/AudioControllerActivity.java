package com.erpdevelopment.vbvm.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService.LocalBinder;
import com.erpdevelopment.vbvm.db.DBHandleFavorites;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Favorite;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.FavoritesLRU;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.IMediaPlayerServiceClient;
import com.erpdevelopment.vbvm.utils.PDFTools;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AudioControllerActivity extends Activity implements OnSeekBarChangeListener, IMediaPlayerServiceClient{
	 
    private AudioPlayerService mService;
    private ImageView imgStudy;
    private ImageButton btnPlay;
    private ImageButton btnStop;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnForward;
    private ImageButton btnBackward;
	private SeekBar songProgressBar;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private TextView tvTitle;
	private TextView tvDescription;
	private int currentSongIndex = 0; 
	public static int position;
	public static int positionTest;
//	private int seekForwardTime = 60000; // 1 minute
//	private int seekBackwardTime = 60000; // 1 minute
	private int seekForwardTime = 15000; // 15 sec
	private int seekBackwardTime = 15000; // 15 sec	
	private ImageLoader2 imageLoader;
	private LinearLayout llImageRead;	
	private LinearLayout llImagePresent;
	private LinearLayout llImageHandout;
	private String readSource;
	private String presentSource;
	private String handoutSource;
//	private ImageView btnBack;
	private ImageView imgDownload;
	private ImageView imgShare;
//	private ToggleButton toggleAddFavorite;
//	private TextView tvDownloadInProgress;
	private DownloadFileAsync downloadTask;
	private ProgressDialog mProgressDialog;
	private Utilities utils = new Utilities();
	
	SharedPreferences preferenceManager;
	DownloadManager downloadManager;
	
	private String thumbnailSource;
	private String description;
	private String title;
	private int size = 0;	
	private Study study;
//	private TextView tvTitleTop;
	private TextView tvTitleStudy;
	private TextView tvTypeStudy;
	private TextView tvCountLessons;
	private int countLessons;
	private int countLessonsComplete;
	private static boolean optionSelected = false;
	private Lesson lastLesson;
	private TextView tvProgressPercent;
	private ProgressBar progressBar;
	private static final String emailContent = "Listen to this bible study from Verse By Verse Ministry: ";
	private static final String emailContent2 = ", http://www.versebyverseministry.org/bible-studies/#VBVMI";
	
	private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            mService = ((LocalBinder) binder).getService();
            
            System.out.println("onServiceConnected...");
            if ( mService.isPlayingLesson() ) {
            	if ( AudioPlayerService.created ) {
            		btnPlay.setImageResource(R.drawable.media_pause);
            		System.out.println("mp is playing lesson");
            	}
            }
            
            //send this instance to the service, so it can make callbacks on this instance as a client
            mService.setClient(AudioControllerActivity.this);
//            mService.playAudio(currentSongIndex);
        } 
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        } 
    };
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
   
        Utilities.setActionBar(this, null);
        
		imgStudy = (ImageView) findViewById(R.id.img_study);
        btnPlay = (ImageButton) findViewById(R.id.btn_play_mini);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        llImageRead = (LinearLayout) findViewById(R.id.ll_image_read);
        llImagePresent = (LinearLayout) findViewById(R.id.ll_image_present);
        llImageHandout = (LinearLayout) findViewById(R.id.ll_image_handout);
        mProgressDialog = new ProgressDialog(this);
        imageLoader = new ImageLoader2(this);
        imgDownload = (ImageView) findViewById(R.id.img_download);
        imgShare = (ImageView) findViewById(R.id.img_share);
//        tvDownloadInProgress = (TextView) findViewById(R.id.download_in_progress);
        tvTitleStudy = (TextView) findViewById(R.id.tv_title_study);
        tvTypeStudy = (TextView) findViewById(R.id.tv_type_study);
        tvCountLessons = (TextView) findViewById(R.id.tv_qty_lessons);
        tvProgressPercent = (TextView) findViewById(R.id.tv_progress_percent);
        progressBar = (ProgressBar) findViewById(R.id.circularProgressbar);

		songProgressBar.setOnSeekBarChangeListener(this); // Register listener for updating progress bar when playing audio
		
        Bundle b = getIntent().getExtras();
		currentSongIndex = b.getInt("position");
		thumbnailSource = b.getString("thumbnailSource");
		description = b.getString("description");
		title = b.getString("title");
		size = b.getInt("size");
		readSource = b.getString("readSource");
		presentSource = b.getString("presentSource");
		handoutSource = b.getString("handoutSource");		
		
		if ( !(readSource.equals("")) && readSource != null )
			llImageRead.setVisibility(View.VISIBLE);
		if ( !(presentSource.equals("")) && presentSource != null )
			llImagePresent.setVisibility(View.VISIBLE);
		if ( !(handoutSource.equals("")) && handoutSource != null )
			llImageHandout.setVisibility(View.VISIBLE);
			
        imageLoader.DisplayImage(thumbnailSource, imgStudy);
		
		tvDescription.setText(description);
		tvTitle.setText(title);
		
        bindToService();
        
//        if ( AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().equals("") ) {
//        	btnPlay.setEnabled(false);
//        	btnStop.setEnabled(false);
//        	btnBackward.setEnabled(false);
//        	btnForward.setEnabled(false);
//        	imgDownload.setVisibility(View.GONE);
//        } else {
//        	btnPlay.setEnabled(true);
//        	btnStop.setEnabled(true);
//        	btnBackward.setEnabled(true);
//        	btnForward.setEnabled(true);
//        	imgDownload.setVisibility(View.VISIBLE);
//        }
        
        /**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				songProgressBar.setEnabled(true);
				// check for already playing
				if(mService.isPlayingLesson()){
					if(mService.isCreated()){
						mService.pauseLesson();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.media_play);
					}
				}else{					
					// Resume song
					if(mService.isCreated()){
						mService.playAudio();
					}else{
						if (mService.isStopped())
							AudioPlayerService.playAfterStop = true;
//						mService.playAudio(currentSongIndex);
						AudioPlayerService.currentPositionInTrack = 0;
					}
					DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "partial");
//					FilesManager.lastLessonId = AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty();
//					long newCurrentPosInTrack = AudioPlayerService.listTempLesson2.get(currentSongIndex).getCurrentPosition();
					DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "playing");

					btnPlay.setImageResource(R.drawable.media_pause);
				}
			}
		});
		
		btnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mService.stopLesson();
				songProgressBar.setProgress(0);
				songCurrentDurationLabel.setText("0:00");
				btnPlay.setImageResource(R.drawable.media_play);
				songProgressBar.setEnabled(false);
				DBHandleLessons.updateLessonState(FilesManager.lastLessonId, 0, "new");
				FilesManager.lastLessonId = "";
				AudioPlayerService.currentPositionInTrack = 0;
				AudioPlayerService.savedOldPositionInTrack = 0;
				Editor e = MainActivity.settings.edit();
				e.putLong("currentPositionInTrack", 0);
				e.commit();
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Save previous lesson state
				if ( !mService.isStopped() && mService.isPlayingLesson() )
					DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
				int tempCurrentSOngIndex = currentSongIndex;
				if(tempCurrentSOngIndex < (size - 1)){
					tempCurrentSOngIndex = tempCurrentSOngIndex + 1;
				}else{
					tempCurrentSOngIndex = 0;
				}
//				Lesson nextLessonDB = DBHandleLessons.getLessonById(AudioPlayerService.listTempLesson2.get(tempCurrentSOngIndex).getIdProperty());
				Lesson nextLessonDB = null;
//				int nextLessonDownloadStatus = nextLessonDB.getDownloadStatus();
				int nextLessonDownloadStatus = nextLessonDB.getDownloadStatusAudio();
				if ( !CheckConnectivity.isOnline(AudioControllerActivity.this) && (nextLessonDownloadStatus != 1) ) {
					CheckConnectivity.showMessage(AudioControllerActivity.this);
				} else {
					songProgressBar.setEnabled(true);
					mService.setCreated(false);
					// check if next lesson is there or not
					if(currentSongIndex < (size - 1)){
						currentSongIndex = currentSongIndex + 1;
					}else{
						// play first lesson
						currentSongIndex = 0;
					}
//					btnPlay.setImageResource(R.drawable.media_pause);
					btnPlay.setImageResource(R.drawable.media_play);
					//Update current position in track
					DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack);
//					Lesson nextLesson = DBHandleLessons.getLessonById(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty());
//					AudioPlayerService.currentPositionInTrack = nextLesson.getCurrentPosition();
//					AudioPlayerService.savedOldPositionInTrack = nextLesson.getCurrentPosition();
					//Update to current lesson pdf files
//					readSource = AudioPlayerService.listTempLesson2.get(currentSongIndex).getTranscript();
//					presentSource = AudioPlayerService.listTempLesson2.get(currentSongIndex).getStudentAid();
//					handoutSource = AudioPlayerService.listTempLesson2.get(currentSongIndex).getTeacherAid();
//					FilesManager.lastLessonId = nextLesson.getIdProperty();
					updateDownloadImage();
					updateFavorites(menuActionbar);
//					mService.playAudio(currentSongIndex);
//					if ( AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().equals("") ) {
//			        	btnPlay.setEnabled(false);
//			        	btnStop.setEnabled(false);
//			        	btnBackward.setEnabled(false);
//			        	btnForward.setEnabled(false);
//			        	imgDownload.setVisibility(View.GONE);
//			        } else {
//			        	btnPlay.setEnabled(true);
//			        	btnStop.setEnabled(true);
//			        	btnBackward.setEnabled(true);
//			        	btnForward.setEnabled(true);
//			        	imgDownload.setVisibility(View.VISIBLE);
//			        }
				}
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				// Save previous lesson state
				if ( !mService.isStopped() && mService.isPlayingLesson() )
					DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
				int tempCurrentSOngIndex = currentSongIndex;
				if(tempCurrentSOngIndex > 0){
					tempCurrentSOngIndex = tempCurrentSOngIndex - 1;
				}else{	
					tempCurrentSOngIndex = size - 1;
				}
//				Lesson nextLessonDB = DBHandleLessons.getLessonById(AudioPlayerService.listTempLesson2.get(tempCurrentSOngIndex).getIdProperty());
//				int nextLessonDownloadStatus = nextLessonDB.getDownloadStatus();
//				int nextLessonDownloadStatus = nextLessonDB.getDownloadStatusAudio();
//				if ( !CheckConnectivity.isOnline(AudioControllerActivity.this) && (nextLessonDownloadStatus != 1) ) {
//					CheckConnectivity.showMessage(AudioControllerActivity.this);
//				} else {
//					songProgressBar.setEnabled(true);
//					mService.setCreated(false);
//					if(currentSongIndex > 0){
//						currentSongIndex = currentSongIndex - 1;
//					}else{
//						// play last lesson
//						currentSongIndex = size - 1;
//					}
////					btnPlay.setImageResource(R.drawable.media_pause);
//					btnPlay.setImageResource(R.drawable.media_play);
//					//Update current position in track
//					DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack);
//					Lesson nextLesson = DBHandleLessons.getLessonById(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty());
//					AudioPlayerService.currentPositionInTrack = nextLesson.getCurrentPosition();
//					AudioPlayerService.savedOldPositionInTrack = nextLesson.getCurrentPosition();
//					//Update to current lesson pdf files
//					readSource = AudioPlayerService.listTempLesson2.get(currentSongIndex).getTranscript();
//					presentSource = AudioPlayerService.listTempLesson2.get(currentSongIndex).getStudentAid();
//					handoutSource = AudioPlayerService.listTempLesson2.get(currentSongIndex).getTeacherAid();
//					FilesManager.lastLessonId = nextLesson.getIdProperty();
//					updateDownloadImage();
//					updateFavorites(menuActionbar);
//					mService.playAudio(currentSongIndex);
//					if ( AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().equals("") ) {
//			        	btnPlay.setEnabled(false);
//			        	btnStop.setEnabled(false);
//			        	btnBackward.setEnabled(false);
//			        	btnForward.setEnabled(false);
//			        	imgDownload.setVisibility(View.GONE);
//			        } else {
//			        	btnPlay.setEnabled(true);
//			        	btnStop.setEnabled(true);
//			        	btnBackward.setEnabled(true);
//			        	btnForward.setEnabled(true);
//			        	imgDownload.setVisibility(View.VISIBLE);
//			        }
//				}
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mService.isPlayingLesson()){
					mService.removeHandlerCallbacks();
					int currentPosition = mService.getCurrentPosition();
					int totalDuration = mService.getDuration();
					// check if seekForward time is lesser than song duration
					if(currentPosition + seekForwardTime <= totalDuration){
						sendStopTrackingTouch(currentPosition + seekForwardTime);
					}else{
						// forward to end position
						sendStopTrackingTouch(totalDuration);
					}
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mService.isPlayingLesson()){
					mService.removeHandlerCallbacks();
					int currentPosition = mService.getCurrentPosition();
					// check if seekBackward time is greater than 0 sec
					if(currentPosition - seekBackwardTime >= 0){
						// backward song
						sendStopTrackingTouch(currentPosition - seekBackwardTime);
					}else{
						// backward to starting position
						sendStopTrackingTouch(0);
					}
				}
			}
		});
		
		// Register to receive messages.
        // We are registering an observer (progressReceiver) to receive Intents
        // with actions named "UpdateProgress".
        LocalBroadcastManager.getInstance(this).registerReceiver(progressReceiver,
            new IntentFilter("UpdateProgress"));
        
        /*****************************************************************/
        /****************** Audio IS_SERVICE_RUNNING ****************************/
        /*****************************************************************/
        
        updateDownloadImage(); //Show download status
        
        imgDownload.setOnClickListener(new Button.OnClickListener(){
   
		    @SuppressLint("NewApi")
			@Override
		    public void onClick(View arg0) {
		    	if ( !CheckConnectivity.isOnline(AudioControllerActivity.this)) {
					CheckConnectivity.showMessage(AudioControllerActivity.this);
				} else {
//			    	if ( !DownloadAllService.IS_SERVICE_RUNNING ) {
////			    		int downloadStatus = AudioPlayerService.listTempLesson2.get(currentSongIndex).getDownloadStatus();
//						int downloadStatus = AudioPlayerService.listTempLesson2.get(currentSongIndex).getDownloadStatusAudio();
//			    		if ( downloadStatus == 0 ){
//							String audioUrl = AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource();
//							String pdfUrl1 = AudioPlayerService.listTempLesson2.get(currentSongIndex).getTranscript();
//							String pdfUrl2 = AudioPlayerService.listTempLesson2.get(currentSongIndex).getStudentAid();
//							String pdfUrl3 = AudioPlayerService.listTempLesson2.get(currentSongIndex).getTeacherAid();
//							downloadTask = new DownloadFileAsync();
//							downloadTask.execute( audioUrl, pdfUrl1, pdfUrl2, pdfUrl3 );
//							DBHandleLessons.updateLessonDownloadStatus(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty(), 2);
////					    	tvDownloadInProgress.setVisibility(View.VISIBLE);
//					    	imgDownload.setVisibility(View.GONE);
//			    		}
//			    	} else {
//			    		Toast.makeText(AudioControllerActivity.this,
//			    	              "Please wait. Another download in progress...",
//			    	              Toast.LENGTH_LONG).show();
//			    	}
				}
	    }});
        
        imgShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendEmail();
			}
		});
        
//        toggleAddFavorite.setVisibility(View.VISIBLE);
//        toggleAddFavorite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				Lesson lesson = AudioPlayerService.listTempLesson2.get(FilesManager.positionLessonInList);
//				if ( !isChecked ) {
//					FavoritesLRU.deleteLruItem(lesson.getIdProperty());
//					toggleAddFavorite.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_add_fav));
//				} else {					
//		        	ItemInfo item = new ItemInfo();
//		        	item.setId(lesson.getIdProperty());
//		        	item.setType("lesson");
//		        	item.setItem(lesson);
//		        	FavoritesLRU.addLruItem(lesson.getIdProperty(), item);
//					toggleAddFavorite.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_favorited));
//				}
//			}
//		});
    }    
    
    private void updateDownloadImage() {
//    	Lesson lesson = DBHandleLessons.getLessonById(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty());
//    	int downloadStatus = lesson.getDownloadStatus();
//		int downloadStatus = lesson.getDownloadStatusAudio();
//    	String audioSource = lesson.getIdProperty();
        // Check if already downloaded, not downloaded or in progress
//        if ( downloadStatus == 0 && !audioSource.equals(""))
//        {
//        	imgDownload.setVisibility(View.VISIBLE);
////        	tvDownloadInProgress.setVisibility(View.GONE);
//        } else {
//        	if (downloadStatus == 1) {
//        		imgDownload.setVisibility(View.GONE);
////            	tvDownloadInProgress.setVisibility(View.VISIBLE);
////            	tvDownloadInProgress.setText("Downloaded");
//        	} else {
//        		imgDownload.setVisibility(View.GONE);
////            	tvDownloadInProgress.setVisibility(View.VISIBLE);
////            	tvDownloadInProgress.setText("Downloading...");
//        	}
//        }
    }
    
    /**************************************************/
    
    public static final String LOG_TAG = "Android Downloader";
    
    //initialize our progress dialog/bar
    private ProgressDialog mProgressDialog2;
    private File rootDir = Environment.getExternalStorageDirectory();
    private File outFile;  
    
    //this is our download file asynctask
    private class DownloadFileAsync extends AsyncTask<String, String, String> {
         
//    	private volatile boolean running = true;
    	private FileCache fileCache;
    	FileOutputStream f;
    	InputStream in;
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imgDownload.setVisibility(View.GONE);
//            tvDownloadInProgress.setVisibility(View.VISIBLE);
            createDialog();
            fileCache = new FileCache(AudioControllerActivity.this);
        }
         
        @Override
        protected String doInBackground(String... params) {
        	String audioUrl = params[0];
        	String pdf1Url = params[1];
        	String pdf2Url = params[2];
        	String pdf3Url = params[3];
        	
	        downloadFile(audioUrl);
	        downloadFile(pdf1Url);
	        downloadFile(pdf2Url);
	        downloadFile(pdf3Url);        	

            return null;
        }
        
        private void downloadFile(String url) {
        	outFile = fileCache.getFileAudioFolder(url);
		  	if (outFile.exists()) {
		    	outFile.delete();
		    	outFile = fileCache.getFileAudioFolder(url);
		    }
        	try {
                //connecting to url
                URL u = new URL(url);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                 
                //lenghtOfFile is used for calculating download progress
                int lenghtOfFile = c.getContentLength();
                 
                //this is where the file will be seen after the download
                f = new FileOutputStream(outFile.getPath());
                //file input is from the url
                in = c.getInputStream();
 
                //here's the download code
                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                 
                while ((len1 = in.read(buffer)) > 0) {
                    total += len1; //total = total + len1
                    publishProgress("" + (int)((total*100)/lenghtOfFile));
                    f.write(buffer, 0, len1);
                }
                f.close();

            } catch (InterruptedIOException ie) {
                Log.d(LOG_TAG, ie.getMessage());
//                DBHandleLessons.updateLessonDownloadStatus(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty(), 0);
                outFile.delete();
            } catch (Exception e) {
//            	DBHandleLessons.updateLessonDownloadStatus(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty(), 0);
                outFile.delete();
            }
        }
         
        protected void onProgressUpdate(String... progress) {
             //Log.d(LOG_TAG,progress[0]);
             mProgressDialog2.setProgress(Integer.parseInt(progress[0]));
        }
 
        @Override
        protected void onPostExecute(String unused) {
            //dismiss the dialog after the file was downloaded
            mProgressDialog2.dismiss();
            imgDownload.setVisibility(View.GONE);
//            tvDownloadInProgress.setVisibility(View.GONE);
            Toast.makeText(AudioControllerActivity.this, "Lesson Downloaded!", Toast.LENGTH_LONG).show();
//		  	DBHandleLessons.updateLessonDownloadStatus(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty(), 1);
        }
        
        @Override
        protected void onCancelled() {
//        	running = false;
        }
        
        //our progress bar settings
        private void createDialog() {
            mProgressDialog2 = new ProgressDialog(AudioControllerActivity.this);
            mProgressDialog2.setMessage("Downloading lesson");
            mProgressDialog2.setIndeterminate(false);
            mProgressDialog2.setMax(100);
            mProgressDialog2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog2.setCancelable(false);
            mProgressDialog2.show();
        }
    }
     
    //function to verify if directory exists
    public void checkAndCreateDirectory(String dirName){
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
    }
     
    public void onClickPDF(View v){
		PDFTools pdf = new PDFTools();
		switch (v.getId()) {
			case R.id.img_pdf_read:	//PDFTools.showPDFUrl(AudioControllerActivity.this, readSource);
				pdf.showPDFUrl(AudioControllerActivity.this, readSource);
				break;				
			case R.id.img_pdf_present:	//PDFTools.showPDFUrl(AudioControllerActivity.this, presentSource);			
				pdf.showPDFUrl(AudioControllerActivity.this, presentSource);
				break;
			case R.id.img_pdf_handout:	//PDFTools.showPDFUrl(AudioControllerActivity.this, handoutSource);			
				pdf.showPDFUrl(AudioControllerActivity.this, handoutSource);
				break;	
			default:
				break;
		}
	}
    
    /**
     * Binds to the instance of MediaPlayerService. If no instance of MediaPlayerService exists, it first starts
     * a new instance of the service.
     */
    public void bindToService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra("currentSongIndex", currentSongIndex);
        
        if (MediaPlayerServiceRunning()) {
            // Bind to LocalService
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    
    /** Determines if the MediaPlayerService is already running.
     * @return true if the service is running, false otherwise.
     */
    private boolean MediaPlayerServiceRunning() { 
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.speakingcode.example.android.MediaPlayerService".equals(service.service.getClassName()))
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
	     songCurrentDurationLabel.setText(currentDurationLabel);
	     songTotalDurationLabel.setText(totalDurationLabel);
	     songProgressBar.setProgress(progress);
	     String currentTitleLabel = intent.getStringExtra("currentTitle");
	     String currentDescriptionLabel = intent.getStringExtra("currentDescription");
	     tvTitle.setText(currentTitleLabel);
	     tvDescription.setText(currentDescriptionLabel);
	     boolean isLessonComplete = intent.getBooleanExtra("isLessonComplete", false);
	     if ( isLessonComplete )
	    	 updateBibleStudyProgress();	     
	   }
	 };
	 
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
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
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void sendStopTrackingTouch(int currentPosition) {
		Intent intent = new Intent("StopTrackingTouch");
		intent.putExtra("currentPosition", currentPosition);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
 
    @Override
    protected void onDestroy() {
        unbindService(this.mConnection);
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver);
        super.onDestroy();
    }
	@Override
	public void onInitializePlayerStart(String message) {
//		if ( !AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().trim().equals("") ) {
//			mProgressDialog = ProgressDialog.show(this, "", message, true);
//	        mProgressDialog.getWindow().setGravity(Gravity.CENTER);
//	        mProgressDialog.setCancelable(false);
//		}
	}
	@Override
	public void onInitializePlayerSuccess() {
//		if ( !AudioPlayerService.listTempLesson2.get(currentSongIndex).getAudioSource().trim().equals("") )
			mProgressDialog.dismiss();
	}
	@Override
	public void onError() {
		
	}
	
	@Override
	protected void onResume() {
		study = (Study) getIntent().getExtras().getParcelable("study");
		FilesManager.lastStudyTitle = study.getTitle();
		Editor editor = MainActivity.settings.edit();
		editor.remove("lastStudyTitle");
		editor.putString("lastStudyTitle", FilesManager.lastStudyTitle);
		editor.commit();
		tvTitleStudy.setText(study.getTitle());
		tvTypeStudy.setText(study.getType());
		Utilities.setActionBar(this, study.getTitle());
		updateBibleStudyProgress();
		super.onResume();		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void onClickLessonsBottomMenu(View v) {
		Intent intent = new Intent(this, BibleStudyLessonsActivity.class);
		intent.putExtra("study", study);
		startActivity(intent);
	}
	
	private void updateBibleStudyProgress() {		
		List<Lesson> listLessons = DBHandleLessons.getLessons(study.getIdProperty());
		countLessonsComplete = 0;
		countLessons = 0;
		for (Lesson lesson: listLessons) {
			if (lesson.getState().equalsIgnoreCase("complete")) 
				countLessonsComplete++;
			countLessons++;
		}
		int percent = (countLessonsComplete*100)/countLessons;
		progressBar.setProgress(percent);		
		tvProgressPercent.setText(percent + " %");
	    tvCountLessons.setText(countLessons + " Lessons");
	}
	
	private boolean checkFavoriteOn() {
		Favorite item = DBHandleFavorites.getFavoriteById(FilesManager.lastLessonId);
		if ( item != null )
			return true;
		else
			return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_details, menu);
        
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
	            finish();
	            return true;
		    	
		    case R.id.action_favorite:
		    	optionSelected = true;
		    	invalidateOptionsMenu();
	    }
		return super.onOptionsItemSelected(item);
	}
	
	Menu menuActionbar;
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menuActionbar = menu;
		updateFavorites(menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void updateFavorites(Menu menu) {
		if(checkFavoriteOn())
            menu.getItem(0).setIcon(R.drawable.icon_favorited);
        else
            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
		lastLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
		if (optionSelected) {
			if(!checkFavoriteOn()){
	            menu.getItem(0).setIcon(R.drawable.icon_favorited);
	            // Add item to Favorite List
				ItemInfo item = new ItemInfo();
	        	item.setId(FilesManager.lastLessonId);
	        	item.setType("lesson");
	        	item.setItem(lastLesson);		            	
	        	FavoritesLRU.addLruItem(lastLesson.getIdProperty(), item);
			} else {
	            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
	            // Remove item from Favorite List
				FavoritesLRU.deleteLruItem(lastLesson.getIdProperty());
	        }
			optionSelected = false;
		}
	}
	
	private void sendEmail() {
		//get to, subject and content from the user input and store as string.
//		  String emailTo 		= editTextEailTo.getText().toString();
//		  String emailSubject 	= editTextEmailSubject.getText().toString();
//		  String emailContent 	= editTextEmailContent.getText().toString();

		  Intent emailIntent = new Intent(Intent.ACTION_SEND);
//		  emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ emailTo});
		  emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "" });
		  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
		  emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent + title + emailContent2);
		  /// use below 2 commented lines if need to use BCC an CC feature in email
		  //emailIntent.putExtra(Intent.EXTRA_CC, new String[]{ to});
		  //emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{to});
		  ////use below 3 commented lines if need to send attachment
		  emailIntent .setType("text/plain");
//		  emailIntent .putExtra(Intent.EXTRA_SUBJECT, "My Picture");
//		  emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.parse("file://sdcard/captureimage.png"));

		  //need this to prompts email client only
		  emailIntent.setType("message/rfc822");

		  startActivity(Intent.createChooser(emailIntent, "Select an Email Client:"));

	}
	
}
