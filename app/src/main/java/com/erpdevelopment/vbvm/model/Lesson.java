package com.erpdevelopment.vbvm.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Lesson implements Parcelable {
	
	private String transcript = "";
	private String postedDate = "";
	private String averageRating = "";
	private String dateStudyGiven = "";
	private String teacherAid = "";
	private String lessonsDescription = "";
	private String idProperty = "";
	private String videoLength = "";
	private String videoSource = "";
	private String title = "";
	private String location = "";
	private List<Topic> topicList = new ArrayList<>();
	private String audioLength = "";
	private String audioSource = "";
	private String studentAid = "";
	private boolean playing;
	private int progressPercentage;
	private long currentPosition;
	private String idStudy = "";
	private String studyThumbnailSource = "";
	private int studyLessonsSize;
	private String state = "";
	private int positionInList;
//	private int downloadStatus;
	private int downloadStatusAudio;
	private int downloadStatusTeacherAid;
	private int downloadStatusTranscript;
//	private Study study;
	private int downloadProgressAudio;
	private int downloadProgressTeacher;
	private int downloadProgressTranscript;
	private boolean section;
	private boolean sectionCompleted;

	public boolean isSection() {
		return section;
	}

	public void setSection(boolean section) {
		this.section = section;
	}

	public boolean isSectionCompleted() {
		return sectionCompleted;
	}

	public void setSectionCompleted(boolean sectionCompleted) {
		this.sectionCompleted = sectionCompleted;
	}

	public Lesson() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

	public String getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}

	public String getDateStudyGiven() {
		return dateStudyGiven;
	}

	public void setDateStudyGiven(String dateStudyGiven) {
		this.dateStudyGiven = dateStudyGiven;
	}

	public String getTeacherAid() {
		return teacherAid;
	}

	public void setTeacherAid(String teacherAid) {
		this.teacherAid = teacherAid;
	}

	public String getLessonsDescription() {
		return lessonsDescription;
	}

	public void setLessonsDescription(String lessonsDescription) {
		this.lessonsDescription = lessonsDescription;
	}

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(String videoLength) {
		this.videoLength = videoLength;
	}

	public String getVideoSource() {
		return videoSource;
	}

	public void setVideoSource(String videoSource) {
		this.videoSource = videoSource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<Topic> getTopics() {
		return topicList;
	}

	public void setTopics(List<Topic> topics) {
		this.topicList = topics;
	}

	public String getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(String audioLength) {
		this.audioLength = audioLength;
	}

	public String getAudioSource() {
		return audioSource;
	}

	public void setAudioSource(String audioSource) {
		this.audioSource = audioSource;
	}

	public String getStudentAid() {
		return studentAid;
	}

	public void setStudentAid(String studentAid) {
		this.studentAid = studentAid;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public int getProgressPercentage() {
		return progressPercentage;
	}

	public void setProgressPercentage(int progressPercentage) {
		this.progressPercentage = progressPercentage;
	}

	/**
	 * @return the currentPosition
	 */
	public long getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * @param currentPosition the currentPosition to set
	 */
	public void setCurrentPosition(long currentPosition) {
		this.currentPosition = currentPosition;
	}

	public String getIdStudy() {
		return idStudy;
	}

	public void setIdStudy(String idStudy) {
		this.idStudy = idStudy;
	}

	public String getStudyThumbnailSource() {
		return studyThumbnailSource;
	}

	public void setStudyThumbnailSource(String studyThumbnailSource) {
		this.studyThumbnailSource = studyThumbnailSource;
	}

	public int getStudyLessonsSize() {
		return studyLessonsSize;
	}

	public void setStudyLessonsSize(int studyLessonsSize) {
		this.studyLessonsSize = studyLessonsSize;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getPositionInList() {
		return positionInList;
	}

	public void setPositionInList(int positionInList) {
		this.positionInList = positionInList;
	}

//	public int getDownloadStatus() {
//		return downloadStatus;
//	}
//
//	public void setDownloadStatus(int downloadStatus) {
//		this.downloadStatus = downloadStatus;
//	}

	public int getDownloadStatusAudio() {
		return downloadStatusAudio;
	}

	public void setDownloadStatusAudio(int downloadStatusAudio) {
		this.downloadStatusAudio = downloadStatusAudio;
	}

	public int getDownloadStatusTeacherAid() {
		return downloadStatusTeacherAid;
	}

	public void setDownloadStatusTeacherAid(int downloadStatusTeacherAid) {
		this.downloadStatusTeacherAid = downloadStatusTeacherAid;
	}

	public int getDownloadStatusTranscript() {
		return downloadStatusTranscript;
	}

	public void setDownloadStatusTranscript(int downloadStatusTranscript) {
		this.downloadStatusTranscript = downloadStatusTranscript;
	}

//	public Study getStudy() { return study; }
//
//	public void setStudy(Study study) { this.study = study; }

	public int getDownloadProgressAudio() {
		return downloadProgressAudio;
	}

	public void setDownloadProgressAudio(int downloadProgressAudio) {
		this.downloadProgressAudio = downloadProgressAudio;
	}

	public int getDownloadProgressTeacher() {
		return downloadProgressTeacher;
	}

	public void setDownloadProgressTeacher(int downloadProgressTeacher) {
		this.downloadProgressTeacher = downloadProgressTeacher;
	}

	public int getDownloadProgressTranscript() {
		return downloadProgressTranscript;
	}

	public void setDownloadProgressTranscript(int downloadProgressTranscript) {
		this.downloadProgressTranscript = downloadProgressTranscript;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idProperty);
		dest.writeString(postedDate);
		dest.writeString(transcript);
		dest.writeString(averageRating);
		dest.writeString(dateStudyGiven);
		dest.writeString(teacherAid);
		dest.writeString(lessonsDescription);
		dest.writeString(videoLength);
		dest.writeString(videoSource);
		dest.writeString(title);
		dest.writeString(location);
		dest.writeString(audioLength);
		dest.writeString(audioSource);
		dest.writeString(studentAid);
		dest.writeList(topicList);
		dest.writeLong(currentPosition);
		dest.writeInt(progressPercentage);
		dest.writeString(idStudy);
		dest.writeString(studyThumbnailSource);
		dest.writeInt(studyLessonsSize);
		dest.writeString(state);
		dest.writeInt(positionInList);
//		dest.writeInt(downloadStatus);
		dest.writeInt(downloadStatusAudio);
		dest.writeInt(downloadStatusTeacherAid);
		dest.writeInt(downloadStatusTranscript);
//		dest.writeParcelable(study, flags);
		dest.writeInt(downloadProgressAudio);
		dest.writeInt(downloadProgressTeacher);
		dest.writeInt(downloadProgressTranscript);
		dest.writeByte((byte) (section ? 1 : 0));
	}	
	
	private void readFromParcel(Parcel in) {
		idProperty = in.readString();
		postedDate = in.readString();
		transcript = in.readString();
		averageRating = in.readString();
		dateStudyGiven = in.readString();
		teacherAid = in.readString();
		lessonsDescription = in.readString();
		videoLength = in.readString();
		videoSource = in.readString();
		title = in.readString();
		location = in.readString();
		audioLength = in.readString();
		audioSource = in.readString();
		studentAid = in.readString();
		topicList = new ArrayList<Topic>();
		in.readList(topicList, getClass().getClassLoader());
		currentPosition = in.readLong();
		progressPercentage = in.readInt();
		idStudy = in.readString();
		studyThumbnailSource = in.readString();
		studyLessonsSize = in.readInt();
		state = in.readString();
		positionInList = in.readInt();
//		downloadStatus = in.readInt();
		downloadStatusAudio = in.readInt();
		downloadStatusTeacherAid = in.readInt();
		downloadStatusTranscript = in.readInt();
//		study = (Study) in.readParcelable(Study.class.getClassLoader());
		downloadProgressAudio = in.readInt();
		downloadProgressTeacher = in.readInt();
		downloadProgressTranscript = in.readInt();
		section = (in.readByte() != 0);
	}
	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Lesson(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
		    	new Parcelable.Creator() {
		            public Lesson createFromParcel(Parcel in) {
		                return new Lesson(in);
		            }
		 
		            public Lesson[] newArray(int size) {
		                return new Lesson[size];
		            }
		        };


}
