package com.erpdevelopment.vbvm.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
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
	  public static final String NOTIFICATION = "com.erpdevelopment.vbvm2.service.receiver";
	  public static final String NOTIFICATION2 = "com.erpdevelopment.vbvm2.service.receiver2";
	  public static String downloadAllTitle = "";
	  private FileCache fileCache;
	  public static int countDownloads = 0;	  // number of current downloading files
	  public static boolean downloading = false;
	  //private static ByteArrayBuffer baf;

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

      // will be called asynchronously by Android
   	  @Override
   	  protected void onHandleIntent(Intent intent) {
	  	  downloadAllTitle = intent.getExtras().getString("studyTitle");
		  Lesson lesson = (Lesson) intent.getExtras().getParcelable("lesson");
		  String idLesson = lesson.getIdProperty();			  
		  
		  int downloadStatus = lesson.getDownloadStatus();
		  String audioUrl = lesson.getAudioSource();
		  String pdfUrl1 = lesson.getTranscript();
		  String pdfUrl2 = lesson.getStudentAid();
		  String pdfUrl3 = lesson.getTeacherAid();
		  
		  if ( downloadStatus == 0 ){
			  if ( !audioUrl.equals("") ) {
				  DBHandleLessons.updateLessonDownloadStatus(idLesson, 2);
				  Intent i = new Intent(NOTIFICATION2);	    
		   		  i.putExtra("updateDownloadingStatus", true);
		   		  sendBroadcast(i);
		   		  downloadFromUrl(idLesson, audioUrl);
			  }				  
			  if ( !pdfUrl1.equals("") ) {
				 downloadFromUrl(idLesson, pdfUrl1);
			  }				  
			  if ( !pdfUrl2.equals("") ) {
				 downloadFromUrl(idLesson, pdfUrl2);
			  }
			  if ( !pdfUrl3.equals("") ) {
				 downloadFromUrl(idLesson, pdfUrl3);
			  }
			  DBHandleLessons.updateLessonDownloadStatus(idLesson, 1);
			  publishResults(idLesson, BitmapManager.getFileNameFromUrl(audioUrl), audioUrl);
		  }
   	  }
	  
	  public void downloadFromUrl(String idLesson, String urlPath) {
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

		      URLConnection ucon = url.openConnection();
	          InputStream is = ucon.getInputStream();
//	          BufferedInputStream bis = new BufferedInputStream(is);
//
//	           /*
//	            * Read bytes to the Buffer until there is nothing more to read(-1).
//	            */
////	          ByteArrayBuffer baf = new ByteArrayBuffer(5000);
//	          baf = new ByteArrayBuffer(10000);
//	          int current = 0;
//	          while ((current = bis.read()) != -1) {
//	             baf.append((byte) current);
//	          }
//
//	           /* Convert the Bytes read to a String. */
//	          FileOutputStream fos = new FileOutputStream(outputTemp);
//	          fos.write(baf.toByteArray());
//	          fos.flush();
//	          fos.close();
//	          baf.clear();
//	          baf = null;
//	          Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

	          
	          /*
               * Read bytes to the Buffer until there is nothing more to read(-1) and write on the fly in the file.
               */
              FileOutputStream fos = new FileOutputStream(outputTemp);
              final int BUFFER_SIZE = 23 * 1024;
              BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
              byte[] baf = new byte[BUFFER_SIZE];
              int actual = 0;
              while (actual != -1) {
                  actual = bis.read(baf, 0, BUFFER_SIZE);
				  fos.write(baf, 0, actual);
              }	          
              fos.close();
              Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	          
	          // successfully finished
			  result = Activity.RESULT_OK;
			  //copy downloaded file from temp to audio folder
			  File output = fileCache.getFileAudioFolder(urlPath);		  	
			  fileCache.copyFile(outputTemp, output);
			  outputTemp.delete();
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
	    Intent intent = new Intent(NOTIFICATION);	    
	    intent.putExtra(RESULT, result);
	    intent.putExtra(FILENAME, fileName);
	    intent.putExtra(URL, urlPath);
	    intent.putExtra("idLesson", idLesson);
	    sendBroadcast(intent);
	  }

}
