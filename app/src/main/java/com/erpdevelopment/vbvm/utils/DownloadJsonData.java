package com.erpdevelopment.vbvm.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.ArticlesAdapter;
import com.erpdevelopment.vbvm.adapter.VideoChannelsAdapter;
import com.erpdevelopment.vbvm.adapter.AnswersAdapter;
import com.erpdevelopment.vbvm.adapter.StudiesAdapter;
import com.erpdevelopment.vbvm.db.DBHandleArticles;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.db.DBHandleAnswers;
import com.erpdevelopment.vbvm.db.DBHandleStudies;
import com.erpdevelopment.vbvm.db.DBHandleVideos;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.service.WebServiceCall;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by usuario on 13/09/2016.
 */
public class DownloadJsonData {

    private ProgressDialog pDialog;
    private ScrollView mScroll;
    private static DownloadJsonData sInstance;
    private AtomicInteger countThreads = new AtomicInteger();
    private Activity mActivity;
    private AQuery mAQuery;
    private TextView tvCountStudiesNew;
    private TextView tvCountStudiesOld;
    private TextView tvCountStudiesSingle;
    private TextView tvStudiesNew;
    private TextView tvStudiesOld;
    private TextView tvStudiesSingle;
    public static final int COUNT_PARALLEL_DOWNLOADS = 3;

