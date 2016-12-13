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
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.utils.BitmapManager2;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

public class DownloadAllService extends IntentService {

	private int result = Activity.RESULT_CANCELED;
	public static final String URL = "urlpath";
	public static final String FILENAME = "filename";
	public static final String RESULT = "result";
	public static final String NOTIFICATION_DOWNLOAD_ALL_PROGRESS = "notification_download_all_progress";
	public static final String NOTIFICATION_DOWNLOAD_ALL_COMPLETE = "notification_download_all_complete";
	private static final int DOWNLOAD_BUFFER_SIZE = 4096;
	private FileCache fileCache;
	public static int countDownloads = 0;	  // number of current IS_SERVICE_RUNNING files
	public static boolean downloading = false;
	public static boolean stopped = false;
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
		  System.out.println("DownloadAllService.onHandleIntent: " + intent.getExtras().getString("idLesson"));
		  if ( !stopped ) {
			  downloading = true;
			  String idLesson = intent.getExtras().getString("idLesson");
			  String url = intent.getExtras().getString("url");
			  String downloadType = intent.getExtras().getString("downloadType");
			  mIdLesson = idLesson;
			  mDownloadProgress = 0;
			  mDownloadType = downloadType;
			  DBHandleLessons.updateLessonDownloadStatus(idLesson, 2, downloadType);
			  downloadFromUrl(idLesson, url, downloadType);
		  } else {
			  downloading = false;
		  }
   	  }
	  
	  public void downloadFromUrl(String idLesson, String urlPath, String downloadType) {
		  File outputTemp = null;
		  try {
			  outputTemp = fileCache.getFileTempFolder(urlPath);		    
			  if (outputTemp.exists()) {
				  outputTemp.delete();
			      outputTemp = fileCache.getFileTempFolder(urlPath);
			  }
	          URL url = new URL(urlPath);
	          long startTime = System.currentTimeMillis();
		      URLConnection conn = url.openConnection();
			  BufferedInputStream inStream = new BufferedInputStream(conn.getInputStream());
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
					  mDownloadProgress = 0;
					  handler.removeCallbacks(runnable);
					  break;
				  }
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
			  } else {
				  result = Activity.RESULT_OK;
				  //copy downloaded file from temp to audio folder
				  File output = fileCache.getFileAudioFolder(urlPath);
				  fileCache.copyFile(outputTemp, output);
				  outputTemp.delete();
				  DBHandleLessons.updateLessonDownloadStatus(idLesson, 1, downloadType);
				  publishDownloadResults(idLesson, BitmapManager2.getFileNameFromUrl(urlPath), downloadType);
				  handler.removeCallbacks(runnable);
			  }
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
//		sendBroadcast(intent);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void publishDownloadResults(String idLesson, String fileName, String downloadType) {
		Intent intent = new Intent(NOTIFICATION_DOWNLOAD_ALL_COMPLETE);
		intent.putExtra(RESULT, result);
		intent.putExtra(FILENAME, fileName);
		intent.putExtra("idLesson", idLesson);
		intent.putExtra("downloadType", downloadType);
//		sendBroadcast(intent);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		stopped = false;
		super.onDestroy();
	}


}
