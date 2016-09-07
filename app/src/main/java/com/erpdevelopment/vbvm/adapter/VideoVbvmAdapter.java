package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;

public class VideoVbvmAdapter extends BaseAdapter {

	private Activity activity;
	private List<VideoVbvm> videos;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	private ImageLoader imageLoader;

	public VideoVbvmAdapter(Activity a, List<VideoVbvm> listChannels) {
		activity = a;
		videos = listChannels;
		imageLoader = new ImageLoader(activity);
	}
	
	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
	   VideoVbvm video = (VideoVbvm) getItem(position);    
       if (convertView == null) {
	      LayoutInflater inflater = activity.getLayoutInflater();
          convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
       }
       ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
       img.setBackgroundResource(R.drawable.youtube3);
//       imageLoader.DisplayImage(video.getThumbnailSource(), img);
       
       TextView txt = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
       txt.setText(video.getTitle());

       TextView txt2 = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
       txt2.setVisibility(View.GONE);

       if(selectedPos == position)
    	   convertView.setBackgroundColor(Color.CYAN);
       else	    	   
    	   convertView.setBackgroundColor(Color.WHITE);	       
       
	   return convertView;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}
	
	public void setVideoListItems(List<VideoVbvm> newList) {
	    videos = newList;
	    notifyDataSetChanged();
	}
}
