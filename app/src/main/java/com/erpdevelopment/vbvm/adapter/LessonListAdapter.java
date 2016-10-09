package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.FontManager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LessonListAdapter extends BaseAdapter {

	private Context context;
	private List<Lesson> lessons;

	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	
	public LessonListAdapter(Context context, List<Lesson> lessons) {
		this.context = context;
		this.lessons = lessons;
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
		Lesson lesson = lessons.get(position);

		TextView tvLessonNo = (TextView) convertView.findViewById(R.id.tv_lesson_no);
		TextView tvLessonLength = (TextView) convertView.findViewById(R.id.tv_lesson_length);
		TextView tvLessonDescription = (TextView) convertView.findViewById(R.id.tv_lesson_description);

		tvLessonNo.setText(lesson.getTitle().substring(lesson.getTitle().lastIndexOf(" ")+1));
		tvLessonLength.setText(lesson.getAudioLength());
		tvLessonDescription.setText(lesson.getLessonsDescription());

//		if ( (lesson.getTeacherAid() != null) && !(lesson.getTeacherAid().isEmpty()) ) {
//
//		}

		TextView tvIconTeacherAid = (TextView) convertView.findViewById(R.id.tv_icon_teacher_aid);
		TextView tvIconTranscript = (TextView) convertView.findViewById(R.id.tv_icon_transcript);
		TextView tvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);

		tvIconTeacherAid.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		tvIconTranscript.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		tvIconPlayMini.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));

//		TextView title = (TextView) convertView.findViewById(R.id.tv_lesson_title);
//		title.setText(lesson.getTitle());
//
//		TextView length = (TextView) convertView.findViewById(R.id.tv_lesson_length);
//		length.setText(lesson.getAudioLength());
//
//		if (selectedPos == position){
//			convertView.setBackgroundColor(Color.CYAN);
//        } else {
//			convertView.setBackgroundColor(Color.WHITE);
//        }
//
//		ImageView imageLesson = (ImageView) convertView.findViewById(R.id.img_lesson);
//
//				if ( lesson.getState() != null ) {
//					if ( !lesson.getAudioSource().trim().equals("") ) {
//						if (lesson.getState().equals("new"))
//							imageLesson.setBackgroundResource(R.drawable.play_color);
//						else if (lesson.getState().equals("complete"))
//							imageLesson.setBackgroundResource(R.drawable.play_complete);
//						else if (lesson.getState().equals("partial"))
//							imageLesson.setBackgroundResource(R.drawable.play_partial);
//						else if (lesson.getState().equals("playing"))
//							imageLesson.setBackgroundResource(R.drawable.playing_now);
//					} else {
//						if (lesson.getState().equals("complete")) {
//							imageLesson.setBackgroundResource(R.drawable.play_complete);
//						} else {
//							imageLesson.setBackgroundResource(R.drawable.play_color);
//						}
//					}
//				}
//
//		ImageView imgDownload = (ImageView) convertView.findViewById(R.id.img_download_1);
//		TextView tvDownloading = (TextView) convertView.findViewById(R.id.tv_item_downloading);
//
//		int downloadStatus = lesson.getDownloadStatus();
//
//		if ( !lesson.getAudioSource().trim().equals("") )
//		{
//			switch (downloadStatus) {
//				case 0: imgDownload.setVisibility(View.VISIBLE);
//						tvDownloading.setVisibility(View.GONE);
//					break;
//				case 1: imgDownload.setVisibility(View.GONE);
//						tvDownloading.setVisibility(View.VISIBLE);
//					    tvDownloading.setText("Downloaded");
//					break;
//				case 2: imgDownload.setVisibility(View.GONE);
//						tvDownloading.setVisibility(View.VISIBLE);
//					    tvDownloading.setText("Downloading...");
//				default:
//					break;
//			}
//		} else {
//			//Lesson not available yet
//			imgDownload.setVisibility(View.GONE);
//			tvDownloading.setVisibility(View.GONE);
//		}
		
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
