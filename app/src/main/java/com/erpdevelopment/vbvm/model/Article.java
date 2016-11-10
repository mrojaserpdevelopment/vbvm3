package com.erpdevelopment.vbvm.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable, Comparable<Article>{
	
	private String postedDate;
	private String category;
	private String averageRating;
	private String articlesDescription;
	private String body;
	private String idProperty;
	private String authorName;
	private String articleThumbnailSource;
	private String authorThumbnailSource;
	private String title;
	private String articleThumbnailAltText;
	private String authorThumbnailAltText;
	private List<String> topics;
	
	public Article() {
		super();
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

	public String getArticlesDescription() {
		return articlesDescription;
	}

	public void setArticlesDescription(String articlesDescription) {
		this.articlesDescription = articlesDescription;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getArticleThumbnailSource() {
		return articleThumbnailSource;
	}

	public void setArticleThumbnailSource(String articleThumbnailSource) {
		this.articleThumbnailSource = articleThumbnailSource;
	}

	public String getAuthorThumbnailSource() {
		return authorThumbnailSource;
	}

	public void setAuthorThumbnailSource(String authorThumbnailSource) {
		this.authorThumbnailSource = authorThumbnailSource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public String getArticleThumbnailAltText() {
		return articleThumbnailAltText;
	}

	public void setArticleThumbnailAltText(String articleThumbnailAltText) {
		this.articleThumbnailAltText = articleThumbnailAltText;
	}

	public String getAuthorThumbnailAltText() {
		return authorThumbnailAltText;
	}

	public void setAuthorThumbnailAltText(String authorThumbnailAltText) {
		this.authorThumbnailAltText = authorThumbnailAltText;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(postedDate);
		dest.writeString(category);
		dest.writeString(averageRating);
		dest.writeString(articlesDescription);
		dest.writeString(body);
		dest.writeString(idProperty);
		dest.writeString(authorName);
		dest.writeString(articleThumbnailSource);
		dest.writeString(authorThumbnailSource);
		dest.writeString(title);
		dest.writeString(articleThumbnailAltText);
		dest.writeString(authorThumbnailAltText);
		dest.writeList(topics);
	}	
	
	private void readFromParcel(Parcel in) {
		postedDate = in.readString();
		category = in.readString();
		averageRating = in.readString();
		articlesDescription = in.readString();
		body = in.readString();
		idProperty = in.readString();
		authorName = in.readString();
		articleThumbnailSource = in.readString();
		authorThumbnailSource = in.readString();
		title = in.readString();
		articleThumbnailAltText = in.readString();
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
	public Article(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<Article> CREATOR =
		    	new Parcelable.Creator<Article>() {
		            public Article createFromParcel(Parcel in) {
		                return new Article(in);
		            }
		 
		            public Article[] newArray(int size) {
		                return new Article[size];
		            }
		        };

	@Override
	public int compareTo(Article article) {
		return article.getPostedDate().compareTo(getPostedDate());
	}
}
