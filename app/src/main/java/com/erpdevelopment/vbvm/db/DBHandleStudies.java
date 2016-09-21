package com.erpdevelopment.vbvm.db;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.ChannelVbvm;
import com.erpdevelopment.vbvm.model.EventVbvm;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.QandAPost;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.model.VideoVbvm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBHandleStudies {

	// Logcat tag
    private static final String LOG = "DatabaseHelper Study";
    
    private static final String COLUMN_ID_STUDY = "id_study";
    private static final String COLUMN_THUMBNAIL_SOURCE = "thumbnail_source";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_THUMBNAIL_ALT_TEXT = "thumbnail_alt_text";
    private static final String COLUMN_PODCAST_LINK = "podcast_link";
    private static final String COLUMN_AVERAGE_RATING = "average_rating";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TYPE = "type";
    
	public static long createStudy(Study study) {
	    ContentValues values = new ContentValues();
	    values.put("id_study", study.getIdProperty());
	    values.put("thumbnail_source", study.getThumbnailSource());
	    values.put("title", study.getTitle());
	    values.put("thumbnail_alt_text", study.getThumbnailAltText());
	    values.put("podcast_link", study.getPodcastLink());
	    values.put("average_rating", study.getAverageRating());
	    values.put("description", study.getStudiesDescription());
	    values.put("type", study.getType());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("study", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    		Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  	Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;
	}
	
	public static long createLesson(Lesson lesson) {
	    ContentValues values = new ContentValues();
	    values.put("id_lesson", lesson.getIdProperty());
	    values.put("description", lesson.getLessonsDescription());
	    values.put("posted_date", lesson.getPostedDate());
	    values.put("transcript", lesson.getTranscript());
	    values.put("date_study_given", lesson.getDateStudyGiven());
	    values.put("teacher_aid", lesson.getTeacherAid());
	    values.put("average_rating", lesson.getAverageRating());
	    values.put("video_source", lesson.getVideoSource());
	    values.put("video_length", lesson.getVideoLength());
	    values.put("title", lesson.getTitle());
	    values.put("location", lesson.getLocation());
	    values.put("audio_source", lesson.getAudioSource());
	    values.put("audio_length", lesson.getAudioLength());
	    values.put("student_aid", lesson.getStudentAid());
	    values.put("id_study", lesson.getIdStudy());
	    values.put("progress_percentage", 0);
	    values.put("current_position", 0);
	    values.put("study_lessons_size", lesson.getStudyLessonsSize());
	    values.put("study_thumbnail_source", lesson.getStudyThumbnailSource());
	    values.put("state", "new");
	    values.put("position_in_list", lesson.getPositionInList());
	    long row_id = 0;
	    try {
	    	row_id = MainActivity.db.insertOrThrow("lesson", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException lesson", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception lesson", "exception insert on the next line");
    	}
	    return row_id;
	}
	
	public static long createTopicLesson(Topic topic) {
	    ContentValues values = new ContentValues();
	    values.put("id_topic", topic.getIdProperty());
	    values.put("topic", topic.getTopic());
	    values.put("id_lesson", topic.getIdParent());

	    long row_id = 0;
	    try {
	    	row_id = MainActivity.db.insertOrThrow("topic_lesson", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    		Log.i("SQLiteEx", exception.getMessage());
    	}
    	catch(Exception exception) {
      	  	Log.i("Exception topic_lesson", exception.getMessage());
    	}
	    return row_id;
	}
	
	public static long createArticle(Article article) {
		ContentValues values = new ContentValues();
	    values.put("id_article", article.getIdProperty());
	    values.put("posted_date", article.getPostedDate());
	    values.put("category", article.getCategory());
	    values.put("average_rating", article.getAverageRating());
	    values.put("article_description", article.getArticlesDescription());
	    values.put("body", article.getBody());
	    values.put("author_name", article.getAuthorName());
	    values.put("article_thumbnail_source", article.getArticleThumbnailSource());
	    values.put("author_thumbnail_source", article.getAuthorThumbnailSource());
	    values.put("title", article.getTitle());
	    values.put("article_thumbnail_alt_text", article.getArticleThumbnailAltText());
	    values.put("author_thumbnail_alt_text", article.getAuthorThumbnailAltText());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("article", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;	    
	}
	
	public static long createEvent(EventVbvm event) {
	    ContentValues values = new ContentValues();
	    values.put("id_event", event.getIdProperty());
	    values.put("location", event.getLocation());
	    values.put("thumbnail_source", event.getThumbnailSource());
	    values.put("map", event.getMap());
	    values.put("posted_date", event.getPostedDate());
	    values.put("thumbnail_alt_text", event.getThumbnailAltText());
	    values.put("title", event.getTitle());
	    values.put("event_date", event.getEventDate());
	    values.put("events_description", event.getEventsDescription());
	    values.put("expires_date", event.getExpiresDate());
	    values.put("type", event.getType());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("event", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;	    
	}
	
	public static long createPost(QandAPost post) {
	    ContentValues values = new ContentValues();
	    values.put("id_post", post.getIdProperty());
	    values.put("posted_date", post.getPostedDate());
	    values.put("category", post.getCategory());
	    values.put("average_rating", post.getAverageRating());
	    values.put("post_description", post.getqAndAPostsDescription());
	    values.put("body", post.getBody());
	    values.put("author_name", post.getAuthorName());
	    values.put("author_thumbnail_source", post.getAuthorThumbnailSource());
	    values.put("post_thumbnail_source", post.getqAndAThumbnailSource());
	    values.put("title", post.getTitle());
	    values.put("post_thumbnail_alt_text", post.getqAndAThumbnailAltText());
	    values.put("author_thumbnail_alt_text", post.getAuthorThumbnailAltText());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("post", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;
	}
	
	public static long createChannel(ChannelVbvm channel) {
	    ContentValues values = new ContentValues();
	    values.put("id_channel", channel.getIdProperty());
	    values.put("posted_date", channel.getPostedDate());
	    values.put("average_rating", channel.getAverageRating());
	    values.put("description", channel.getDescription());
	    values.put("title", channel.getTitle());
	    values.put("thumbnail_source", channel.getThumbnailSource());
	    values.put("thumbnail_alt_text", channel.getThumbnailAltText());	    
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("channel", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;	    
	}
	
	public static long createVideo(VideoVbvm video) {
	    ContentValues values = new ContentValues();
	    values.put("id_video", video.getIdProperty());
	    values.put("posted_date", video.getPostedDate());
	    values.put("recorded_date", video.getRecordedDate());
	    values.put("category", video.getCategory());
	    values.put("average_rating", video.getAverageRating());
	    values.put("description", video.getDescription());
	    values.put("title", video.getTitle());
	    values.put("thumbnail_source", video.getThumbnailSource());
	    values.put("thumbnail_alt_text", video.getThumbnailAltText());	    
	    values.put("video_source", video.getVideoSource());
	    values.put("video_length", video.getVideoLength());
	    values.put("id_channel", video.getIdChannel());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("video", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;	    
	}
	
	public static long createTopicPost(Topic topic) {		
	    ContentValues values = new ContentValues();
	    values.put("id_topic", topic.getIdProperty());
	    values.put("topic", topic.getTopic());
	    values.put("id_post", topic.getIdParent());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("topic_post", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}    
	    return row_id;
	}
	
	public static long createTopicArticle(Topic topic) {		
	    ContentValues values = new ContentValues();
	    values.put("id_topic", topic.getIdProperty());
	    values.put("topic", topic.getTopic());
	    values.put("id_article", topic.getIdParent());
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("topic_article", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    	  Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
      	  Log.i("Exception", "exception insert on the next line");
    	}    
	    return row_id;
	}
	
	public static long createFavorite(String idItem, String tableName, String thumbnailSource) {
	    ContentValues values = new ContentValues();
	    values.put("id_item", idItem);
	    values.put("table_name", tableName);
	    values.put("thumbnail_source", thumbnailSource);
	    long row_id = 0;
	    try {
		    row_id = MainActivity.db.insertOrThrow("recently_viewed", null, values);
    	}catch(SQLiteConstraintException e){
    		Log.i("SQLiteConstraintEx", e.getMessage());
    	}catch(SQLiteException exception) {
    		Log.i("SQLiteException", "exception insert on the next line");
    	}
    	catch(Exception exception) {
    		Log.i("Exception", "exception insert on the next line");
    	}
	    return row_id;	    
	}
	
	public static List<Study> getAllStudies() {
	    List<Study> studies = new ArrayList<>();
	    String selectQuery = "SELECT  * FROM study ;";	 
	    Log.e(LOG, selectQuery);	 
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
	    if (c.moveToFirst()) {
	        do {
	        	Study study = new Study();
	        	study.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_STUDY))));
	        	study.setThumbnailSource(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE))));
	        	study.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
	        	study.setThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT))));
	        	study.setPodcastLink(c.getString((c.getColumnIndex(COLUMN_PODCAST_LINK))));
	        	study.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
	        	study.setStudiesDescription(c.getString((c.getColumnIndex(COLUMN_DESCRIPTION))));
	        	study.setType(c.getString((c.getColumnIndex(COLUMN_TYPE))));
	            studies.add(study);
	        } while (c.moveToNext());
	    }
	    c.close();
	    return studies;
	}

	public static List<List<Study>> getAllStudiesByType() {
		List<List<Study>> studies = new ArrayList<>();
		List<Study> studiesNew = new ArrayList<>();
		List<Study> studiesOld = new ArrayList<>();
		List<Study> studiesSingle = new ArrayList<>();
		String selectQuery = "SELECT * FROM study ;";
		Log.e(LOG, selectQuery);
		Cursor c = MainActivity.db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				Study study = new Study();
				study.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_STUDY))));
				study.setThumbnailSource(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE))));
				study.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
				study.setThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT))));
				study.setPodcastLink(c.getString((c.getColumnIndex(COLUMN_PODCAST_LINK))));
				study.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
				study.setStudiesDescription(c.getString((c.getColumnIndex(COLUMN_DESCRIPTION))));
				study.setType(c.getString((c.getColumnIndex(COLUMN_TYPE))));
				if (study.getType().contains("New")) {
					studiesNew.add(study);
				}
				if (study.getType().contains("Old")) {
					studiesOld.add(study);
				}
				if (study.getType().contains("Single")) {
					studiesSingle.add(study);
				}
			} while (c.moveToNext());
			studies.add(studiesNew);
			studies.add(studiesOld);
			studies.add(studiesSingle);
		}
		c.close();
		return studies;
	}
	
	public static Study getStudyById(String idStudy) {
	    String selectQuery = "SELECT  * FROM study WHERE id_study = '" + idStudy + "';";	 
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	Study study = null;
    	if (c.moveToFirst()) {
    		c.moveToFirst();
    		study = new Study();
    		study.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_STUDY))));
        	study.setThumbnailSource(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE))));
        	study.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
        	study.setThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT))));
        	study.setPodcastLink(c.getString((c.getColumnIndex(COLUMN_PODCAST_LINK))));
        	study.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
        	study.setStudiesDescription(c.getString((c.getColumnIndex(COLUMN_DESCRIPTION))));
        	study.setType(c.getString((c.getColumnIndex(COLUMN_TYPE))));
    	}
		c.close();		
	    return study;
	}
	
}
