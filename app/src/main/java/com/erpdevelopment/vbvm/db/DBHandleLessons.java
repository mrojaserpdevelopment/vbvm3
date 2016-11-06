package com.erpdevelopment.vbvm.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;

public class DBHandleLessons {
	
    private static final String LOG = "DatabaseHelper Lesson";

	private static final String COLUMN_ID_LESSON = "id_lesson";
    private static final String COLUMN_DESCRIPTION_LESSON = "description";
    private static final String COLUMN_POSTED_DATE = "posted_date";
    private static final String COLUMN_TRANSCRIPT = "transcript";
    private static final String COLUMN_DATE_STUDY_GIVEN = "date_study_given";
    private static final String COLUMN_TEACHER_AID = "teacher_aid";
    private static final String COLUMN_AVERAGE_RATING_LESSON = "average_rating";
    private static final String COLUMN_VIDEO_SOURCE = "video_source";
    private static final String COLUMN_VIDEO_LENGTH = "video_length";
    private static final String COLUMN_TITLE_LESSON = "title";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_AUDIO_SOURCE = "audio_source";
    private static final String COLUMN_AUDIO_LENGTH = "audio_length";
    private static final String COLUMN_STUDENT_AID = "student_aid";
    private static final String COLUMN_ID_STUDY_FK = "id_study";
    private static final String COLUMN_PROGRESS_PERCENTAGE = "progress_percentage";
    private static final String COLUMN_CURRENT_POSITION = "current_position";
    private static final String COLUMN_STUDY_LESSONS_SIZE = "study_lessons_size";
    private static final String COLUMN_STUDY_THUMBNAIL_SOURCE = "study_thumbnail_source";
    private static final String COLUMN_STATE_LESSON = "state";
    private static final String COLUMN_POSITION_IN_LIST = "position_in_list";
    private static final String COLUMN_DOWNLOAD_STATUS = "download_status"; // 0=Not downloaded  1=Downloaded  2=In progress
	private static final String COLUMN_DOWNLOAD_STATUS_AUDIO = "download_status_audio"; // 0=Not downloaded  1=Downloaded  2=In progress
	private static final String COLUMN_DOWNLOAD_STATUS_TEACHER = "download_status_teacher"; // 0=Not downloaded  1=Downloaded  2=In progress
	private static final String COLUMN_DOWNLOAD_STATUS_TRANSCRIPT = "download_status_transcript"; // 0=Not downloaded  1=Downloaded  2=In progress
    
