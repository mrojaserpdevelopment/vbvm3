package com.erpdevelopment.vbvm.service.downloader;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service {

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
    public static DownloaderThread downloaderThread;

    public static Map<String,Thread> threadMap = new HashMap<>();

    public static synchronized void incrementCount() {
        countDownloads++;
    }
    public static synchronized void decrementCount() {
        countDownloads--;
    }

    public DownloadService() {
        fileCache = new FileCache(MainActivity.mainCtx);
        downloading = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("DownloadService.onStartCommand");
        downloaderThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

//    public static final String DOWNLOAD_FROM_URL = "from_url";
//    public static final String MESSENGER = "messenger";
//    public static final int SUCCESSFUL = "download_successful".hashCode();
//    public static final int FAILED = "download_failed".hashCode();
//    public static final String REQUEST_ID = "request_id";
//    public static final String DOWNLOAD_THREAD = "download_thread";

    public static int startDownload(Context ctx) {
        Intent intent = new Intent(ctx, DownloadService.class);
        ctx.startService(intent);
        return 0;
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
