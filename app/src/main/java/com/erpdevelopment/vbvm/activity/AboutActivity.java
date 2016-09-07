package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.VbvmAdapter;
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

public class AboutActivity extends Activity {

	private ListView lv;
//	private TextView tvTitle;
	private VbvmAdapter vbvmAdapter;
//	private static String[] values = new String[] { "Donate", "Contact", "Our Mission",
//	        "Our Beliefs", "Our Services", "Our History", "How To Get Involved", "Where Your Money Goes",
//	        "About The Founder", "Our Staff", "Board of Directors" };
	private static String[] values = new String[] { 
		"Our History", 
		"Our Beliefs", 
		"How To Get Involved", 
		"Where Your Money Goes",
		"About The Founder", 
		"Our Services", 
		"Our Mission", 
		"Our Staff", 
		"Board of Directors"
         };
//	private SharedPreferences prefs;
//	private LinearLayout llButtonHome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_listview);
//		llButtonHome = (LinearLayout) findViewById(R.id.ll_button_vbvm);
//		llButtonHome.setBackgroundResource(R.drawable.bottom_menu_bar);
		
//		tvTitle = (TextView) findViewById(R.id.tv_title_top_control_bar);
//		tvTitle.setText("About VBVMI");
		
		Utilities.setActionBar(this, null);
		
	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }    
	    
		vbvmAdapter = new VbvmAdapter(this, list);		
		lv = (ListView) findViewById(R.id.lv_bible_studies);
        lv.setAdapter(vbvmAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
//				if ( !CheckConnectivity.isOnline(AboutActivity.this)) {
//					CheckConnectivity.showMessage(AboutActivity.this);
//				} else {
					// user clicked a list item, make it "selected"
					vbvmAdapter.setSelectedPosition(position);
	
					//Storing Data using SharedPreferences						
//					SharedPreferences.Editor edit = prefs.edit();
//					edit.remove("posVbvm").commit();
//		            edit.putInt("posVbvm", position).commit();
//					
//		            // save index and top position
//		            int index = lv.getFirstVisiblePosition();	            
//		            
//		            edit.remove("indexVbvm").commit();
//		            edit.putInt("indexVbvm", index).commit();
		            
					Intent i = new Intent(AboutActivity.this, AboutDetailsActivity.class);
					i.putExtra("posSelected", position);
					
					switch (position) {
					
//						case 0:	Intent intent = new Intent(AboutActivity.this, VbvmDonateActivity.class);
//								intent.putExtra("url", "https://www.versebyverseministry.org/donate");
//								startActivity(intent);					
//							break;
						case 0: startActivity(i);
							break;
						case 1:	startActivity(i);
							break;
						case 2:	startActivity(i);
							break;
						case 3:	startActivity(i);
							break;
						case 4:	startActivity(i);
							break;
						case 5:	startActivity(i);
							break;
						case 6:	startActivity(i);
							break;
						case 7:	Intent intentStaff = new Intent(AboutActivity.this, StaffBoardActivity.class);
								intentStaff.putExtra("category", "staff");
								startActivity(intentStaff);	
							break;
						case 8: Intent intentBoard = new Intent(AboutActivity.this, StaffBoardActivity.class);
								intentBoard.putExtra("category", "board");
								startActivity(intentBoard);
							break;
						
					}
//				}
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