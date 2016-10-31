package com.erpdevelopment.vbvm.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.db.DBHandleStudies;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.ChannelVbvm;
import com.erpdevelopment.vbvm.model.EventVbvm;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.QandAPost;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.JSONParser;

public class WebServiceCall {

	public static final String JSON_STUDY_URL = "http://www.versebyverseministry.org/core/json/";
	public static final String JSON_ARTICLE_URL = "http://www.versebyverseministry.org/core/json-articles/";
	public static final String JSON_QANDA_URL = "http://www.versebyverseministry.org/core/json-qa/";
	public static final String JSON_EVENTS_URL = "http://www.versebyverseministry.org/core/json-events/";
	public static final String JSON_STUDY_DETAIL_URL = "http://www.versebyverseministry.org/core/json-lessons/";
	public static final String JSON_CHANNELS_URL = "http://www.versebyverseministry.org/core/json-channels/";
	
	//JSON Node Names 
	public static final String TAG_VERSE_BY_VERSE = "VerseByVerse";
	public static final String TAG_QANDAPOSTS = "QandAPosts";
	public static final String TAG_STUDIES = "studies";
	public static final String TAG_ARTICLES = "articles";
	public static final String TAG_EVENTS = "events";
	public static final String TAG_TOPICS = "topics";
	public static final String TAG_LESSONS = "lessons";
	public static final String TAG_CHANNELS = "channels";
	public static final String TAG_VIDEOS = "videos";

	JSONObject vbv;
	JSONArray topics;
	JSONArray studies;	
	JSONArray articles;
	JSONArray qAPosts;
	JSONArray events;
	JSONArray channels;
	JSONArray videos;
	
	List<Study> listStudies;	
	List<Topic> listTopics;
	List<Lesson> listLessons;
	List<Topic> listLessonTopics;	
	List<Article> listArticles;
	List<QandAPost> listQAPosts;
	List<EventVbvm> listEvents;
	List<String> listQAPostTopics;
	List<ChannelVbvm> listChannels;
	List<VideoVbvm> listVideos;
	
	public static Set<String> topicSet = new HashSet<>();
	public static Set<String> authorNameSet = new HashSet<>();
	
	Study study;
	Topic topic;
	Lesson lesson;
	Article article;
	QandAPost qAPost;
	EventVbvm event;
	ChannelVbvm channel;
	VideoVbvm video;
	
	public static String authorNames[] = {
		"Stephen Armstrong", 
		"Melissa Church", 
		"Brian Smith", 
		"Ivette Irizarry", 
		"Brady Stephenson" };
	
//	public static boolean createdDB = false;

	public static boolean studiesInDB = false;	
	public static boolean lessonsInDB = false;
	public static boolean articlesInDB = false;
	public static boolean postsInDB = false;
	public static boolean eventsInDB = false;
	public static boolean videosInDB = false;
	
