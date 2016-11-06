package com.erpdevelopment.vbvm.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.utils.BitmapManager2;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

public class DownloadAllService extends IntentService {

	private int result = Activity.RESULT_CANCELED;
	public static final String URL = "urlpath";
	public static final String FILENAME = "filename";
	public static final String RESULT = "result";
	public static final String NOTIFICATION2 = "com.erpdevelopment.vbvm2.service.receiver2";
	public static final String NOTIFICATION_START = "notification_start";
	public static final String NOTIFICATION_DOWNLOAD_ALL_PROGRESS = "notification_download_all_progress";
	public static final String NOTIFICATION_DOWNLOAD_ALL_COMPLETE = "notification_download_all_complete";
	public static String downloadAllTitle = "";
	private FileCache fileCache;
	public static int countDownloads = 0;	  // number of current IS_SERVICE_RUNNING files
	public static boolean downloading = false;
	//private static ByteArrayBuffer baf;
	private static final int DOWNLOAD_BUFFER_SIZE = 4096;
	private boolean stopped = false;

	private String mIdLesson;
	private String mDownloadType;
	private int mDownloadProgress;

	  public DownloadAllService() {
	    super("DownloadAllService");
	    fileCache = new FileCache(MainActivity.mainCtx);
	    downloading = true;
	  }

      public static synchronized void incrementCount() {
    	  countDownloads++;
      }
      
      public static synchronized void decrementCount() {
          countDownloads--;
      }

	@Override
	public void onCreate() {
		super.onCreate();
	}

	// will be called asynchronously by Android
   	  @Override
   	  protected void onHandleIntent(Intent intent) {
		  System.out.println("DownloadAllService.onHandleIntent");
		  downloading = true;
//		  lesson = (Lesson) intent.getExtras().getParcelable("lesson");
		  String idLesson = intent.getExtras().getString("idLesson");
		  String url = intent.getExtras().getString("url");
		  String downloadType = intent.getExtras().getString("downloadType");

		  mIdLesson = idLesson;
		  mDownloadProgress = 0;
		  mDownloadType = downloadType;

//		  if ( !url.equals("") ) {
			  DBHandleLessons.updateLessonDownloadStatus(idLesson, 2, downloadType);
//			  Intent i = new Intent(NOTIFICATION2);
//			  i.putExtra("updateDownloadingStatus", true);
//			  sendBroadcast(i);
//		  publishDownloadStart(idLesson);
		  downloadFromUrl(idLesson, url, downloadType);
//		  publishDownloadResults(idLesson, BitmapManager.getFileNameFromUrl(url), url);
//		  }
//		  IS_SERVICE_RUNNING = false;
//		  }
   	  }
	  
