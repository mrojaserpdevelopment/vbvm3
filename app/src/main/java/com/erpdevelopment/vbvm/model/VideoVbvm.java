package com.erpdevelopment.vbvm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoVbvm implements Parcelable {

	private String idProperty;
	private String postedDate;
	private String recordedDate;
	private String category;
	private String averageRating;
	private String description;
	private String title;
	private String thumbnailSource;
	private String thumbnailAltText;
	private String videoSource;
	private String videoLength;	
	private String idChannel;	
	
	public VideoVbvm() {
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

	public String getRecordedDate() {
		return recordedDate;
	}

	public void setRecordedDate(String recordedDate) {
		this.recordedDate = recordedDate;
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

	public String getVideoSource() {
		return videoSource;
	}

	public void setVideoSource(String videoSource) {
		this.videoSource = videoSource;
	}

	public String getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(String videoLength) {
		this.videoLength = videoLength;
	}

	public String getIdChannel() {
		return idChannel;
	}

	public void setIdChannel(String idChannel) {
		this.idChannel = idChannel;
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
		dest.writeString(recordedDate);
		dest.writeString(category);
		dest.writeString(averageRating);
		dest.writeString(description);
		dest.writeString(title);
		dest.writeString(thumbnailSource);
		dest.writeString(thumbnailAltText);
		dest.writeString(videoSource);
		dest.writeString(videoLength);
		dest.writeString(idChannel);
	}
	
	private void readFromParcel(Parcel in) {		
		idProperty = in.readString();
		postedDate = in.readString();
		recordedDate = in.readString();
		category = in.readString();
		averageRating = in.readString();
		description = in.readString();
		title = in.readString();
		thumbnailSource = in.readString();
		thumbnailAltText = in.readString();
		videoSource = in.readString();
		videoLength = in.readString();
		idChannel = in.readString();
	}
	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public VideoVbvm(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<VideoVbvm> CREATOR =
		    	new Parcelable.Creator<VideoVbvm>() {
		            public VideoVbvm createFromParcel(Parcel in) {
		                return new VideoVbvm(in);
		            }
		 
		            public VideoVbvm[] newArray(int size) {
		                return new VideoVbvm[size];
		            }
		        };

}
