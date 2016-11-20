package com.erpdevelopment.vbvm.activity;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.db.DatabaseManager;
import com.erpdevelopment.vbvm.db.VbvmDatabaseOpenHelper;
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
import com.erpdevelopment.vbvm.utils.Utilities;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements StudiesFragment.OnStudyItemSelectedListener,
		ArticlesFragment.OnArticleSelectedListener,
		AnswersFragment.OnAnswerSelectedListener,
		VideoChannelsFragment.OnVideoChannelSelectedListener {

	public static final String DIRECTORY_IMAGES = "vbvm";
	public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String SETTING_IMAGES_DIRECTORY_NAME = "vbvmi";
	public static final String SETTING_DATABASE_FILE_PATH = "db-path";

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
	private ImageButton btnPlayMini;

	private TextView tvPlayerLessonTitleMini;
	private TextView tvPlayerLessonDescriptionMini;

	private Study mStudy;
	private Article mArticle;
	private Answer mAnswer;
	private VideoChannel mVideoChannel;

	public static String lastFragSelected = "Studies";
	public static String lastFragInStudies = "Studies";
	public static String lastFragInArticles = "Articles";
	public static String lastFragInAnswers = "Answers";
	public static String lastFragInVideoChannels = "VideoChannels";

	public static String locale;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainCtx = this;
        settings = getPreferences(Activity.MODE_PRIVATE);
        VbvmDatabaseOpenHelper mDbHelper = VbvmDatabaseOpenHelper.getInstance(mainCtx);
        DatabaseManager.initializeInstance(mDbHelper);
        db = DatabaseManager.getInstance().openDatabase();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			locale = getResources().getConfiguration().getLocales().get(0).getDisplayName();
		} else {
			locale = getResources().getConfiguration().locale.getDisplayName();
		}

		viewMiniPlayer = (RelativeLayout) findViewById(R.id.view_mini_player);
		slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		slidingLayout.setDragView(viewMiniPlayer.findViewById(R.id.ll_player_mini_title));
		tvPlayerLessonTitleMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_title_mini);
		tvPlayerLessonDescriptionMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_description_mini);

		lastFragSelected = "Studies";
		lastFragInStudies = "Studies";
		lastFragInArticles = "Articles";
		lastFragInAnswers = "Answers";
		lastFragInVideoChannels = "VideoChannels";

		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(false); // disable the button
			actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
			actionBar.setDisplayShowHomeEnabled(false); // remove the icon
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.show();
		}

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
					System.out.println("MainActivity.onTabSelected " + lastFragInStudies);
					if (lastFragInStudies.equals("Studies"))
						displayFragmentStudies();
					else
						displayFragmentLessons();
				}
				if (tabId == R.id.tab_articles) {
					if (lastFragInArticles.equals("Articles"))
						displayFragmentArticles();
					else
						displayFragmentArticleDetails();
				}
				if (tabId == R.id.tab_answers) {
					if (lastFragInAnswers.equals("Answers"))
						displayFragmentAnswers();
					else
						displayFragmentAnswerDetails();
				}
				if (tabId == R.id.tab_videos) {
					if (lastFragInVideoChannels.equals("VideoChannels"))
						displayFragmentVideoChannels();
					else
						displayFragmentVideos();
				}
			}
		});

//		if (AudioPlayerService.created) {
//			if (AudioPlayerService.stopped)
//				btnPlayMini.setImageResource(R.drawable.icon_mini_play);
//			else
//				btnPlayMini.setImageResource(R.drawable.icon_media_pause_16);
//		}
	}

	// Replace the switch method
	protected void displayFragmentStudies() {
		lastFragSelected = "Studies";
		lastFragInStudies = "Studies";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentStudies.isAdded()) { // if the fragment is already in container
			ft.show(fragmentStudies);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentStudies, "Studies");
		}
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Studies");

		// Commit changes
		ft.commit();
	}

	protected void displayFragmentArticles() {
		lastFragSelected = "Articles";
		lastFragInArticles = "Articles";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentArticles.isAdded()) { // if the fragment is already in container
			ft.show(fragmentArticles);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentArticles, "Articles");
