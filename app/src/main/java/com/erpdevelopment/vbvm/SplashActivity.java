package com.erpdevelopment.vbvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.erpdevelopment.vbvm.db.DBHandleAnswers;
import com.erpdevelopment.vbvm.db.DBHandleArticles;
import com.erpdevelopment.vbvm.db.DBHandleStudies;
import com.erpdevelopment.vbvm.db.DBHandleVideos;
import com.erpdevelopment.vbvm.db.DatabaseManager;
import com.erpdevelopment.vbvm.db.VbvmDatabaseOpenHelper;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends Activity {	
	
	private long splashDelay = 2000;
	private ProgressDialog pDialog;
	private AQuery mAQuery;

	private static int downloadCounter = 0;

	private static synchronized void incrementCount() {
		downloadCounter++;
	}
	private static synchronized void decrementCount() {
		downloadCounter--;
	}

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		mAQuery = new AQuery(this);

		MainActivity.settings = getPreferences(Activity.MODE_PRIVATE);
		WebServiceCall.articlesInDB = MainActivity.settings.getBoolean("articlesInDB", false);

		VbvmDatabaseOpenHelper mDbHelper = VbvmDatabaseOpenHelper.getInstance(this);
		DatabaseManager.initializeInstance(mDbHelper);
		MainActivity.db = DatabaseManager.getInstance().openDatabase();

		TimerTask task = new TimerTask() {
	      @Override
	      public void run() {
	        Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
	        startActivity(mainIntent);
	        finish(); //Destruimos esta activity para prevenir que el usuario retorne aqui presionando el boton Atras.
	      }
	    };
	    Timer timer = new Timer();
//	    timer.schedule(task, splashDelay);//Pasado los 3 segundos dispara la tarea
		checkUserFirstVisit();
//		if (WebServiceCall.articlesInDB) {
////			Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
////			startActivity(mainIntent);
////			finish();
//			timer.schedule(task, splashDelay);
//		} else {
			asyncJsonArticles();
			asyncJsonAnswers();
			asyncJsonVideos();
			handler.postDelayed(runnable, 100);
//		}
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			System.out.println("checking downloadCounter: " + downloadCounter);
			if (downloadCounter == 0) {
				Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish(); //Destruimos esta activity para prevenir que el usuario retorne aqui presionando el boton Atras.
				return;
			}
			handler.postDelayed(this, 100);
		}
	};

	public void asyncJsonArticles(){
		incrementCount();
		WebServiceCall.articlesInDB = MainActivity.settings.getBoolean("articlesInDB", false);
		if ( WebServiceCall.articlesInDB ){
			Log.d("Database", "Articles - working offline on DB...");
			FilesManager.listArticles = DBHandleArticles.getAllArticles(false);
			decrementCount();
		} else {
			if ( !CheckConnectivity.isOnline(this)) {
				CheckConnectivity.showMessage(this);
			} else {
				String url = WebServiceCall.JSON_ARTICLE_URL;
				long expire = 60 * 60 * 1000;

				mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

					@Override
					public void callback(String url, JSONObject json, AjaxStatus status) {
						if(json != null) {
							List<Article> listArticles = null;
							try {
								JSONObject vbv = json.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
								JSONArray articles = vbv.getJSONArray(WebServiceCall.TAG_ARTICLES);
								for ( int i=0; i < articles.length(); i++ ) {
									JSONObject c = articles.getJSONObject(i);
									Article article = new Article();
									article.setPostedDate(c.getString("postedDate")+"000");
									article.setCategory(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("category"))));
									article.setAverageRating(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("averageRating"))));
									article.setArticlesDescription(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("description"))));
									article.setBody(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("body"))));
									article.setIdProperty(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("ID"))));
									article.setAuthorName(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("authorName"))));
									article.setArticleThumbnailSource(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("articleThumbnailSource"))));
									article.setAuthorThumbnailSource(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("authorThumbnailSource"))));
									article.setTitle(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("title"))));
									article.setArticleThumbnailAltText(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("articleThumbnailAltText"))));
									article.setAuthorThumbnailAltText(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("authorThumbnailAltText"))));
								//List of topics for filter
								JSONArray jsonArrayTopics = c.getJSONArray("topics");
								for ( int j=0; j < jsonArrayTopics.length(); j++ ) {
									JSONObject p = jsonArrayTopics.getJSONObject(j);
									Topic topic = new Topic();
									topic.setIdProperty(p.getString("ID"));
									topic.setTopic(Utilities.capitalizeFirst(p.getString("topic")));
									topic.setIdParent(article.getIdProperty());
									DBHandleStudies.createTopicArticle(topic);
								}
								DBHandleStudies.createArticle(article);
							}
							listArticles = DBHandleArticles.getAllArticles(false);
								//Save state flag for sync Webservice/DB
								SharedPreferences.Editor e = MainActivity.settings.edit();
								e.putBoolean("articlesInDB", true);
								e.commit();
								Log.i("ArticlesActivity info", "Update DB with data from Webservice");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							FilesManager.listArticles = listArticles;
						} else
							Toast.makeText(mAQuery.getContext(), getResources().getString(R.string.error_message_connecting), Toast.LENGTH_SHORT).show();
