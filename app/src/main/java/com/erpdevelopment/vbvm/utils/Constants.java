package com.erpdevelopment.vbvm.utils;

public class Constants {

	public Constants() {}

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
		String ABOUT = "http://www.versebyverseministry.org/about/";
		String EVENTS = "http://www.versebyverseministry.org/events/";
		String DONATE = "http://www.versebyverseministry.org/about/financial_support";
		String CONTACT = "http://www.versebyverseministry.org/contact/";
	}

	public interface LESSON_FILE {
		String AUDIO = "audio";
		String TRANSCRIPT = "transcript";
		String TEACHER = "teacher";
	}

	public interface LESSON_STATE {
		String NEW = "new";
		String PLAYING = "playing";
		String PARTIAL = "partial";
		String COMPLETE = "complete";
	}

	public interface STUDY_TYPE {
		String OLD = "Old Testament Books";
		String NEW = "New Testament Books";
		String SINGLE = "Single Teachings";
		String TOPICAL = "Topical Series";
	}

}