    public static DownloadJsonData getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadJsonData();
        }
        return sInstance;
    }

    public void asyncJsonGetStudies(Activity activity, StudiesAdapter studiesAdapterNew,
                                    StudiesAdapter studiesAdapterOld, StudiesAdapter studiesAdapterSingle,
                                    View rootView, ScrollView scroll){
        mActivity = activity;
        mAQuery = new AQuery(activity);
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage(activity.getResources().getString(R.string.msg_progress_dialog_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        mScroll = scroll;
        tvCountStudiesNew = (TextView) rootView.findViewById(R.id.tvCountStudiesNew);
        tvCountStudiesOld = (TextView) rootView.findViewById(R.id.tvCountStudiesOld);
        tvCountStudiesSingle = (TextView) rootView.findViewById(R.id.tvCountStudiesSingle);
        tvStudiesNew = (TextView) rootView.findViewById(R.id.tvStudiesNew);
        tvStudiesOld = (TextView) rootView.findViewById(R.id.tvStudiesOld);
        tvStudiesSingle = (TextView) rootView.findViewById(R.id.tvStudiesSingle);

//        tvStudiesNew.setVisibility(View.VISIBLE);
//        tvStudiesOld.setVisibility(View.VISIBLE);
//        tvStudiesSingle.setVisibility(View.VISIBLE);

        WebServiceCall.studiesInDB = MainActivity.settings.getBoolean("studiesInDB", false);
        if ( WebServiceCall.studiesInDB ){
            Log.d("Database", "Studies - working offline on DB...");
//            List<Study> dbListStudies = DBHandleStudies.getAllStudies();
//            List<Lesson> dbListLessons = DBHandleLessons.getLessons(null);
//            studiesAdapter.setLessonListItems(dbListLessons);
//            studiesAdapter.setStudyListItems(dbListStudies);
//            FilesManager.listStudies = dbListStudies;

            System.out.println("asyncJsonGetStudies updating from db");
            List<List<Study>> dbListStudiesByType = DBHandleStudies.getAllStudiesByType();
            FilesManager.listStudiesTypeNew = dbListStudiesByType.get(0);
            FilesManager.listStudiesTypeOld = dbListStudiesByType.get(1);
            FilesManager.listStudiesTypeSingle = dbListStudiesByType.get(2);
            studiesAdapterNew.setStudyListItems(dbListStudiesByType.get(0));
            studiesAdapterOld.setStudyListItems(dbListStudiesByType.get(1));
            studiesAdapterSingle.setStudyListItems(dbListStudiesByType.get(2));

            Resources res = mActivity.getResources();
            String messageCountStudies = res.getString(R.string.message_count_studies, dbListStudiesByType.get(0).size());
            tvCountStudiesNew.setText(messageCountStudies);
            messageCountStudies = res.getString(R.string.message_count_studies, dbListStudiesByType.get(1).size());
            tvCountStudiesOld.setText(messageCountStudies);
            messageCountStudies = res.getString(R.string.message_count_studies, dbListStudiesByType.get(2).size());
            tvCountStudiesSingle.setText(messageCountStudies);

            tvStudiesNew.setVisibility(View.VISIBLE);
            tvStudiesOld.setVisibility(View.VISIBLE);
            tvStudiesSingle.setVisibility(View.VISIBLE);

//            new asyncGetAllLessons().execute();
//            asyncJsonArticles();
//            asyncJsonAnswers();
//            asyncJsonVideos();
            mScroll.scrollTo(0, 0);
            pDialog.dismiss();
        } else {
            if ( !CheckConnectivity.isOnline(activity)) {
                CheckConnectivity.showMessage(activity);
                pDialog.dismiss();
            } else {
                String url = WebServiceCall.JSON_STUDY_URL;
                final long expire = 30 * 60 * 1000;

                mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        if(json != null){
                            //successful ajax call, show status code and json content
                            String responseString = StringEscapeUtils.unescapeHtml(json.toString());
                            final List<Study> newListStudies = new ArrayList<>();
                            List<Study> listStudiesNew = new ArrayList<>();
                            List<Study> listStudiesOld = new ArrayList<>();
                            List<Study> listStudiesSingle = new ArrayList<>();
                            try {
                                JSONObject jsonResponse = new JSONObject(responseString);
                                JSONObject vbv = jsonResponse.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
                                // Getting JSON Array
                                final JSONArray studies = vbv.getJSONArray(WebServiceCall.TAG_STUDIES);
                                System.out.println("IS_SERVICE_RUNNING studies...");
                                for ( int i=0; i < studies.length(); i++ ) {
                                    final Study study = new Study();
                                    study.setThumbnailSource(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("thumbnailSource")));
                                    study.setTitle(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("title")));
                                    study.setThumbnailAltText(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("thumbnailAltText")));
                                    study.setPodcastLink(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("podcastLink")));
                                    study.setAverageRating(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("averageRating")));
                                    study.setStudiesDescription(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("description")));
                                    study.setType(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("type")));
                                    study.setIdProperty(StringEscapeUtils.unescapeJava(studies.getJSONObject(i).getString("ID")));
                                    newListStudies.add(study);
                                    if ( study.getType().contains("New") ) {
                                        listStudiesNew.add(study);
                                    }
                                    if ( study.getType().contains("Old") ) {
                                        listStudiesOld.add(study);
                                    }
                                    if ( study.getType().contains("Single") ) {
                                        listStudiesSingle.add(study);
                                    }
                                    DBHandleStudies.createStudy(study);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            FilesManager.listStudies = newListStudies;
                            FilesManager.listStudiesTypeNew = listStudiesNew;
                            FilesManager.listStudiesTypeOld = listStudiesOld;
                            FilesManager.listStudiesTypeSingle = listStudiesSingle;

                            studiesAdapterNew.setStudyListItems(FilesManager.listStudiesTypeNew);
                            studiesAdapterOld.setStudyListItems(FilesManager.listStudiesTypeOld);
                            studiesAdapterSingle.setStudyListItems(FilesManager.listStudiesTypeSingle);

                            //Save state flag for sync Webservice/DB
                            SharedPreferences.Editor e = MainActivity.settings.edit();
                            e.putBoolean("studiesInDB", true);
                            e.commit();

                        }else{
                            //ajax error, show error code
                            Toast.makeText(mAQuery.getContext(), "Server not available. Please try later...", Toast.LENGTH_LONG).show();
                        }
                        scroll.scrollTo(0, 0);
                        pDialog.dismiss();

                        Resources res = mActivity.getResources();
                        String messageCountStudies = res.getString(R.string.message_count_studies, (FilesManager.listStudiesTypeNew).size());
                        tvCountStudiesNew.setText(messageCountStudies);
                        messageCountStudies = res.getString(R.string.message_count_studies, (FilesManager.listStudiesTypeOld).size());
                        tvCountStudiesOld.setText(messageCountStudies);
                        messageCountStudies = res.getString(R.string.message_count_studies, (FilesManager.listStudiesTypeSingle).size());
                        tvCountStudiesSingle.setText(messageCountStudies);

                        tvStudiesNew.setVisibility(View.VISIBLE);
                        tvStudiesOld.setVisibility(View.VISIBLE);
                        tvStudiesSingle.setVisibility(View.VISIBLE);

