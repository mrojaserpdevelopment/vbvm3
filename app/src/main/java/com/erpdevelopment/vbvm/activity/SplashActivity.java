package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.erpdevelopment.vbvm.R;
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

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
	
	private AQuery mAQuery;
	private static int downloadCounter = 3;
	private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

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
		showPermissionsRequest();
	}

	private void showPermissionsRequest() {
		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();
		if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
			permissionsNeeded.add("Storage");
		if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
			permissionsNeeded.add("Phone");
		if (permissionsList.size() > 0) {
			if (permissionsNeeded.size() > 0) {
				// Need Rationale
				String message = "You need to grant access to " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++)
					message = message + ", " + permissionsNeeded.get(i);
				showMessageOKCancel(message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which==-1) {
									ActivityCompat.requestPermissions(SplashActivity.this,
											permissionsList.toArray(new String[permissionsList.size()]),
											REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
								} else {
									finish();
								}
							}
						});
				return;
			}
			ActivityCompat.requestPermissions(SplashActivity.this,
					permissionsList.toArray(new String[permissionsList.size()]),
					REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			return;
		}
		startApp();
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(SplashActivity.this)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", okListener)
				.create()
				.show();
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
				return false;
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
			{
				Map<String, Integer> perms = new HashMap<String, Integer>();
				// Initial
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted
					startApp();
				} else {
					// Permission Denied
					Toast.makeText(SplashActivity.this, "Some Permission is Denied", Toast.LENGTH_LONG)
							.show();
					finish();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void startApp() {
		mAQuery = new AQuery(this);

		MainActivity.settings = getPreferences(Activity.MODE_PRIVATE);
		WebServiceCall.studiesInDB = MainActivity.settings.getBoolean("studiesInDB", false);

		VbvmDatabaseOpenHelper mDbHelper = VbvmDatabaseOpenHelper.getInstance(this);
		DatabaseManager.initializeInstance(mDbHelper);
		MainActivity.db = DatabaseManager.getInstance().openDatabase();

		checkUserFirstVisit();
		downloadCounter = 3;
		asyncJsonArticles();
		asyncJsonAnswers();
		asyncJsonVideos();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (downloadCounter == 0) {
						Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
						startActivity(mainIntent);
						finish(); //Destruimos esta activity para prevenir que el usuario retorne aqui presionando el boton Atras.
						return;
					}
				}
			}
		}).start();
	}


//	private Runnable runnable = new Runnable() {
//		@Override
//		public void run() {
//			System.out.println("checking downloadCounter: " + downloadCounter);
//			if (downloadCounter == 0) {
//				Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
//				startActivity(mainIntent);
//				finish(); //Destruimos esta activity para prevenir que el usuario retorne aqui presionando el boton Atras.
//				return;
//			}
//			handler.postDelayed(this, 100);
//		}
//	};

	public void asyncJsonArticles(){
//		incrementCount();
//		System.out.println("checking downloadCounter: " + downloadCounter);
		WebServiceCall.articlesInDB = MainActivity.settings.getBoolean("articlesInDB", false);
		if ( WebServiceCall.articlesInDB ){
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.d("Database", "Articles - working offline on DB...");
					FilesManager.listArticles = DBHandleArticles.getAllArticles(false);
					decrementCount();
//					System.out.println("checking downloadCounter: " + downloadCounter);
				}
			}).start();
//			Log.d("Database", "Articles - working offline on DB...");
//			FilesManager.listArticles = DBHandleArticles.getAllArticles(false);
//			decrementCount();
//			System.out.println("checking downloadCounter: " + downloadCounter);
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
//						System.out.println("checking downloadCounter: " + downloadCounter);
					}
				});
			}
		}
	}

	public void asyncJsonAnswers(){
//		incrementCount();
//		System.out.println("checking downloadCounter: " + downloadCounter);
		WebServiceCall.postsInDB = MainActivity.settings.getBoolean("postsInDB", false);
		if ( WebServiceCall.postsInDB ) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.d("Database", "QA Posts - working offline on DB...");
					FilesManager.listAnswers = DBHandleAnswers.getAllPosts(false);
					decrementCount();
//					System.out.println("checking downloadCounter: " + downloadCounter);
				}
			}).start();
//			Log.d("Database", "QA Posts - working offline on DB...");
//			FilesManager.listAnswers = DBHandleAnswers.getAllPosts(false);
//			decrementCount();
		} else {
			if ( !CheckConnectivity.isOnline(this) ) {
				CheckConnectivity.showMessage(this);
			} else {
				String url = WebServiceCall.JSON_QANDA_URL;
				long expire = 30 * 60 * 1000;
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
						decrementCount();
//						System.out.println("checking downloadCounter: " + downloadCounter);
					}
				});
			}
		}
	}

	public void asyncJsonVideos(){

//		incrementCount();
//		System.out.println("checking downloadCounter: " + downloadCounter);
		WebServiceCall.videosInDB = MainActivity.settings.getBoolean("videosInDB", false);
		if ( WebServiceCall.videosInDB ) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.d("Database", "Videos - working offline on DB...");
					FilesManager.listVideoChannels = DBHandleVideos.getChannels();
					decrementCount();
//					System.out.println("checking downloadCounter: " + downloadCounter);
				}
			}).start();

//			Log.d("Database", "Videos - working offline on DB...");
//			FilesManager.listVideoChannels = DBHandleVideos.getChannels();
//			decrementCount();
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
//						System.out.println("checking downloadCounter: " + downloadCounter);
					}
				});
			}
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
		FilesManager.lastLessonId = MainActivity.settings.getString("currentLessonId","");
		return false;
	}

//	@Override
//	protected void onStop() {
//		super.onStop();
//		handler.removeCallbacks(runnable);
//	}
}
