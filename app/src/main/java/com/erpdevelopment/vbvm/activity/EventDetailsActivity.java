package com.erpdevelopment.vbvm.activity;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.db.DBHandleFavorites;
import com.erpdevelopment.vbvm.model.EventVbvm;
import com.erpdevelopment.vbvm.model.Favorite;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.utils.FavoritesLRU;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class EventDetailsActivity extends Activity {

	private EventVbvm event;
	private TextView tvEventDate;
	private TextView tvEventTitle;
	private TextView tvEventLocation;
	private TextView tvEventDescription;
	private static boolean optionSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);
		
		Utilities.setActionBar(this, "Event");
		
		tvEventDate = (TextView) findViewById(R.id.tv_event_date);
		tvEventTitle = (TextView) findViewById(R.id.tv_event_title);
		tvEventLocation = (TextView) findViewById(R.id.tv_event_location);
		tvEventDescription = (TextView) findViewById(R.id.tv_event_description);
		
		Bundle b = getIntent().getExtras();
		Parcelable p = b.getParcelable("event");
		event = (EventVbvm) p;

		tvEventDate.setText(event.getEventDate());
		tvEventTitle.setText(event.getTitle());
		tvEventLocation.setText(event.getLocation());
		tvEventDescription.setText(event.getEventsDescription());
		tvEventDescription.setMovementMethod(new ScrollingMovementMethod());
		
	}
	
	private boolean checkFavoriteOn() {
		Favorite item = DBHandleFavorites.getFavoriteById(event.getIdProperty());
		if ( item != null )
			return true;
		else
			return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_details, menu);

        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		    	Intent parentIntent = NavUtils.getParentActivityIntent(this);
		    	if ( parentIntent != null ) {
		            parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		            startActivity(parentIntent);
		    	}
	            finish();
	            return true;
		        
		    case R.id.action_favorite:
		    	optionSelected = true;
		    	invalidateOptionsMenu();
	    }
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		if(checkFavoriteOn())
            menu.getItem(0).setIcon(R.drawable.icon_favorited);
        else
            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
		
		if (optionSelected) {
			if(!checkFavoriteOn()){
	            menu.getItem(0).setIcon(R.drawable.icon_favorited);
	            // Add item to Favorite List
				ItemInfo item = new ItemInfo();
	        	item.setId(event.getIdProperty());
	        	item.setType("event");
	        	item.setItem(event);		            	
	        	FavoritesLRU.addLruItem(event.getIdProperty(), item);
			} else {
	            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
	            // Remove item from Favorite List
				FavoritesLRU.deleteLruItem(event.getIdProperty());
	        }
			optionSelected = false;
		}

		return super.onPrepareOptionsMenu(menu);
	}

}
