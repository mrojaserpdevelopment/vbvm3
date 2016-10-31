package com.erpdevelopment.vbvm.adapter;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.helper.AudioPlayerHelper;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.service.DownloadServiceTest;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.service.downloader.DownloaderThread;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.FontManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	public static final String LOG_TAG = "Android Downloader";
	private static int count = 0;
	private ArrayList<Lesson> listTempLesson = new ArrayList<Lesson>();
	private View rootView;
	private Intent intentDownloadAll;

	// Used to communicate state changes in the DownloaderThreadTest
	public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
	public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
	public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
	public static final int MESSAGE_CONNECTING_STARTED = 1004;
	public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;

	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public LessonsAdapter(Context context, List<Lesson> lessons) {
		this.context = context;
		this.lessons = lessons;
	}

	public LessonsAdapter(Context context, List<Lesson> lessons, View rootView) {
		this.context = context;
		this.lessons = lessons;
		this.rootView = rootView;
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
			viewHolderItem.itemTvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);
			viewHolderItem.itemTvIconTeacherAid = (TextView) convertView.findViewById(R.id.tv_icon_teacher_aid);
			viewHolderItem.itemTvIconTranscript = (TextView) convertView.findViewById(R.id.tv_icon_transcript);
			viewHolderItem.itemPbAudio = (ProgressBar) convertView.findViewById(R.id.pb_audio);
			viewHolderItem.itemPbTeacher = (ProgressBar) convertView.findViewById(R.id.pb_teacher);
			viewHolderItem.itemPbTranscript = (ProgressBar) convertView.findViewById(R.id.pb_transcript);
			viewHolderItem.itemViewDownloadedAudio = convertView.findViewById(R.id.vw_downloaded_audio);
			viewHolderItem.itemViewDownloadedTeacher = convertView.findViewById(R.id.vw_downloaded_teach);
			viewHolderItem.itemViewDownloadedTranscript = convertView.findViewById(R.id.vw_downloaded_trans);
			viewHolderItem.rlTeacherAid = (RelativeLayout) convertView.findViewById(R.id.rl_teacher_aid);
			viewHolderItem.rlTranscript = (RelativeLayout) convertView.findViewById(R.id.rl_transcript);
			viewHolderItem.rlPlayMini = (RelativeLayout) convertView.findViewById(R.id.rl_play_mini);
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}

		final Lesson lesson = lessons.get(position);

		viewHolderItem.itemTvLessonNo.setText(lesson.getTitle().substring(lesson.getTitle().lastIndexOf(" ")+1));
		viewHolderItem.itemTvLessonDescription.setText(lesson.getLessonsDescription());
		viewHolderItem.itemTvLessonLength.setText(lesson.getAudioLength());
		viewHolderItem.itemTvIconTeacherAid.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		viewHolderItem.itemTvIconTranscript.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		viewHolderItem.itemTvIconPlayMini.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		viewHolderItem.itemPbAudio.setProgress(lesson.getDownloadProgressAudio());
		viewHolderItem.itemPbTeacher.setProgress(lesson.getDownloadProgressTeacher());
		viewHolderItem.itemPbTranscript.setProgress(lesson.getDownloadProgressTranscript());
		viewHolderItem.itemViewDownloadedAudio.setVisibility(View.GONE);

		if (lesson.getAudioSource().equals("")) {
			viewHolderItem.itemTvIconPlayMini.setVisibility(View.INVISIBLE);
			viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
		} else {
			viewHolderItem.itemTvIconPlayMini.setVisibility(View.VISIBLE);
			if (lesson.getDownloadStatusAudio() == 1) {
				viewHolderItem.itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
				viewHolderItem.itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				viewHolderItem.itemViewDownloadedAudio.setVisibility(View.VISIBLE);
			} else if (lesson.getDownloadStatusAudio() == 2) {
				viewHolderItem.itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_stop));
				viewHolderItem.itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				viewHolderItem.itemViewDownloadedAudio.setVisibility(View.INVISIBLE);
			} else {
				viewHolderItem.itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
				viewHolderItem.itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				viewHolderItem.itemViewDownloadedAudio.setVisibility(View.INVISIBLE);
			}
		}
		if (lesson.getTeacherAid().equals("")) {
			viewHolderItem.itemTvIconTeacherAid.setVisibility(View.INVISIBLE);
			viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
		} else {
			viewHolderItem.itemTvIconTeacherAid.setVisibility(View.VISIBLE);
			if (lesson.getDownloadStatusTeacherAid() == 1)
				viewHolderItem.itemViewDownloadedTeacher.setVisibility(View.VISIBLE);
			else
				viewHolderItem.itemViewDownloadedTeacher.setVisibility(View.INVISIBLE);
		}

		if (lesson.getTranscript().equals("")) {
			viewHolderItem.itemTvIconTranscript.setVisibility(View.INVISIBLE);
			viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
		} else {
			viewHolderItem.itemTvIconTranscript.setVisibility(View.VISIBLE);
			if (lesson.getDownloadStatusTranscript() == 1)
				viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.VISIBLE);
			else
				viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.INVISIBLE);
		}

		viewHolderItem.rlPlayMini.setOnClickListener(view -> onClickItemLessons(view,lesson,position));
		viewHolderItem.rlTeacherAid.setOnClickListener(view -> onClickItemLessons(view,lesson,position));
		viewHolderItem.rlTranscript.setOnClickListener(view -> onClickItemLessons(view,lesson,position));

		return convertView;
	}

	static class ViewHolderItem {
		RelativeLayout rlTeacherAid;
		RelativeLayout rlTranscript;
		RelativeLayout rlPlayMini;
		TextView itemTvLessonNo;
		TextView itemTvLessonLength;
		TextView itemTvLessonDescription;
		TextView itemTvIconTeacherAid;
		TextView itemTvIconTranscript;
		TextView itemTvIconPlayMini;
		ProgressBar itemPbAudio;
		ProgressBar itemPbTeacher;
		ProgressBar itemPbTranscript;
		View itemViewDownloadedTeacher;
		View itemViewDownloadedTranscript;
		View itemViewDownloadedAudio;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public void setLessonListItems(List<Lesson> newList) {
	    lessons = newList;
//		TextView tvTitleLessons = (TextView) rootView.findViewById(R.id.tv_title_lessons);
//		TextView tvCountLessons = (TextView) rootView.findViewById(R.id.tv_lesson_count);
		RelativeLayout rlTitleLvLessons = (RelativeLayout) rootView.findViewById(R.id.rl_title_lv_lessons);
		if (newList.size()==0){
//			tvTitleLessons.setVisibility(View.GONE);
//			tvCountLessons.setVisibility(View.GONE);
			rlTitleLvLessons.setVisibility(View.GONE);
		} else {
//			tvTitleLessons.setVisibility(View.VISIBLE);
//			tvCountLessons.setVisibility(View.VISIBLE);
			rlTitleLvLessons.setVisibility(View.VISIBLE);
		}
	    notifyDataSetChanged();
	}

	public void setIntentServiceDownloadAll(Intent intent) {
		intentDownloadAll = intent;
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
//				System.out.println("LessonsAdapter.onClick play clicked... " + lesson.getTitle());
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

	private void onClickItemLessons(View view, Lesson lesson, int position) {

		DownloaderThread thread = null;
		int status = 0;
		String downloadUrl = "";
		String downloadType = "";
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
		System.out.println("LessonsAdapter.onClickItemLessons: " + status);
		if ( downloadUrl.isEmpty() )
			return;
		if ( status == 0 && !DownloadServiceTest.downloading && DownloadService.countDownloads < 2 ) {
			if (downloadType.equals("audio")) {
				TextView tvPlayMini = (TextView) view.findViewById(R.id.tv_icon_play_mini);
				tvPlayMini.setText(context.getResources().getString(R.string.fa_icon_stop));
				tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
			}
			thread = new DownloaderThread(activityHandler, lesson, downloadUrl, downloadType, (Activity) context, this);
			DownloadService.downloaderThread = thread;
			DownloadService.startDownload(context);
			DownloadService.threadMap.put(lesson.getIdProperty(),thread);
			for (int i=0; i<lessons.size(); i++) {
				if (lessons.get(i).getIdProperty().equals(lesson.getIdProperty())) {
					lesson.setDownloadStatusAudio(2);
					break;
				}
			}
			setLessonListItems(lessons);
		}
		if ( status == 2 ) {
			if ( !DownloadServiceTest.downloading ) {
				stopDownload(view,downloadType,lesson);
			} else {
				stopDownloadAll(view,downloadType,lesson);
			}
		}
		if (status == 1) {
//			lesson.setStudyThumbnailSource(study.getThumbnailSource());
//			lesson.setStudyLessonsSize(study.getLessons().size());
//			lesson = DBHandleLessons.getLessonById(lesson.getIdProperty());
			System.out.println("LessonsAdapter.onClickItemLessons: download status " + lesson.getDownloadStatusAudio());
			if (downloadType.equals("audio")) {
				if (!(lesson.getIdProperty().equals(FilesManager.lastLessonId))) {
					AudioPlayerService.created = false;
					//save current/old position in track before updating to new position
					if (!FilesManager.lastLessonId.equals("")) {
						System.out.println("old lesson Id is: " + FilesManager.lastLessonId);
						DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, (int) AudioPlayerService.currentPositionInTrack);
						System.out.println("position saved is: " + AudioPlayerService.currentPositionInTrack);

						Lesson oldLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
						if (oldLesson.getState().equals("playing"))
							DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
					}
//					Lesson oldLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
//					if (oldLesson.getState().equals("playing"))
//						DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
					//Update position in track with new selected lesson's
					Lesson newLesson = DBHandleLessons.getLessonById(lesson.getIdProperty());
					//								DBHandleLessons.updateLessonState(newLesson.getIdProperty(), newLesson.getCurrentPosition(), "playing");
					AudioPlayerService.currentPositionInTrack = newLesson.getCurrentPosition();
					AudioPlayerService.savedOldPositionInTrack = AudioPlayerService.currentPositionInTrack;
					System.out.println("position restored is: " + AudioPlayerService.currentPositionInTrack);
				}

				FilesManager.lastLessonId = lesson.getIdProperty();
//				for (int i = 0; i < lesson.getStudyLessonsSize(); i++) {
//					Lesson les = new Lesson();
//					les.setIdProperty(lesson.getIdProperty());
//					les.setAudioSource(lesson.getAudioSource());
//					les.setTitle(lesson.getTitle());
//					les.setLessonsDescription(lesson.getLessonsDescription());
//					les.setStudyThumbnailSource(lesson.getStudyThumbnailSource());
//					listTempLesson.add(les);
//				}
				for (int i = 0; i<lessons.size(); i++) {
					Lesson les = new Lesson();
					les.setIdProperty(lessons.get(i).getIdProperty());
					les.setAudioSource(lessons.get(i).getAudioSource());
					les.setTitle(lessons.get(i).getTitle());
					les.setLessonsDescription(lessons.get(i).getLessonsDescription());
					les.setStudyThumbnailSource(lessons.get(i).getStudyThumbnailSource());
					listTempLesson.add(les);
				}
				AudioPlayerService.listTempLesson2 = listTempLesson;
				System.out.println("lesson to be playted: " + FilesManager.lastLessonId);
				Bundle bundle = new Bundle();
				bundle.putInt("position", position);
				bundle.putString("thumbnailSource", lesson.getStudyThumbnailSource());
				bundle.putString("description", lesson.getLessonsDescription());
				bundle.putString("title", lesson.getTitle());
				bundle.putInt("size", lesson.getStudyLessonsSize());
				AudioPlayerHelper helper = new AudioPlayerHelper();
//				AudioPlayerHelper helper = AudioPlayerHelper.getInstance();
				helper.setBundleExtras(bundle);
				helper.initContext((Activity) context, rootView);
			}
		}
	}

	private void stopDownload(View view, String downloadType, Lesson lesson) {
//		if ( !DownloadServiceTest.downloading ) {
//			//Downloading a single lesson
			if (downloadType.equals("audio")) {
				Thread t = DownloadService.threadMap.get(lesson.getIdProperty());
				if (t != null) {
					t.interrupt();
//				for (int i = 0; i < lessons.size(); i++) {
//					if (lessons.get(i).getIdProperty().equals(lesson.getIdProperty())) {
//						lesson.setDownloadStatusAudio(0);
//						break;
//					}
//				}
					lesson.setDownloadStatusAudio(0);
					setLessonListItems(lessons);
				}
				TextView tvPlayMini = (TextView) view.findViewById(R.id.tv_icon_play_mini);
				tvPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
				tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			}
//		} else {
			//Downloading all lessons
//				stopDownloadAll();
//				LessonsFragment lf = LessonsFragment.newInstance(0);
//				context.stopService(lf.getIntentDownloadAll());
//			((Activity) context).stopService(intentDownloadAll);
//		}
	}

	private void stopDownloadAll(View view, String downloadType, Lesson lesson) {
//		if (downloadType.equals("audio")) {
//			((Activity) context).stopService(intentDownloadAll);
//			TextView tvPlayMini = (TextView) view.findViewById(R.id.tv_icon_play_mini);
//			tvPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
//			tvPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//
//			//				for (int i = 0; i < lessons.size(); i++) {
////					if (lessons.get(i).getIdProperty().equals(lesson.getIdProperty())) {
////						lesson.setDownloadStatusAudio(0);
////						break;
////					}
////				}
//			lesson.setDownloadStatusAudio(0);
//			setLessonListItems(lessons);
//		}
//		((Activity) context).stopService(intentDownloadAll);
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