    public static List<Lesson> getLessons(String id_study) {
	    List<Lesson> lessons = new ArrayList<Lesson>();
	    String selectQuery = "";
	    if ( id_study == null )
	    	selectQuery = "SELECT * FROM lesson ORDER BY " + COLUMN_POSTED_DATE + " DESC LIMIT 8"; //Query for Newest list	    
	    else
	    	selectQuery = "SELECT * FROM lesson WHERE id_study = '" + id_study + "'";	 
	    Log.e(LOG, selectQuery);	 
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
	    if (c.moveToFirst()) {
	        do {
	        	Lesson lesson = new Lesson();
	        	lesson.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_LESSON))));
	        	lesson.setLessonsDescription(c.getString((c.getColumnIndex(COLUMN_DESCRIPTION_LESSON))));
	        	lesson.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
	        	lesson.setTranscript(c.getString((c.getColumnIndex(COLUMN_TRANSCRIPT))));
	        	lesson.setDateStudyGiven(c.getString((c.getColumnIndex(COLUMN_DATE_STUDY_GIVEN))));
	        	lesson.setTeacherAid(c.getString((c.getColumnIndex(COLUMN_TEACHER_AID))));
	        	lesson.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING_LESSON))));
	        	lesson.setVideoSource(c.getString((c.getColumnIndex(COLUMN_VIDEO_SOURCE))));
	        	lesson.setVideoLength(c.getString((c.getColumnIndex(COLUMN_VIDEO_LENGTH))));
	        	lesson.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE_LESSON))));
	        	lesson.setLocation(c.getString((c.getColumnIndex(COLUMN_LOCATION))));
	        	lesson.setAudioSource(c.getString((c.getColumnIndex(COLUMN_AUDIO_SOURCE))));
	        	lesson.setAudioLength(c.getString((c.getColumnIndex(COLUMN_AUDIO_LENGTH))));
	        	lesson.setStudentAid(c.getString((c.getColumnIndex(COLUMN_STUDENT_AID))));
	        	lesson.setIdStudy(c.getString((c.getColumnIndex(COLUMN_ID_STUDY_FK))));
	        	lesson.setProgressPercentage(c.getInt((c.getColumnIndex(COLUMN_PROGRESS_PERCENTAGE))));
	        	lesson.setCurrentPosition(c.getLong((c.getColumnIndex(COLUMN_CURRENT_POSITION))));
	        	lesson.setStudyLessonsSize(c.getInt((c.getColumnIndex(COLUMN_STUDY_LESSONS_SIZE))));
	        	lesson.setStudyThumbnailSource(c.getString((c.getColumnIndex(COLUMN_STUDY_THUMBNAIL_SOURCE))));
	        	lesson.setState(c.getString((c.getColumnIndex(COLUMN_STATE_LESSON))));
	        	lesson.setPositionInList(c.getInt((c.getColumnIndex(COLUMN_POSITION_IN_LIST))));
//	        	lesson.setDownloadStatus(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS))));
				lesson.setDownloadStatusAudio(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_AUDIO))));
				lesson.setDownloadStatusTeacherAid(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_TEACHER))));
				lesson.setDownloadStatusTranscript(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_TRANSCRIPT))));
//				Study study = DBHandleStudies.getStudyById(id_study);
//				lesson.setStudy(study);
	        	lessons.add(lesson);
	        } while (c.moveToNext());
	    }
	    c.close();
	    return lessons;
	}
	
	public static Lesson getLessonById(String idLesson) {
	    String selectQuery = "SELECT * FROM lesson WHERE id_lesson = '" + idLesson + "';";
	    Log.e(LOG, selectQuery);	    
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
	    Lesson lesson = null;
	    if (c.moveToFirst()) {
	    	c.moveToFirst();
	    	lesson = new Lesson();
	    	lesson.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_LESSON))));
	    	lesson.setLessonsDescription(c.getString((c.getColumnIndex(COLUMN_DESCRIPTION_LESSON))));
	    	lesson.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
	    	lesson.setTranscript(c.getString((c.getColumnIndex(COLUMN_TRANSCRIPT))));
	    	lesson.setDateStudyGiven(c.getString((c.getColumnIndex(COLUMN_DATE_STUDY_GIVEN))));
	    	lesson.setTeacherAid(c.getString((c.getColumnIndex(COLUMN_TEACHER_AID))));
	    	lesson.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING_LESSON))));
	    	lesson.setVideoSource(c.getString((c.getColumnIndex(COLUMN_VIDEO_SOURCE))));
	    	lesson.setVideoLength(c.getString((c.getColumnIndex(COLUMN_VIDEO_LENGTH))));
	    	lesson.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE_LESSON))));
	    	lesson.setLocation(c.getString((c.getColumnIndex(COLUMN_LOCATION))));
	    	lesson.setAudioSource(c.getString((c.getColumnIndex(COLUMN_AUDIO_SOURCE))));
	    	lesson.setAudioLength(c.getString((c.getColumnIndex(COLUMN_AUDIO_LENGTH))));
	    	lesson.setStudentAid(c.getString((c.getColumnIndex(COLUMN_STUDENT_AID))));
	    	lesson.setIdStudy(c.getString((c.getColumnIndex(COLUMN_ID_STUDY_FK))));
	    	lesson.setProgressPercentage(c.getInt((c.getColumnIndex(COLUMN_PROGRESS_PERCENTAGE))));
	    	lesson.setCurrentPosition(c.getLong((c.getColumnIndex(COLUMN_CURRENT_POSITION))));
	    	lesson.setStudyLessonsSize(c.getInt((c.getColumnIndex(COLUMN_STUDY_LESSONS_SIZE))));
	    	lesson.setStudyThumbnailSource(c.getString((c.getColumnIndex(COLUMN_STUDY_THUMBNAIL_SOURCE))));
	    	lesson.setState(c.getString((c.getColumnIndex(COLUMN_STATE_LESSON))));	 
        	lesson.setPositionInList(c.getInt((c.getColumnIndex(COLUMN_POSITION_IN_LIST))));
