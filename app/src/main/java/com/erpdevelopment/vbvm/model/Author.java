package com.erpdevelopment.vbvm.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable{

	private String id;
	private String authorName;
	private String authorThumbNailSource;
	private List<Article> listArticles;	
	
	public Author() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorThumbNailSource() {
		return authorThumbNailSource;
	}

	public void setAuthorThumbNailSource(String authorThumbNailSource) {
		this.authorThumbNailSource = authorThumbNailSource;
	}

	public List<Article> getListArticles() {
		return listArticles;
	}

	public void setListArticles(List<Article> listArticles) {
		this.listArticles = listArticles;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeList(listArticles);
		dest.writeString(id);
		dest.writeString(authorName);
		dest.writeString(authorThumbNailSource);
	}
	
	private void readFromParcel(Parcel in) {
		listArticles = new ArrayList<Article>();
		in.readList(listArticles, getClass().getClassLoader());
		id = in.readString();
		authorName = in.readString();
		authorThumbNailSource = in.readString();
	}

	
	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Author(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<Author> CREATOR =
		    	new Parcelable.Creator<Author>() {
		            public Author createFromParcel(Parcel in) {
		                return new Author(in);
		            }
		 
		            public Author[] newArray(int size) {
		                return new Author[size];
		            }
		        };
}
