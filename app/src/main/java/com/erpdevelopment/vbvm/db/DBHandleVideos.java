package com.erpdevelopment.vbvm.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.Constants;

public class DBHandleVideos {

	private static final String LOG = "DbHelper DBHandleVideos";
    
	private static final String COLUMN_ID_CHANNEL = "id_channel";
	private static final String COLUMN_POSTED_DATE = "posted_date";
	private static final String COLUMN_AVERAGE_RATING = "average_rating";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_THUMBNAIL_SOURCE = "thumbnail_source";
	private static final String COLUMN_THUMBNAIL_ALT_TEXT = "thumbnail_alt_text";
	
	// Table Video
	private static final String COLUMN_ID_VIDEO = "id_video";
	private static final String COLUMN_RECORDED_DATE = "recorded_date";
	private static final String COLUMN_CATEGORY = "category";
	private static final String COLUMN_VIDEO_SOURCE = "video_source";
	private static final String COLUMN_VIDEO_LENGTH = "video_length";
    
	public static List<VideoChannel> getChannels() {
	    String selectQuery = "SELECT * FROM " + Constants.TABLE_CHANNEL + " ORDER BY " + COLUMN_POSTED_DATE + " DESC";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<VideoChannel> channels = new ArrayList<VideoChannel>();
    	VideoChannel channel = null;
	    if (c.moveToFirst()) {
	        do {
	        	channel = new VideoChannel();
				channel.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_CHANNEL)));
				channel.setPostedDate(c.getString(c.getColumnIndex(COLUMN_POSTED_DATE)));
	    		channel.setAverageRating(c.getString(c.getColumnIndex(COLUMN_AVERAGE_RATING)));
	    		channel.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
	    		channel.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
	    		channel.setThumbnailSource(c.getString(c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE)));
	    		channel.setThumbnailAltText(c.getString(c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT)));
				List<VideoVbvm> listVideoVbvm = getVideosByChannel(channel.getIdProperty());
				channel.setVideos(listVideoVbvm);
	    		channels.add(channel);
	        } while (c.moveToNext());
	    }
	    c.close();	    
	    return channels;
	}
	
	public static List<VideoVbvm> getVideosByChannel(String idChannel) {
	    String selectQuery = "SELECT * FROM " + Constants.TABLE_VIDEO + " WHERE id_channel = '" + idChannel + "';";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<VideoVbvm> videos = new ArrayList<VideoVbvm>();
    	VideoVbvm video = null;	    
	    if (c.moveToFirst()) {
	        do {
	        	video = new VideoVbvm();
	    		video.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_VIDEO)));
	    		video.setPostedDate(c.getString(c.getColumnIndex(COLUMN_POSTED_DATE)));
	    		video.setRecordedDate(c.getString(c.getColumnIndex(COLUMN_RECORDED_DATE)));	    		
	    		video.setCategory(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
	    		video.setAverageRating(c.getString(c.getColumnIndex(COLUMN_AVERAGE_RATING)));
	    		video.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
	    		video.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
	    		video.setThumbnailSource(c.getString(c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE)));
	    		video.setThumbnailAltText(c.getString(c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT)));
	    		video.setVideoSource(c.getString(c.getColumnIndex(COLUMN_VIDEO_SOURCE)));
	    		video.setVideoLength(c.getString(c.getColumnIndex(COLUMN_VIDEO_LENGTH)));
	    		video.setIdChannel(c.getString(c.getColumnIndex(COLUMN_ID_CHANNEL)));
	    		videos.add(video);
	        } while (c.moveToNext());
	    }
	    c.close();
	    return videos;
	}
	
	public static VideoVbvm getVideoById(String idVideo) {
	    String selectQuery = "SELECT * FROM " + Constants.TABLE_VIDEO + " WHERE id_video = '" + idVideo + "';";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	VideoVbvm video = null;	    
	    if (c.moveToFirst()) {
	        do {
	        	video = new VideoVbvm();
	    		video.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_VIDEO)));
	    		video.setPostedDate(c.getString(c.getColumnIndex(COLUMN_POSTED_DATE)));
	    		video.setRecordedDate(c.getString(c.getColumnIndex(COLUMN_RECORDED_DATE)));	    		
	    		video.setCategory(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
	    		video.setAverageRating(c.getString(c.getColumnIndex(COLUMN_AVERAGE_RATING)));
	    		video.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
	    		video.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
	    		video.setThumbnailSource(c.getString(c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE)));
	    		video.setThumbnailAltText(c.getString(c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT)));
	    		video.setVideoSource(c.getString(c.getColumnIndex(COLUMN_VIDEO_SOURCE)));
	    		video.setVideoLength(c.getString(c.getColumnIndex(COLUMN_VIDEO_LENGTH)));
	    		video.setIdChannel(c.getString(c.getColumnIndex(COLUMN_ID_CHANNEL)));	    		
	        } while (c.moveToNext());
	    }
	    c.close();
	    return video;
	}
	
}
