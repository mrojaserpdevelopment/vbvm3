package com.erpdevelopment.vbvm.model;

import java.util.List;

public class VerseByVerse {
	
	private List<QandAPost> qAndAPosts;
	private List<Study> studies;
	private List<Article> articles;
	private List<EventVbvm> events;
	
	public VerseByVerse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<QandAPost> getqAndAPosts() {
		return qAndAPosts;
	}

	public void setqAndAPosts(List<QandAPost> qAndAPosts) {
		this.qAndAPosts = qAndAPosts;
	}

	public List<Study> getStudies() {
		return studies;
	}

	public void setStudies(List<Study> studies) {
		this.studies = studies;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public List<EventVbvm> getEvents() {
		return events;
	}

	public void setEvents(List<EventVbvm> events) {
		this.events = events;
	}	
	
}
