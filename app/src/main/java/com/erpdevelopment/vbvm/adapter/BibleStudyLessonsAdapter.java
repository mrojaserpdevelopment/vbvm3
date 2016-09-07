package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BibleStudyLessonsAdapter extends BaseAdapter {

	Context context;
	List<Lesson> lessons;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	
	public BibleStudyLessonsAdapter(Context c, List<Lesson> l) {
		context = c;
		lessons = l;
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
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_listview_lesson, null);
		}
		Lesson item = lessons.get(position);

		TextView title = (TextView) convertView.findViewById(R.id.tv_lesson_title);
		title.setText(item.getTitle());
		
		TextView length = (TextView) convertView.findViewById(R.id.tv_lesson_length);
		length.setText(item.getAudioLength());
				
		if (selectedPos == position){
			convertView.setBackgroundColor(Color.CYAN);
        } else {
			convertView.setBackgroundColor(Color.WHITE);
        }
		
		ImageView imageLesson = (ImageView) convertView.findViewById(R.id.img_lesson);		
		
//		if (item.getIdProperty().equals(FilesManager.lastLessonId)){
//			imageLesson.setBackgroundResource(R.drawable.playing_now);
//			convertView.setBackgroundColor(Color.CYAN);
//		} else {
//			if (item.getCurrentPosition() > 0) {
//				imageLesson.setBackgroundResource(R.drawable.play_partial);				
//			} else {
//				if (item.getState()!= null){
//					if (item.getState().equals("new"))
//						imageLesson.setBackgroundResource(R.drawable.play_color);
//					else
//						imageLesson.setBackgroundResource(R.drawable.play_complete);
//				}
//			}
//			convertView.setBackgroundColor(Color.WHITE);
//		}
		
//		if (item.getIdProperty().equals(FilesManager.lastLessonId)){
//			imageLesson.setBackgroundResource(R.drawable.playing_now);
//			convertView.setBackgroundColor(Color.CYAN);
//		} else {
//			if (item.getCurrentPosition() > 0) {
//				imageLesson.setBackgroundResource(R.drawable.play_partial);				
//			} else {
				if ( item.getState() != null ) {
					if ( !item.getAudioSource().trim().equals("") ) {
						if (item.getState().equals("new"))
							imageLesson.setBackgroundResource(R.drawable.play_color);
						else if (item.getState().equals("complete"))
							imageLesson.setBackgroundResource(R.drawable.play_complete);
						else if (item.getState().equals("partial"))
							imageLesson.setBackgroundResource(R.drawable.play_partial);
						else if (item.getState().equals("playing"))
							imageLesson.setBackgroundResource(R.drawable.playing_now);
					} else {
						if (item.getState().equals("complete")) {
							imageLesson.setBackgroundResource(R.drawable.play_complete);
						} else {
							imageLesson.setBackgroundResource(R.drawable.play_color);
						}
					}
				}
//			}
//			convertView.setBackgroundColor(Color.WHITE);
//		}
		
		ImageView imgDownload = (ImageView) convertView.findViewById(R.id.img_download_1);
		TextView tvDownloading = (TextView) convertView.findViewById(R.id.tv_item_downloading);
		
		int downloadStatus = item.getDownloadStatus();
		
		if ( !item.getAudioSource().trim().equals("") ) 
		{	
			switch (downloadStatus) {
				case 0: imgDownload.setVisibility(View.VISIBLE);
						tvDownloading.setVisibility(View.GONE);
					break;
				case 1: imgDownload.setVisibility(View.GONE);
						tvDownloading.setVisibility(View.VISIBLE);
					    tvDownloading.setText("Downloaded");
					break;	
				case 2: imgDownload.setVisibility(View.GONE);
						tvDownloading.setVisibility(View.VISIBLE);
					    tvDownloading.setText("Downloading...");
				default:
					break;
			}
		} else {
			//Lesson not available yet
			imgDownload.setVisibility(View.GONE);
			tvDownloading.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public void setStudyDetailsListItems(List<Lesson> newList) {
	    lessons = newList;
	    notifyDataSetChanged();
	}
}
