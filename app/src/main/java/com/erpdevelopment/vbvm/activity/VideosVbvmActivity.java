package com.erpdevelopment.vbvm.activity;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.VideoVbvmAdapter;
import com.erpdevelopment.vbvm.db.DBHandleVideos;
import com.erpdevelopment.vbvm.model.ChannelVbvm;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class VideosVbvmActivity extends Activity {

	private VideoVbvmAdapter videosAdapter;	
	private ListView lvVideos;
	private ChannelVbvm channel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_listview);
		
		channel = (ChannelVbvm) getIntent().getExtras().getParcelable("channel");
		Utilities.setActionBar(this, channel.getTitle());
		
		List<VideoVbvm> videosList = DBHandleVideos.getVideosByChannel(channel.getIdProperty());				
		videosAdapter = new VideoVbvmAdapter(this, videosList);		
		lvVideos = (ListView) findViewById(R.id.lv_bible_studies);
        lvVideos.setAdapter(videosAdapter);
        lvVideos.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				videosAdapter.setSelectedPosition(position);
				VideoVbvm video = (VideoVbvm) parent.getItemAtPosition(position);
				Intent intent = new Intent(VideosVbvmActivity.this, VideoVbvmDetailsActivity.class);
				intent.putExtra("video", video);
		        startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

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
		        
	    }
		return super.onOptionsItemSelected(item);
	}

}
