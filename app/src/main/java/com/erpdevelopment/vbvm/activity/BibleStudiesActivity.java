package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.BibleStudiesAdapter;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.ConstantsVbvm;
import com.erpdevelopment.vbvm.utils.FilesManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class BibleStudiesActivity extends Activity {

	private LinearLayout llButtonHome;
	private ListView lv;
	private TextView tv;
	private BibleStudiesAdapter adapter;
	private SharedPreferences prefs;
	private List<Study> listStudiesWithFilter = new ArrayList<Study>();
	private ImageView imgSearchButton;
	private boolean filterdListSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_listview);
		llButtonHome = (LinearLayout) findViewById(R.id.ll_button_library);
		llButtonHome.setBackgroundResource(R.drawable.bottom_menu_bar);
		
		imgSearchButton = (ImageView) findViewById(R.id.btn_search);
		imgSearchButton.setVisibility(View.VISIBLE);
		
		prefs = getSharedPreferences(ConstantsVbvm.VBVM_PREFS, Context.MODE_PRIVATE);        	 
		
		tv = (TextView) findViewById(R.id.tv_title_top_control_bar);
		tv.setText("Bible Studies");
		
		adapter = new BibleStudiesAdapter(this, FilesManager.listStudies);
		
		lv = (ListView) findViewById(R.id.lv_bible_studies);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// user clicked a list item, make it "selected"
				adapter.setSelectedPosition(position);

				if ( !filterdListSelected ) {
					//Storing Data using SharedPreferences						
					SharedPreferences.Editor edit = prefs.edit();
					edit.remove("posStudy").commit();
		            edit.putInt("posStudy", position).commit();
		            
		            // save index and top position
		            int index = lv.getFirstVisiblePosition();
		            edit.remove("indexStudy").commit();
		            edit.putInt("indexStudy", index).commit();
				}

				Study study = (Study) parent.getItemAtPosition(position);
				Intent i = new Intent(BibleStudiesActivity.this, BibleStudyLessonsActivity.class);
				i.putExtra("study", study);
				startActivity(i);
			}
		});
	}
	
	public void onClickControlBar(View v){
		switch(v.getId()){
	        case R.id.button_home:
	        	startActivity(new Intent(BibleStudiesActivity.this, MainActivity.class));
	        	break;
	        case R.id.button_library:
	        	break;
	        case R.id.button_calendar:
				startActivity(new Intent(BibleStudiesActivity.this, EventsActivity.class));	        	
				break;
	        case R.id.button_chat:
	        	startActivity(new Intent(BibleStudiesActivity.this, QAndAPostsActivity.class));
	        	break;
	        case R.id.button_vbvm:
	        	startActivity(new Intent(BibleStudiesActivity.this, AboutActivity.class));
	        	break;
		}
	}

	@Override
	protected void onStart() {
		filterdListSelected = false;
		restoreSelectedRow();
		super.onStart();
	}
	
	private void restoreSelectedRow(){
		prefs = getSharedPreferences(ConstantsVbvm.VBVM_PREFS, Context.MODE_PRIVATE);
		int position = prefs.getInt("posStudy", -1);
		int index = prefs.getInt("indexStudy", -1);		
		if ( position != -1 ) {
			adapter.setSelectedPosition(position);
			if ( index != -1 ) {
				View vi = lv.getChildAt(0);
	            int top = (vi == null) ? 0 : vi.getTop();            
	            // restore index and position
	            lv.setSelectionFromTop(index, top);
			}
		}
	}
	
	private void removeSelectedRow() {
		//Storing Data using SharedPreferences						
		SharedPreferences.Editor edit = prefs.edit();
		edit.remove("posStudy").commit();
        edit.remove("indexStudy").commit();
	}
	
	public void onClickFilterStudies(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(BibleStudiesActivity.this);    	
		builder.setTitle(BibleStudiesActivity.this.getResources().getString(R.string.title_dialog_find_bible_studies))
				.setItems(R.array.items_find_bible_studies, 
						new DialogInterface.OnClickListener() {									
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								switch (which) {
									case 0:	filterListStudies("Old");
											filterdListSelected = true;
											adapter.setStudyListItems(listStudiesWithFilter);
											removeSelectedRow();
											break;
									case 1: filterListStudies("New");
											filterdListSelected = true;
											adapter.setStudyListItems(listStudiesWithFilter);
											removeSelectedRow();
											break;
									case 2: filterListStudies("Single");
											filterdListSelected = true;
											adapter.setStudyListItems(listStudiesWithFilter);
											removeSelectedRow();
											break;
									case 3: adapter.setStudyListItems(FilesManager.listStudies);
											filterdListSelected = false;
											restoreSelectedRow();
											break;		
									default:
										break;
								}
							}							
						})
		        .setCancelable(false)
		        .setPositiveButton(BibleStudiesActivity.this.getResources().getString(R.string.dialog_button_cancel),
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                    	filterdListSelected = false;
		                    	dialog.cancel();
		                    }
		                });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void filterListStudies(String pattern) {
		List<Study> tempList = FilesManager.listStudies;
		listStudiesWithFilter.clear();
		for ( int i=0; i < tempList.size(); i++ ){
			if ( tempList.get(i).getType().startsWith(pattern) )
				listStudiesWithFilter.add(tempList.get(i));
		}
	}

}
