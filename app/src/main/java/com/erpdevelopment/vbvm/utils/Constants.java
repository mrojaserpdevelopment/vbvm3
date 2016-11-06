package com.erpdevelopment.vbvm.utils;

public class Constants {

	public Constants() {
		// TODO Auto-generated constructor stub
	}
	
	public static String VBVM_PREFS = "com.erpdevelopment.vbvm";
	public static String DEFAULT_THUMBNAILSORUCE = "http://www.versebyverseministry.org/images/uploads/galatians.jpg";
	public static int DEFAULT_LESSON_INDEX = 0;
	public static String DEFAULT_DESCRIPTION = "";
	public static String DEFAULT_TITLE = "";
	public static int DEFAULT_SIZE = 0;
	public static final String FILENAME_CONTACT = "contact.txt";
	public static final String FILENAME_MISSION = "mission.txt";
	public static final String FILENAME_BELIEFS = "beliefs.txt";
	public static final String FILENAME_SERVICES = "services.txt";
	public static final String FILENAME_HISTORY = "history.txt";
	public static final String FILENAME_HOW = "how.txt";
	public static final String FILENAME_WHERE = "where.txt";
	public static final String FILENAME_ABOUT = "about.txt";
	
	public static final String FILENAME_STAFF_KATHRYN = "staff_kathryn.txt";
	public static final String FILENAME_STAFF_MELISSA = "staff_melissa.txt";
	public static final String FILENAME_STAFF_FEDERICO = "staff_federico.txt";
	public static final String FILENAME_STAFF_TONYA = "staff_tonya.txt";
	public static final String FILENAME_STAFF_BRADY = "staff_brady.txt";
	
	public static final String FILENAME_BOARD_STEPHEN = "board_stephen.txt";
	public static final String FILENAME_BOARD_BRIAN = "board_brian.txt";
	public static final String FILENAME_BOARD_JOE = "board_joe.txt";
	public static final String FILENAME_BOARD_RON = "board_ron.txt";
	public static final String FILENAME_BOARD_JERRY = "board_jerry.txt";
	public static final String FILENAME_BOARD_ANONYMOUS = "board_anonymous.txt";

	//Database tables	
	public static final String TABLE_STUDY = "study";
	public static final String TABLE_LESSON = "lesson";
	public static final String TABLE_TOPIC_LESSON = "topic_lesson";
	public static final String TABLE_ARTICLE = "article";
	public static final String TABLE_EVENT = "event";
	public static final String TABLE_POST = "post";
	public static final String TABLE_TOPIC_POST = "topic_post";
	public static final String TABLE_RECENTLY_VIEWED = "recently_viewed";
	public static final String TABLE_CHANNEL = "channel";
	public static final String TABLE_VIDEO = "video";

	public interface ACTION {
		String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
		String PREV_ACTION = "com.truiton.foregroundservice.action.prev";
		String PLAY_ACTION = "com.truiton.foregroundservice.action.play";
		String NEXT_ACTION = "com.truiton.foregroundservice.action.next";
		String STARTFOREGROUND_ACTION = "com.erpdevelopment.vbvm.action.startforeground";
		String STOPFOREGROUND_ACTION = "com.erpdevelopment.vbvm.action.stopforeground";
	}

	public interface NOTIFICATION_ID {
		int FOREGROUND_SERVICE = 101;
	}

	public interface URL_VBVMI {
//		int FOREGROUND_SERVICE = 101;
		String ABOUT = "http://www.versebyverseministry.org/about/";
		String EVENTS = "http://www.versebyverseministry.org/events/";
		String DONATE = "http://www.versebyverseministry.org/about/financial_support";
		String CONTACT = "http://www.versebyverseministry.org/contact/";
	}



	//LessonsListAdapter - DownloadThread
//	// Used to communicate state changes in the DownloaderThreadTest
//	public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
//	public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
//	public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
//	public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
//	public static final int MESSAGE_CONNECTING_STARTED = 1004;
//	public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;
}