//                        new asyncGetAllLessons().execute();
//                        asyncJsonArticles();
//                        asyncJsonAnswers();
//                        asyncJsonVideos();
                    }
                });
            }
            mScroll.scrollTo(0, 0);
        }
    }

    private class asyncGetAllLessons extends AsyncTask< Study, String, String > {

        private List<Lesson> list = new ArrayList<Lesson>();

        protected void onPreExecute() {
        }

        protected String doInBackground(Study... params) {
            List<Study> listStudies = DBHandleStudies.getAllStudies();
            for (Study study: listStudies) {
                WebServiceCall.lessonsInDB = MainActivity.settings.getBoolean("lessonsInDB", false);
                list = DBHandleLessons.getLessons(study.getIdProperty());
                //Check whether this lessons are in DB or synchronization is ON
                if ( (list.size() == 0) || ( !WebServiceCall.lessonsInDB ) ) {
                    if ( !CheckConnectivity.isOnline(mActivity) ) {
                        CheckConnectivity.showMessage(mActivity);
                    } else {
                        list = new WebServiceCall().getStudyLessons(study); //get and save lessons to DB
                        Log.i("BibleStudyDetails", "Update DB with data from Webservice");
                    }
                }
            }
            if (list != null)
                return "1";
            else
                return "";
        }

        protected void onPostExecute(String result) {
            System.out.println("finished IS_SERVICE_RUNNING lessons...");
            //Get all the lessons in DB
            List<Lesson> dbListLessons = DBHandleLessons.getLessons(null);
//            newestAdapter.setLessonListItems(dbListLessons);
//            asyncJsonArticles();
//            asyncJsonAnswers();
//            asyncJsonVideos();
            pDialog.dismiss();
            if ( countThreads.incrementAndGet() == COUNT_PARALLEL_DOWNLOADS ) {
                countThreads.set(0);
                pDialog.dismiss();
                System.out.println("asyncJsonGetStudies: pDialog.dismiss()...");
            }
        }

    }

    public void asyncJsonArticles(ArticlesAdapter adapter){

        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage(mActivity.getResources().getString(R.string.msg_progress_dialog_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
//        pDialog.show();

        WebServiceCall.articlesInDB = MainActivity.settings.getBoolean("articlesInDB", false);
        if ( WebServiceCall.articlesInDB ){
            Log.d("Database", "Articles - working offline on DB...");
            FilesManager.listArticles = DBHandleArticles.getAllArticles(false);
            adapter.setArticleListItems(FilesManager.listArticles);
            pDialog.dismiss();
        } else {
            if ( !CheckConnectivity.isOnline(mActivity)) {
                CheckConnectivity.showMessage(mActivity);
                pDialog.dismiss();
            } else {
                String url = WebServiceCall.JSON_ARTICLE_URL;
                long expire = 30 * 60 * 1000;

                mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        if(json != null) {
                            List<Article> listArticles = null;
                            try {
                                JSONObject vbv = json.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
                                JSONArray articles = vbv.getJSONArray(WebServiceCall.TAG_ARTICLES);
                                for ( int i=0; i < articles.length(); i++ ) {
                                    JSONObject c = articles.getJSONObject(i);
                                    Article article = new Article();
                                    article.setPostedDate(c.getString("postedDate")+"000");
                                    article.setCategory(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("category"))));
                                    article.setAverageRating(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("averageRating"))));
                                    article.setArticlesDescription(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("description"))));
                                    article.setBody(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("body"))));
                                    article.setIdProperty(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("ID"))));
                                    article.setAuthorName(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("authorName"))));
                                    article.setArticleThumbnailSource(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("articleThumbnailSource"))));
                                    article.setAuthorThumbnailSource(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("authorThumbnailSource"))));
                                    article.setTitle(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("title"))));
                                    article.setArticleThumbnailAltText(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("articleThumbnailAltText"))));
                                    article.setAuthorThumbnailAltText(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("authorThumbnailAltText"))));
                                    List<String> topics = new ArrayList<String>();
                                    //List of topics for filter
                                    JSONArray jsonArrayTopics = c.getJSONArray("topics");
                                    for ( int j=0; j < jsonArrayTopics.length(); j++ ) {
                                        JSONObject p = jsonArrayTopics.getJSONObject(j);
                                        topics.add(p.getString("topic"));
                                        Topic topic = new Topic();
                                        topic.setIdProperty(p.getString("ID"));
                                        topic.setTopic(p.getString("topic"));
                                        topic.setIdParent(article.getIdProperty());
                                        DBHandleStudies.createTopicArticle(topic);
                                    }
                                    DBHandleStudies.createArticle(article);
                                }
                                listArticles = DBHandleArticles.getAllArticles(false);
                                //Save state flag for sync Webservice/DB
                                SharedPreferences.Editor e = MainActivity.settings.edit();
                                e.putBoolean("articlesInDB", true);
                                e.commit();
                                Log.i("ArticlesActivity info", "Update DB with data from Webservice");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            FilesManager.listArticles = listArticles;
                            adapter.setArticleListItems(listArticles);
                        } else
                            Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
