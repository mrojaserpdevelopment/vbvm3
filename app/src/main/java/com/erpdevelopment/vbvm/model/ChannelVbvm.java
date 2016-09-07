package com.erpdevelopment.vbvm.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ChannelVbvm implements Parcelable {
	
	private String idProperty;
	private String postedDate;
	private String averageRating;
	private String description;
	private String title;
	private String thumbnailSource;
	private String thumbnailAltText;
	private List<VideoVbvm> videos;

	public ChannelVbvm() {
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

	public String getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnailSource() {
		return thumbnailSource;
	}

	public void setThumbnailSource(String thumbnailSource) {
		this.thumbnailSource = thumbnailSource;
	}

	public String getThumbnailAltText() {
		return thumbnailAltText;
	}

	public void setThumbnailAltText(String thumbnailAltText) {
		this.thumbnailAltText = thumbnailAltText;
	}

	public List<VideoVbvm> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoVbvm> videos) {
		this.videos = videos;
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
		dest.writeString(averageRating);
		dest.writeString(description);
		dest.writeString(title);
		dest.writeString(thumbnailSource);
		dest.writeString(thumbnailAltText);
		dest.writeList(videos);
	}
	
	private void readFromParcel(Parcel in) {
		idProperty = in.readString();
		postedDate = in.readString();
		averageRating = in.readString();
		description = in.readString();
		title = in.readString();
		thumbnailSource = in.readString();
		thumbnailAltText = in.readString();
		videos = new ArrayList<VideoVbvm>();
		in.readList(videos, getClass().getClassLoader());		
	}
	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public ChannelVbvm(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<ChannelVbvm> CREATOR =
		    	new Parcelable.Creator<ChannelVbvm>() {
		            public ChannelVbvm createFromParcel(Parcel in) {
		                return new ChannelVbvm(in);
		            }
		 
		            public ChannelVbvm[] newArray(int size) {
		                return new ChannelVbvm[size];
		            }
		        };

}