//        	lesson.setDownloadStatus(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS))));
			lesson.setDownloadStatusAudio(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_AUDIO))));
			lesson.setDownloadStatusTeacherAid(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_TEACHER))));
			lesson.setDownloadStatusTranscript(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_TRANSCRIPT))));
	    }
	    c.close();
	    return lesson;
	}

	public static List<Lesson> getLessonsByState(String state) {
		List<Lesson> lessons = new ArrayList<Lesson>();
		String selectQuery = "";
		if ( state == null )
			selectQuery = "SELECT * FROM lesson ORDER BY " + COLUMN_POSTED_DATE + " DESC LIMIT 8"; //Query for Newest list
		else
			selectQuery = "SELECT * FROM lesson WHERE state = '" + state + "'";
		Log.e(LOG, selectQuery);
		Cursor c = MainActivity.db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				Lesson lesson = new Lesson();
				lesson.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_LESSON))));
				lesson.setLessonsDescription(c.getString((c.getColumnIndex(COLUMN_DESCRIPTION_LESSON))));
				lesson.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
				lesson.setTranscript(c.getString((c.getColumnIndex(COLUMN_TRANSCRIPT))));
				lesson.setDateStudyGiven(c.getString((c.getColumnIndex(COLUMN_DATE_STUDY_GIVEN))));
				lesson.setTeacherAid(c.getString((c.getColumnIndex(COLUMN_TEACHER_AID))));
				lesson.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING_LESSON))));
				lesson.setVideoSource(c.getString((c.getColumnIndex(COLUMN_VIDEO_SOURCE))));
				lesson.setVideoLength(c.getString((c.getColumnIndex(COLUMN_VIDEO_LENGTH))));
				lesson.setTitle(c.getString((c.getColumnIndex(COLUMN_TITLE_LESSON))));
				lesson.setLocation(c.getString((c.getColumnIndex(COLUMN_LOCATION))));
				lesson.setAudioSource(c.getString((c.getColumnIndex(COLUMN_AUDIO_SOURCE))));
				lesson.setAudioLength(c.getString((c.getColumnIndex(COLUMN_AUDIO_LENGTH))));
				lesson.setStudentAid(c.getString((c.getColumnIndex(COLUMN_STUDENT_AID))));
				lesson.setIdStudy(c.getString((c.getColumnIndex(COLUMN_ID_STUDY_FK))));
				lesson.setProgressPercentage(c.getInt((c.getColumnIndex(COLUMN_PROGRESS_PERCENTAGE))));
				lesson.setCurrentPosition(c.getLong((c.getColumnIndex(COLUMN_CURRENT_POSITION))));
				lesson.setStudyLessonsSize(c.getInt((c.getColumnIndex(COLUMN_STUDY_LESSONS_SIZE))));
				lesson.setStudyThumbnailSource(c.getString((c.getColumnIndex(COLUMN_STUDY_THUMBNAIL_SOURCE))));
				lesson.setState(c.getString((c.getColumnIndex(COLUMN_STATE_LESSON))));
				lesson.setPositionInList(c.getInt((c.getColumnIndex(COLUMN_POSITION_IN_LIST))));
