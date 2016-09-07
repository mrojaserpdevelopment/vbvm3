package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.EventVbvm;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventsAdapter extends BaseAdapter {

	private Activity activity;
	private List<EventVbvm> events;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	
	public EventsAdapter(Activity a, List<EventVbvm> eventsList) {
		activity = a;
		events = eventsList;
	}
	
	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
		// Get the data item for this position
	       EventVbvm event = (EventVbvm) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
	       }
	       TextView txt = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
	       txt.setText(event.getTitle());

	       TextView txt2 = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
	       txt2.setText(event.getEventDate());

	       ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
	       img.setImageResource(R.drawable.bottom_bar_icon_calendar);
		   
	       if(selectedPos == position)
	    	   convertView.setBackgroundColor(Color.CYAN);
	       else	    	   
	    	   convertView.setBackgroundColor(Color.WHITE);
	       
		   return convertView;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public void setEventsListItems(List<EventVbvm> newList) {
	    events = newList;
	    notifyDataSetChanged();
	}

}
