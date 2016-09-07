package com.erpdevelopment.vbvm.fragment;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.EventDetailsActivity;
import com.erpdevelopment.vbvm.adapter.EventsAdapter;
import com.erpdevelopment.vbvm.db.DBHandleEvents;
import com.erpdevelopment.vbvm.model.EventVbvm;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EventsFragment extends Fragment {

	View rootView;
	private List<EventVbvm> eventsList;
	private EventsAdapter eventsAdapter;	
	private ListView lvEvents;
	public EventsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_events, container, false);
         
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		
		Utilities.setActionBar(getActivity(), "Events");

		eventsList = new ArrayList<EventVbvm>();				
		eventsAdapter = new EventsAdapter(getActivity(), eventsList);		
		lvEvents = (ListView) rootView.findViewById(R.id.lv_events);
		lvEvents.setAdapter(eventsAdapter);
		lvEvents.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				eventsAdapter.setSelectedPosition(position);
				EventVbvm event = (EventVbvm) parent.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), EventDetailsActivity.class);
				i.putExtra("event", event);
				startActivity(i);
			}
		});
		new AsyncGetEvents().execute();        
	}
	
	private class AsyncGetEvents extends AsyncTask< String, String, List<EventVbvm> > {
	   	 
    	ProgressDialog pDialog;
    	
    	protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
		protected List<EventVbvm> doInBackground(String... params) {
			Log.d("onPostExecute=", "Loading list Events");	        
	        List<EventVbvm> listEvents = null;
	    	WebServiceCall.eventsInDB = MainActivity.settings.getBoolean("eventsInDB", false);			
			if ( WebServiceCall.eventsInDB )
				listEvents = DBHandleEvents.getAllEvents();
			else {
	    		if ( !CheckConnectivity.isOnline(getActivity())) {
	    			CheckConnectivity.showMessage(getActivity());
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
	
}