//	        	lesson.setDownloadStatus(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS))));
				lesson.setDownloadStatusAudio(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_AUDIO))));
				lesson.setDownloadStatusTeacherAid(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_TEACHER))));
				lesson.setDownloadStatusTranscript(c.getInt((c.getColumnIndex(COLUMN_DOWNLOAD_STATUS_TRANSCRIPT))));
//				Study study = DBHandleStudies.getStudyById(id_study);
//				lesson.setStudy(study);
				lessons.add(lesson);
			} while (c.moveToNext());
		}
		c.close();
		return lessons;
	}

	public static int saveCurrentPositionInTrack(String idLesson, long currentPositon){
		 int updatedRows = 0;
		 if( MainActivity.db != null ){
		  ContentValues values = new ContentValues();
		  values.put("current_position", currentPositon);
		  updatedRows = (int) MainActivity.db.update("lesson", values, "id_lesson = ?", new String[]{idLesson});  
		 }
		return updatedRows;
	}
	
	/**
	 * Remove all lessons.
	 */
	public static void removeAllLessons()
	{
	    // db.delete(String tableName, String whereClause, String[] whereArgs);
	    // If whereClause is null, it will delete all rows.
	    MainActivity.db.delete("lesson", null, null);
	}
	
	public static int updateLessonState(String idLesson, long currentPositon, String state){
		 int updatedRows = 0;
		 if( MainActivity.db != null ){
		  ContentValues values = new ContentValues();
		  values.put("current_position", currentPositon);
		  values.put("state", state);
		  updatedRows = (int) MainActivity.db.update("lesson", values, "id_lesson = ?", new String[]{idLesson});  
		 }
         return updatedRows;
	}

	public static void updateLessonDownloadStatus(String idLesson, int downloadStatus) {

	}
	
	public static void updateLessonDownloadStatus(String idLesson, int downloadStatus, String downloadType){
//		 int updatedRows = 0;
//		 if( MainActivity.db != null ){
//		  ContentValues values = new ContentValues();
//		  values.put("COLUMN_DOWNLOAD_STATUS", downloadStatus);
//		  updatedRows = (int) MainActivity.db.update("lesson", values, "id_lesson = ?", new String[]{idLesson});
//		 }
//		 return updatedRows;

		switch (downloadType) {
			case "audio": updateLessonDownloadStatusAudio(idLesson, downloadStatus);
				break;
			case "teacher": updateLessonDownloadStatusTeacher(idLesson, downloadStatus);
				break;
			case "transcript": updateLessonDownloadStatusTranscript(idLesson, downloadStatus);
				break;
		}
	}

	public static int updateLessonDownloadStatusAudio(String idLesson, int downloadStatus){
		int updatedRows = 0;
		if( MainActivity.db != null ){
			ContentValues values = new ContentValues();
			values.put(COLUMN_DOWNLOAD_STATUS_AUDIO, downloadStatus);
			updatedRows = (int) MainActivity.db.update("lesson", values, "id_lesson = ?", new String[]{idLesson});
		}
		return updatedRows;
	}

	public static int updateLessonDownloadStatusTeacher(String idLesson, int downloadStatus){
		int updatedRows = 0;
		if( MainActivity.db != null ){
			ContentValues values = new ContentValues();
			values.put(COLUMN_DOWNLOAD_STATUS_TEACHER, downloadStatus);
			updatedRows = (int) MainActivity.db.update("lesson", values, "id_lesson = ?", new String[]{idLesson});
		}
		return updatedRows;
	}

	public static int updateLessonDownloadStatusTranscript(String idLesson, int downloadStatus){
		int updatedRows = 0;
		if( MainActivity.db != null ){
			ContentValues values = new ContentValues();
			values.put(COLUMN_DOWNLOAD_STATUS_TRANSCRIPT, downloadStatus);
			updatedRows = (int) MainActivity.db.update("lesson", values, "id_lesson = ?", new String[]{idLesson});
		}
		return updatedRows;
	}
}
