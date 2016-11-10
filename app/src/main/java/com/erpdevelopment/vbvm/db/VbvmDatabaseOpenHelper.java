package com.erpdevelopment.vbvm.db;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.erpdevelopment.vbvm.MainActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VbvmDatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_DIR = "DB_DIR";	
    public static final String DATABASE_FILE_NAME = "sfdroid.sqlite";    
    
    private static final String DATABASE_NAME = "vbvmi.db";
    
    private static final int DATABASE_VERSION = 1;
    
    private static VbvmDatabaseOpenHelper sInstance;
    
    private static final String CREATE_TABLE_STUDY = "CREATE TABLE IF NOT EXISTS study (" +
    		" id_study TEXT PRIMARY KEY," +
    		" thumbnail_source TEXT," +
    		" title TEXT," +
    		" thumbnail_alt_text TEXT," +
    		" podcast_link TEXT," +
    		" average_rating TEXT," +
    		" description TEXT," +
    		" type TEXT );";
    
    private static final String CREATE_TABLE_LESSON = "CREATE TABLE IF NOT EXISTS lesson (" +
    		" id_lesson TEXT PRIMARY KEY," +
    		" description TEXT," +
    		" posted_date TEXT," +
    		" transcript TEXT," +
    		" date_study_given TEXT," +
    		" teacher_aid TEXT," +
    		" average_rating TEXT," +
    		" video_source TEXT," +
    		" video_length TEXT," +
    		" title TEXT," +
    		" location TEXT," +
    		" audio_source TEXT," +
    		" audio_length TEXT," +
    		" student_aid TEXT," +
    		" id_study TEXT," +
    		" progress_percentage INTEGER," +
    		" current_position INTEGER," +
    		" study_lessons_size INTEGER," +
    		" study_thumbnail_source TEXT," +
    		" state TEXT, " +
    		" position_in_list INTEGER," +
			" download_status INTEGER," +
			" download_status_audio INTEGER," +
			" download_status_teacher INTEGER," +
    		" download_status_transcript INTEGER );";

    private static final String CREATE_TABLE_TOPIC_LESSON = "CREATE TABLE IF NOT EXISTS topic_lesson (" +
    		" _id INTEGER PRIMARY KEY AUTOINCREMENT," +
    		" id_topic TEXT," +
    		" topic TEXT," +
    		" id_lesson TEXT );";
    
    private static final String CREATE_TABLE_ARTICLE = "CREATE TABLE IF NOT EXISTS article (" +
    		" id_article TEXT PRIMARY KEY," +
    		" posted_date TEXT," +
    		" category TEXT," +
    		" average_rating TEXT," +
    		" article_description TEXT," +
    		" body TEXT," +
    		" author_name TEXT," +
    		" article_thumbnail_source TEXT," +
    		" author_thumbnail_source TEXT," +
    		" title TEXT," +
    		" article_thumbnail_alt_text TEXT," +
    		" author_thumbnail_alt_text TEXT );";
    
    private static final String CREATE_TABLE_TOPIC_ARTICLE = "CREATE TABLE IF NOT EXISTS topic_article (" +
    		" id_topic TEXT," +
    		" topic TEXT," +
    		" id_article TEXT," +
			" PRIMARY KEY (id_topic,id_article));";
    
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS event (" +
    		" id_event TEXT PRIMARY KEY," +
    		" location TEXT," +
    		" thumbnail_source TEXT," +
    		" map TEXT," +
    		" posted_date TEXT," +
    		" thumbnail_alt_text TEXT," +
    		" title TEXT," +
    		" event_date TEXT," +
    		" events_description TEXT," +
    		" expires_date TEXT," +
    		" type TEXT );";

    private static final String CREATE_TABLE_POST = "CREATE TABLE IF NOT EXISTS post (" +
    		" id_post TEXT PRIMARY KEY," +
    		" posted_date TEXT," +
    		" category TEXT," +
    		" average_rating TEXT," +
    		" post_description TEXT," +
    		" body TEXT," +
    		" author_name TEXT," +
    		" post_thumbnail_source TEXT," +
    		" author_thumbnail_source TEXT," +
    		" title TEXT," +
    		" post_thumbnail_alt_text TEXT," +
    		" author_thumbnail_alt_text TEXT );";
    
    private static final String CREATE_TABLE_TOPIC_POST = "CREATE TABLE IF NOT EXISTS topic_post (" +
    		" id_topic TEXT," +
    		" topic TEXT," +
    		" id_post TEXT," +
			" PRIMARY KEY (id_topic,id_post));";

	private static final String CREATE_TABLE_RECENTLY_VIEWED = "CREATE TABLE IF NOT EXISTS recently_viewed (" +
    		" _id INTEGER PRIMARY KEY AUTOINCREMENT," +
    		" id_item TEXT," +
    		" table_name TEXT," +
    		" thumbnail_source TEXT );";
    
    private static final String CREATE_TABLE_CHANNEL = "CREATE TABLE IF NOT EXISTS channel (" +
    		" id_channel TEXT PRIMARY KEY," +
    		" posted_date TEXT," +
    		" average_rating TEXT," +
    		" description TEXT," +
    		" title TEXT," +
    		" thumbnail_source TEXT," +    		
    		" thumbnail_alt_text TEXT );";
    
    private static final String CREATE_TABLE_VIDEO = "CREATE TABLE IF NOT EXISTS video (" +
    		" id_video TEXT PRIMARY KEY," +
    		" posted_date TEXT," +
    		" recorded_date TEXT," +
    		" category TEXT," +
    		" average_rating TEXT," +
    		" description TEXT," +
    		" title TEXT," +
    		" thumbnail_source TEXT," +    		
    		" thumbnail_alt_text TEXT," +
    		" video_source TEXT," +
    		" video_length TEXT," +
    		" id_channel TEXT );";
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering by testream.
     * */
    public void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = new FileInputStream(MainActivity.mainCtx.getFilesDir().getAbsolutePath() + "/" + DATABASE_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DATABASE_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);    		
    		Log.v("Copiando Datos en DB", "Copiando Datos en DB en progreso...");
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close(); 
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {        
		// creating required tables
        db.execSQL(CREATE_TABLE_STUDY);
        db.execSQL(CREATE_TABLE_LESSON);
        db.execSQL(CREATE_TABLE_ARTICLE);
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_TOPIC_LESSON);
        db.execSQL(CREATE_TABLE_TOPIC_POST);
        db.execSQL(CREATE_TABLE_RECENTLY_VIEWED);
        db.execSQL(CREATE_TABLE_CHANNEL);
        db.execSQL(CREATE_TABLE_VIDEO);
        db.execSQL(CREATE_TABLE_TOPIC_ARTICLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		onCreate(db);
	}

	public static VbvmDatabaseOpenHelper getInstance(Context context) {

	    // Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (sInstance == null) {
	      //String dbpath = MainActivity.settings.getString(MainActivity.SETTING_DATABASE_FILE_PATH, null);
	      sInstance = new VbvmDatabaseOpenHelper(context.getApplicationContext());
	    }
	    return sInstance;
	}	
	
	private VbvmDatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}	
			
	private void execSQL(SQLiteDatabase db, String sql){
		
		try{
		
			if (sql.trim().length() > 0) db.execSQL(sql);
		}
		catch (Exception e) {}
	}

	public void execSQL(String sql){
		
		if (sql == null)
			return;
		
		SQLiteDatabase db = getWritableDatabase();
		execSQL(db, sql);		
	}
	
	public void execSQLFromFile(String fileName) throws IOException{
		
		SQLiteDatabase db = getWritableDatabase();		
		Scanner scanner = new Scanner(new FileInputStream(MainActivity.mainCtx.getFilesDir().getAbsolutePath() + "/" + fileName), "UTF-8");
		    
	    try {		    
	    	while (scanner.hasNextLine()){
	    		execSQL(db, scanner.nextLine());
	    	}
	    }	    
	    finally{
	    	scanner.close();
	    }		 
	}	

}

