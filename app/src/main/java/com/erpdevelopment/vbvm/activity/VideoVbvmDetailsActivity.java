package com.erpdevelopment.vbvm.activity;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.db.DBHandleFavorites;
import com.erpdevelopment.vbvm.model.Favorite;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.FavoritesLRU;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoVbvmDetailsActivity extends Activity {
	
	private VideoVbvm video;
	private TextView tvTitleVideo;
	private ImageView imgVideoThumbnail;
	private static boolean optionSelected = false;
 
	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_details);
		
		video = (VideoVbvm) getIntent().getExtras().getParcelable("video");
		Utilities.setActionBar(this, "Video");
//		
		tvTitleVideo = (TextView) findViewById(R.id.tv_title_video);
		tvTitleVideo.setText(video.getTitle());
		
//        toggleAddFavorite = (ToggleButton) findViewById(R.id.img_add_favorite);
//		toggleAddFavorite.setVisibility(View.VISIBLE);
//        toggleAddFavorite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				
//				if ( !isChecked ) {
//					FavoritesLRU.deleteLruItem(video.getIdProperty());
//					toggleAddFavorite.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_add_fav));
//				} else {					
//					// Add item to Favorite List
//					ItemInfo item = new ItemInfo();
//	            	item.setId(video.getIdProperty());
//	            	item.setType("video");
//	            	item.setItem(video);		            	
//	            	FavoritesLRU.addLruItem(video.getIdProperty(), item);
//	            	toggleAddFavorite.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_favorited));
//				}
//			}
//		});
        
		imgVideoThumbnail = (ImageView) findViewById(R.id.img_video_thumbnail);
        imgVideoThumbnail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoSource()));
		        startActivity(intent);
			}
		});
	}
	
	private boolean checkFavoriteOn() {
		Favorite item = DBHandleFavorites.getFavoriteById(video.getIdProperty());
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
	        	item.setId(video.getIdProperty());
	        	item.setType("video");
	        	item.setItem(video);		            	
	        	FavoritesLRU.addLruItem(video.getIdProperty(), item);
			} else {
	            menu.getItem(0).setIcon(R.drawable.icon_add_favorite);
	            // Remove item from Favorite List
				FavoritesLRU.deleteLruItem(video.getIdProperty());
	        }
			optionSelected = false;
		}
		return super.onPrepareOptionsMenu(menu);
	}


}
