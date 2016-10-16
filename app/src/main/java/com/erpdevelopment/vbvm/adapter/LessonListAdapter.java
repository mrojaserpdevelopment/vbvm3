package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.service.downloader.DownloaderThread;
import com.erpdevelopment.vbvm.utils.FontManager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LessonListAdapter extends BaseAdapter {

	private Context context;
	private List<Lesson> lessons;
//	private Lesson lesson;
	public static final String LOG_TAG = "Android Downloader";
	private static int count = 0;

	// Used to communicate state changes in the DownloaderThreadTest
	public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
	public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
	public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
	public static final int MESSAGE_CONNECTING_STARTED = 1004;
	public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;

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
		ViewHolderItem viewHolderItem = null;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_listview_lesson, parent, false);
			viewHolderItem = new ViewHolderItem();
			viewHolderItem.itemTvLessonNo = (TextView) convertView.findViewById(R.id.tv_lesson_no);
			viewHolderItem.itemTvLessonLength = (TextView) convertView.findViewById(R.id.tv_lesson_length);
			viewHolderItem.itemTvLessonDescription = (TextView) convertView.findViewById(R.id.tv_lesson_description);
			viewHolderItem.itemTvIconTeacherAid = (TextView) convertView.findViewById(R.id.tv_icon_teacher_aid);
			viewHolderItem.itemTvIconTranscript = (TextView) convertView.findViewById(R.id.tv_icon_transcript);
			viewHolderItem.itemTvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);
			viewHolderItem.itemPbPlayer = (ProgressBar) convertView.findViewById(R.id.pb_player);
			viewHolderItem.rlTeacherAid = (RelativeLayout) convertView.findViewById(R.id.rl_teacher_aid);
			viewHolderItem.rlTranscript = (RelativeLayout) convertView.findViewById(R.id.rl_transcript);
			viewHolderItem.rlPlayMini = (RelativeLayout) convertView.findViewById(R.id.rl_play_mini);
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}
		final Lesson lesson = lessons.get(position);