	  public void downloadFromUrl(String idLesson, String urlPath, String downloadType) {
		  File outputTemp = null;
		  int downloadStatus = 0;
		  try {
			  outputTemp = fileCache.getFileTempFolder(urlPath);		    
			  if (outputTemp.exists()) {
				  outputTemp.delete();
			      outputTemp = fileCache.getFileTempFolder(urlPath);
			  }
	          URL url = new URL(urlPath);
	          long startTime = System.currentTimeMillis();
	          Log.d("DownloadManager", "download begining");
	          Log.d("DownloadManager", "download url:" + url);

		      URLConnection conn = url.openConnection();
	          InputStream is = conn.getInputStream();
			  // start download
			  BufferedInputStream inStream = new BufferedInputStream(conn.getInputStream());
//			  outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
			  FileOutputStream fileStream = new FileOutputStream(outputTemp);
			  BufferedOutputStream outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
			  byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
			  int bytesRead = 0, totalRead = 0;
  			  int lengthOfFile = conn.getContentLength();
			  handler.postDelayed(runnable, 100);
			  while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
			  {
				  outStream.write(data, 0, bytesRead);
				  totalRead += bytesRead;
				  if(stopped){
//					  publishDownloadProgress(idLesson,0,downloadType);
					  mDownloadProgress = 0;
					  break;
				  }
//				  publishDownloadProgress(idLesson,  (int)((totalRead*100)/lengthOfFile), downloadType);
				  mDownloadProgress = (int)((totalRead*100)/lengthOfFile);
			  }
			  mDownloadProgress = 0;
			  Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

			  outStream.close();
			  fileStream.close();
			  inStream.close();

			  if (stopped) {
				  result = Activity.RESULT_CANCELED;
				  outputTemp.delete();
				  DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, downloadType);
				  publishDownloadResults(idLesson, BitmapManager2.getFileNameFromUrl(urlPath), downloadType);
				  downloading = false;
				  stopSelf();
			  } else {
				  // successfully finished
				  result = Activity.RESULT_OK;
				  //copy downloaded file from temp to audio folder
				  File output = fileCache.getFileAudioFolder(urlPath);
				  fileCache.copyFile(outputTemp, output);
				  downloadStatus = 1;
				  outputTemp.delete();
				  DBHandleLessons.updateLessonDownloadStatus(idLesson, 1, downloadType);
				  publishDownloadResults(idLesson, BitmapManager2.getFileNameFromUrl(urlPath), downloadType);
				  handler.removeCallbacks(runnable);
			  }
//			  outputTemp.delete();
//			  DBHandleLessons.updateLessonDownloadStatus(idLesson, downloadStatus, downloadType);
//			  publishDownloadResults(idLesson, BitmapManager.getFileNameFromUrl(urlPath), downloadType);
		   }
		   catch(MalformedURLException e)
		   {
			   DBHandleLessons.updateLessonDownloadStatus(idLesson, 0);
			   outputTemp.delete();
		   }
		   catch(FileNotFoundException e)
		   {
			   DBHandleLessons.updateLessonDownloadStatus(idLesson, 0);
			   outputTemp.delete();
		   }
		   catch(IOException e)
		   {
               DBHandleLessons.updateLessonDownloadStatus(idLesson, 0);
			   outputTemp.delete();
			   e.printStackTrace();
		   } 
		   catch(Exception e)
		   {
			   DBHandleLessons.updateLessonDownloadStatus(idLesson, 0);
			   outputTemp.delete();
			   e.printStackTrace();
		   }
	  }

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			publishDownloadProgress(mIdLesson, mDownloadProgress, mDownloadType);
			handler.postDelayed(this, 1000);
		}
	};

	private void publishDownloadProgress(String idLesson, int downloadProgress, String downloadType) {
		Intent intent = new Intent(NOTIFICATION_DOWNLOAD_ALL_PROGRESS);
		intent.putExtra("idLesson", idLesson);
		intent.putExtra("downloadProgress", downloadProgress);
		intent.putExtra("downloadType", downloadType);
		sendBroadcast(intent);
	}

	private void publishDownloadResults(String idLesson, String fileName, String downloadType) {
		Intent intent = new Intent(NOTIFICATION_DOWNLOAD_ALL_COMPLETE);
		intent.putExtra(RESULT, result);
		intent.putExtra(FILENAME, fileName);
		intent.putExtra("idLesson", idLesson);
		intent.putExtra("downloadType", downloadType);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
//		stopped = true;
		System.out.println("DownloadAllService.onDestroy");
		super.onDestroy();
	}

	private BroadcastReceiver receiverStopDownload = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			boolean oneDownloadComplete = false; //At least one download has finished successfully
			if (bundle != null) {
//                int resultCode = bundle.getInt(DownloadAllService.RESULT);
//                String fileName = bundle.getString(DownloadAllService.FILENAME);
//                if (resultCode == RESULT_OK) {
//                    System.out.println("Downloaded: " + fileName);
//                    Toast.makeText(BibleStudyLessonsActivity.this,
//                            "Downloaded: " + fileName,
//                            Toast.LENGTH_LONG).show();
//                    oneDownloadComplete = true;
//                    new asyncGetStudyLessons().execute(mStudy);
//                } else {
//                    Toast.makeText(BibleStudyLessonsActivity.this, "Download failed!: " + fileName,
//                            Toast.LENGTH_LONG).show();
//                }
//                DownloadAllService.decrementCount();
//                if (oneDownloadComplete) {
//                    if (DownloadAllService.countDownloads == 0){
//                        DownloadAllService.IS_SERVICE_RUNNING = false;
//                        tvDownloading.setVisibility(View.GONE);
//                        tvDownloading.setText("");
//                        tvTitle.setVisibility(View.VISIBLE);
//                        imgMenuBarOptions.setVisibility(View.VISIBLE);
//                        new asyncGetStudyLessons().execute(mStudy);
//                    }
//                }
			}
		}
	};


}