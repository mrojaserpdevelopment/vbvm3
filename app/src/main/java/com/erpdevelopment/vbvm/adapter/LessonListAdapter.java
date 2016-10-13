package com.erpdevelopment.vbvm.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.service.DownloadService;
import com.erpdevelopment.vbvm.service.DownloadService2;
import com.erpdevelopment.vbvm.service.DownloaderThread2;
import com.erpdevelopment.vbvm.utils.FontManager;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LessonListAdapter extends BaseAdapter {

	private Context context;
	private List<Lesson> lessons;
//	private Lesson lesson;
	public static final String LOG_TAG = "Android Downloader";
	private static int count = 0;

	// Used to communicate state changes in the DownloaderThread
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
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_listview_lesson, parent, false);
		}
		final Lesson lesson = lessons.get(position);

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
//		TextView tvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);

		tvIconTeacherAid.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
		tvIconTranscript.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
//		tvIconPlayMini.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));

		ImageButton imgBtnPlay = (ImageButton) convertView.findViewById(R.id.img_btn_play_mini);

//		tvIconPlayMini.setOnClickListener(this);

//		System.out.println("LessonListAdapter.getView Progress: " + lesson.getDownloadProgress());


		ProgressBar pbPlayer = (ProgressBar) convertView.findViewById(R.id.pb_player);
//		if (DownloadService.lesson!=null && lesson.getIdProperty().equals(DownloadService.lesson.getIdProperty())) {
//			System.out.println("LessonListAdapter.getView Progress: " + lesson.getDownloadProgress());
			if (lesson.getDownloadProgress() > 0) {
//				System.out.println("LessonListAdapter.getView Progress: " + lesson.getDownloadProgress());
				pbPlayer.setProgress(lesson.getDownloadProgress());
			}
//		}