//							Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
						decrementCount();
					}
				});
			}
		}
	}

	public void asyncJsonAnswers(){

		WebServiceCall.postsInDB = MainActivity.settings.getBoolean("postsInDB", false);
		if ( WebServiceCall.postsInDB ) {
			Log.d("Database", "QA Posts - working offline on DB...");
			FilesManager.listAnswers = DBHandleAnswers.getAllPosts(false);
		} else {
			if ( !CheckConnectivity.isOnline(this) ) {
				CheckConnectivity.showMessage(this);
			} else {
				String url = WebServiceCall.JSON_QANDA_URL;
				long expire = 30 * 60 * 1000;
				incrementCount();
				mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

					@Override
					public void callback(String url, JSONObject json, AjaxStatus status) {

						if(json != null) {
							List<Answer> listAnswers = null;
							try {
								JSONObject vbv = json.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
								JSONArray qAPosts = vbv.getJSONArray(WebServiceCall.TAG_QANDAPOSTS);
								Set<String> topicSet = new HashSet<>();
								List<String> topics;
								for ( int i=0; i < qAPosts.length(); i++ ) {
									JSONObject c = qAPosts.getJSONObject(i);
									Answer qAPost = new Answer();
									qAPost.setPostedDate(StringEscapeUtils.unescapeJava(c.getString("postedDate")+"000"));
									qAPost.setCategory(StringEscapeUtils.unescapeJava(c.getString("category")));
									qAPost.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
									qAPost.setqAndAPostsDescription(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("description"))));
									qAPost.setBody(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("body"))).toString());
									qAPost.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
									qAPost.setAuthorName(StringEscapeUtils.unescapeJava(c.getString("authorName")));
									qAPost.setqAndAThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("qAndAThumbnailSource")));
									qAPost.setAuthorThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailSource")));
									qAPost.setTitle(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("title"))));
									qAPost.setqAndAThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("qAndAThumbnailAltText")));
									qAPost.setAuthorThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailAltText")));
									topics = new ArrayList<String>();
									//List of topics for filter
									JSONArray jsonArrayTopics = c.getJSONArray("topics");
									for ( int j=0; j < jsonArrayTopics.length(); j++ ) {
										JSONObject p = jsonArrayTopics.getJSONObject(j);
										topics.add(p.getString("topic"));
										Topic topic = new Topic();
										topic.setIdProperty(p.getString("ID"));
										topic.setTopic(Utilities.capitalizeFirst(p.getString("topic")));
										topic.setIdParent(qAPost.getIdProperty());
										DBHandleStudies.createTopicPost(topic);
									}
									qAPost.setTopics(topics);
									DBHandleStudies.createPost(qAPost);
								}
								listAnswers = DBHandleAnswers.getAllPosts(false);
								//Save state flag for sync Webservice/DB
								SharedPreferences.Editor e = MainActivity.settings.edit();
								e.putBoolean("postsInDB", true);
								e.commit();
								Log.i("PostsActivity info", "Updated DB / QA post with data from Webservice");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							System.out.println("SplashActivity.callback: " + FilesManager.listAnswers.size());
							FilesManager.listAnswers = listAnswers;
						} else
							Toast.makeText(mAQuery.getContext(), getResources().getString(R.string.error_message_connecting), Toast.LENGTH_SHORT).show();
						decrementCount();					}
				});
			}
		}
	}

	public void asyncJsonVideos(){

		incrementCount();
		WebServiceCall.videosInDB = MainActivity.settings.getBoolean("videosInDB", false);
		if ( WebServiceCall.videosInDB ) {
			Log.d("Database", "Videos - working offline on DB...");
			FilesManager.listVideoChannels = DBHandleVideos.getChannels();
			decrementCount();
		} else {
			if ( !CheckConnectivity.isOnline(this)) {
				CheckConnectivity.showMessage(this);
			} else {
				String url = WebServiceCall.JSON_CHANNELS_URL;
				long expire = 30 * 60 * 1000;

				mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

					@Override
					public void callback(String url, JSONObject json, AjaxStatus status) {

						if(json != null) {
							String responseString = StringEscapeUtils.unescapeHtml(json.toString());
							List<VideoChannel> listVideoChannels = new ArrayList<VideoChannel>();
							try {
								JSONObject jsonResponse = new JSONObject(responseString);
								JSONObject vbv = jsonResponse.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
								JSONArray channels = vbv.getJSONArray(WebServiceCall.TAG_CHANNELS);
								for ( int i=0; i < channels.length(); i++ ) {
									JSONObject c = channels.getJSONObject(i);
									VideoChannel channel = new VideoChannel();
									channel.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
									channel.setPostedDate(c.getString("postedDate")+"000");
									channel.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
									channel.setDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
									channel.setTitle(StringEscapeUtils.unescapeJava(c.getString("title")));
									channel.setThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("thumbnailSource")));
									channel.setThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("thumbnailAltText")));
									List<VideoVbvm> listVideos = new ArrayList<VideoVbvm>();
									JSONArray jsonArrayVideos = c.getJSONArray(WebServiceCall.TAG_VIDEOS);
									for ( int j=0; j < jsonArrayVideos.length(); j++ ) {
										JSONObject p = jsonArrayVideos.getJSONObject(j);
										VideoVbvm video = new VideoVbvm();
										video.setIdProperty(StringEscapeUtils.unescapeJava(p.getString("ID")));
										video.setDescription(StringEscapeUtils.unescapeJava(p.getString("description")));
										video.setVideoSource(StringEscapeUtils.unescapeJava(p.getString("videoSource")));
										video.setVideoLength(StringEscapeUtils.unescapeJava(p.getString("videoLength")));
										video.setTitle(StringEscapeUtils.unescapeJava(p.getString("title")));
										video.setThumbnailSource(StringEscapeUtils.unescapeJava(p.getString("thumbnailSource")));
										video.setThumbnailAltText(StringEscapeUtils.unescapeJava(p.getString("thumbnailAltText")));
										video.setIdChannel(channel.getIdProperty());
										listVideos.add(video);
										DBHandleStudies.createVideo(video);
									}
									channel.setVideos(listVideos);
									listVideoChannels.add(channel);
									DBHandleStudies.createChannel(channel);
								}
								listVideoChannels = DBHandleVideos.getChannels();
								Collections.sort(listVideoChannels);
								//Save state flag for sync Webservice/DB
								SharedPreferences.Editor e = MainActivity.settings.edit();
								e.putBoolean("videosInDB", true);
								e.commit();
								Log.i("HomeFragment info", "Updated DB / Videos from Webservice");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							FilesManager.listVideoChannels = listVideoChannels;
						} else
							Toast.makeText(mAQuery.getContext(), getResources().getString(R.string.error_message_connecting), Toast.LENGTH_SHORT).show();
//							Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
						decrementCount();
					}
				});
			}
//			mScroll.scrollTo(0, 0);
		}
	}

	private boolean checkUserFirstVisit(){
		long lastUpdateTime = MainActivity.settings.getLong("lastUpdateKey", 0L);
		long timeElapsed = System.currentTimeMillis() - lastUpdateTime;
		// YOUR UPDATE FREQUENCY HERE
		final long UPDATE_FREQ = 1000 * 60 * 60 * 3; //Every 1 hour
		SharedPreferences.Editor e = MainActivity.settings.edit();
		if (timeElapsed > UPDATE_FREQ) {
			e.putBoolean("studiesInDB", false);
			e.putBoolean("lessonsInDB", false);
			e.putBoolean("articlesInDB", false);
			e.putBoolean("postsInDB", false);
			e.putBoolean("eventsInDB", false);
			e.putBoolean("videosInDB", false);
			Log.i("MainActivity info", "Update DB with data from Webservice");
		}
		// STORE LATEST UPDATE TIME
		e.putLong("lastUpdateKey", System.currentTimeMillis());
		e.commit();
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeCallbacks(runnable);
	}
}