//                        pDialog.dismiss();
                    }
                });
            }
            mScroll.scrollTo(0, 0);
        }
    }

    public void asyncJsonAnswers(AnswersAdapter adapter){

        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage(mActivity.getResources().getString(R.string.msg_progress_dialog_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
//        pDialog.show();


        Log.d("onPostExecute=", "Cargando lista Posts");
        WebServiceCall.postsInDB = MainActivity.settings.getBoolean("postsInDB", false);
        if ( WebServiceCall.postsInDB ) {
            Log.d("Database", "QA Posts - working offline on DB...");
            FilesManager.listAnswers = DBHandleAnswers.getAllPosts(false);
            adapter.setQAndAPostsListItems(FilesManager.listAnswers);
            pDialog.dismiss();
        } else {
            if ( !CheckConnectivity.isOnline(mActivity) ) {
                CheckConnectivity.showMessage(mActivity);
            } else {
                String url = WebServiceCall.JSON_QANDA_URL;
                long expire = 30 * 60 * 1000;

                mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {

                        if(json != null) {
                            List<Answer> listAnswers = null;
                            try {
                                JSONObject vbv = json.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
                                JSONArray qAPosts = vbv.getJSONArray(WebServiceCall.TAG_QANDAPOSTS);
                                List<String> topics;
                                for ( int i=0; i < qAPosts.length(); i++ ) {
                                    JSONObject c = qAPosts.getJSONObject(i);
                                    Answer qAPost = new Answer();
                                    qAPost.setPostedDate(StringEscapeUtils.unescapeJava(c.getString("postedDate")+"000"));
                                    qAPost.setCategory(StringEscapeUtils.unescapeJava(c.getString("category")));
                                    qAPost.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
                                    qAPost.setqAndAPostsDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
                                    qAPost.setBody(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("body"))).toString());
                                    qAPost.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
                                    qAPost.setAuthorName(StringEscapeUtils.unescapeJava(c.getString("authorName")));
                                    qAPost.setqAndAThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("qAndAThumbnailSource")));
                                    qAPost.setAuthorThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailSource")));
                                    qAPost.setTitle(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml(c.getString("title"))));
                                    qAPost.setqAndAThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("qAndAThumbnailAltText")));
                                    qAPost.setAuthorThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("authorThumbnailAltText")));
                                    topics = new ArrayList<String>();
                                    //List of topics for filter
                                    JSONArray jsonArrayTopics = c.getJSONArray("topics");
                                    for ( int j=0; j < jsonArrayTopics.length(); j++ ) {
                                        JSONObject p = jsonArrayTopics.getJSONObject(j);
                                        topics.add(p.getString("topic"));
                                        Topic topic = new Topic();
                                        topic.setIdProperty(p.getString("ID"));
                                        topic.setTopic(p.getString("topic"));
                                        topic.setIdParent(qAPost.getIdProperty());
                                        DBHandleStudies.createTopicPost(topic);
                                    }
                                    qAPost.setTopics(topics);
                                    DBHandleStudies.createPost(qAPost);
                                }
                                listAnswers = DBHandleAnswers.getAllPosts(false);
                                //Save state flag for sync Webservice/DB
                                SharedPreferences.Editor e = MainActivity.settings.edit();
                                e.putBoolean("postsInDB", true);
                                e.commit();
                                Log.i("PostsActivity info", "Updated DB / QA post with data from Webservice");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            FilesManager.listAnswers = listAnswers;
                            adapter.setQAndAPostsListItems(listAnswers);
                        } else
                            Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
