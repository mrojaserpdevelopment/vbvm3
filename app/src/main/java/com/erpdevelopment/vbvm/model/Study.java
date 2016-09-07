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
	private List<Topic> topics;
	private List<Lesson> lessons;
	
	public Study() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
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

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(topics);
		dest.writeList(lessons);
		dest.writeString(thumbnailSource);
		dest.writeString(title);
		dest.writeString(podcastLink);
		dest.writeString(thumbnailAltText);
		dest.writeString(averageRating);
		dest.writeString(studiesDescription);
		dest.writeString(type);
		dest.writeString(idProperty);
	}	
	
	private void readFromParcel(Parcel in) {
		topics = new ArrayList<Topic>();
		lessons = new ArrayList<Lesson>();
		in.readList(topics, getClass().getClassLoader());
		in.readList(lessons, getClass().getClassLoader());
		thumbnailSource = in.readString();
		title = in.readString();
		podcastLink = in.readString();
		averageRating = in.readString();
		thumbnailAltText = in.readString();
		studiesDescription = in.readString();
		type = in.readString();
		idProperty = in.readString();
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
