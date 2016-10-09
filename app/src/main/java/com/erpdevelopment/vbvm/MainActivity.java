package com.erpdevelopment.vbvm;

import java.util.ArrayList;

import com.erpdevelopment.vbvm.adapter.NavDrawerListAdapter;
import com.erpdevelopment.vbvm.db.DatabaseManager;
import com.erpdevelopment.vbvm.db.VbvmDatabaseOpenHelper;
import com.erpdevelopment.vbvm.fragment.AnswersFragment;
import com.erpdevelopment.vbvm.fragment.ArticlesFragment;
import com.erpdevelopment.vbvm.fragment.LessonsFragment;
import com.erpdevelopment.vbvm.fragment.StudiesFragment;
import com.erpdevelopment.vbvm.fragment.VideosFragment;
import com.erpdevelopment.vbvm.model.NavDrawerItem;
import com.erpdevelopment.vbvm.model.Study;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IdRes;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements StudiesFragment.OnItemSelectedListener {

	public static final String DIRECTORY_IMAGES = "vbvm";
	public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String SETTING_IMAGES_DIRECTORY_NAME = "vbvmi";
	public static final String SETTING_DATABASE_FILE_PATH = "db-path";

	public static SQLiteDatabase db;
	public static Context mainCtx;
	public static SharedPreferences settings;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

	private ViewPager viewPagerMain;
//	private CustomPagerAdapter mAdapter;
	private ActionBar actionBar;

	private StudiesFragment fragmentStudies;
	private ArticlesFragment fragmentArticles;
	private AnswersFragment fragmentAnswers;
	private VideosFragment fragmentVideos;
	private LessonsFragment fragmentLessons;

//	private FragmentTransaction ft;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

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

		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(false); // disable the button
			actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
			actionBar.setDisplayShowHomeEnabled(false); // remove the icon
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.show();
		}

//		viewPagerMain = (ViewPager) findViewById(R.id.viewPagerMain);
//		mAdapter = new CustomPagerAdapter(getSupportFragmentManager());
//		viewPagerMain.setAdapter(mAdapter);

//		initNavigationDrawer(savedInstanceState);
		//initUI();
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		FragmentTransaction ft = fragmentManager.beginTransaction();
//		ArticlesFragment fArticles = new ArticlesFragment();
//		AnswersFragment fAnswers = new AnswersFragment();
//		VideosFragment fVideos = new VideosFragment();
//		StudiesFragment fStudies = new StudiesFragment();

//		ft.add(R.id.frame_container, fArticles);
//		ft.hide(fArticles);
//		ft.add(R.id.frame_container, fAnswers);
//		ft.hide(fAnswers);
//		ft.add(R.id.frame_container, fVideos);
//		ft.hide(fVideos);
//		ft.add(R.id.frame_container, fStudies);
//		ft.show(fStudies);
//		ft.commit();

		//currentFragment = firstTabFragment;

//		ft = getSupportFragmentManager().beginTransaction();

