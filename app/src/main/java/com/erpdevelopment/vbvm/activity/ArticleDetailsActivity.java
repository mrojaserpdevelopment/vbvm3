package com.erpdevelopment.vbvm.activity;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.db.DBHandleFavorites;
import com.erpdevelopment.vbvm.model.Article;
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

public class ArticleDetailsActivity extends Activity {

	private Article article;
	private TextView tvTitleArticle;
	private TextView tvAuthorName;
	private TextView tvDescription;
	private TextView tvTopics;
	private static boolean optionSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);

		tvAuthorName = (TextView) findViewById(R.id.tv_event_date);
		tvTitleArticle = (TextView) findViewById(R.id.tv_event_title);
		tvDescription = (TextView) findViewById(R.id.tv_event_description);
		tvTopics = (TextView) findViewById(R.id.tv_event_location);
		
		Bundle b = getIntent().getExtras();
		Parcelable p = b.getParcelable("article");
		article = (Article) p;
		
		tvAuthorName.setText(article.getAuthorName());
		tvTitleArticle.setText(article.getTitle());
		tvDescription.setText(article.getBody());
		tvDescription.setMovementMethod(new ScrollingMovementMethod());	
		
		Utilities.setActionBar(this, article.getTitle());
		
		List<String> topics = article.getTopics();
		String topicsString = "";
		for (String topic: topics) {
			topicsString = topicsString + " " + topic;
		}
		tvTopics.setText(topicsString);
		
	}

	private boolean checkFavoriteOn() {
		Favorite item = DBHandleFavorites.getFavoriteById(article.getIdProperty());
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
	        	item.setId(article.getIdProperty());
	        	item.setType("article");
	        	item.setItem(article);		            	
	        	FavoritesLRU.addLruItem(article.getIdProperty(), item);
			} else {
	            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
	            // Remove item from Favorite List
				FavoritesLRU.deleteLruItem(article.getIdProperty());
	        }
			optionSelected = false;
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
}