//		tvIconPlayMini.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
////				onClickLesson(view, lesson);
//				onClickItemLessons(view,lesson);
//			}
//		});

		imgBtnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickItemLessons(view,lesson);
			}
		});

		tvIconTeacherAid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				onClickLesson(view, lesson);
				onClickItemLessons(view,lesson);
			}
		});

		tvIconTranscript.setOnClickListener(new View.OnClickListener() {
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
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public void setStudyDetailsListItems(List<Lesson> newList) {
	    lessons = newList;
	    notifyDataSetChanged();
	}

	private void onClickLesson(View view, Lesson lesson) {
		if ( DownloadService.downloading ) {
			System.out.println("wait, a download is in progress...");
			return;
		}
		int status = 0;
		String downloadType = "";
		String url = "";
		switch (view.getId()) {
//			case R.id.tv_icon_play_mini:
			case R.id.img_btn_play_mini:
				System.out.println("LessonListAdapter.onClick play clicked... " + lesson.getTitle());
				status = lesson.getDownloadStatusAudio();
				downloadType = "audio";
				url = lesson.getAudioSource();
				break;
			case R.id.tv_icon_teacher_aid:
				status = lesson.getDownloadStatusTeacherAid();
				downloadType = "teacher";
				url = lesson.getTeacherAid();
				break;
			case R.id.tv_icon_transcript:
				status = lesson.getDownloadStatusTranscript();
				downloadType = "transcript";
				url = lesson.getTranscript();
				break;
		}
		if ( status == 0 ) {
			Intent intent = new Intent(context, DownloadService.class);
			intent.putExtra("lesson", lesson);
			intent.putExtra("downloadType", downloadType);
			intent.putExtra("url", url);
			context.startService(intent);
			((TextView)view).setText(context.getResources().getString(R.string.fa_icon_stop));
		} else if ( status == 1 ) {
			//Downloaded
		} else if ( status == 2 ) {
			//Downloading
		}
	}

//	@Override
//	public void onClick(View view) {
//		DownloaderThread2 thread;
//		System.out.println("LessonListAdapter.onClick");
//		switch (view.getId()) {
//			case R.id.tv_icon_play_mini:
//				System.out.println("LessonListAdapter.onClick play clicked...");
//				if (lesson.getDownloadStatusAudio() == 0) {
////					thread = new DownloaderThread2(lesson, lesson.getAudioSource(), "audio");
////					DownloadService2.downloaderThread = thread;
////					DownloadService2.startDownload(context);
//
//					Intent intent = new Intent(context, DownloadService.class);
//					intent.putExtra("lesson", lesson);
//					intent.putExtra("url", lesson.getAudioSource());
//					intent.putExtra("downloadType", "audio");
//					context.startService(intent);
//					DownloadService.incrementCount();
//				}
//				break;
//			case R.id.tv_icon_teacher_aid:
////				thread = new DownloaderThread2(lesson, lesson.getTeacherAid(), "teacher");
//				break;
//			case R.id.tv_icon_transcript:
////				thread = new DownloaderThread2(lesson, lesson.getTranscript(), "transcript");
//				break;
//
//		}
//	}

	private void onClickItemLessons(View view, Lesson lesson) {


		DownloaderThread2 thread = null;
		int status = 0;
		String downloadType = "";
		String url = "";

		switch (view.getId()) {
//			case R.id.tv_icon_play_mini:
			case R.id.img_btn_play_mini:

				status = DBHandleLessons.getLessonById(lesson.getIdProperty()).getDownloadStatusAudio();
				System.out.println("LessonListAdapter.onClickItemLessons: " + status);
				if (status == 0) {
					thread = new DownloaderThread2(activityHandler, lesson, lesson.getAudioSource(), "audio");
					DownloadService2.downloaderThread = thread;
					System.out.println("thread.getId(): " + thread.getId());
					System.out.println("thread.getName(): " + thread.getName());
					DownloadService2.startDownload(context);

					DownloadService2.threadMap.put(lesson.getIdProperty(),thread);
//					Intent intent = new Intent(context, DownloadService.class);
//					intent.putExtra("lesson", lesson);
//					intent.putExtra("url", lesson.getAudioSource());
//					intent.putExtra("downloadType", "audio");
//					context.startService(intent);
//					DownloadService.incrementCount();
				} else if (status == 2) {
					System.out.println("stopping download...");
					Thread t = Utilities.getThreadByName(lesson.getDownloadThreadName());
					System.out.println("getId(): " + t.getId());
					System.out.println("getName(): " + t.getName());



					if (thread != null) {
						thread.getId();
						thread.interrupt();
					} else {
						System.out.println("Thread is null...");
					}
				}
				break;
			case R.id.tv_icon_teacher_aid:
//				thread = new DownloaderThread2(lesson, lesson.getTeacherAid(), "teacher");
				break;
			case R.id.tv_icon_transcript:
//				thread = new DownloaderThread2(lesson, lesson.getTranscript(), "transcript");
				break;

		}

	}

	//this is our download file asynctask
	private class DownloadFileAsync extends AsyncTask<String, String, String> {

		//initialize our progress dialog/bar
		private ProgressDialog mProgressDialog2;
		private File rootDir = Environment.getExternalStorageDirectory();
		private File outFile;

		//    	private volatile boolean running = true;
		private FileCache fileCache;
		FileOutputStream f;
		InputStream in;

		String idLesson;
		String url;
		String downloadType;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			imgDownload.setVisibility(View.GONE);
//            tvDownloadInProgress.setVisibility(View.VISIBLE);
			createDialog();
			fileCache = new FileCache(context);
		}

		@Override
		protected String doInBackground(String... params) {
//			String audioUrl = params[0];
//			String pdf1Url = params[1];
//			String pdf2Url = params[2];
//			String pdf3Url = params[3];
//
//			downloadFile(audioUrl);
//			downloadFile(pdf1Url);
//			downloadFile(pdf2Url);
//			downloadFile(pdf3Url);

			idLesson = params[0];
			url = params[1];
			downloadType = params[2];
			downloadFile(url);

			return null;
		}

		private void downloadFile(String url) {
			outFile = fileCache.getFileAudioFolder(url);
			if (outFile.exists()) {
				outFile.delete();
				outFile = fileCache.getFileAudioFolder(url);
			}
			try {
				//connecting to url
				URL u = new URL(url);
				HttpURLConnection c = (HttpURLConnection) u.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();

				//lenghtOfFile is used for calculating download progress
				int lenghtOfFile = c.getContentLength();

				//this is where the file will be seen after the download
				f = new FileOutputStream(outFile.getPath());
				//file input is from the url
				in = c.getInputStream();

				//here's the download code
				byte[] buffer = new byte[1024];
				int len1 = 0;
				long total = 0;

				while ((len1 = in.read(buffer)) > 0) {
					total += len1; //total = total + len1
					publishProgress("" + (int)((total*100)/lenghtOfFile));
					f.write(buffer, 0, len1);
				}
				f.close();

			} catch (InterruptedIOException ie) {
				Log.d(LOG_TAG, ie.getMessage());
//				DBHandleLessons.updateLessonDownloadStatus(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty(), 0);
				DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, downloadType);
				outFile.delete();
			} catch (Exception e) {
//				DBHandleLessons.updateLessonDownloadStatus(AudioPlayerService.listTempLesson2.get(currentSongIndex).getIdProperty(), 0);
				DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, downloadType);
				outFile.delete();
			}
		}

		protected void onProgressUpdate(String... progress) {
			//Log.d(LOG_TAG,progress[0]);
			mProgressDialog2.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			//dismiss the dialog after the file was downloaded
			mProgressDialog2.dismiss();
//			imgDownload.setVisibility(View.GONE);
//            tvDownloadInProgress.setVisibility(View.GONE);
			Toast.makeText(context, "Lesson Downloaded!", Toast.LENGTH_LONG).show();
			DBHandleLessons.updateLessonDownloadStatus(idLesson, 1, downloadType);
		}

		@Override
		protected void onCancelled() {
//        	running = false;
		}

		//our progress bar settings
		private void createDialog() {
			mProgressDialog2 = new ProgressDialog(context);
			mProgressDialog2.setMessage("Downloading lesson");
			mProgressDialog2.setIndeterminate(false);
			mProgressDialog2.setMax(100);
			mProgressDialog2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog2.setCancelable(false);
			mProgressDialog2.show();
		}
	}

	/**
	 * This is the Handler for this activity. It will receive messages from the
	 * DownloaderThread and make the necessary updates to the UI.
	 */
	public Handler activityHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				/*
				 * Handling MESSAGE_UPDATE_PROGRESS_BAR:
				 * 1. Get the current progress, as indicated in the arg1 field
				 *    of the Message.
				 * 2. Update the progress bar.
				 */
				case MESSAGE_UPDATE_PROGRESS_BAR:
//					if(progressDialog != null)
//					{
//						int currentProgress = msg.arg1;
//						progressDialog.setProgress(currentProgress);
//					}

//					String idLesson = bundle.getString("idLesson");
//					int downloadProgress = bundle.getInt("downloadProgress");
//					List<Lesson> listLessons = mStudy.getLessons();
//					for (int i=0; i<listLessons.size(); i++) {
//						if (listLessons.get(i).getIdProperty().equals(idLesson)) {
//							listLessons.get(i).setDownloadProgress(downloadProgress);
//							break;
//						}
//					}

					String idLesson = msg.getData().getString("idLesson");
					int downloadProgress = msg.getData().getInt("downloadProgress");
					List<Lesson> listLessons = lessons;
					for (int i=0; i<listLessons.size(); i++) {
						if (listLessons.get(i).getIdProperty().equals(idLesson)) {
							listLessons.get(i).setDownloadProgress(downloadProgress);
							break;
						}
					}
					setStudyDetailsListItems(listLessons);
					break;

				/*
				 * Handling MESSAGE_CONNECTING_STARTED:
				 * 1. Get the URL of the file being downloaded. This is stored
				 *    in the obj field of the Message.
				 * 2. Create an indeterminate progress bar.
				 * 3. Set the message that should be sent if user cancels.
				 * 4. Show the progress bar.
				 */
				case MESSAGE_CONNECTING_STARTED:
