package com.erpdevelopment.vbvm.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.erpdevelopment.vbvm.model.EventVbvm;

public class DBHandleEvents {

	private static final String LOG = "DatabaseHelper Events";
    
	private static final String COLUMN_ID_EVENT = "id_event";
	private static final String COLUMN_LOCATION = "location";
	private static final String COLUMN_THUMBNAIL_SOURCE = "thumbnail_source";
	private static final String COLUMN_MAP = "map";
	private static final String COLUMN_POSTED_DATE = "posted_date";
	private static final String COLUMN_THUMBNAIL_ALT_TEXT = "thumbnail_alt_text";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_EVENT_DATE = "event_date";
	private static final String COLUMN_EVENTS_DESCRIPTION = "events_description";
	private static final String COLUMN_EXPIRES_DATE = "expires_date";
	private static final String COLUMN_TYPE = "type";
	
	public static List<EventVbvm> getAllEvents() {
	    String selectQuery = "SELECT  * FROM event;";	 
	    Log.e(LOG, selectQuery);
		//MainActivity.mDbHelper = VbvmDatabaseOpenHelper.getInstance(MainActivity.mainCtx);
//	    SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();
	    
	    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
	    
	    Cursor c = db.rawQuery(selectQuery, null);
    	List<EventVbvm> events = new ArrayList<EventVbvm>();
    	EventVbvm event = null;	    
	    if (c.moveToFirst()) {
	        do {
	        	event = new EventVbvm();
	        	event.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_EVENT)));
	        	event.setPostedDate(c.getString((c.getColumnIndex(COLUMN_LOCATION))));
	        	event.setThumbnailSource(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE))));
	        	event.setMap(c.getString((c.getColumnIndex(COLUMN_MAP))));
	        	event.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
	        	event.setThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT))));
	        	event.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
	        	event.setEventDate(c.getString((c.getColumnIndex(COLUMN_EVENT_DATE))));
	        	event.setEventsDescription(c.getString((c.getColumnIndex(COLUMN_EVENTS_DESCRIPTION))));
	        	event.setExpiresDate(c.getString((c.getColumnIndex(COLUMN_EXPIRES_DATE))));
	        	event.setType(c.getString((c.getColumnIndex(COLUMN_TYPE))));
	    		events.add(event);
	        } while (c.moveToNext());
	    }
	    c.close();
//	    db.close();
	    
	    DatabaseManager.getInstance().closeDatabase();
	    
	    return events;
	}
	
	public static EventVbvm getEventById(String idEvent) {
	    String selectQuery = "SELECT  * FROM event WHERE id_event = '" + idEvent + "';";	 
	    Log.e(LOG, selectQuery);
		//MainActivity.mDbHelper = VbvmDatabaseOpenHelper.getInstance(MainActivity.mainCtx);
//	    SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();
	    
	    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
	    
	    Cursor c = db.rawQuery(selectQuery, null);
    	EventVbvm event = null;
    	if (c.moveToFirst()) {
    		c.moveToFirst();
    		event = new EventVbvm();
        	event.setIdProperty(c.getString(c.getColumnIndex(COLUMN_ID_EVENT)));
        	event.setPostedDate(c.getString((c.getColumnIndex(COLUMN_LOCATION))));
        	event.setThumbnailSource(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_SOURCE))));
        	event.setMap(c.getString((c.getColumnIndex(COLUMN_MAP))));
        	event.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
        	event.setThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_THUMBNAIL_ALT_TEXT))));
        	event.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE))));
        	event.setEventDate(c.getString((c.getColumnIndex(COLUMN_EVENT_DATE))));
        	event.setEventsDescription(c.getString((c.getColumnIndex(COLUMN_EVENTS_DESCRIPTION))));
        	event.setExpiresDate(c.getString((c.getColumnIndex(COLUMN_EXPIRES_DATE))));
        	event.setType(c.getString((c.getColumnIndex(COLUMN_TYPE))));
    	}
	    c.close();
//	    db.close();
	    
	    DatabaseManager.getInstance().closeDatabase();
	    
	    return event;
	}
}
