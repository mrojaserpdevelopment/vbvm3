package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.utils.Utilities;

public class VideoChannelsAdapter extends BaseAdapter{

	private Activity activity;
	private int selectedPos = -1;	// init value for not-selected
	private List<VideoChannel> listVideoChannels;

	public VideoChannelsAdapter(Activity a, List<VideoChannel> listVideoChannels) {
		activity = a;
		this.listVideoChannels = listVideoChannels;
	}
	
	@Override
	public int getCount() {
		return listVideoChannels.size();
	}

	@Override
	public Object getItem(int position) {
		return listVideoChannels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		VideoChannel videoChannel = (VideoChannel) getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_listview_articles_answers, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_article_title);
			viewHolder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_article_author);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_article_date);
			viewHolder.llTopics = (LinearLayout) convertView.findViewById(R.id.ll_articles_topics);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(videoChannel.getTitle());
		int countVideos = videoChannel.getVideos().size();
		String textCountVideos = countVideos + " video" + (countVideos > 1 ? "s" : "");
		viewHolder.tvAuthor.setText(textCountVideos);
		viewHolder.tvDate.setText(Utilities.getSimpleDateFormat(videoChannel.getPostedDate(),"dd/MM/yy"));
		viewHolder.llTopics.setVisibility(View.GONE);

		if(selectedPos == position)
			convertView.setBackgroundColor(Color.CYAN);
		else
			convertView.setBackgroundColor(Color.WHITE);

		return convertView;
	}

	static class ViewHolder {
		TextView tvTitle;
		TextView tvAuthor;
		TextView tvDate;
		LinearLayout llTopics;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}
	
	public void setVideoListItems(List<VideoChannel> newList) {
	    selectedPos = -1;
		listVideoChannels = newList;
	    notifyDataSetChanged();
	}
}
