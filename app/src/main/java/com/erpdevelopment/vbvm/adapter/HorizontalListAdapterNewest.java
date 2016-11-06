package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListAdapterNewest extends BaseAdapter {

	private Activity activity;
	private List<Lesson> lessons;
    private ImageLoader2 imageLoader;
    
    public HorizontalListAdapterNewest(Activity activity, List<Lesson> lessons) {
		this.activity = activity;
		this.lessons = lessons;
		// Create ImageLoader object to download and show image in list
		// Call ImageLoader constructor to initialize FileCache
		imageLoader = new ImageLoader2(activity);
	}
	
	@Override
	public int getCount() {
		return lessons.size();
	}

	@Override
	public Object getItem(int position) {
		return lessons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		        
		// Get the data item for this position
		Lesson lesson = (Lesson) getItem(position);    
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_hlistview_study, null);
		}
		// Lookup view for data population
		TextView txt = (TextView) convertView.findViewById(R.id.txt);
		txt.setText(lesson.getTitle());
		// Populate the data into the template view using the data object
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		imageLoader.DisplayImage(lesson.getStudyThumbnailSource(), img);
       
		return convertView;
	}

	public void setLessonListItems(List<Lesson> newList) {
		lessons = newList;
	    notifyDataSetChanged();
	}
	
}
