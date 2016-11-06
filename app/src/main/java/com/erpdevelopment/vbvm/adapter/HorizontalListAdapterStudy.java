package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListAdapterStudy extends BaseAdapter {

	private Activity activity;
	private List<Study> studies;
    public ImageLoader2 imageLoader;
    public HorizontalListAdapterStudy(Activity a, List<Study> study) {
		activity = a;
		studies = study;
		// Create ImageLoader object to download and show image in list
		// Call ImageLoader constructor to initialize FileCache
		imageLoader = new ImageLoader2(activity);
	}
	
	@Override
	public int getCount() {
		return studies.size();
	}

	@Override
	public Object getItem(int position) {
		return studies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
		// Get the data item for this position
	       Study study = (Study) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_hlistview_study, null);
	       }
	       // Lookup view for data population
	       TextView txt = (TextView) convertView.findViewById(R.id.txt);
	       txt.setText(study.getTitle());
	       // Populate the data into the template view using the data object
	       ImageView img = (ImageView) convertView.findViewById(R.id.img);
	       imageLoader.DisplayImage(study.getThumbnailSource(), img);
	       
	       return convertView;
	}

	public void setStudyListItems(List<Study> newList) {
	    studies = newList;
	    notifyDataSetChanged();
	}
	
}