	public List<Study> getStudies(Context context) {		
				
		// Creating new JSON Parser
		JSONParser jParser = new JSONParser();

		// Getting JSON from URL
		JSONObject json = jParser.getJSONFromUrl(JSON_STUDY_URL);
		
		try {
			vbv = json.getJSONObject(TAG_VERSE_BY_VERSE);
			// Getting JSON Array
			studies = vbv.getJSONArray(TAG_STUDIES);
			listStudies = new ArrayList<Study>();
			for ( int i=0; i < studies.length(); i++ ) {
				
				study = new Study();				
				study.setThumbnailSource(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("thumbnailSource")));
				study.setTitle(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("title")));
				study.setThumbnailAltText(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("thumbnailAltText")));
				study.setPodcastLink(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("podcastLink")));
				study.setAverageRating(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("averageRating")));
				study.setStudiesDescription(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("description")));
				study.setType(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("type")));
				study.setIdProperty(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("ID")));
				listStudies.add(study);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listStudies;
	}
	
	public List<Lesson> getStudyLessons(Study study) {		
		
		// Creating new JSON Parser
		JSONParser jParser = new JSONParser();

		// Getting JSON from URL
		JSONObject json = jParser.getJSONFromUrl(JSON_STUDY_DETAIL_URL + study.getIdProperty() + "/");

		try {
			vbv = json.getJSONObject(TAG_VERSE_BY_VERSE);
			
			// Getting JSON Array
			JSONArray lessonArray = vbv.getJSONArray(TAG_LESSONS);
			listLessons = new ArrayList<Lesson>();
			
			for ( int j=0; j < lessonArray.length(); j++ ) {
				lesson = new Lesson();
				lesson.setIdProperty(lessonArray.getJSONObject(j).getString("ID"));
				lesson.setLessonsDescription(lessonArray.getJSONObject(j).getString("description"));
				lesson.setPostedDate(lessonArray.getJSONObject(j).getString("postedDate"));
				lesson.setTranscript(lessonArray.getJSONObject(j).getString("transcript"));
				lesson.setDateStudyGiven(lessonArray.getJSONObject(j).getString("dateStudyGiven"));
				lesson.setTeacherAid(lessonArray.getJSONObject(j).getString("teacherAid"));
				lesson.setAverageRating(lessonArray.getJSONObject(j).getString("averageRating"));
				lesson.setVideoSource(lessonArray.getJSONObject(j).getString("videoSource"));
				lesson.setVideoLength(lessonArray.getJSONObject(j).getString("videoLength"));
				lesson.setTitle(lessonArray.getJSONObject(j).getString("title"));
				lesson.setLocation(lessonArray.getJSONObject(j).getString("location"));
				lesson.setAudioSource(lessonArray.getJSONObject(j).getString("audioSource"));
				lesson.setAudioLength(lessonArray.getJSONObject(j).getString("audioLength"));
				lesson.setStudentAid(lessonArray.getJSONObject(j).getString("studentAid"));
				lesson.setPositionInList(j);
				lesson.setIdStudy(study.getIdProperty());
				lesson.setStudyThumbnailSource(study.getThumbnailSource());
				lesson.setStudyLessonsSize(lessonArray.length());
//				lesson.setDownloadStatus(0);
				lesson.setDownloadStatusAudio(0);
				lesson.setDownloadStatusTeacherAid(0);
				lesson.setDownloadStatusTranscript(0);
//				lesson.setStudy(study);
				lesson.setState("new");
				
				JSONArray ltopics = lessonArray.getJSONObject(j).getJSONArray(TAG_TOPICS);
				
				listLessonTopics = new ArrayList<Topic>();					
				for (int k=0; k < ltopics.length(); k++){
					topic = new Topic();
					topic.setIdProperty(ltopics.getJSONObject(k).getString("ID"));
					topic.setTopic(ltopics.getJSONObject(k).getString("topic"));
					topic.setIdParent(lesson.getIdProperty());
					listLessonTopics.add(topic);
						DBHandleStudies.createTopicLesson(topic);
				}
				lesson.setTopics(listLessonTopics);	
				listLessons.add(lesson);
				DBHandleStudies.createLesson(lesson);
			}
			//Save state flag for sync Webservice/DB
			Editor e = MainActivity.settings.edit();
			e.putBoolean("lessonsInDB", true);
			e.commit();			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listLessons;		
	}
	
	public List<QandAPost> getQandAPosts() {		
		
		// Creating new JSON Parser
		JSONParser jParser = new JSONParser();

		// Getting JSON from URL
		JSONObject json = jParser.getJSONFromUrl(JSON_QANDA_URL);
		
		try {
			vbv = json.getJSONObject(TAG_VERSE_BY_VERSE);
			
			// Getting JSON Array
			qAPosts = vbv.getJSONArray(TAG_QANDAPOSTS);
			listQAPosts = new ArrayList<QandAPost>();
			topicSet = new HashSet<String>();
			List<String> topics;
			for ( int i=0; i < qAPosts.length(); i++ ) {
				JSONObject c = qAPosts.getJSONObject(i);
				qAPost = new QandAPost();
				long timeMills = Long.parseLong(c.getString("postedDate")+"000");
				Date d = new Date(timeMills);
				DateFormat df = DateFormat.getDateInstance();
				String date = df.format(d);	
				qAPost.setPostedDate(date);
				qAPost.setCategory(StringEscapeUtils.unescapeJava(c.getString("category")));
				qAPost.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
				qAPost.setqAndAPostsDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
				qAPost.setBody(StringEscapeUtils.unescapeJava(c.getString("body")));
				qAPost.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
				qAPost.setAuthorName(StringEscapeUtils.unescapeJava(c.getString("authorName")));
				qAPost.setqAndAThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("qAndAThumbnailSource")));
				qAPost.setAuthorThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailSource")));
				qAPost.setTitle(StringEscapeUtils.unescapeJava(c.getString("title")));
				qAPost.setqAndAThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("qAndAThumbnailAltText")));
				qAPost.setAuthorThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailAltText")));				
				topics = new ArrayList<String>();
				//List of topics for filter
				JSONArray jsonArrayTopics = c.getJSONArray("topics");
				for ( int j=0; j < jsonArrayTopics.length(); j++ ) {
					JSONObject p = jsonArrayTopics.getJSONObject(j);
					topicSet.add(p.getString("topic"));
					topics.add(p.getString("topic"));
					Topic topic = new Topic();
					topic.setIdProperty(p.getString("ID"));
					topic.setTopic(p.getString("topic"));
					topic.setIdParent(qAPost.getIdProperty());
					DBHandleStudies.createTopicPost(topic);
				}
				qAPost.setTopics(topics);
				listQAPosts.add(qAPost);
