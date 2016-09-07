package com.erpdevelopment.vbvm.activity;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.db.DBHandleFavorites;
import com.erpdevelopment.vbvm.model.Favorite;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.QandAPost;
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

public class QAndAPostDetailsActivity extends Activity {

	private QandAPost post;
	private TextView tvPostAuthor;
	private TextView tvPostDate;
	private TextView tvPostTopics;
	private TextView tvPostTitle;
	private TextView tvPostDescription;
	private static boolean optionSelected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_details);
		
		tvPostAuthor = (TextView) findViewById(R.id.tv_post_author);
		tvPostDate = (TextView) findViewById(R.id.tv_post_date);
		tvPostTitle = (TextView) findViewById(R.id.tv_post_title);
		tvPostTopics = (TextView) findViewById(R.id.tv_post_topics);
		tvPostDescription = (TextView) findViewById(R.id.tv_post_description);
		tvPostDescription.setMovementMethod(new ScrollingMovementMethod());
		
		Bundle b = getIntent().getExtras();
		Parcelable p = b.getParcelable("post");
		post = (QandAPost) p;
		
		Utilities.setActionBar(this, post.getTitle());
		
		tvPostAuthor.setText(post.getAuthorName());
		tvPostDate.setText(post.getPostedDate());
		tvPostTitle.setText(post.getTitle());
		tvPostDescription.setText(post.getBody());
		
		List<String> topicList = post.getTopics();
		StringBuilder strTopics = new StringBuilder();
		for (int i=0; i<topicList.size(); i++){
			strTopics.append(topicList.get(i));
			if ( i != (topicList.size()-1) )
				strTopics.append(" ,");
		}
		tvPostTopics.setText(strTopics.toString());	
		
	}
	
	private boolean checkFavoriteOn() {
		Favorite item = DBHandleFavorites.getFavoriteById(post.getIdProperty());
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
	        	item.setId(post.getIdProperty());
	        	item.setType("post");
	        	item.setItem(post);		            	
	        	FavoritesLRU.addLruItem(post.getIdProperty(), item);
			} else {
	            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
	            // Remove item from Favorite List
				FavoritesLRU.deleteLruItem(post.getIdProperty());
	        }
			optionSelected = false;
		}

		return super.onPrepareOptionsMenu(menu);
	}

}