//			ft.addToBackStack(null);
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setLogo(Utilities.getTextViewAsDrawable(this,"Articles"));
		actionBar.setTitle("Articles");
		actionBar.show();

		ft.commit();
	}

	protected void displayFragmentAnswers() {
		lastFragSelected = "Answers";
		lastFragInAnswers = "Answers";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentAnswers.isAdded()) { // if the fragment is already in container
			ft.show(fragmentAnswers);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentAnswers, "Answers");
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Answers");
		actionBar.show();

		ft.commit();
	}

	protected void displayFragmentVideoChannels() {
		lastFragSelected = "VideoChannels";
		lastFragInVideoChannels = "VideoChannels";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentVideoChannels.isAdded()) { // if the fragment is already in container
			ft.show(fragmentVideoChannels);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentVideoChannels, "Videos");
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
		actionBar.setTitle("Videos");
		actionBar.show();

		ft.commit();
	}

	protected void displayFragmentLessons() {
		lastFragSelected = "Lessons";
		lastFragInStudies = "Lessons";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentLessons.isAdded()) { // if the fragment is already in container
			ft.show(fragmentLessons);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentLessons, "Lessons");
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		Drawable textHomeUp = Utilities.getTextViewAsDrawable(this, "Studies");
		actionBar.setLogo(textHomeUp);
		actionBar.show();

		Bundle bundle = new Bundle();
		bundle.putParcelable("study", mStudy);
		fragmentLessons.getArguments().putAll(bundle);

		ft.detach(fragmentLessons);
		ft.attach(fragmentLessons);
		ft.commit();
	}

	protected void displayFragmentArticleDetails() {
		lastFragSelected = "ArticleDetails";
		lastFragInArticles = "ArticleDetails";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentArticleDetails.isAdded()) { // if the fragment is already in container
			ft.show(fragmentArticleDetails);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentArticleDetails, "ArticleDetails");
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }

		Utilities.setActionBar(this, mArticle.getTitle());

		Bundle bundle = new Bundle();
		bundle.putParcelable("article", mArticle);
		fragmentArticleDetails.getArguments().putAll(bundle);

		ft.detach(fragmentArticleDetails);
		ft.attach(fragmentArticleDetails);

		ft.commit();
	}

	protected void displayFragmentAnswerDetails() {
		lastFragSelected = "AnswerDetails";
		lastFragInAnswers = "AnswerDetails";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentAnswerDetails.isAdded()) { // if the fragment is already in container
			ft.show(fragmentAnswerDetails);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentAnswerDetails, "AnswerDetails");
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }

		Utilities.setActionBar(this, mAnswer.getTitle());

		Bundle bundle = new Bundle();
		bundle.putParcelable("answer", mAnswer);
		fragmentAnswerDetails.getArguments().putAll(bundle);

		ft.detach(fragmentAnswerDetails);
		ft.attach(fragmentAnswerDetails);

		ft.commit();
	}

	protected void displayFragmentVideos() {
		lastFragSelected = "Videos";
		lastFragInVideoChannels = "Videos";
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentVideos.isAdded()) { // if the fragment is already in container
			ft.show(fragmentVideos);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentVideos, "Videos");
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		if (fragmentVideoChannels.isAdded()) { ft.hide(fragmentVideoChannels); }
		if (fragmentArticleDetails.isAdded()) { ft.hide(fragmentArticleDetails); }
		if (fragmentAnswerDetails.isAdded()) { ft.hide(fragmentAnswerDetails); }

		Utilities.setActionBar(this, mVideoChannel.getTitle());

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
	protected void onStart() {
		super.onStart();
//		if (AudioPlayerService.created) {
//			viewMiniPlayer.setVisibility(View.VISIBLE);
//			slidingLayout.setShadowHeight(4);
//			slidingLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.sliding_panel_height));
//			tvPlayerLessonTitleMini.setText(settings.getString("lessonTitle",""));
//			tvPlayerLessonDescriptionMini.setText(settings.getString("lessonDescription",""));
//			System.out.println("MainActivity.onStart 1");
//		} else {
//			viewMiniPlayer.setVisibility(View.GONE);
//			slidingLayout.setShadowHeight(0);
//			slidingLayout.setPanelHeight(0);
//			System.out.println("MainActivity.onStart 2");
//		}
//		checkUserFirstVisit();
	}



	//    /**
//     * CHECK LAST UPDATE TIME
//     */
    private boolean checkUserFirstVisit(){
        long lastUpdateTime = MainActivity.settings.getLong("lastUpdateKey", 0L);
        long timeElapsed = System.currentTimeMillis() - lastUpdateTime;
        // YOUR UPDATE FREQUENCY HERE
//			final long UPDATE_FREQ = 1000 * 60 * 60 * 12;
        final long UPDATE_FREQ = 1000 * 60 * 60 * 1; //Every 1 hour
        SharedPreferences.Editor e = MainActivity.settings.edit();
        if (timeElapsed > UPDATE_FREQ) {
            e.putBoolean("studiesInDB", false);
            e.putBoolean("lessonsInDB", false);
            e.putBoolean("articlesInDB", false);
            e.putBoolean("postsInDB", false);
            e.putBoolean("eventsInDB", false);
            e.putBoolean("videosInDB", false);
            Log.i("MainActivity info", "Update DB with data from Webservice");
        }
        // STORE LATEST UPDATE TIME
        e.putLong("lastUpdateKey", System.currentTimeMillis());
        e.commit();
        return false;
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
				System.out.println("MainActivity.onOptionsItemSelected...");
				switch (lastFragSelected) {
					case "Lessons": displayFragmentStudies();
						break;
					case "ArticleDetails": displayFragmentArticles();
						break;
					case "AnswerDetails": displayFragmentAnswers();
						break;
					case "Videos": displayFragmentVideoChannels();
						break;
					default:
						break;
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

//	private void handleBackEvent() {
//		switch (lastFragSelected) {
//			case "Studies": displayFragmentStudies();
//				break;
//			case "Articles": displayFragmentArticles();
//				break;
//			case "Answers": displayFragmentAnswers();
//				break;
//			case "VideoChannels": displayFragmentVideoChannels();
//				break;
//			case "Lessons": displayFragmentStudies();
//				break;
//			case "ArticleDetails": displayFragmentArticles();
//				break;
//			case "AnswerDetails": displayFragmentAnswers();
//				break;
//			case "Videos": displayFragmentVideoChannels();
//				break;
//			default:
//				break;
//		}
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		System.out.println("MainActivity.onConfigurationChanged");
	}

	@Override
	protected void onStop() {
		System.out.println("MainActivity.onStop");
		AudioPlayerHelper helper = new AudioPlayerHelper();
		helper.unregisterReceiverProgress(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		AudioPlayerHelper helper = new AudioPlayerHelper();
//		helper.unregisterReceiverProgress(this);
//		System.out.println("MainActivity.onDestroy");
	}

}