//		TextView tvLessonNo = (TextView) convertView.findViewById(R.id.tv_lesson_no);
//		TextView tvLessonLength = (TextView) convertView.findViewById(R.id.tv_lesson_length);
//		TextView tvLessonDescription = (TextView) convertView.findViewById(R.id.tv_lesson_description);
//
//		tvLessonNo.setText(lesson.getTitle().substring(lesson.getTitle().lastIndexOf(" ")+1));
//		tvLessonLength.setText(lesson.getAudioLength());
//		tvLessonDescription.setText(lesson.getLessonsDescription());
//
//		TextView tvIconTeacherAid = (TextView) convertView.findViewById(R.id.tv_icon_teacher_aid);
//		TextView tvIconTranscript = (TextView) convertView.findViewById(R.id.tv_icon_transcript);
//		TextView tvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);
//
//		tvIconTeacherAid.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
//		tvIconTranscript.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
//		tvIconPlayMini.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
//
//		ProgressBar pbPlayer = (ProgressBar) convertView.findViewById(R.id.pb_player);
//		pbPlayer.setProgress(lesson.getDownloadProgress());

		viewHolderItem.itemTvLessonNo.setText(lesson.getTitle().substring(lesson.getTitle().lastIndexOf(" ")+1));
		viewHolderItem.itemTvLessonDescription.setText(lesson.getLessonsDescription());
		viewHolderItem.itemTvLessonLength.setText(lesson.getAudioLength());
		viewHolderItem.itemTvIconTeacherAid.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		viewHolderItem.itemTvIconTranscript.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		viewHolderItem.itemTvIconPlayMini.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		viewHolderItem.itemPbPlayer.setProgress(lesson.getDownloadProgress());

		viewHolderItem.rlPlayMini.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					onClickItemLessons(view,lesson);
			}
		});

		viewHolderItem.rlTeacherAid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				onClickLesson(view, lesson);
				onClickItemLessons(view,lesson);
			}
		});

		viewHolderItem.rlTranscript.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				onClickLesson(view, lesson);
				onClickItemLessons(view,lesson);
			}
		});

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

	static class ViewHolderItem {
		TextView itemTvLessonNo;
		TextView itemTvLessonLength;
		TextView itemTvLessonDescription;
		TextView itemTvIconTeacherAid;
		TextView itemTvIconTranscript;
		TextView itemTvIconPlayMini;
		ProgressBar itemPbPlayer;
		RelativeLayout rlTeacherAid;
		RelativeLayout rlTranscript;
		RelativeLayout rlPlayMini;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public void setLessonListItems(List<Lesson> newList) {
	    lessons = newList;
	    notifyDataSetChanged();
	}

//	private void onClickLesson(View view, Lesson lesson) {
//		if ( DownloadServiceTest.downloading ) {
//			System.out.println("wait, a download is in progress...");
//			return;
//		}
//		int status = 0;
//		String downloadType = "";
//		String url = "";
//		switch (view.getId()) {
//			case R.id.tv_icon_play_mini:
////			case R.id.toggle_play_mini:
//				System.out.println("LessonListAdapter.onClick play clicked... " + lesson.getTitle());
//				status = lesson.getDownloadStatusAudio();
//				downloadType = "audio";
//				url = lesson.getAudioSource();
//				break;
//			case R.id.tv_icon_teacher_aid:
//				status = lesson.getDownloadStatusTeacherAid();
//				downloadType = "teacher";
//				url = lesson.getTeacherAid();
//				break;
//			case R.id.tv_icon_transcript:
//				status = lesson.getDownloadStatusTranscript();
//				downloadType = "transcript";
//				url = lesson.getTranscript();
//				break;
//		}
//		if ( status == 0 ) {
//			Intent intent = new Intent(context, DownloadServiceTest.class);
//			intent.putExtra("lesson", lesson);
//			intent.putExtra("downloadType", downloadType);
//			intent.putExtra("url", url);
//			context.startService(intent);
//			((TextView)view).setText(context.getResources().getString(R.string.fa_icon_stop));
//		} else if ( status == 1 ) {
//			//Downloaded
//		} else if ( status == 2 ) {
//			//Downloading
//		}
//	}

	private void onClickItemLessons(View view, Lesson lesson) {

		DownloaderThread thread = null;
		int status = 0;
		String downloadUrl = "";
		String downloadType = "";
		System.out.println("LessonListAdapter.onClickItemLessons: " + status);
		switch (view.getId()) {
			case R.id.rl_play_mini:
				status = lesson.getDownloadStatusAudio();
				downloadUrl = lesson.getAudioSource();
				downloadType = "audio";
				break;
			case R.id.rl_teacher_aid:
				status = lesson.getDownloadStatusTeacherAid();
				downloadUrl = lesson.getTeacherAid();
				downloadType = "teacher";
				break;
			case R.id.rl_transcript:
				status = lesson.getDownloadStatusTranscript();
				downloadUrl = lesson.getTranscript();
				downloadType = "transcript";
				break;
		}
		System.out.println("DownloadService.countDownloads: " + DownloadService.countDownloads + " - Status: " + status);
		if (status == 0 && DownloadService.countDownloads < 2) {
			if (downloadType.equals("audio"))
				((TextView) view.findViewById(R.id.tv_icon_play_mini)).setText(context.getResources().getString(R.string.fa_icon_stop));
			thread = new DownloaderThread(activityHandler, lesson, downloadUrl, downloadType, (Activity) context, this);
			DownloadService.downloaderThread = thread;
			System.out.println("thread.getId(): " + thread.getId());
//			System.out.println("thread.getName(): " + thread.getName());
			DownloadService.startDownload(context);
			DownloadService.threadMap.put(lesson.getIdProperty(),thread);
			for (int i=0; i<lessons.size(); i++) {
				if (lessons.get(i).getIdProperty().equals(lesson.getIdProperty())) {
					lesson.setDownloadStatusAudio(2);
//					System.out.println("updating status: 2...");
					break;
				}
			}
			setLessonListItems(lessons);
		}
		if (status == 2) {
			if (downloadType.equals("audio"))
				((TextView) view.findViewById(R.id.tv_icon_play_mini)).setText(context.getResources().getString(R.string.fa_icon_play_mini));
			System.out.println("stopping download...");
			Thread t = DownloadService.threadMap.get(lesson.getIdProperty());
			if (t != null) {
				System.out.println("getId(): " + t.getId());
//				System.out.println("getName(): " + t.getName());
				t.interrupt();
				for (int i=0; i<lessons.size(); i++) {
					if (lessons.get(i).getIdProperty().equals(lesson.getIdProperty())) {
						lesson.setDownloadStatusAudio(0);
						break;
					}
				}
				setLessonListItems(lessons);
			}
		}
	}

	/**
	 * This is the Handler for this activity. It will receive messages from the
	 * DownloaderThreadTest and make the necessary updates to the UI.
	 */
	public Handler activityHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case MESSAGE_UPDATE_PROGRESS_BAR:
					String idLesson = msg.getData().getString("idLesson");
					int downloadProgress = msg.getData().getInt("downloadProgress");
					List<Lesson> listLessons = lessons;
					for (int i=0; i<listLessons.size(); i++) {
						if (listLessons.get(i).getIdProperty().equals(idLesson)) {
							listLessons.get(i).setDownloadProgress(downloadProgress);
							break;
						}
					}
					setLessonListItems(listLessons);
					break;

				case MESSAGE_CONNECTING_STARTED:
					break;

				case MESSAGE_DOWNLOAD_STARTED:
					break;

				case MESSAGE_DOWNLOAD_COMPLETE:
					break;

				case MESSAGE_DOWNLOAD_CANCELED:
					break;

				case MESSAGE_ENCOUNTERED_ERROR:
					break;

				default:
					break;
			}
		}
	};
}