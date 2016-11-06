package com.erpdevelopment.vbvm.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Answer implements Parcelable, Comparable<Answer>{

	private String idProperty;
	private String postedDate;
	private String category;
	private String averageRating;
	private String qAndAPostsDescription;
	private String body;
	private String authorName;
	private String authorThumbnailSource;
	private String qAndAThumbnailSource;
	private String title;
	private String qAndAThumbnailAltText;
	private String authorThumbnailAltText;
	private List<String> topics;
	
	public Answer() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	public String getIdProperty() {
		return idProperty;
	}



	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}



	public String getPostedDate() {
		return postedDate;
	}



	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}



	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	public String getAverageRating() {
		return averageRating;
	}



	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}



	public String getqAndAPostsDescription() {
		return qAndAPostsDescription;
	}



	public void setqAndAPostsDescription(String qAndAPostsDescription) {
		this.qAndAPostsDescription = qAndAPostsDescription;
	}



	public String getBody() {
		return body;
	}



	public void setBody(String body) {
		this.body = body;
	}



	public String getAuthorName() {
		return authorName;
	}



	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}



	public String getAuthorThumbnailSource() {
		return authorThumbnailSource;
	}

	public void setAuthorThumbnailSource(String authorThumbnailSource) {
		this.authorThumbnailSource = authorThumbnailSource;
	}

	public String getqAndAThumbnailSource() {
		return qAndAThumbnailSource;
	}



	public void setqAndAThumbnailSource(String qAndAThumbnailSource) {
		this.qAndAThumbnailSource = qAndAThumbnailSource;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getqAndAThumbnailAltText() {
		return qAndAThumbnailAltText;
	}



	public void setqAndAThumbnailAltText(String qAndAThumbnailAltText) {
		this.qAndAThumbnailAltText = qAndAThumbnailAltText;
	}



	public String getAuthorThumbnailAltText() {
		return authorThumbnailAltText;
	}



	public void setAuthorThumbnailAltText(String authorThumbnailAltText) {
		this.authorThumbnailAltText = authorThumbnailAltText;
	}



	public List<String> getTopics() {
		return topics;
	}



	public void setTopics(List<String> topics) {
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
		dest.writeString(postedDate);
		dest.writeString(category);
		dest.writeString(averageRating);
		dest.writeString(qAndAPostsDescription);
		dest.writeString(body);
		dest.writeString(authorName);
		dest.writeString(authorThumbnailSource);
		dest.writeString(qAndAThumbnailSource);
		dest.writeString(title);
		dest.writeString(qAndAThumbnailAltText);
		dest.writeString(authorThumbnailAltText);
		dest.writeList(topics);
	} 
	
	private void readFromParcel(Parcel in) {		
		idProperty = in.readString();
		postedDate = in.readString();
		category = in.readString();
		averageRating = in.readString();
		qAndAPostsDescription = in.readString();
		body = in.readString();
		authorName = in.readString();
		authorThumbnailSource = in.readString();
		qAndAThumbnailSource = in.readString();
		title = in.readString();
		qAndAThumbnailAltText = in.readString();
		authorThumbnailAltText = in.readString();
		topics = new ArrayList<String>();
		in.readList(topics, getClass().getClassLoader());		
	}

	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Answer(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Answer> CREATOR =
		    	new Parcelable.Creator<Answer>() {
		            public Answer createFromParcel(Parcel in) {
		                return new Answer(in);
		            }
		 
		            public Answer[] newArray(int size) {
		                return new Answer[size];
		            }
		        };


	@Override
	public int compareTo(Answer answer) {
		return answer.getPostedDate().compareTo(getPostedDate());
	}
}
