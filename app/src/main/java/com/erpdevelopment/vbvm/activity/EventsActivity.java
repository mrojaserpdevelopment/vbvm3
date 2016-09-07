package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.EventsAdapter;
import com.erpdevelopment.vbvm.db.DBHandleEvents;
import com.erpdevelopment.vbvm.model.EventVbvm;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.ConstantsVbvm;
import com.erpdevelopment.vbvm.utils.FavoritesLRU;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class EventsActivity extends Activity {

	private LinearLayout llButtonHome;
	private List<EventVbvm> eventsList;
	private EventsAdapter eventsAdapter;	
	private AsyncGetEvents asyncGetEvents;
	private ListView lv;
	private TextView tvTile;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_listview);
		llButtonHome = (LinearLayout) findViewById(R.id.ll_button_calendar);
		llButtonHome.setBackgroundResource(R.drawable.bottom_menu_bar);

		prefs = getSharedPreferences(ConstantsVbvm.VBVM_PREFS, Context.MODE_PRIVATE);
		
		tvTile = (TextView) findViewById(R.id.tv_title_top_control_bar);
		tvTile.setText("Events");
		
		eventsList = new ArrayList<EventVbvm>();				
		eventsAdapter = new EventsAdapter(this, eventsList);		
		lv = (ListView) findViewById(R.id.lv_bible_studies);
        lv.setAdapter(eventsAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {

				eventsAdapter.setSelectedPosition(position);

				//Storing Data using SharedPreferences						
				SharedPreferences.Editor edit = prefs.edit();
				edit.remove("posEvent").commit();
	            edit.putInt("posEvent", position).commit();
	            
	         	// save index and top position
	            int index = lv.getFirstVisiblePosition();
	            edit.remove("indexEvent").commit();
	            edit.putInt("indexEvent", index).commit();
				
				EventVbvm event = (EventVbvm) parent.getItemAtPosition(position);
				
				// Add item to Recently Viewed List
				ItemInfo item = new ItemInfo();
            	item.setId(event.getIdProperty());
            	item.setType("event");
            	item.setPosition(String.valueOf(position));
            	item.setItem(event);		            	
            	FavoritesLRU.addLruItem(event.getIdProperty(), item);
            	
				Intent i = new Intent(EventsActivity.this, EventDetailsActivity.class);
				i.putExtra("event", event);
				startActivity(i);
			}
		});
        asyncGetEvents = new AsyncGetEvents();
        asyncGetEvents.execute();
	}

	private class AsyncGetEvents extends AsyncTask< String, String, List<EventVbvm> > {
	   	 
    	ProgressDialog pDialog;
    	
    	protected void onPreExecute() {
            pDialog = new ProgressDialog(EventsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
		protected List<EventVbvm> doInBackground(String... params) {
			Log.d("onPostExecute=", "Loading list Events");	        
	        List<EventVbvm> listEvents = new ArrayList<EventVbvm>();
	    	WebServiceCall.eventsInDB = MainActivity.settings.getBoolean("eventsInDB", false);			
			if ( WebServiceCall.eventsInDB )
				listEvents = DBHandleEvents.getAllEvents();
			else {
	    		if ( !CheckConnectivity.isOnline(EventsActivity.this)) {
	    			CheckConnectivity.showMessage(EventsActivity.this);
	    		} else {
					listEvents = new WebServiceCall().getEvents();
					//Save state flag for sync Webservice/DB
	    			Editor e = MainActivity.settings.edit();
	    			e.putBoolean("eventsInDB", true);
	    			e.commit();
					Log.i("EventsActivity info", "Update DB with data from Webservice");
	    		}
			}
			return listEvents;
		}
       
        protected void onPostExecute(List<EventVbvm> result) { 
        	eventsAdapter.setEventsListItems(result);
            pDialog.dismiss();
        }
		
    }
	
	public void onClickControlBar(View v){
		switch(v.getId()){
	        case R.id.button_home:
	        	startActivity(new Intent(EventsActivity.this, MainActivity.class));
	        	break;
	        case R.id.button_library:
	        	startActivity(new Intent(EventsActivity.this, BibleStudiesActivity.class));
	        	break;
	        case R.id.button_calendar:
				break;
	        case R.id.button_chat:
	        	startActivity(new Intent(EventsActivity.this, QAndAPostsActivity.class));
	        	break;
	        case R.id.button_vbvm:
	        	startActivity(new Intent(EventsActivity.this, AboutActivity.class));
	        	break;
		}
	}
	
	@Override
	protected void onStart() {
		prefs = getSharedPreferences(ConstantsVbvm.VBVM_PREFS, Context.MODE_PRIVATE);
		int position = prefs.getInt("posEvent", -1);
		int index = prefs.getInt("indexEvent", -1);		
		if ( position != -1 ) {
			eventsAdapter.setSelectedPosition(position);
			if ( index != -1 ) {
				View vi = lv.getChildAt(0);
	            int top = (vi == null) ? 0 : vi.getTop();            
	            // restore index and position
	            lv.setSelectionFromTop(index, top);
			}
		}
		super.onStart();
	}
}
