package com.erpdevelopment.vbvm.db;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.service.WebServiceCall;

public class DBHandleAnswers {

	private static final String LOG = "DatabaseHelper Answers";
    
	private static final String COLUMN_ID_POST = "id_post";
	private static final String COLUMN_POSTED_DATE = "posted_date";
	private static final String COLUMN_CATEGORY = "category";
	private static final String COLUMN_AVERAGE_RATING = "average_rating";
	private static final String COLUMN_POST_DESCRIPTION = "post_description";
	private static final String COLUMN_BODY = "body";
	private static final String COLUMN_AUTHOR_NAME = "author_name";
	private static final String COLUMN_AUTHOR_THUMBNAIL_SOURCE = "author_thumbnail_source";
	private static final String COLUMN_POST_THUMBNAIL_SOURCE = "post_thumbnail_source";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_POST_THUMBNAIL_ALT_TEXT = "post_thumbnail_alt_text";
	private static final String COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT = "author_thumbnail_alt_text";	

    
	public static List<Answer> getAllPosts(boolean limitCount) {
	    String selectQuery;
		if (limitCount)
	    	selectQuery = "SELECT * FROM post ORDER BY " + COLUMN_POSTED_DATE + " DESC LIMIT 1";
	    else
	    	selectQuery = "SELECT * FROM post ORDER BY " + COLUMN_POSTED_DATE + " DESC";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<Answer> posts = new ArrayList<Answer>();
    	Answer post = null;
	    if (c.moveToFirst()) {
	        do {
	        	post = new Answer();
				post.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_POST)));
				post.setPostedDate(c.getString(c.getColumnIndex(COLUMN_POSTED_DATE)));
	    		post.setCategory(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
	    		post.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
	    		post.setqAndAPostsDescription(c.getString((c.getColumnIndex(COLUMN_POST_DESCRIPTION))));
	    		post.setBody(c.getString((c.getColumnIndex(COLUMN_BODY))));
	    		post.setAuthorName(c.getString((c.getColumnIndex(COLUMN_AUTHOR_NAME))));
	    		post.setAuthorThumbnailSource(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_SOURCE))));
	    		post.setqAndAThumbnailSource(c.getString((c.getColumnIndex(COLUMN_POST_THUMBNAIL_SOURCE))));
	    		post.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
	    		post.setqAndAThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_POST_THUMBNAIL_ALT_TEXT))));
	    		post.setAuthorThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT))));
	        	List<String> topics = getTopicsPost(post.getIdProperty());
	        	post.setTopics(topics);
	    		posts.add(post);
	        } while (c.moveToNext());
			Collections.sort(posts);
	    }
	    c.close();
	    return posts;
	}
	
	public static Answer getPostById(String idPost) {
	    String selectQuery = "SELECT  * FROM post WHERE id_post = '" + idPost + "';";	 
	    Log.e(LOG, selectQuery);
	    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
    	Answer post = null;
    	if (c.moveToFirst()) {
    		c.moveToFirst();
    		post = new Answer();
    		post.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_POST)));
			long timeMills = Long.parseLong(c.getString(c.getColumnIndex(COLUMN_POSTED_DATE)));
			Date d = new Date(timeMills);
			DateFormat df = DateFormat.getDateInstance();
			String date = df.format(d);
			post.setPostedDate(date);
    		post.setCategory(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
    		post.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
    		post.setqAndAPostsDescription(c.getString((c.getColumnIndex(COLUMN_POST_DESCRIPTION))));
    		post.setBody(c.getString((c.getColumnIndex(COLUMN_BODY))));
    		post.setAuthorName(c.getString((c.getColumnIndex(COLUMN_AUTHOR_NAME))));
    		post.setAuthorThumbnailSource(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_SOURCE))));
    		post.setqAndAThumbnailSource(c.getString((c.getColumnIndex(COLUMN_POST_THUMBNAIL_SOURCE))));
    		post.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
    		post.setqAndAThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_POST_THUMBNAIL_ALT_TEXT))));
    		post.setAuthorThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT))));
    		c.close();
    	}
	    c.close();
	    return post;
	}
	
	public static List<String> getTopicsPost(String idPost) {
    	String selectQuery = "SELECT * FROM topic_post WHERE id_post = '" + idPost + "'";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<String> topics = new ArrayList<String>();
	    if (c.moveToFirst()) {
	        do {
	            WebServiceCall.topicSet.add(c.getString(c.getColumnIndex("topic")));
	            topics.add(c.getString(c.getColumnIndex("topic")));
	        } while (c.moveToNext());
	    }
	    c.close();
	    return topics;
	}
	
}