//                        pDialog.dismiss();
                    }
                });
            }
            mScroll.scrollTo(0, 0);
        }
    }

    public void asyncJsonVideos(VideoChannelsAdapter adapter){

        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage(mActivity.getResources().getString(R.string.msg_progress_dialog_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
//        pDialog.show();

        WebServiceCall.videosInDB = MainActivity.settings.getBoolean("videosInDB", false);
        if ( WebServiceCall.videosInDB ) {
            Log.d("Database", "Videos - working offline on DB...");
            FilesManager.listVideoChannels = DBHandleVideos.getChannels();
            adapter.setVideoListItems(FilesManager.listVideoChannels);
//            pDialog.dismiss();
        } else {
            if ( !CheckConnectivity.isOnline(mActivity)) {
                CheckConnectivity.showMessage(mActivity);
            } else {
                String url = WebServiceCall.JSON_CHANNELS_URL;
                long expire = 30 * 60 * 1000;

                mAQuery.ajax(url, JSONObject.class, expire, new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {

                        if(json != null) {
                            String responseString = StringEscapeUtils.unescapeHtml(json.toString());
                            List<VideoChannel> listVideoChannels = new ArrayList<VideoChannel>();
                            try {
                                JSONObject jsonResponse = new JSONObject(responseString);
                                JSONObject vbv = jsonResponse.getJSONObject(WebServiceCall.TAG_VERSE_BY_VERSE);
                                JSONArray channels = vbv.getJSONArray(WebServiceCall.TAG_CHANNELS);
                                for ( int i=0; i < channels.length(); i++ ) {
                                    JSONObject c = channels.getJSONObject(i);
                                    VideoChannel channel = new VideoChannel();
                                    channel.setIdProperty(StringEscapeUtils.unescapeJava(c.getString("ID")));
                                    channel.setPostedDate(c.getString("postedDate")+"000");
                                    channel.setAverageRating(StringEscapeUtils.unescapeJava(c.getString("averageRating")));
                                    channel.setDescription(StringEscapeUtils.unescapeJava(c.getString("description")));
                                    channel.setTitle(StringEscapeUtils.unescapeJava(c.getString("title")));
                                    channel.setThumbnailSource(StringEscapeUtils.unescapeJava(c.getString("thumbnailSource")));
                                    channel.setThumbnailAltText(StringEscapeUtils.unescapeJava(c.getString("thumbnailAltText")));
                                    List<VideoVbvm> listVideos = new ArrayList<VideoVbvm>();
                                    JSONArray jsonArrayVideos = c.getJSONArray(WebServiceCall.TAG_VIDEOS);
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
                                    listVideoChannels.add(channel);
                                    DBHandleStudies.createChannel(channel);
                                }
                                listVideoChannels = DBHandleVideos.getChannels();
                                Collections.sort(listVideoChannels);
                                //Save state flag for sync Webservice/DB
                                SharedPreferences.Editor e = MainActivity.settings.edit();
                                e.putBoolean("videosInDB", true);
                                e.commit();
                                Log.i("HomeFragment info", "Updated DB / Videos from Webservice");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            FilesManager.listVideoChannels = listVideoChannels;
                            adapter.setVideoListItems(listVideoChannels);
                        } else
                            Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
//                        pDialog.dismiss();
                    }
                });
            }
            mScroll.scrollTo(0, 0);
        }
    }
}
