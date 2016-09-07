package com.erpdevelopment.vbvm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Topic implements Parcelable{
	
	private String idProperty;
	private String topic;
	private String idParent;
	
	public Topic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getIdParent() {
		return idParent;
	}

	public void setIdParent(String idParent) {
		this.idParent = idParent;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idProperty);
		dest.writeString(topic);
		dest.writeString(idParent);
	}
	
	private void readFromParcel(Parcel in) {
		idProperty = in.readString();
		topic = in.readString();
		idParent = in.readString();
	}
	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Topic(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<Topic> CREATOR =
		    	new Parcelable.Creator<Topic>() {
		            public Topic createFromParcel(Parcel in) {
		                return new Topic(in);
		            }
		 
		            public Topic[] newArray(int size) {
		                return new Topic[size];
		            }
		        };
	
}
