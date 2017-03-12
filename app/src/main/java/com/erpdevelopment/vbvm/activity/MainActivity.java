package com.erpdevelopment.vbvm.activity;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.fragment.AnswerDetailsFragment;
import com.erpdevelopment.vbvm.fragment.AnswersFragment;
import com.erpdevelopment.vbvm.fragment.ArticleDetailsFragment;
import com.erpdevelopment.vbvm.fragment.ArticlesFragment;
import com.erpdevelopment.vbvm.fragment.LessonsFragment;
import com.erpdevelopment.vbvm.fragment.StudiesFragment;
import com.erpdevelopment.vbvm.fragment.VideoChannelsFragment;
import com.erpdevelopment.vbvm.fragment.VideosFragment;
import com.erpdevelopment.vbvm.helper.AudioPlayerHelper;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.Constants;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements StudiesFragment.OnStudyItemSelectedListener,
		ArticlesFragment.OnArticleSelectedListener,
		AnswersFragment.OnAnswerSelectedListener,
		VideoChannelsFragment.OnVideoChannelSelectedListener {

	public static final String DIRECTORY_IMAGES = "vbvm";
	public static final String SETTING_IMAGES_DIRECTORY_NAME = "vbvmi";
	public static final String SETTING_DATABASE_FILE_PATH = "db-path";
    public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

	public static SQLiteDatabase db;
	public static Context mainCtx;
	public static SharedPreferences settings;

	private ActionBar actionBar;

	private StudiesFragment fragmentStudies;
	private ArticlesFragment fragmentArticles;
	private AnswersFragment fragmentAnswers;
	private VideosFragment fragmentVideos;
	private LessonsFragment fragmentLessons;
	private ArticleDetailsFragment fragmentArticleDetails;
	private AnswerDetailsFragment fragmentAnswerDetails;
	private VideoChannelsFragment fragmentVideoChannels;

	private RelativeLayout viewMiniPlayer;
	private SlidingUpPanelLayout slidingLayout;

	private Study mStudy;
	private Article mArticle;
	private Answer mAnswer;
	private VideoChannel mVideoChannel;

	public static String lastFragSelected = Constants.VBVMI_SECTIONS.STUDIES;
	public static String lastFragInStudies = Constants.VBVMI_SECTIONS.STUDIES;
	public static String lastFragInArticles = Constants.VBVMI_SECTIONS.ARTICLES;
	public static String lastFragInAnswers = Constants.VBVMI_SECTIONS.ANSWERS;
	public static String lastFragInVideoChannels = Constants.VBVMI_SECTIONS.VIDEO_CHANNELS;

	public static String locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainCtx = this;

		locale = Utilities.getLocale(this);

		viewMiniPlayer = (RelativeLayout) findViewById(R.id.view_mini_player);
		slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		slidingLayout.setDragView(viewMiniPlayer.findViewById(R.id.ll_player_mini_title));

		lastFragSelected = Constants.VBVMI_SECTIONS.STUDIES;
		lastFragInStudies = Constants.VBVMI_SECTIONS.STUDIES;
		lastFragInArticles = Constants.VBVMI_SECTIONS.ARTICLES;
		lastFragInAnswers = Constants.VBVMI_SECTIONS.ANSWERS;
		lastFragInVideoChannels = Constants.VBVMI_SECTIONS.VIDEO_CHANNELS;

		fragmentStudies = StudiesFragment.newInstance(0);
		fragmentArticles = ArticlesFragment.newInstance(0);
		fragmentAnswers = AnswersFragment.newInstance(0);
		fragmentLessons = LessonsFragment.newInstance(0);
		fragmentArticleDetails = ArticleDetailsFragment.newInstance();
		fragmentAnswerDetails = AnswerDetailsFragment.newInstance();
		fragmentVideoChannels = VideoChannelsFragment.newInstance(0);
		fragmentVideos = VideosFragment.newInstance(0);

		BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
		bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelected(@IdRes int tabId) {
				if (tabId == R.id.tab_studies) {
					if (lastFragInStudies.equals(Constants.VBVMI_SECTIONS.STUDIES))
						displayFragmentStudies();
					else
						displayFragmentLessons();
				}
				if (tabId == R.id.tab_articles) {
					if (lastFragInArticles.equals(Constants.VBVMI_SECTIONS.ARTICLES))
						displayFragmentArticles();
					else
						displayFragmentArticleDetails();
				}
				if (tabId == R.id.tab_answers) {
					if (lastFragInAnswers.equals(Constants.VBVMI_SECTIONS.ANSWERS))
						displayFragmentAnswers();
					else
						displayFragmentAnswerDetails();
				}
				if (tabId == R.id.tab_videos) {
					if (lastFragInVideoChannels.equals(Constants.VBVMI_SECTIONS.VIDEO_CHANNELS))
						displayFragmentVideoChannels();
					else
						displayFragmentVideos();
				}
			}
		});
	}

	protected void displayFragmentStudies() {
		lastFragSelected = Constants.VBVMI_SECTIONS.STUDIES;
		lastFragInStudies = Constants.VBVMI_SECTIONS.STUDIES;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentStudies.isAdded()) { // if the fragment is already in container
			ft.show(fragmentStudies);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentStudies, Constants.VBVMI_SECTIONS.STUDIES);
		}
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(Constants.VBVMI_SECTIONS.STUDIES);
		actionBar.show();

		ft.commit();
	}

	protected void displayFragmentArticles() {
		lastFragSelected = Constants.VBVMI_SECTIONS.ARTICLES;
		lastFragInArticles = Constants.VBVMI_SECTIONS.ARTICLES;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentArticles.isAdded()) { // if the fragment is already in container
			ft.show(fragmentArticles);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentArticles, Constants.VBVMI_SECTIONS.ARTICLES);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		Utilities.setActionBar(this, Constants.VBVMI_SECTIONS.ARTICLES, false);

		ft.commit();
	}

	protected void displayFragmentAnswers() {
		lastFragSelected = Constants.VBVMI_SECTIONS.ANSWERS;
		lastFragInAnswers = Constants.VBVMI_SECTIONS.ANSWERS;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentAnswers.isAdded()) { // check the fragment is already in container
			ft.show(fragmentAnswers);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentAnswers, Constants.VBVMI_SECTIONS.ANSWERS);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		Utilities.setActionBar(this, Constants.VBVMI_SECTIONS.ANSWERS, false);

		ft.commit();
	}

	protected void displayFragmentVideoChannels() {
		lastFragSelected = Constants.VBVMI_SECTIONS.VIDEO_CHANNELS;
		lastFragInVideoChannels = Constants.VBVMI_SECTIONS.VIDEO_CHANNELS;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentVideoChannels.isAdded()) { // if the fragment is already in container
			ft.show(fragmentVideoChannels);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentVideoChannels, Constants.VBVMI_SECTIONS.VIDEOS);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(Constants.VBVMI_SECTIONS.VIDEOS);
		actionBar.show();

		ft.commit();
	}

	protected void displayFragmentLessons() {
		lastFragSelected = Constants.VBVMI_SECTIONS.LESSONS;
		lastFragInStudies = Constants.VBVMI_SECTIONS.LESSONS;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentLessons.isAdded()) { // if the fragment is already in container
			ft.show(fragmentLessons);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentLessons, Constants.VBVMI_SECTIONS.LESSONS);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		Utilities.setActionBar(this, mStudy.getTitle(), true);

		Bundle bundle = new Bundle();
		bundle.putParcelable("study", mStudy);
		fragmentLessons.getArguments().putAll(bundle);

		ft.detach(fragmentLessons);
		ft.attach(fragmentLessons);
		ft.commit();
	}

	protected void displayFragmentArticleDetails() {
		lastFragSelected = Constants.VBVMI_SECTIONS.ARTICLE_DETAILS;
		lastFragInArticles = Constants.VBVMI_SECTIONS.ARTICLE_DETAILS;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentArticleDetails.isAdded()) { // if the fragment is already in container
			ft.show(fragmentArticleDetails);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentArticleDetails, Constants.VBVMI_SECTIONS.ARTICLE_DETAILS);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }

		Utilities.setActionBar(this, mArticle.getTitle(), true);

		Bundle bundle = new Bundle();
		bundle.putParcelable("article", mArticle);
		fragmentArticleDetails.getArguments().putAll(bundle);

		ft.detach(fragmentArticleDetails);
		ft.attach(fragmentArticleDetails);

		ft.commit();
	}

	protected void displayFragmentAnswerDetails() {
		lastFragSelected = Constants.VBVMI_SECTIONS.ANSWER_DETAILS;
		lastFragInAnswers = Constants.VBVMI_SECTIONS.ANSWER_DETAILS;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentAnswerDetails.isAdded()) { // if the fragment is already in container
			ft.show(fragmentAnswerDetails);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentAnswerDetails, Constants.VBVMI_SECTIONS.ANSWER_DETAILS);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }

		Utilities.setActionBar(this, mAnswer.getTitle(), true);

		Bundle bundle = new Bundle();
		bundle.putParcelable("answer", mAnswer);
		fragmentAnswerDetails.getArguments().putAll(bundle);

		ft.detach(fragmentAnswerDetails);
		ft.attach(fragmentAnswerDetails);

		ft.commit();
	}

	protected void displayFragmentVideos() {
		lastFragSelected = Constants.VBVMI_SECTIONS.VIDEOS;
		lastFragInVideoChannels = Constants.VBVMI_SECTIONS.VIDEOS;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentVideos.isAdded()) { // if the fragment is already in container
			ft.show(fragmentVideos);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentVideos, Constants.VBVMI_SECTIONS.VIDEOS);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }

		Utilities.setActionBar(this, mVideoChannel.getTitle(), true);

		Bundle bundle = new Bundle();
		bundle.putParcelable("videoChannel", mVideoChannel);
		fragmentVideos.getArguments().putAll(bundle);

		ft.detach(fragmentVideos);
		ft.attach(fragmentVideos);

		ft.commit();
	}

	@Override
	public void onStudyItemSelected(Study study) {
		mStudy = study;
		displayFragmentLessons();
	}

	@Override
	public void onArticleSelected(Article article) {
		mArticle = article;
		displayFragmentArticleDetails();
	}

	@Override
	public void onAnswerSelected(Answer answer) {
		mAnswer = answer;
		displayFragmentAnswerDetails();
	}

	@Override
	public void onVideoChannelSelected(VideoChannel videoChannel) {
		mVideoChannel = videoChannel;
		displayFragmentVideos();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_fragment_studies, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			case android.R.id.home:
				switch (lastFragSelected) {
					case Constants.VBVMI_SECTIONS.LESSONS: displayFragmentStudies();
						break;
					case Constants.VBVMI_SECTIONS.ARTICLE_DETAILS: displayFragmentArticles();
						break;
					case Constants.VBVMI_SECTIONS.ANSWER_DETAILS: displayFragmentAnswers();
						break;
					case Constants.VBVMI_SECTIONS.VIDEOS: displayFragmentVideoChannels();
						break;
					default:
						break;
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStart() {
		if (AudioPlayerHelper.playerInstance != null)
			AudioPlayerHelper.playerInstance.registerProgressReceiver();
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (AudioPlayerHelper.playerInstance != null)
			AudioPlayerHelper.playerInstance.unregisterProgressReceiver();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
//		Intent i = new Intent(Intent.ACTION_MAIN);
//		i.addCategory(Intent.CATEGORY_HOME);
//		startActivity(i);
		moveTaskToBack(true);
	}
}