//				if (createdDB)
					DBHandleStudies.createPost(qAPost);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listQAPosts;
	}	
	
	public List<EventVbvm> getEvents() {		
		
		// Creating new JSON Parser
		JSONParser jParser = new JSONParser();

		// Getting JSON from URL
		JSONObject json = jParser.getJSONFromUrl(JSON_EVENTS_URL);
		
		try {
			vbv = json.getJSONObject(TAG_VERSE_BY_VERSE);
			
			// Getting JSON Array
			events = vbv.getJSONArray(TAG_EVENTS);
			listEvents = new ArrayList<EventVbvm>();
			
			for ( int i=0; i < events.length(); i++ ) {
				JSONObject c = events.getJSONObject(i);
				event = new EventVbvm();
				event.setPostedDate(StringEscapeUtils.unescapeJava(c.getString("postedDate")));
				event.setLocation(StringEscapeUtils.unescapeJava(c.getString("location")));
				event.setThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("thumbnailSource")));
				event.setMap(StringEscapeUtils.unescapeJava(c.getString("map")));
				event.setTitle(StringEscapeUtils.unescapeJava(c.getString("title")));
				event.setEventDate(StringEscapeUtils.unescapeJava(c.getString("eventDate")));
				event.setThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("thumbnailAltText")));
				event.setEventsDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
				event.setExpiresDate(StringEscapeUtils.unescapeJava(c.getString("expiresDate")));
				event.setType(StringEscapeUtils.unescapeJava(c.getString("type")));
				event.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
				listEvents.add(event);	
				DBHandleStudies.createEvent(event);
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listEvents;		
	}
	
	public List<ChannelVbvm> getVideos() {		
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(JSON_CHANNELS_URL);		
		try {
			vbv = json.getJSONObject(TAG_VERSE_BY_VERSE);			
			channels = vbv.getJSONArray(TAG_CHANNELS);
			listChannels = new ArrayList<ChannelVbvm>();
			for ( int i=0; i < channels.length(); i++ ) {
				JSONObject c = channels.getJSONObject(i);
				channel = new ChannelVbvm();
				channel.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));				
				channel.setPostedDate(String.valueOf(Long.parseLong(c.getString("postedDate")+"000")));
				channel.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
				channel.setDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
				channel.setTitle(StringEscapeUtils.unescapeJava(c.getString("title")));
				channel.setThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("thumbnailSource")));
				channel.setThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("thumbnailAltText")));
				List<VideoVbvm> listVideos = new ArrayList<VideoVbvm>();
				JSONArray jsonArrayVideos = c.getJSONArray(TAG_VIDEOS);
				for ( int j=0; j < jsonArrayVideos.length(); j++ ) {
					JSONObject p = jsonArrayVideos.getJSONObject(j);
					VideoVbvm video = new VideoVbvm();
					video.setIdProperty(StringEscapeUtils.unescapeJava(p.getString("ID")));
					video.setDescription(StringEscapeUtils.unescapeJava(p.getString("description")));
					video.setVideoSource(StringEscapeUtils.unescapeJava(p.getString("videoSource")));
					video.setVideoLength(StringEscapeUtils.unescapeJava(p.getString("videoLength")));
					video.setTitle(StringEscapeUtils.unescapeJava(p.getString("title")));
					video.setThumbnailSource(StringEscapeUtils.unescapeJava(p.getString("thumbnailSource")));
					video.setThumbnailAltText(StringEscapeUtils.unescapeJava(p.getString("thumbnailAltText")));
					video.setIdChannel(channel.getIdProperty());
					listVideos.add(video);
					DBHandleStudies.createVideo(video);
				}				
				channel.setVideos(listVideos);
				listChannels.add(channel);
				DBHandleStudies.createChannel(channel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listChannels;
	}	
	
	public List<Article> getArticlesByAuthor(String authorName) {
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(JSON_ARTICLE_URL);		
		try {
			vbv = json.getJSONObject(TAG_VERSE_BY_VERSE);
			articles = vbv.getJSONArray(TAG_ARTICLES);
			listArticles = new ArrayList<Article>();			
			for ( int i=0; i < articles.length(); i++ ) {
				JSONObject c = articles.getJSONObject(i);
				if (c.getString("authorName").equals(authorName)){
//					article = new Article();
//					Date d = new Date(c.getLong("postedDate"));
//					DateFormat df = DateFormat.getDateInstance();
//					String date = df.format(d);
//					article.setPostedDate(date);
					article.setPostedDate(c.getString("postedDate"));
					article.setCategory(StringEscapeUtils.unescapeJava(c.getString("category")));
					article.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
					article.setArticlesDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
					article.setBody(StringEscapeUtils.unescapeJava(c.getString("body")));
					article.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
					article.setAuthorName(StringEscapeUtils.unescapeJava(c.getString("authorName")));
					article.setArticleThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("articleThumbnailSource")));
					article.setAuthorThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailSource")));
					article.setTitle(StringEscapeUtils.unescapeJava(c.getString("title")));
					article.setArticleThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("articleThumbnailAltText")));
					article.setAuthorThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailAltText")));				
					listArticles.add(article);
				}
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return listArticles;		
	}	
	
	
	
}
