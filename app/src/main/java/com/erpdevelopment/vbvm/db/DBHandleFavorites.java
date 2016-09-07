package com.erpdevelopment.vbvm.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.model.Favorite;

public class DBHandleFavorites {
	
    private static final String LOG = "DbHelper Recently View";

	public static List<Favorite> getFavorites() {
	    List<Favorite> items = new ArrayList<Favorite>();
	    String selectQuery = "SELECT  * FROM recently_viewed";	 
	    Log.e(LOG, selectQuery);   
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	Favorite item = new Favorite();
	        	item.setIdItem(c.getString(c.getColumnIndex("id_item")));	        	
	        	item.setTableName(c.getString(c.getColumnIndex("table_name")));
	        	item.setThumbnailSource(c.getString(c.getColumnIndex("thumbnail_source")));	        	
	        	items.add(item);
	        } while (c.moveToNext());
	    }
	    c.close();
	    return items;
	}
	
	public static Favorite getFavoriteById(String id) {
		Favorite item = null;
	    String selectQuery = "SELECT * FROM recently_viewed where id_item = '" + id + "'";	 
	    Log.e(LOG, selectQuery);   
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	item = new Favorite();
	        	item.setIdItem(c.getString(c.getColumnIndex("id_item")));	        	
	        	item.setTableName(c.getString(c.getColumnIndex("table_name")));
	        	item.setThumbnailSource(c.getString(c.getColumnIndex("thumbnail_source")));	        	
	        } while (c.moveToNext());
	    }
	    c.close();
	    return item;
	}
	
	/**
	 * Remove all recently viewed items.
	 */
	public static void removeAllFavorites()
	{
	    // db.delete(String tableName, String whereClause, String[] whereArgs);
	    // If whereClause is null, it will delete all rows.
	    MainActivity.db.delete("recently_viewed", null, null);
	}
	
	/**
	 * Remove all recently viewed items.
	 */
	public static int removeFromFavorites(String id)
	{
		System.out.println("deleting from favorites...");
	    return MainActivity.db.delete("recently_viewed", "id_item = ?", new String[]{id});
	}
	
}
