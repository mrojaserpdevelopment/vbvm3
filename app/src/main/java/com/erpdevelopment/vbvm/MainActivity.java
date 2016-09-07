package com.erpdevelopment.vbvm;

import java.util.ArrayList;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.erpdevelopment.vbvm.activity.DonateActivity;
import com.erpdevelopment.vbvm.adapter.CustomPagerAdapter;
import com.erpdevelopment.vbvm.adapter.NavDrawerListAdapter;
import com.erpdevelopment.vbvm.db.DatabaseManager;
import com.erpdevelopment.vbvm.db.VbvmDatabaseOpenHelper;
import com.erpdevelopment.vbvm.fragment.AboutFragment;
import com.erpdevelopment.vbvm.fragment.ContactFragment;
import com.erpdevelopment.vbvm.fragment.EventsFragment;
import com.erpdevelopment.vbvm.fragment.LibraryFragment;
import com.erpdevelopment.vbvm.model.NavDrawerItem;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity {

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
	private CustomPagerAdapter mAdapter;
	private ActionBar actionBar;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

	// UI
//	private AHBottomNavigationViewPager viewPager;
	private AHBottomNavigation bottomNavigation;
//	private AHBottomNavigationAdapter navigationAdapter;
//	private FloatingActionButton floatingActionButton;
	private boolean useMenuResource = true;
	private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_main2);

		mainCtx = this;
        settings = getPreferences(Activity.MODE_PRIVATE);
        VbvmDatabaseOpenHelper mDbHelper = VbvmDatabaseOpenHelper.getInstance(mainCtx);
        DatabaseManager.initializeInstance(mDbHelper);
        db = DatabaseManager.getInstance().openDatabase();

		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);

		viewPagerMain = (ViewPager) findViewById(R.id.viewPagerMain);
		mAdapter = new CustomPagerAdapter(getSupportFragmentManager());
		viewPagerMain.setAdapter(mAdapter);

//		initNavigationDrawer(savedInstanceState);
		initUI();
	}

	private void initUI() {

		bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
//		viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);
//		floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

//		if (useMenuResource) {
//			tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
//			navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.main);
//			navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
//		} else {
//			AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_apps_black_24dp, R.color.color_tab_1);
//			AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_maps_local_bar, R.color.color_tab_2);
//			AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_maps_local_restaurant, R.color.color_tab_3);

			AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.bottom_menu_studies, R.drawable.bottom_bar_icon_library, R.color.colorBottomNavigationInactive);
			AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.bottom_menu_articles, R.drawable.bottom_bar_icon_library, R.color.colorBottomNavigationInactive);
			AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.bottom_menu_answers, R.drawable.bottom_bar_icon_library, R.color.colorBottomNavigationInactive);
			AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.bottom_menu_videos, R.drawable.bottom_bar_icon_library, R.color.colorBottomNavigationInactive);

			bottomNavigationItems.add(item1);
			bottomNavigationItems.add(item2);
			bottomNavigationItems.add(item3);
			bottomNavigationItems.add(item4);

			bottomNavigation.addItems(bottomNavigationItems);
//		}

		bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
		bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
    protected void onResume() {
    	super.onResume();
    }

	private void initNavigationDrawer(Bundle savedInstanceState) {
		mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
//        // Find People
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
//        // Photos
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
//        // Communities, Will add a counter here
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
//        // Pages
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//        // What's hot, We  will add a counter here
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));

     // adding nav drawer items to array
//        // My Home
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
//        // Browse Library
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(, -1)));
//        // Events
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
//        // Donate, Will add a counter here
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
//        // Connect
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//        // About, We  will add a counter here
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

     // Recycle the typed array
        navMenuIcons.recycle();

//        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        final ActionBar actionBar = getActionBar();

        // enabling action bar app icon and behaving it as toggle button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        actionBar.setBackgroundDrawable();
//        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
//				setImageAppCenter();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
//        mDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes)
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
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


		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

	}


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

	private class DrawerItemClickListener implements OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//	        drawerLayout.closeDrawer(drawerList);
	    	// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
//			mDrawerLayout.closeDrawer(mDrawerList);
			displayView(position);
	        new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
//	                switchFragments(position); // your fragment transactions go here
//	                displayView(position);
	            	mDrawerLayout.closeDrawer(mDrawerList);
	            }
	        }, 300);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			//fragment = new HomeFragment();
			break;
		case 1:
			fragment = new LibraryFragment();
			break;
		case 2:
			fragment = new EventsFragment();
			break;
		case 3:
//			fragment = new DonateFragment();
			startActivity(new Intent(this, DonateActivity.class));
			break;
		case 4:
			fragment = new ContactFragment();
			break;
		case 5:
			fragment = new AboutFragment();
//			startActivity(new Intent(this, AboutActivity.class));
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

//			// update selected item and title, then close the drawer
//			mDrawerList.setItemChecked(position, true);
//			mDrawerList.setSelection(position);
//			setTitle(navMenuTitles[position]);
//			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setImageAppCenter() {
		Dialog overlayInfo = new Dialog(MainActivity.this);
        // Making sure there's no title.
        overlayInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Making dialog content transparent.
        overlayInfo.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        // Removing window dim normally visible when dialog are shown.
        overlayInfo.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // Setting position of content, relative to window.
        WindowManager.LayoutParams params = overlayInfo.getWindow().getAttributes();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//        params.x = 100;
        params.y = 30;
        // If user taps anywhere on the screen, dialog will be cancelled.
        overlayInfo.setCancelable(false);
        // Setting the content using prepared XML layout file.
        overlayInfo.setContentView(R.layout.main_overlay_image);
        overlayInfo.show();
	}

}
