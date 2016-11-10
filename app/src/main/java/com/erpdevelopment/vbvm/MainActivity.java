package com.erpdevelopment.vbvm;

import java.util.ArrayList;

import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.adapter.NavDrawerListAdapter;
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
import com.erpdevelopment.vbvm.model.NavDrawerItem;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IdRes;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements StudiesFragment.OnStudyItemSelectedListener,
		ArticlesFragment.OnArticleSelectedListener,
		AnswersFragment.OnAnswerSelectedListener,
		VideoChannelsFragment.OnVideoChannelSelectedListener {

	public static final String DIRECTORY_IMAGES = "vbvm";
	public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String SETTING_IMAGES_DIRECTORY_NAME = "vbvmi";
	public static final String SETTING_DATABASE_FILE_PATH = "db-path";

	public static final String LAST_FRAGMENT_STUDIES = "last_fragment_selected";

	public static SQLiteDatabase db;
	public static Context mainCtx;
	public static SharedPreferences settings;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

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

	private TextView tvPlayerLessonTitleMini;
	private TextView tvPlayerLessonDescriptionMini;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

	private Study mStudy;
	private Article mArticle;
	private Answer mAnswer;
	private VideoChannel mVideoChannel;
	private VideoVbvm mVideo;

	public static String lastFragmentSelected = "Studies";
	public static String lastFragInStudies = "Studies";
	public static String lastFragInArticles = "Articles";
	public static String lastFragInAnswers = "Answers";
	public static String lastFragInVideoChannels = "VideoChannels";

//	private static final int PIXELS_PANEL_HEIGHT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);
		mainCtx = this;
        settings = getPreferences(Activity.MODE_PRIVATE);
        VbvmDatabaseOpenHelper mDbHelper = VbvmDatabaseOpenHelper.getInstance(mainCtx);
        DatabaseManager.initializeInstance(mDbHelper);
        db = DatabaseManager.getInstance().openDatabase();

		viewMiniPlayer = (RelativeLayout) findViewById(R.id.view_mini_player);
		slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		tvPlayerLessonTitleMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_title_mini);
		tvPlayerLessonDescriptionMini = (TextView) viewMiniPlayer.findViewById(R.id.tv_player_lesson_description_mini);

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
//		bottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.blue));
		bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelected(@IdRes int tabId) {
				if (tabId == R.id.tab_studies) {
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

	}

	// Replace the switch method
	protected void displayFragmentStudies() {
		lastFragmentSelected = "Studies";
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

//		SharedPreferences.Editor e = MainActivity.settings.edit();
//		e.putString(LAST_FRAGMENT_STUDIES, "Studies");
//		e.commit();

		// Commit changes
		ft.commit();
	}

	protected void displayFragmentArticles() {
		lastFragmentSelected = "Articles";
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

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setLogo(Utilities.getTextViewAsDrawable(this,"Articles"));
		actionBar.setTitle("Articles");

		ft.commit();
	}

	protected void displayFragmentAnswers() {
		lastFragmentSelected = "Answers";
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

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Answers");

		ft.commit();
	}

	protected void displayFragmentVideoChannels() {
		lastFragmentSelected = "VideoChannels";
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

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Videos");

		ft.commit();
	}

	protected void displayFragmentLessons() {
		lastFragmentSelected = "Lessons";
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

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setTitle(mStudy.getTitle());
		Drawable textHomeUp = Utilities.getTextViewAsDrawable(this, "Studies");
		actionBar.setLogo(textHomeUp);

//		SharedPreferences.Editor e = MainActivity.settings.edit();
//		e.putString(LAST_FRAGMENT_STUDIES, "Lessons");
//		e.commit();

		Bundle bundle = new Bundle();
		bundle.putParcelable("study", mStudy);
		fragmentLessons.getArguments().putAll(bundle);

		ft.detach(fragmentLessons);
		ft.attach(fragmentLessons);

		ft.commit();
	}

	protected void displayFragmentArticleDetails() {
		lastFragmentSelected = "ArticleDetails";
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

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mArticle.getTitle());
		Drawable textHomeUp = Utilities.getTextViewAsDrawable(this, "Articles");
		actionBar.setLogo(textHomeUp);

		Bundle bundle = new Bundle();
		bundle.putParcelable("article", mArticle);
		fragmentArticleDetails.getArguments().putAll(bundle);

		ft.detach(fragmentArticleDetails);
		ft.attach(fragmentArticleDetails);

		ft.commit();
	}

	protected void displayFragmentAnswerDetails() {
		lastFragmentSelected = "AnswerDetails";
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

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mAnswer.getTitle());
		Drawable textHomeUp = Utilities.getTextViewAsDrawable(this, "Answers");
		actionBar.setLogo(textHomeUp);

		Bundle bundle = new Bundle();
		bundle.putParcelable("answer", mAnswer);
		fragmentAnswerDetails.getArguments().putAll(bundle);

		ft.detach(fragmentAnswerDetails);
		ft.attach(fragmentAnswerDetails);

		ft.commit();
	}

	protected void displayFragmentVideos() {
		lastFragmentSelected = "Videos";
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

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mVideoChannel.getTitle());
		Drawable textHomeUp = Utilities.getTextViewAsDrawable(this, "Videos");
		actionBar.setLogo(textHomeUp);

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
		if (AudioPlayerService.created) {
			viewMiniPlayer.setVisibility(View.VISIBLE);
			slidingLayout.setShadowHeight(4);
			slidingLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.sliding_panel_height));
			tvPlayerLessonTitleMini.setText(settings.getString("lessonTitle",""));
			tvPlayerLessonDescriptionMini.setText(settings.getString("lessonDescription",""));
			System.out.println("MainActivity.onStart 1");
		} else {
			viewMiniPlayer.setVisibility(View.GONE);
			slidingLayout.setShadowHeight(0);
			slidingLayout.setPanelHeight(0);
			System.out.println("MainActivity.onStart 2");
		}
		checkUserFirstVisit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioPlayerHelper helper = new AudioPlayerHelper();
		helper.unregisterReceiverProgress(this);
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
				switch (lastFragmentSelected) {
					case "Lessons": displayFragmentStudies();
						break;
					case "ArticleDetails": displayFragmentAnswers();
						break;
					case "AnswerDetails": displayFragmentArticles();
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

}
