package com.erpdevelopment.vbvm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.LessonListAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.service.DownloadServiceTest;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.BitmapManager;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BibleStudyLessonsActivity extends Activity {

	private ListView lvLessons;
	private Study mStudy;
	private LessonListAdapter adapter;
	private List<Lesson> lessons;
	public static boolean lessonListSelected = false;
	static int counter = 0;
	private ArrayList<Lesson> listTempLesson = new ArrayList<Lesson>();
	private TextView tvDownloading;
	private FileCache fileCache;
	private ImageView imgMenuBarCancel;
	private ImageView imgMenuBarOptions;
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_details);
		
		final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_filter);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
		
		//Top Menu Bar
		imgMenuBarCancel = (ImageView) findViewById(R.id.img_filter_cancel);
		imgMenuBarOptions = (ImageView) findViewById(R.id.img_more_options);
		imgMenuBarOptions.setVisibility(View.VISIBLE);
		lvLessons = (ListView) findViewById(R.id.lv_lessons);
		tvDownloading = (TextView) findViewById(R.id.downloading);
		tvTitle = (TextView) findViewById(R.id.tv_filter_title);
		tvTitle.setText("Lessons");

		
		lessons = new ArrayList<Lesson>();
		adapter = new LessonListAdapter(this, lessons);
		lvLessons.setAdapter(adapter);
		
		imgMenuBarCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		imgMenuBarOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(BibleStudyLessonsActivity.this);    	
				builder.setTitle(BibleStudyLessonsActivity.this.getResources().getString(R.string.title_dialog_mark_lessons_as_played))
						.setItems(R.array.items_mark_lessons_as_played, 
								new DialogInterface.OnClickListener() {									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
										switch (which) {
											case 0:	markAllAsPlayed("complete");
													break;
											case 1: markAllAsPlayed("new");
													break;
											case 2: downloadAllLessons();
													break;
											case 3: deleteAllLessons();
													break;
											default:
												break;
										}
									}							
								})
				        .setCancelable(false)
				        .setPositiveButton(BibleStudyLessonsActivity.this.getResources().getString(R.string.dialog_button_cancel),
				                new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int id) {
				                    	dialog.cancel();
				                    }
				                });
				AlertDialog alert = builder.create();
				alert.show();
				
			}
		});
		
	    fileCache = new FileCache(MainActivity.mainCtx);
		
	    registerReceiver(receiver, new IntentFilter(DownloadServiceTest.NOTIFICATION_COMPLETE));
    	registerReceiver(receiverDownloading, new IntentFilter(DownloadServiceTest.NOTIFICATION2));
	}
	
    private class asyncGetStudyLessons extends AsyncTask< Study, String, String > {
    	 
		private ProgressDialog pDialog;
		private Study study;
		private List<Lesson> list = new ArrayList<Lesson>();
    	
    	protected void onPreExecute() {
            pDialog = new ProgressDialog(BibleStudyLessonsActivity.this);
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
	    		if ( !CheckConnectivity.isOnline(BibleStudyLessonsActivity.this)) {
	    			CheckConnectivity.showMessage(BibleStudyLessonsActivity.this);
	    		} else {
					list = new WebServiceCall().getStudyLessons(study); //get and save lessons to DB
					Log.i("BibleStudyDetailsActiv", "Update DB with data from Webservice");
	    		}
			} 			
			study.setLessons(list);
			mStudy.setLessons(list);
			
			resetDownloadingState();
			
			if (list != null)
				return "1";
			else
				return "";
        }
       
        protected void onPostExecute(String result) {
        	
            pDialog.dismiss();//ocultamos progess dialog.  	            	
            
            if (result != ""){
            	adapter.setLessonListItems(mStudy.getLessons());
            	//remember selected row
//            	prefs = getSharedPreferences(ConstantsVbvm.VBVM_PREFS, Context.MODE_PRIVATE);
        		//int position = prefs.getInt("posLesson", -1);
        		int index = MainActivity.settings.getInt("indexLesson", -1);		
        		String savedTitle = MainActivity.settings.getString("studyTitle", "");						
        		if (savedTitle.equals(study.getTitle())) {
        			// user clicked a list item, make it "selected"
        			//if ( position != -1 ) {
        				//adapter.setSelectedPosition(position);
        				if ( index != -1 ) {
        					View vi = lvLessons.getChildAt(0);
        		            int top = (vi == null) ? 0 : vi.getTop();            
        		            // restore index and position
        		            lvLessons.setSelectionFromTop(index, top);
        				}
        			//}	
        		}
        		
  	            //lvLessons.setAdapter(adapter);
  	            lvLessons.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parentView, View view,
							int position, long id) {
						
						Lesson lesson = (Lesson) parentView.getItemAtPosition(position);
						if ( !CheckConnectivity.isOnline(BibleStudyLessonsActivity.this) && lesson.getDownloadStatus() != 1 ) {
							CheckConnectivity.showMessage(BibleStudyLessonsActivity.this);
						} else {
							adapter.setSelectedPosition(position);
	
							//Storing Data using SharedPreferences						
							SharedPreferences.Editor edit = MainActivity.settings.edit();
							edit.remove("posLesson");
							edit.remove("studyTitle");
				            edit.putInt("posLesson", position);
				            edit.putString("studyTitle", study.getTitle());
				            edit.commit();
							
				         	// save index and top position
				            int index = lvLessons.getFirstVisiblePosition();
				            edit.remove("indexLesson").commit();
				            edit.putInt("indexLesson", index).commit();
				            
							lesson.setStudyThumbnailSource(study.getThumbnailSource());
							lesson.setStudyLessonsSize(study.getLessons().size());
							
							if (!(lesson.getIdProperty().equals(FilesManager.lastLessonId))){
								AudioPlayerService.created = false;
								//save current/old position in track before updating to new position
								if (!FilesManager.lastLessonId.equals("")) {
									System.out.println("old lesson Id is: " + FilesManager.lastLessonId);
									DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, (int)AudioPlayerService.currentPositionInTrack);
									System.out.println("position saved is: " + AudioPlayerService.currentPositionInTrack);
								}
								Lesson oldLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
//								if ( !oldLesson.getState().equals("complete") )
								if ( oldLesson.getState().equals("playing") )
									DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
								//Update position in track with new selected lesson's
								Lesson newLesson = DBHandleLessons.getLessonById(lesson.getIdProperty());
//								DBHandleLessons.updateLessonState(newLesson.getIdProperty(), newLesson.getCurrentPosition(), "playing");
								AudioPlayerService.currentPositionInTrack = newLesson.getCurrentPosition();
								AudioPlayerService.savedOldPositionInTrack = AudioPlayerService.currentPositionInTrack;
								System.out.println("position restored is: " + AudioPlayerService.currentPositionInTrack);	
							}						
							
//							FilesManager.positionLessonInList = position;
							FilesManager.lastLessonId = lesson.getIdProperty();
			            	
							Lesson les = null;
			            	for (int i=0; i<study.getLessons().size(); i++){
			        			les = new Lesson();
			        			les.setIdProperty(study.getLessons().get(i).getIdProperty());
			        			les.setAudioSource(study.getLessons().get(i).getAudioSource());
			        			les.setTitle(study.getLessons().get(i).getTitle());
			        			les.setLessonsDescription(study.getLessons().get(i).getLessonsDescription());
			        			les.setStudyThumbnailSource(study.getThumbnailSource());
			        			les.setStudyLessonsSize(study.getLessons().size());
			        			les.setTranscript(study.getLessons().get(i).getTranscript());
			        			les.setStudentAid(study.getLessons().get(i).getStudentAid());
			        			les.setTeacherAid(study.getLessons().get(i).getTeacherAid());
			        			listTempLesson.add(les);	
			            	}	
			        		
			                AudioPlayerService.listTempLesson2 = listTempLesson;
			            
							Intent i = new Intent(BibleStudyLessonsActivity.this, AudioControllerActivity.class);
//			                Intent i = getIntent();
							i.putExtra("position", position);
							i.putExtra("lessonIdProperty", lesson.getIdProperty());
							i.putExtra("thumbnailSource", study.getThumbnailSource());
							i.putExtra("description", study.getLessons().get(position).getLessonsDescription());
							i.putExtra("title", study.getLessons().get(position).getTitle());
							i.putExtra("size", study.getLessons().size());
							i.putExtra("readSource", study.getLessons().get(position).getTranscript());
							i.putExtra("presentSource", study.getLessons().get(position).getStudentAid());
							i.putExtra("handoutSource", study.getLessons().get(position).getTeacherAid());
							i.putExtra("study", mStudy);
//							setResult(RESULT_OK, i);
//							finish();
				    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //This will clear all the activities on top of AudioControllerActivity
							startActivity(i);
//							finish();
						}
					}
				});
            }else{
            	Log.e("onPostExecute=", "List is null"); 
            }
        }
    }
    
    @Override
    protected void onStart() {
    	Bundle b = getIntent().getExtras();
		Parcelable p = b.getParcelable("study");
		mStudy = (Study) p;
//    	MainActivity.settings = getSharedPreferences(ConstantsVbvm.VBVM_PREFS, Context.MODE_PRIVATE);
		//int position = prefs.getInt("posLesson", -1);
		int index = MainActivity.settings.getInt("indexLesson", -1);		
		String savedTitle = MainActivity.settings.getString("studyTitle", "");						
		if (savedTitle.equals(mStudy.getTitle())) {
			// user clicked a list item, make it "selected"
			//if ( position != -1 ) {
				//adapter.setSelectedPosition(position);
				if ( index != -1 ) {
					View vi = lvLessons.getChildAt(0);
		            int top = (vi == null) ? 0 : vi.getTop();            
		            // restore index and position
		            lvLessons.setSelectionFromTop(index, top);
				}
			//}	
		}
    	super.onStart();
    }
    
    @Override
    protected void onResume() {
    	new asyncGetStudyLessons().execute(mStudy);
    	adapter.setLessonListItems(mStudy.getLessons());
    	if ( DownloadServiceTest.downloading ){
    		tvDownloading.setText("Downloading all: " + DownloadServiceTest.downloadAllTitle);
	        tvDownloading.setVisibility(View.VISIBLE);
	        tvTitle.setVisibility(View.GONE);
	        imgMenuBarOptions.setVisibility(View.GONE);
    	} else {
    		// if not downloading study, check and reset state for each lesson
    		for (Lesson lesson: mStudy.getLessons()){
    			if ( lesson.getDownloadStatus() == 2 )
    				DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0);
    		}
    		fileCache.clearTempFolder();
	        tvDownloading.setVisibility(View.GONE);
	        tvTitle.setVisibility(View.VISIBLE);
	        imgMenuBarOptions.setVisibility(View.VISIBLE);
    	}
    	super.onResume();
    }
    
    @Override
	  protected void onPause() {
	    super.onPause();
	  }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
	    unregisterReceiver(receiver);
	    unregisterReceiver(receiverDownloading);
    	super.onDestroy();
    }
	
    private void markAllAsPlayed(String state) {
    	List<Lesson> lessons = new ArrayList<Lesson>();
    	for (Lesson lesson : mStudy.getLessons()){
//    		if (!(lesson.getIdProperty().equals(FilesManager.lastLessonId))){
	    		DBHandleLessons.updateLessonState(lesson.getIdProperty(), 0, state);
	    		lesson.setCurrentPosition(0);
	    		lesson.setState(state);
//    		}
	    	lessons.add(lesson);	    		
    	}
    	adapter.setLessonListItems(lessons);
    }
    
    private void downloadAllLessons() {
        if ( !DownloadServiceTest.downloading ) {
        	// Check if there's at least one lesson not downloaded
        	boolean allDownloaded = true;
        	for ( int i=0; i<mStudy.getLessons().size(); i++ ) {
        		if ( !mStudy.getLessons().get(i).getAudioSource().equals("") ){
        			if ( mStudy.getLessons().get(i).getDownloadStatus() == 0 ) {
        				allDownloaded = false;
        				break;
        			}
        		}
        	}        	
        	if ( !allDownloaded ) {
        		tvDownloading.setText("Downloading all: " + DownloadServiceTest.downloadAllTitle);
			    tvDownloading.setVisibility(View.VISIBLE);
			    tvTitle.setVisibility(View.GONE);
			    imgMenuBarOptions.setVisibility(View.GONE);
		        for ( int i=0; i<mStudy.getLessons().size(); i++ ) {
		        	Lesson lesson = mStudy.getLessons().get(i);
		        	//Download if lesson is not downloaded or is downloading
		        	if ( lesson.getDownloadStatus() == 0 || lesson.getDownloadStatus() == 2 )
		        	{
		        		if ( !lesson.getAudioSource().equals("") ) {
			        		Intent intent = new Intent(this, DownloadServiceTest.class);
						    intent.putExtra("lesson", lesson);
						    intent.putExtra("studyTitle", mStudy.getTitle());
						    startService(intent);	
						    DownloadServiceTest.incrementCount();
//						    tvDownloading.setText("Downloading all: " + DownloadServiceTest.downloadAllTitle);
//						    tvDownloading.setVisibility(View.VISIBLE);
//						    tvTitle.setVisibility(View.GONE);
//						    imgMenuBarOptions.setVisibility(View.GONE);
			        	}
		        	}
		        }
        	} else {
        		Toast.makeText(BibleStudyLessonsActivity.this,
        	              "All lessons already downloaded!",
        	              Toast.LENGTH_LONG).show();
        	}
        } else {
        	Toast.makeText(BibleStudyLessonsActivity.this,
  	              "Please wait. Another download is in progress...",
  	              Toast.LENGTH_LONG).show();
        }
    }
    
    private void deleteAllLessons() {
    	
    	if ( !DownloadServiceTest.downloading ) {
	    	boolean deleted = false;
	    	int count = 0;
	    	for (int i=0; i<mStudy.getLessons().size(); i++){
	    		
	    		String audioUrl = mStudy.getLessons().get(i).getAudioSource();
	    		String pdfUrl1 = mStudy.getLessons().get(i).getTranscript();
	    		String pdfUrl2 = mStudy.getLessons().get(i).getTeacherAid();
	    		String pdfUrl3 = mStudy.getLessons().get(i).getStudentAid();
	    		int status = mStudy.getLessons().get(i).getDownloadStatus();
	    		String idLesson = mStudy.getLessons().get(i).getIdProperty();
	    		if ( status==1 || status==2 ){
	    			if (!audioUrl.equals(""))
	    				new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(audioUrl)).delete();
	    			if (!pdfUrl1.equals(""))
	    				new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(pdfUrl1)).delete();
	    			if (!pdfUrl2.equals(""))
	    				new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(pdfUrl2)).delete();
	    			if (!pdfUrl3.equals(""))
	    				new File(FileCache.cacheDirAudio.getAbsolutePath(), BitmapManager.getFileNameFromUrl(pdfUrl3)).delete();
	    			DBHandleLessons.updateLessonDownloadStatus(idLesson, 0);
	    			count++;
	    			deleted = true;
	    		}	    				       
	    	}
	    	if (deleted) {
	    		Toast.makeText(this, count + " lesson(s) deleted", Toast.LENGTH_LONG).show();
	    		new asyncGetStudyLessons().execute(mStudy);
	    	} else {
	    		Toast.makeText(this, "No lesson(s) to delete", Toast.LENGTH_LONG).show();
	    	}
    	} else {
        	Toast.makeText(BibleStudyLessonsActivity.this,
    	              "Please wait. A download is in progress...",
    	              Toast.LENGTH_LONG).show();
        }	
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	      Bundle bundle = intent.getExtras();
	      boolean oneDownloadComplete = false; //At least one download has finished successfully 
	      if (bundle != null) {
		    int resultCode = bundle.getInt(DownloadServiceTest.RESULT);
	        String fileName = bundle.getString(DownloadServiceTest.FILENAME);
	        if (resultCode == RESULT_OK) {
	        	System.out.println("Downloaded: " + fileName);
	          Toast.makeText(BibleStudyLessonsActivity.this,
	              "Downloaded: " + fileName,
	              Toast.LENGTH_LONG).show();
	          oneDownloadComplete = true;
		  	  new asyncGetStudyLessons().execute(mStudy);
	        } else {
	          Toast.makeText(BibleStudyLessonsActivity.this, "Download failed!: " + fileName,
	              Toast.LENGTH_LONG).show();
	        }
	        DownloadServiceTest.decrementCount();
	        if (oneDownloadComplete) {
	        	if (DownloadServiceTest.countDownloads == 0){
	        		DownloadServiceTest.downloading = false;
	        		tvDownloading.setVisibility(View.GONE);
				    tvDownloading.setText("");
				    tvTitle.setVisibility(View.VISIBLE);
				    imgMenuBarOptions.setVisibility(View.VISIBLE);
	        		new asyncGetStudyLessons().execute(mStudy);
		        }
	        }
	      }
	    }
	  };
	
	  private BroadcastReceiver receiverDownloading = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
		    if (bundle != null) {
			    boolean updateDownloadingStatus = bundle.getBoolean("updateDownloadingStatus");
			    if (updateDownloadingStatus)
			    	new asyncGetStudyLessons().execute(mStudy);			    
			    tvDownloading.setText("Downloading all: " + DownloadServiceTest.downloadAllTitle);
		        tvDownloading.setVisibility(View.VISIBLE);
		    }	
		}
	  };
	  
	  private void resetDownloadingState() {
		  if ( !DownloadServiceTest.downloading ){
	    		// if not downloading study, check and reset state for each lesson
	    		for (Lesson lesson: mStudy.getLessons()){
	    			if ( lesson.getDownloadStatus() == 2 )
	    				DBHandleLessons.updateLessonDownloadStatus( lesson.getIdProperty(), 0);
	    		}
	    		List<Lesson> list = DBHandleLessons.getLessons(mStudy.getIdProperty());
	    		mStudy.setLessons(list);
	    		fileCache.clearTempFolder();
	    	}
	  }
}