//					if(msg.obj != null && msg.obj instanceof String)
//					{
//						String url = (String) msg.obj;
//						// truncate the url
//						if(url.length() > 16)
//						{
//							String tUrl = url.substring(0, 15);
//							tUrl += "...";
//							url = tUrl;
//						}
//						String pdTitle = thisActivity.getString(R.string.progress_dialog_title_connecting);
//						String pdMsg = thisActivity.getString(R.string.progress_dialog_message_prefix_connecting);
//						pdMsg += " " + url;
//
//						dismissCurrentProgressDialog();
//						progressDialog = new ProgressDialog(thisActivity);
//						progressDialog.setTitle(pdTitle);
//						progressDialog.setMessage(pdMsg);
//						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//						progressDialog.setIndeterminate(true);
//						// set the message to be sent when this dialog is canceled
//						Message newMsg = Message.obtain(this, MESSAGE_DOWNLOAD_CANCELED);
//						progressDialog.setCancelMessage(newMsg);
//						progressDialog.show();
//					}
					break;

				/*
				 * Handling MESSAGE_DOWNLOAD_STARTED:
				 * 1. Create a progress bar with specified max value and current
				 *    value 0; assign it to progressDialog. The arg1 field will
				 *    contain the max value.
				 * 2. Set the title and text for the progress bar. The obj
				 *    field of the Message will contain a String that
				 *    represents the name of the file being downloaded.
				 * 3. Set the message that should be sent if dialog is canceled.
				 * 4. Make the progress bar visible.
				 */
				case MESSAGE_DOWNLOAD_STARTED:
					// obj will contain a String representing the file name
//					if(msg.obj != null && msg.obj instanceof String)
//					{
//						int maxValue = msg.arg1;
//						String fileName = (String) msg.obj;
//						String pdTitle = thisActivity.getString(R.string.progress_dialog_title_downloading);
//						String pdMsg = thisActivity.getString(R.string.progress_dialog_message_prefix_downloading);
//						pdMsg += " " + fileName;
//
//						dismissCurrentProgressDialog();
//						progressDialog = new ProgressDialog(thisActivity);
//						progressDialog.setTitle(pdTitle);
//						progressDialog.setMessage(pdMsg);
//						progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//						progressDialog.setProgress(0);
//						progressDialog.setMax(maxValue);
//						// set the message to be sent when this dialog is canceled
//						Message newMsg = Message.obtain(this, MESSAGE_DOWNLOAD_CANCELED);
//						progressDialog.setCancelMessage(newMsg);
//						progressDialog.setCancelable(true);
//						progressDialog.show();
//					}
					break;

				/*
				 * Handling MESSAGE_DOWNLOAD_COMPLETE:
				 * 1. Remove the progress bar from the screen.
				 * 2. Display Toast that says download is complete.
				 */
				case MESSAGE_DOWNLOAD_COMPLETE:
					String downloadedFile = (String) msg.obj;
					System.out.println("downloaded file is: " + downloadedFile);
//					dismissCurrentProgressDialog();
//					displayMessage(getString(R.string.user_message_download_complete));
					break;

				/*
				 * Handling MESSAGE_DOWNLOAD_CANCELLED:
				 * 1. Interrupt the downloader thread.
				 * 2. Remove the progress bar from the screen.
				 * 3. Display Toast that says download is complete.
				 */
				case MESSAGE_DOWNLOAD_CANCELED:
					System.out.println("download cancelled");
//					if(downloaderThread != null)
//					{
//						downloaderThread.interrupt();
//					}
//					dismissCurrentProgressDialog();
//					displayMessage(getString(R.string.user_message_download_canceled));
					break;

				/*
				 * Handling MESSAGE_ENCOUNTERED_ERROR:
				 * 1. Check the obj field of the message for the actual error
				 *    message that will be displayed to the user.
				 * 2. Remove any progress bars from the screen.
				 * 3. Display a Toast with the error message.
				 */
				case MESSAGE_ENCOUNTERED_ERROR:
					// obj will contain a string representing the error message
					if(msg.obj != null && msg.obj instanceof String)
					{
						String errorMessage = (String) msg.obj;
//						dismissCurrentProgressDialog();
//						displayMessage(errorMessage);
					}
					break;

				default:
					// nothing to do here
					break;
			}
		}
	};


}