//		if (savedInstanceState == null) {
		fragmentStudies = StudiesFragment.newInstance(0);
		fragmentArticles = ArticlesFragment.newInstance(0);
		fragmentAnswers = AnswersFragment.newInstance(0);
		fragmentVideos = VideosFragment.newInstance(0);
		fragmentLessons = LessonsFragment.newInstance(0);

		BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
		bottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.blue));
		bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelected(@IdRes int tabId) {

				if (tabId == R.id.tab_studies) {
					displayFragmentStudies();
				}
				if (tabId == R.id.tab_articles) {
					displayFragmentArticles();
				}
				if (tabId == R.id.tab_answers) {
					displayFragmentAnswers();
				}
				if (tabId == R.id.tab_videos) {
					displayFragmentVideos();
				}
			}
		});

	}

	// Replace the switch method
	protected void displayFragmentStudies() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentStudies.isAdded()) { // if the fragment is already in container
			ft.show(fragmentStudies);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentStudies, "Studies");
		}
		// Hide fragment B
		if (fragmentArticles.isAdded()) { ft.hide(fragmentArticles); }
		// Hide fragment C
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		// Commit changes
		ft.commit();
	}

	protected void displayFragmentArticles() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (fragmentArticles.isAdded()) { // if the fragment is already in container
			ft.show(fragmentArticles);
		} else { // fragment needs to be added to frame container
			ft.add(R.id.frame_container, fragmentArticles, "Articles");
		}
		if (fragmentStudies.isAdded()) { ft.hide(fragmentStudies); }
		if (fragmentAnswers.isAdded()) { ft.hide(fragmentAnswers); }
		if (fragmentVideos.isAdded()) { ft.hide(fragmentVideos); }
		if (fragmentLessons.isAdded()) { ft.hide(fragmentLessons); }
		ft.commit();
	}

	protected void displayFragmentAnswers() {
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
		ft.commit();
	}

	protected void displayFragmentVideos() {
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
		ft.commit();
	}
	@Override
	protected void onStart() {
		super.onStart();
        checkUserFirstVisit();
	}


	@Override
	public void onStudyItemSelected(Study study) {
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
//		Bundle bundle = new Bundle();
//		bundle.putParcelable("study", study);
//		fragmentLessons.setArguments(bundle);

		Bundle bundle = new Bundle();
		bundle.putParcelable("study", study);
		fragmentLessons.getArguments().putAll(bundle);

		ft.detach(fragmentLessons);
		ft.attach(fragmentLessons);

		ft.commit();
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

	//	private void initNavigationDrawer(Bundle savedInstanceState) {
//		mTitle = mDrawerTitle = getTitle();
//
//        // load slide menu items
//        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
//
//        // nav drawer icons from resources
//        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
//
//        navDrawerItems = new ArrayList<NavDrawerItem>();
//
//        // adding nav drawer items to array
//        // Home
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
////        // Find People
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
////        // Photos
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
////        // Communities, Will add a counter here
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
////        // Pages
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
////        // What's hot, We  will add a counter here
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
//
//     // adding nav drawer items to array
////        // My Home
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
////        // Browse Library
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(, -1)));
////        // Events
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
////        // Donate, Will add a counter here
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
////        // Connect
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
////        // About, We  will add a counter here
////        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
//
//     // Recycle the typed array
//        navMenuIcons.recycle();
//
////        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//        // setting the nav drawer list adapter
//        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
//        mDrawerList.setAdapter(adapter);
//
//        final ActionBar actionBar = getActionBar();
//
//        // enabling action bar app icon and behaving it as toggle button
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//
////        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
////        actionBar.setBackgroundDrawable();
////        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));
//
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//				R.drawable.ic_drawer, //nav menu toggle icon
//				R.string.app_name, // nav drawer open - description for accessibility
//				R.string.app_name // nav drawer close - description for accessibility
//		) {
//			public void onDrawerClosed(View view) {
//				getActionBar().setTitle(mTitle);
//				// calling onPrepareOptionsMenu() to show action bar icons
//				invalidateOptionsMenu();
////				setImageAppCenter();
//			}
//
//			public void onDrawerOpened(View drawerView) {
//				getActionBar().setTitle(mDrawerTitle);
//				// calling onPrepareOptionsMenu() to hide action bar icons
//				invalidateOptionsMenu();
//			}
//		};
////        mDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes)
////        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
////				R.string.app_name, // nav drawer open - description for accessibility
////				R.string.app_name // nav drawer close - description for accessibility
////		) {
////			public void onDrawerClosed(View view) {
////				getActionBar().setTitle(mTitle);
////				// calling onPrepareOptionsMenu() to show action bar icons
////				invalidateOptionsMenu();
//////				setImageAppCenter();
////			}
////
////			public void onDrawerOpened(View drawerView) {
////				getActionBar().setTitle(mDrawerTitle);
////				// calling onPrepareOptionsMenu() to hide action bar icons
////				invalidateOptionsMenu();
////			}
////		};
//
//
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//		if (savedInstanceState == null) {
//			// on first time display view for first nav item
//			displayView(0);
//		}
//
//	}


	/**
	 * Slide menu item click listener
	 * */
