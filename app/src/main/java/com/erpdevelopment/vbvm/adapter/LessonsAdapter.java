package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.helper.AudioPlayerHelper;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.service.DownloadAllService;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.service.downloader.DownloaderThread;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.FontManager;
import com.erpdevelopment.vbvm.utils.PDFTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LessonsAdapter extends BaseAdapter {

	private Context context;
	private List<Lesson> lessons;
	private LayoutInflater inflater;
	private View rootView;
	private Intent intentDownloadAll;
	public static Lesson mCurrentLesson;

	// Used to communicate state changes in the DownloaderThreadTest
	public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
	public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
	public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
	public static final int MESSAGE_CONNECTING_STARTED = 1004;
	public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;

	private int sizeListComplete;
	private int sizeListIncomplete;

	private static final int TYPE_LESSON = 0;
	private static final int TYPE_DIVIDER = 1;

	public LessonsAdapter(Context context, List<Lesson> lessons) {
		this.context = context;
		this.lessons = lessons;
	}

	public LessonsAdapter(Context context, List<Lesson> lessons, View rootView) {
		this.context = context;
		this.lessons = lessons;
		this.rootView = rootView;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (((Lesson)getItem(position)).isSection()) {
			return TYPE_DIVIDER;
		}
		return TYPE_LESSON;
	}

	public void setSizeListComplete(int size) {
		sizeListComplete = size;
	}

	public void setSizeListIncomplete(int size) {
		sizeListIncomplete = size;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (convertView==null) {
			if (type == TYPE_DIVIDER) {
				convertView = inflater.inflate(R.layout.item_listview_separator, parent, false);
				convertView.setOnClickListener(null);
				convertView.setOnLongClickListener(null);
				convertView.setLongClickable(false);
			} else
				convertView = inflater.inflate(R.layout.item_listview_lesson, parent, false);
		}
		Lesson lesson = lessons.get(position);
		if (type==TYPE_DIVIDER) {
			TextView title = (TextView) convertView.findViewById(R.id.tv_lessons_completed_title);
			TextView count = (TextView) convertView.findViewById(R.id.tv_lessons_completed_count);
			if (lesson.isSectionCompleted()) {
				title.setText("Completed lessons");
				count.setText(sizeListComplete + "");
			} else {
				title.setText("Lessons");
				count.setText(sizeListIncomplete + "");
			}
		} else {
			TextView itemTvLessonNo = (TextView) convertView.findViewById(R.id.tv_lesson_no);
			TextView itemTvLessonLength = (TextView) convertView.findViewById(R.id.tv_lesson_length);
			TextView itemTvLessonDescription = (TextView) convertView.findViewById(R.id.tv_lesson_description);
			TextView itemTvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);
			TextView itemTvIconTeacherAid = (TextView) convertView.findViewById(R.id.tv_icon_teacher_aid);
			TextView itemTvIconTranscript = (TextView) convertView.findViewById(R.id.tv_icon_transcript);
			ProgressBar itemPbAudio = (ProgressBar) convertView.findViewById(R.id.pb_audio);
			ProgressBar itemPbTeacher = (ProgressBar) convertView.findViewById(R.id.pb_teacher);
			ProgressBar itemPbTranscript = (ProgressBar) convertView.findViewById(R.id.pb_transcript);
			View itemViewDownloadedAudio = convertView.findViewById(R.id.vw_downloaded_audio);
			View itemViewDownloadedTeacher = convertView.findViewById(R.id.vw_downloaded_teach);
			View itemViewDownloadedTranscript = convertView.findViewById(R.id.vw_downloaded_trans);
			RelativeLayout rlTeacherAid = (RelativeLayout) convertView.findViewById(R.id.rl_teacher_aid);
			RelativeLayout rlTranscript = (RelativeLayout) convertView.findViewById(R.id.rl_transcript);
			RelativeLayout rlPlayMini = (RelativeLayout) convertView.findViewById(R.id.rl_play_mini);

			itemTvLessonNo.setText(lesson.getTitle().substring(lesson.getTitle().lastIndexOf(" ") + 1));
			itemTvLessonDescription.setText(lesson.getLessonsDescription());
			itemTvLessonLength.setText(lesson.getAudioLength());
			itemTvIconTeacherAid.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
			itemTvIconTranscript.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
			itemTvIconPlayMini.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
			itemPbAudio.setProgress(lesson.getDownloadProgressAudio());
			itemPbTeacher.setProgress(lesson.getDownloadProgressTeacher());
			itemPbTranscript.setProgress(lesson.getDownloadProgressTranscript());
			itemViewDownloadedAudio.setVisibility(View.INVISIBLE);

			if (lesson.getAudioSource().equals("")) {
				itemTvIconPlayMini.setVisibility(View.INVISIBLE);
				itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
			} else {
				itemTvIconPlayMini.setVisibility(View.VISIBLE);
				if (lesson.getDownloadStatusAudio() == 1) {
					itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
					itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					itemViewDownloadedAudio.setVisibility(View.VISIBLE);
				} else if (lesson.getDownloadStatusAudio() == 2) {
					itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_stop));
					itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					itemViewDownloadedAudio.setVisibility(View.INVISIBLE);
				} else {
					itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
					itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					itemViewDownloadedAudio.setVisibility(View.INVISIBLE);
				}
			}
			if (lesson.getTeacherAid().equals("")) {
				itemTvIconTeacherAid.setVisibility(View.INVISIBLE);
				itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
			} else {
				itemTvIconTeacherAid.setVisibility(View.VISIBLE);
				if (lesson.getDownloadStatusTeacherAid() == 1)
					itemViewDownloadedTeacher.setVisibility(View.VISIBLE);
				else
					itemViewDownloadedTeacher.setVisibility(View.INVISIBLE);
			}

			if (lesson.getTranscript().equals("")) {
				itemTvIconTranscript.setVisibility(View.INVISIBLE);
				itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
			} else {
				itemTvIconTranscript.setVisibility(View.VISIBLE);
				if (lesson.getDownloadStatusTranscript() == 1)
					itemViewDownloadedTranscript.setVisibility(View.VISIBLE);
				else
					itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
			}
			rlPlayMini.setOnClickListener(view -> onClickItemLessons(view, lesson, position));
			rlTeacherAid.setOnClickListener(view -> onClickItemLessons(view, lesson, position));
			rlTranscript.setOnClickListener(view -> onClickItemLessons(view, lesson, position));
		}
		return convertView;
	}

	public void setSelectedPosition(int pos){
		notifyDataSetChanged();
	}

	public void setLessonListItems(List<Lesson> newList) {
	    lessons = newList;
	    notifyDataSetChanged();
	}

	public void setIntentServiceDownloadAll(Intent intent) {
		intentDownloadAll = intent;
	}

	private void onClickItemLessons(View view, Lesson lesson, int position) {
		int status = 0;
		String downloadUrl = "";
		String downloadType = "";
		mCurrentLesson = lesson;
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
		if ( downloadUrl.isEmpty() )
			return;
		if ( status == 0 && !DownloadAllService.downloading && DownloadService.countDownloads < 2 ) {
			if (downloadType.equals("audio")) {
				TextView tvPlayMini = (TextView) view.findViewById(R.id.tv_icon_play_mini);
				tvPlayMini.setText(context.getResources().getString(R.string.fa_icon_stop));
				tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
				lesson.setDownloadStatusAudio(2);
			} else if (downloadType.equals("teacher")) {
				lesson.setDownloadStatusTeacherAid(2);
			} else if (downloadType.equals("transcript")) {
				lesson.setDownloadStatusTranscript(2);
			}

			DownloadService.startDownload((Activity)context, lesson, downloadUrl, downloadType, lessons);
			DownloadService.threadMap.put(lesson.getIdProperty(),DownloadService.downloaderThread);
//			for (int i=0; i<lessons.size(); i++) {
//				if (lessons.get(i).getIdProperty().equals(lesson.getIdProperty())) {
//					lesson.setDownloadStatusAudio(2);
//					break;
//				}
//			}
			setLessonListItems(lessons);
		}
		if (status == 1) {
			if (downloadType.equals("audio")) {
				if (!(lesson.getIdProperty().equals(FilesManager.lastLessonId))) {
					AudioPlayerService.created = false;
					//save current/old position in track before updating to new position
					if (!FilesManager.lastLessonId.equals("")) {
						DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, (int) AudioPlayerService.currentPositionInTrack);
						Lesson oldLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
						if (oldLesson.getState().equals("playing"))
							DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
					}
					Lesson newLesson = DBHandleLessons.getLessonById(lesson.getIdProperty());
					AudioPlayerService.savedOldPositionInTrack =
							AudioPlayerService.currentPositionInTrack =
									newLesson.getCurrentPosition();
				}
				FilesManager.lastLessonId = lesson.getIdProperty();
				AudioPlayerHelper helper = new AudioPlayerHelper();
				helper.setLessonToPlay(lesson);
				helper.initContext((Activity) context, rootView);
			}
			if (downloadType.equals("teacher")) {
				new PDFTools().showPDFUrl(context,lesson.getTeacherAid());
			}
			if (downloadType.equals("transcript")) {
				new PDFTools().showPDFUrl(context,lesson.getTranscript());
			}
		}
		if ( status == 2 ) {
			if ( !DownloadAllService.downloading )
				stopDownload(view,downloadType,lesson);
			else
				stopDownloadAll(view,downloadType,lesson);
		}
	}

	private void stopDownload(View view, String downloadType, Lesson lesson) {
		if (downloadType.equals("audio")) {
			TextView tvPlayMini = (TextView) view.findViewById(R.id.tv_icon_play_mini);
			tvPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
			tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			DownloaderThread t = DownloadService.threadMap.get(lesson.getIdProperty());
			if (t != null) {
				t.stopDownload();
				lesson.setDownloadStatusAudio(0);
				lesson.setDownloadProgressAudio(0);
				setLessonListItems(lessons);
//				t.interrupt();
//				lesson.setDownloadStatusAudio(0);
//				setLessonListItems(lessons);
			}
		}
		//Stop the service if no downloads left
//		if ( DownloadService.IS_SERVICE_RUNNING && DownloadService.countDownloads==0 ) {
//			Intent service = new Intent(context, DownloadService.class);
//			service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//			DownloadService.IS_SERVICE_RUNNING = false;
//			context.startService(service);
//		}
	}

	private void stopDownloadAll(View view, String downloadType, Lesson lesson) {
		DownloadAllService.stopped = true;
		TextView tvPlayMini = (TextView) view.findViewById(R.id.tv_icon_play_mini);
		tvPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
		tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		lesson.setDownloadStatusAudio(0);
		lesson.setDownloadProgressAudio(0);
		setLessonListItems(lessons);
	}

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
							listLessons.get(i).setDownloadProgressAudio(downloadProgress);
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