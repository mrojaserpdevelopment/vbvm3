package com.erpdevelopment.vbvm.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Study implements Parcelable {
	
	private String idProperty;
	private String thumbnailSource;
	private String title;
	private String podcastLink;
	private String averageRating;
	private String thumbnailAltText;
	private String studiesDescription;
	private String type;
	private String lessonCount;
	private List<Topic> topics;
	private List<Lesson> lessons;
	
	public Study() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getThumbnailSource() {
		return thumbnailSource;
	}

	public void setThumbnailSource(String thumbnailSource) {
		this.thumbnailSource = thumbnailSource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPodcastLink() {
		return podcastLink;
	}

	public void setPodcastLink(String podcastLink) {
		this.podcastLink = podcastLink;
	}

	public String getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}

	public String getThumbnailAltText() {
		return thumbnailAltText;
	}

	public void setThumbnailAltText(String thumbnailAltText) {
		this.thumbnailAltText = thumbnailAltText;
	}

	public String getStudiesDescription() {
		return studiesDescription;
	}

	public void setStudiesDescription(String studiesDescription) {
		this.studiesDescription = studiesDescription;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLessonCount() {
		return lessonCount;
	}

	public void setLessonCount(String lessonCount) {
		this.lessonCount = lessonCount;
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idProperty);
		dest.writeString(thumbnailSource);
		dest.writeString(title);
		dest.writeString(podcastLink);
		dest.writeString(thumbnailAltText);
		dest.writeString(averageRating);
		dest.writeString(studiesDescription);
		dest.writeString(type);
		dest.writeString(lessonCount);
		dest.writeList(lessons);
		dest.writeList(topics);
	}	
	
	private void readFromParcel(Parcel in) {
		idProperty = in.readString();
		thumbnailSource = in.readString();
		title = in.readString();
		podcastLink = in.readString();
		averageRating = in.readString();
		thumbnailAltText = in.readString();
		studiesDescription = in.readString();
		type = in.readString();
		lessonCount = in.readString();
		lessons = new ArrayList<Lesson>();
		topics = new ArrayList<Topic>();
		in.readList(lessons, getClass().getClassLoader());
		in.readList(topics, getClass().getClassLoader());
	}

	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Study(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
		    	new Parcelable.Creator() {
		            public Study createFromParcel(Parcel in) {
		                return new Study(in);
		            }
		 
		            public Study[] newArray(int size) {
		                return new Study[size];
		            }
		        };
}
