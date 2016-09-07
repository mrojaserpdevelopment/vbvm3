package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilterListAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> filterableItems;
	
	public FilterListAdapter(Activity a, List<String> listFilterableItems) {
		activity = a;
		filterableItems = listFilterableItems;
	}
	
	@Override
	public int getCount() {
		return filterableItems.size();
	}

	@Override
	public Object getItem(int position) {
		return filterableItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
		// Get the data item for this position
	       String topic = (String) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
	       }
	       
	       LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_listview_item);
	       ll.setVisibility(View.GONE);
	       
	       TextView tvTopic = (TextView) convertView.findViewById(R.id.tv_item_topic);
	       tvTopic.setText(topic);
	       tvTopic.setVisibility(View.VISIBLE);
	       
		   return convertView;
	}
	
	public void setFilterListItems(List<String> newList) {
		filterableItems = newList;
	    notifyDataSetChanged();
	}

}
