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
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;
import com.squareup.picasso.Picasso;

public class VideoVbvmAdapter extends BaseAdapter {

	private Activity activity;
	private List<VideoVbvm> listVideos;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	private ImageLoader2 imageLoader;

	public VideoVbvmAdapter(Activity a, List<VideoVbvm> listVideoVbvm, ImageLoader2 imageLoader) {
		activity = a;
		listVideos = listVideoVbvm;
		this.imageLoader = imageLoader;
	}

	@Override
	public int getCount() {

		return listVideos.size();
	}

	@Override
	public Object getItem(int position) {
		return listVideos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
	    VideoVbvm video = (VideoVbvm) getItem(position);
       	if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
          	convertView = inflater.inflate(R.layout.item_listview_video, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_video_title);
			viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tv_video_description);
			viewHolder.tvLength = (TextView) convertView.findViewById(R.id.tv_video_length);
			viewHolder.imgVideoThumbnail = (ImageView) convertView.findViewById(R.id.img_video_thumbnail);
//			imageLoader.DisplayImage(video.getThumbnailSource(),viewHolder.imgVideoThumbnail);
			convertView.setTag(viewHolder);
       	} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(video.getTitle());
		viewHolder.tvDescription.setText(video.getDescription());
		viewHolder.tvLength.setText(video.getVideoLength());

		Picasso.with(activity)
				.load(video.getThumbnailSource())
				.resize(120,90)
				.centerCrop()
				.into(viewHolder.imgVideoThumbnail);

       if(selectedPos == position)
    	   convertView.setBackgroundColor(Color.CYAN);
       else	    	   
    	   convertView.setBackgroundColor(Color.WHITE);	       
       
	   return convertView;
	}

	static class ViewHolder {
		TextView tvTitle;
		TextView tvDescription;
		TextView tvLength;
		ImageView imgVideoThumbnail;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}
	
	public void setVideoListItems(List<VideoVbvm> newList) {
		listVideos = newList;
	    notifyDataSetChanged();
	}
}
