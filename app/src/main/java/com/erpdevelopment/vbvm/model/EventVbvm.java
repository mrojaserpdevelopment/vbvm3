package com.erpdevelopment.vbvm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventVbvm implements Parcelable{
	
	private String location;
	private String thumbnailSource;
	private String map;
	private String postedDate;
	private String thumbnailAltText;
	private String title;
	private String eventDate;
	private String eventsDescription;
	private String expiresDate;
	private String type;
	private String idProperty;
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getThumbnailSource() {
		return thumbnailSource;
	}
	public void setThumbnailSource(String thumbnailSource) {
		this.thumbnailSource = thumbnailSource;
	}
	public String getMap() {
		return map;
	}
	public void setMap(String map) {
		this.map = map;
	}
	public String getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}
	public String getThumbnailAltText() {
		return thumbnailAltText;
	}
	public void setThumbnailAltText(String thumbnailAltText) {
		this.thumbnailAltText = thumbnailAltText;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getEventsDescription() {
		return eventsDescription;
	}
	public void setEventsDescription(String eventsDescription) {
		this.eventsDescription = eventsDescription;
	}
	public String getExpiresDate() {
		return expiresDate;
	}
	public void setExpiresDate(String expiresDate) {
		this.expiresDate = expiresDate;
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
	public void setIdProperty(String iDProperty) {
		this.idProperty = iDProperty;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(location);
		dest.writeString(thumbnailSource);
		dest.writeString(map);
		dest.writeString(postedDate);
		dest.writeString(thumbnailAltText);
		dest.writeString(title);
		dest.writeString(eventDate);
		dest.writeString(eventsDescription);
		dest.writeString(expiresDate);
		dest.writeString(type);
		dest.writeString(idProperty);
		
	}
	
	private void readFromParcel(Parcel in) {
		location = in.readString();
		thumbnailSource = in.readString();
		map = in.readString();
		postedDate = in.readString();
		thumbnailAltText = in.readString();
		title = in.readString();
		eventDate = in.readString();
		eventsDescription = in.readString();
		expiresDate = in.readString();
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
	public EventVbvm(Parcel in) {
		readFromParcel(in);
	}

	public EventVbvm() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
		    	new Parcelable.Creator() {
		            public EventVbvm createFromParcel(Parcel in) {
		                return new EventVbvm(in);
		            }
		 
		            public EventVbvm[] newArray(int size) {
		                return new EventVbvm[size];
		            }
		        };

}
