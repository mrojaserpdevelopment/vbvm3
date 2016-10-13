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
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.BitmapManager;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

public class DownloadService extends IntentService {

	  private int result = Activity.RESULT_CANCELED;
	  public static final String URL = "urlpath";
	  public static final String FILENAME = "filename";
	  public static final String RESULT = "result";
	  public static final String NOTIFICATION_COMPLETE = "com.erpdevelopment.vbvm2.service.receiver";
	  public static final String NOTIFICATION2 = "com.erpdevelopment.vbvm2.service.receiver2";
	  public static final String NOTIFICATION_PROGRESS = "notification_progress";
	  public static String downloadAllTitle = "";
	  private FileCache fileCache;
	  public static int countDownloads = 0;	  // number of current downloading files
	  public static boolean downloading = false;
	  //private static ByteArrayBuffer baf;
	  private static final int DOWNLOAD_BUFFER_SIZE = 4096;

	  public static Lesson lesson;

	  public DownloadService() {
	    super("DownloadService");
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

		registerReceiver(receiverStopDownload, new IntentFilter(DownloadService.NOTIFICATION_COMPLETE));
	}

	// will be called asynchronously by Android
   	  @Override
   	  protected void onHandleIntent(Intent intent) {
		  downloading = true;
		  lesson = (Lesson) intent.getExtras().getParcelable("lesson");
		  String url = intent.getExtras().getString("url");
		  String downloadType = intent.getExtras().getString("downloadType");

//		  if ( !url.equals("") ) {
			  DBHandleLessons.updateLessonDownloadStatus(lesson.getIdProperty(), 2, downloadType);
			  Intent i = new Intent(NOTIFICATION2);
			  i.putExtra("updateDownloadingStatus", true);
			  sendBroadcast(i);
			  downloadFromUrl(lesson.getIdProperty(), url, downloadType);
			  publishResults(lesson.getIdProperty(), BitmapManager.getFileNameFromUrl(url), url);
//		  }
		  downloading = false;
//		  }
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
	          Log.d("DownloadManager", "download begining");
	          Log.d("DownloadManager", "download url:" + url);

		      URLConnection conn = url.openConnection();
	          InputStream is = conn.getInputStream();
//              FileOutputStream fos = new FileOutputStream(outputTemp);
//              final int BUFFER_SIZE = 23 * 1024;
//              BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
//              byte[] baf = new byte[BUFFER_SIZE];
//              int actual = 0;
//			  int lenghtOfFile = ucon.getContentLength();
//			  int len1 = 0;
//			  long total = 0;
//              while (actual != -1) {
//                  actual = bis.read(baf, 0, BUFFER_SIZE);
//				  fos.write(baf, 0, actual);
//
//				  total += len1; //total = total + len1
//				  publishProgress(idLesson, (int)((total*100)/lenghtOfFile));
//              }
//              fos.close();
//              Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	          
//	          // successfully finished
//			  result = Activity.RESULT_OK;
//			  //copy downloaded file from temp to audio folder
//			  File output = fileCache.getFileAudioFolder(urlPath);
//			  fileCache.copyFile(outputTemp, output);
//			  outputTemp.delete();


			  // start download
			  BufferedInputStream inStream = new BufferedInputStream(conn.getInputStream());
//			  outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
			  FileOutputStream fileStream = new FileOutputStream(outputTemp);
			  BufferedOutputStream outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
			  byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
			  int bytesRead = 0, totalRead = 0;
  			  int lengthOfFile = conn.getContentLength();
			  long total = 0;
			  while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
			  {
				  outStream.write(data, 0, bytesRead);

				  // update progress bar
				  totalRead += bytesRead;
				  int totalReadInKB = totalRead / 1024;
//				  msg = Message.obtain(parentActivity.activityHandler,
//						  AndroidFileDownloader.MESSAGE_UPDATE_PROGRESS_BAR,
//						  totalReadInKB, 0);
//				  parentActivity.activityHandler.sendMessage(msg);
//				  publishProgress(idLesson, (int)((total*100)/lenghtOfFile));
				  publishProgress(idLesson,  (int)((totalRead*100)/lengthOfFile));
			  }
			  Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

			  outStream.close();
			  fileStream.close();
			  inStream.close();

			  // successfully finished
			  result = Activity.RESULT_OK;
			  //copy downloaded file from temp to audio folder
			  File output = fileCache.getFileAudioFolder(urlPath);
			  fileCache.copyFile(outputTemp, output);
			  outputTemp.delete();
			  DBHandleLessons.updateLessonDownloadStatus(idLesson, 1, downloadType);
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

	  private void publishResults(String idLesson, String fileName, String urlPath) {		  
	    Intent intent = new Intent(NOTIFICATION_COMPLETE);
	    intent.putExtra(RESULT, result);
	    intent.putExtra(FILENAME, fileName);
	    intent.putExtra(URL, urlPath);
	    intent.putExtra("idLesson", idLesson);
	    sendBroadcast(intent);
	  }

	  private void publishProgress(String idLesson, int downloadProgress) {
			Intent intent = new Intent(NOTIFICATION_PROGRESS);
//			intent.putExtra(RESULT, result);
//			intent.putExtra(FILENAME, fileName);
//			intent.putExtra(URL, urlPath);
			intent.putExtra("idLesson", idLesson);
		    intent.putExtra("downloadProgress", downloadProgress);
//		    System.out.println("idLesson = [" + idLesson + "], downloadProgress = [" + downloadProgress + "]");
			sendBroadcast(intent);
	  }

	private BroadcastReceiver receiverStopDownload = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			boolean oneDownloadComplete = false; //At least one download has finished successfully
			if (bundle != null) {
//                int resultCode = bundle.getInt(DownloadService.RESULT);
//                String fileName = bundle.getString(DownloadService.FILENAME);
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
//                DownloadService.decrementCount();
//                if (oneDownloadComplete) {
//                    if (DownloadService.countDownloads == 0){
//                        DownloadService.downloading = false;
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
