package com.erpdevelopment.vbvm.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.service.WebServiceCall;

public class DBHandleArticles {

    private static final String LOG = "DatabaseHelper Article";
    
    private static final String COLUMN_ID_ARTICLE = "id_article";
    private static final String COLUMN_POSTED_DATE = "posted_date";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AVERAGE_RATING = "average_rating";
    private static final String COLUMN_ARTICLE_DESCRIPTION = "article_description";
    private static final String COLUMN_BODY = "body";    
    private static final String COLUMN_AUTHOR_NAME = "author_name";
    private static final String COLUMN_ARTICLE_THUMBNAIL_SOURCE = "article_thumbnail_source";
    private static final String COLUMN_AUTHOR_THUMBNAIL_SOURCE = "author_thumbnail_source";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ARTICLE_THUMBNAIL_ALT_TEXT = "article_thumbnail_alt_text";
    private static final String COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT = "author_thumbnail_alt_text";
    
	public static List<Article> getArticlesByAuthor(String authorName) {
	    String selectQuery = "SELECT * FROM article WHERE author_name = '" + authorName + "';";	 
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<Article> articles = new ArrayList<Article>();
    	Article article = null;	    
	    if (c.moveToFirst()) {
	        do {
	        	article = new Article();
	    		article.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_ARTICLE))));
	    		article.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
	    		article.setCategory(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
	    		article.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
	    		article.setArticlesDescription(c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION)));
	    		article.setBody(c.getString(c.getColumnIndex(COLUMN_BODY)));
	    		article.setAuthorName(c.getString((c.getColumnIndex(COLUMN_AUTHOR_NAME))));
	    		article.setArticleThumbnailSource(c.getString((c.getColumnIndex(COLUMN_ARTICLE_THUMBNAIL_SOURCE))));
	    		article.setAuthorThumbnailSource(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_SOURCE))));
	    		article.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
	        	article.setArticleThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_ARTICLE_THUMBNAIL_ALT_TEXT))));
	        	article.setAuthorThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT))));
	            // adding to todo list
	            articles.add(article);
	        } while (c.moveToNext());
	    }
	    c.close();
	    return articles;
	}
	
	public static Article getArticleById(String idArticle) {
	    String selectQuery = "SELECT  * FROM article WHERE id_article = '" + idArticle + "';";	 
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	Article article = null;
    	if (c.moveToFirst()) {
    		c.moveToFirst();
    		article = new Article();
    		article.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_ARTICLE))));
    		article.setPostedDate(c.getString((c.getColumnIndex(COLUMN_POSTED_DATE))));
    		article.setCategory(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
    		article.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
    		article.setArticlesDescription(c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION)));
    		article.setBody(c.getString(c.getColumnIndex(COLUMN_BODY)));
    		article.setAuthorName(c.getString((c.getColumnIndex(COLUMN_AUTHOR_NAME))));
    		article.setArticleThumbnailSource(c.getString((c.getColumnIndex(COLUMN_ARTICLE_THUMBNAIL_SOURCE))));
    		article.setAuthorThumbnailSource(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_SOURCE))));
    		article.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
        	article.setArticleThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_ARTICLE_THUMBNAIL_ALT_TEXT))));
        	article.setAuthorThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT))));
    	}
	    c.close();
	    return article;
	}
	
	public static List<Article> getAllArticles(boolean limitCount) {
		String selectQuery;
		if (limitCount)
	    	selectQuery = "SELECT * FROM article ORDER BY " + COLUMN_POSTED_DATE + " DESC LIMIT 1";
	    else
	    	selectQuery = "SELECT * FROM article ORDER BY " + COLUMN_POSTED_DATE + " DESC";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<Article> articles = new ArrayList<Article>();
    	Article article = null;	    
	    if (c.moveToFirst()) {
	        do {
	        	article = new Article();
	    		article.setIdProperty(c.getString((c.getColumnIndex(COLUMN_ID_ARTICLE))));
	    		article.setPostedDate(c.getString(c.getColumnIndex(COLUMN_POSTED_DATE)));
	    		article.setCategory(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
	    		article.setAverageRating(c.getString((c.getColumnIndex(COLUMN_AVERAGE_RATING))));
	    		article.setArticlesDescription(c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION)));
	    		article.setBody(c.getString(c.getColumnIndex(COLUMN_BODY)));
	    		article.setAuthorName(c.getString((c.getColumnIndex(COLUMN_AUTHOR_NAME))));
	    		article.setArticleThumbnailSource(c.getString((c.getColumnIndex(COLUMN_ARTICLE_THUMBNAIL_SOURCE))));
	    		article.setAuthorThumbnailSource(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_SOURCE))));
	    		article.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
	        	article.setArticleThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_ARTICLE_THUMBNAIL_ALT_TEXT))));
	        	article.setAuthorThumbnailAltText(c.getString((c.getColumnIndex(COLUMN_AUTHOR_THUMBNAIL_ALT_TEXT))));
	        	List<String> topics = getTopicsArticle(article.getIdProperty());
	        	article.setTopics(topics);
				if (!article.getBody().equals(""))
	            	articles.add(article);
	            WebServiceCall.authorNameSet.add(article.getAuthorName());
	        } while (c.moveToNext());
			Collections.sort(articles);
	    }
	    c.close();
	    return articles;
	}
	
	public static List<String> getTopicsArticle(String idArticle) {
    	String selectQuery = "SELECT * FROM topic_article WHERE id_article = '" + idArticle + "'";
	    Log.e(LOG, selectQuery);
	    Cursor c = MainActivity.db.rawQuery(selectQuery, null);
    	List<String> topics = new ArrayList<String>();
	    if (c.moveToFirst()) {
	        do {
				topics.add(c.getString(c.getColumnIndex("topic")));
	        } while (c.moveToNext());
	    }
	    c.close();
	    return topics;
	}
	
	/**
	 * Remove all articles.
	 */
	public static void removeAllArticles()
	{
	    MainActivity.db.delete("article", null, null);
	}
}
