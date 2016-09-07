package com.erpdevelopment.vbvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.db.DBHandleStudies;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

public class SplashActivity extends Activity {	
	
	private long splashDelay = 2000;
	private ProgressDialog pDialog;
	private AQuery aQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		TimerTask task = new TimerTask() {
	      @Override
	      public void run() {
	        Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
	        startActivity(mainIntent);
	        finish(); //Destruimos esta activity para prevenir que el usuario retorne aqui presionando el boton Atras.
	      }
	    };
	    Timer timer = new Timer();
	    timer.schedule(task, splashDelay);//Pasado los 3 segundos dispara la tarea
	    
//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog_loading));
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(false);        
//        
//        aQuery = new AQuery(this);
	}
	
//	private void asyncJsonGetStudies(){
//	    
//		pDialog.show();
//		WebServiceCall.studiesInDB = MainActivity.settings.getBoolean("studiesInDB", false);        
//    	if ( WebServiceCall.studiesInDB ){    		
////    		Log.d("Database", "Studies - working offline on DB...");            
////            List<Study> dbListStudies = DBHandleStudies.getAllStudies();
////			List<Lesson> dbListLessons = DBHandleLessons.getLessons(null);
//////			newestAdapter.setLessonListItems(dbListLessons);
////        	FilesManager.listStudies = dbListStudies;
////        	new asyncGetAllLessons().execute();
//    	} else {    		
//    		if ( !CheckConnectivity.isOnline(SplashActivity.this)) {
//    			CheckConnectivity.showMessage(SplashActivity.this);
//    		} else {    			
//		        String url = WebServiceCall.JSON_STUDY_URL;
//		        final long expire = 30 * 60 * 1000;
//		        
//				aQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {
//		
//		            @Override
//		            public void callback(String url, JSONObject json, AjaxStatus status) {                    
//		                if(json != null){
//		                    //successful ajax call, show status code and json content
//		                	String responseString = StringEscapeUtils.unescapeHtml(json.toString());
//		                	final List<Study> newListStudies = new ArrayList<Study>();
//		    		        try {
//		    		        	JSONObject jsonResponse = new JSONObject(responseString);
//		    					JSONObject vbv = jsonResponse.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
//		    					// Getting JSON Array
//		    					final JSONArray studies = vbv.getJSONArray(WebServiceCall.TAG_STUDIES);
//		    					System.out.println("downloading studies...");
//		    					for ( int i=0; i < studies.length(); i++ ) {
//		    						final Study study = new Study();				
//		    						study.setThumbnailSource(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("thumbnailSource")));
//		    						study.setTitle(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("title")));
//		    						study.setThumbnailAltText(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("thumbnailAltText")));
//		    						study.setPodcastLink(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("podcastLink")));
//		    						study.setAverageRating(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("averageRating")));
//		    						study.setStudiesDescription(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("description")));
//		    						study.setType(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("type")));
//		    						study.setIdProperty(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("ID")));    						
//		    						newListStudies.add(study);
//		    	    				DBHandleStudies.createStudy(study); 
//		    					}
//		    				} catch (JSONException e) {
//		    					e.printStackTrace();
//		    				}    	
//		    	        	FilesManager.listStudies = newListStudies;
//		    	        	
//		    	        	//Save state flag for sync Webservice/DB
//		    				Editor e = MainActivity.settings.edit();
//		    				e.putBoolean("studiesInDB", true);
//		    				e.commit();			
//		    	        	
//		                }else{
//		                    //ajax error, show error code
//		                    Toast.makeText(aQuery.getContext(), "Server not available. Please try later...", Toast.LENGTH_LONG).show();
//		                }
////			        	scroll.scrollTo(0, 0);			        	
////			        	new asyncGetAllLessons().execute();
//		                
//		                for (Study study: FilesManager.listStudies) {
////				        	List<Lesson> list = new WebServiceCall().getStudyLessons(study); //get and save lessons to DB
//				            asyncGetAllLessons(study);
//		                }
//		            }
//		        });
//    		}
//    		
//    	}
//    }
//
//	private void asyncGetAllLessons(final Study study){
//	    
//		pDialog.show();
//		WebServiceCall.lessonsInDB = MainActivity.settings.getBoolean("lessonsInDB", false);        
//    	if ( WebServiceCall.lessonsInDB ){    		
////    		Log.d("Database", "Studies - working offline on DB...");            
////            List<Study> dbListStudies = DBHandleStudies.getAllStudies();
////			List<Lesson> dbListLessons = DBHandleLessons.getLessons(null);
//////			newestAdapter.setLessonListItems(dbListLessons);
////        	FilesManager.listStudies = dbListStudies;
////        	new asyncGetAllLessons().execute();
//    	} else {    		
//    		if ( !CheckConnectivity.isOnline(SplashActivity.this)) {
//    			CheckConnectivity.showMessage(SplashActivity.this);
//    		} else {    			
//		        String url = WebServiceCall.JSON_STUDY_DETAIL_URL + study.getIdProperty();
//		        final long expire = 30 * 60 * 1000;
//		        
//				aQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {
//		
//		            @Override
//		            public void callback(String url, JSONObject json, AjaxStatus status) {                    
//		                if(json != null){
//		                    //successful ajax call, show status code and json content
//		                	String responseString = StringEscapeUtils.unescapeHtml(json.toString());
//		                	List<Lesson> newListLessons = new ArrayList<Lesson>();
//		    		        try {
//		    		        	JSONObject jsonResponse = new JSONObject(responseString);
//		    					JSONObject vbv = jsonResponse.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
//		    					JSONArray lessonArray = vbv.getJSONArray(WebServiceCall.TAG_LESSONS);
//		    					
//		    					List<Topic> listLessonTopics = new ArrayList<Topic>();
//		    					List<Lesson> listLessons = new ArrayList<Lesson>();
//		    					
//		    					for ( int i=0; i < lessonArray.length(); i++ ) {
//		    						Lesson lesson = new Lesson();
//		    						lesson.setIdProperty(lessonArray.getJSONObject(i).getString("ID"));
//		    						lesson.setLessonsDescription(lessonArray.getJSONObject(i).getString("description"));
//		    						lesson.setPostedDate(lessonArray.getJSONObject(i).getString("postedDate"));
//		    						lesson.setTranscript(lessonArray.getJSONObject(i).getString("transcript"));
//		    						lesson.setDateStudyGiven(lessonArray.getJSONObject(i).getString("dateStudyGiven"));
//		    						lesson.setTeacherAid(lessonArray.getJSONObject(i).getString("teacherAid"));
//		    						lesson.setAverageRating(lessonArray.getJSONObject(i).getString("averageRating"));
//		    						lesson.setVideoSource(lessonArray.getJSONObject(i).getString("videoSource"));
//		    						lesson.setVideoLength(lessonArray.getJSONObject(i).getString("videoLength"));
//		    						lesson.setTitle(lessonArray.getJSONObject(i).getString("title"));
//		    						lesson.setLocation(lessonArray.getJSONObject(i).getString("location"));
//		    						lesson.setAudioSource(lessonArray.getJSONObject(i).getString("audioSource"));
//		    						lesson.setAudioLength(lessonArray.getJSONObject(i).getString("audioLength"));
//		    						lesson.setStudentAid(lessonArray.getJSONObject(i).getString("studentAid"));
//		    						lesson.setPositionInList(i);
//		    						lesson.setIdStudy(study.getIdProperty());
//		    						lesson.setStudyThumbnailSource(study.getThumbnailSource());
//		    						lesson.setStudyLessonsSize(lessonArray.length());
//		    						lesson.setDownloadStatus(0);
//		    						
//		    						JSONArray ltopics = lessonArray.getJSONObject(i).getJSONArray(WebServiceCall.TAG_TOPICS);
//		    						
//		    						listLessonTopics = new ArrayList<Topic>();					
//		    						for (int k=0; k < ltopics.length(); k++){
//		    							Topic topic = new Topic();
//		    							topic.setIdProperty(ltopics.getJSONObject(k).getString("ID"));
//		    							topic.setTopic(ltopics.getJSONObject(k).getString("topic"));
//		    							topic.setIdParent(lesson.getIdProperty());
//		    							listLessonTopics.add(topic);
//		    								DBHandleStudies.createTopicLesson(topic);
//		    						}
//		    						lesson.setTopics(listLessonTopics);	
//		    						listLessons.add(lesson);
//		    						DBHandleStudies.createLesson(lesson);
//		    						
//		    						newListLessons.add(lesson);
//		    	    				DBHandleStudies.createStudy(study); 
//		    					}
//		    				} catch (JSONException e) {
//		    					e.printStackTrace();
//		    				}    	
////		    	        	FilesManager.listStudies = newListStudies;
//		    	        	
//		    	        	//Save state flag for sync Webservice/DB
//		    				Editor e = MainActivity.settings.edit();
//		    				e.putBoolean("lessonsInDB", true);
//		    				e.commit();			
//		    	        	
//		                }else{
//		                    //ajax error, show error code
//		                    Toast.makeText(aQuery.getContext(), "Server not available. Please try later...", Toast.LENGTH_LONG).show();
//		                }
////			        	scroll.scrollTo(0, 0);			        	
////			        	new asyncGetAllLessons().execute();			        	
//		            }
//		        });
//    		}
//    		
//    	}
//    }
	
	
}
