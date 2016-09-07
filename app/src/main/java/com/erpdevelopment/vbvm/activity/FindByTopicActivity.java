package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.FilterListAdapter;
import com.erpdevelopment.vbvm.service.WebServiceCall;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FindByTopicActivity extends Activity {

//	private String listContents[];
	private List<String> filterList = new ArrayList<String>();
//	private TextView tvTitle;
	private ListView lvFilterList;
	private TextView tvClear;
	private TextView tvTitle;
	private ImageButton imgCancel;
	private FilterListAdapter adapterFilterList;
	private static int resultCode;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_vbvm_listview);
			
			final ActionBar actionBar = getActionBar();
	        actionBar.setCustomView(R.layout.actionbar_custom_view_filter);
	        actionBar.setDisplayShowTitleEnabled(false);
	        actionBar.setDisplayShowCustomEnabled(true);
	        actionBar.setDisplayUseLogoEnabled(false);
	        actionBar.setDisplayShowHomeEnabled(false);

			tvClear = (TextView) findViewById(R.id.tv_filter_clear);
			tvClear.setVisibility(View.VISIBLE);
			tvTitle = (TextView) findViewById(R.id.tv_filter_title);
			tvTitle.setText("Filter");
			imgCancel = (ImageButton) findViewById(R.id.img_filter_cancel);
	        lvFilterList = (ListView) findViewById(R.id.lv_bible_studies);
            
	        String filterCategory = getIntent().getStringExtra("filterCategory");
	        
	        Iterator<String> iter;
	        if (filterCategory.equals("post")) { 
	        	iter = WebServiceCall.topicSet.iterator();
	        	resultCode = 100;
	        } else {
	        	iter = WebServiceCall.authorNameSet.iterator();
	        	resultCode = 200;
	        }
	        while(iter.hasNext())
	          filterList.add((String)iter.next());
	        Collections.sort(filterList);

	        adapterFilterList = new FilterListAdapter(this, filterList);	        
	        lvFilterList.setAdapter(adapterFilterList);	        
	        lvFilterList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long arg3) {
			        // add some animation when a list item was clicked
//			        Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
//			        fadeInAnimation.setDuration(10);
//			        v.startAnimation(fadeInAnimation);
			        
			        String selectedItemText = (String) parent.getItemAtPosition(position);
			        
					Intent in = new Intent();
					in.putExtra("selectedItem", selectedItemText);
					setResult(resultCode, in);
					finish();				
				}
			});
	        
	        tvClear.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					Intent in = new Intent(FindByTopicActivity.this, QAndAPostsActivity.class);
					in.putExtra("selectedItem", "clear");
					setResult(resultCode, in);
					finish();
				}
			});

	        imgCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					imgCancel.setBackgroundColor(getResources().getColor(R.color.red));
					Intent in = new Intent(FindByTopicActivity.this, QAndAPostsActivity.class);
					in.putExtra("selectedItem", "cancel");
					setResult(resultCode, in);
					finish();
				}
			});
	        
//	        imgCancel.setOnTouchListener(new OnTouchListener() {
//
//	        	@Override
//	        	public boolean onTouch(View v, MotionEvent event) {
//	        	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//	        	           Toast.makeText(FindByTopicActivity.this, "button pressed...", Toast.LENGTH_LONG).show();
//	        	    }
//	        	    else if (event.getAction() == MotionEvent.ACTION_UP) {
//	        	            // set to normal color
//	        	    }
//
//	        	    return true;
//	        	}
//	        });
				
		}
		
}