//	private class SlideMenuClickListener implements
//			ListView.OnItemClickListener {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			// display view for selected nav drawer item
//			displayView(position);
//		}
//	}

//	private class DrawerItemClickListener implements OnItemClickListener {
//	    @Override
//	    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
////	        drawerLayout.closeDrawer(drawerList);
//	    	// update selected item and title, then close the drawer
//			mDrawerList.setItemChecked(position, true);
//			mDrawerList.setSelection(position);
//			setTitle(navMenuTitles[position]);
////			mDrawerLayout.closeDrawer(mDrawerList);
//			displayView(position);
//	        new Handler().postDelayed(new Runnable() {
//	            @Override
//	            public void run() {
////	                switchFragments(position); // your fragment transactions go here
////	                displayView(position);
//	            	mDrawerLayout.closeDrawer(mDrawerList);
//	            }
//	        }, 300);
//	    }
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		System.out.println("creating menu...");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		// if nav drawer is opened, hide the action items
//		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//		//menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
//		return super.onPrepareOptionsMenu(menu);
//	}
//
//	/**
//	 * Diplaying fragment view for selected nav drawer list item
//	 * */
//	private void displayView(int position) {
//		// update the main content by replacing fragments
//		Fragment fragment = null;
//		switch (position) {
//		case 0:
//			//fragment = new HomeFragment();
//			break;
//		case 1:
//			fragment = new LibraryFragment();
//			break;
//		case 2:
//			fragment = new EventsFragment();
//			break;
//		case 3:
////			fragment = new DonateFragment();
//			startActivity(new Intent(this, DonateActivity.class));
//			break;
//		case 4:
//			fragment = new ContactFragment();
//			break;
//		case 5:
//			fragment = new AboutFragment();
////			startActivity(new Intent(this, AboutActivity.class));
//			break;
//
//		default:
//			break;
//		}
//
//		if (fragment != null) {
//			FragmentManager fragmentManager = getFragmentManager();
//			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
//
////			// update selected item and title, then close the drawer
////			mDrawerList.setItemChecked(position, true);
////			mDrawerList.setSelection(position);
////			setTitle(navMenuTitles[position]);
////			mDrawerLayout.closeDrawer(mDrawerList);
//		} else {
//			// error in creating fragment
//			Log.e("MainActivity", "Error in creating fragment");
//		}
//	}
//
//	@Override
//	public void setTitle(CharSequence title) {
//		mTitle = title;
//		getActionBar().setTitle(mTitle);
//	}
//
//	/**
//	 * When using the ActionBarDrawerToggle, you must call it during
//	 * onPostCreate() and onConfigurationChanged()...
//	 */
//
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		// Sync the toggle state after onRestoreInstanceState has occurred.
//		//mDrawerToggle.syncState();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		// Pass any configuration change to the drawer toggls
//		mDrawerToggle.onConfigurationChanged(newConfig);
//	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	private void setImageAppCenter() {
//		Dialog overlayInfo = new Dialog(MainActivity.this);
//        // Making sure there's no title.
//        overlayInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // Making dialog content transparent.
//        overlayInfo.getWindow().setBackgroundDrawable(
//                new ColorDrawable(Color.TRANSPARENT));
//        // Removing window dim normally visible when dialog are shown.
//        overlayInfo.getWindow().clearFlags(
//                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        // Setting position of content, relative to window.
//        WindowManager.LayoutParams params = overlayInfo.getWindow().getAttributes();
//        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
////        params.x = 100;
//        params.y = 30;
//        // If user taps anywhere on the screen, dialog will be cancelled.
//        overlayInfo.setCancelable(false);
//        // Setting the content using prepared XML layout file.
//        overlayInfo.setContentView(R.layout.main_overlay_image);
//        overlayInfo.show();
//	}

}
