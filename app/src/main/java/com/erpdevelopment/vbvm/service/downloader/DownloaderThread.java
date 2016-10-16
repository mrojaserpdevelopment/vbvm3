package com.erpdevelopment.vbvm.service.downloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.adapter.LessonListAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by manuel on 10/10/2016.
 */
public class DownloaderThread extends Thread {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.erpdevelopment.vbvm2.service.receiver";
    public static final String NOTIFICATION2 = "com.erpdevelopment.vbvm2.service.receiver2";
    private FileCache fileCache;
    public static boolean downloading = false;
    public int downloadProgress = 0;

    //private static final int DOWNLOAD_BUFFER_SIZE = 4096;
    private static final int DOWNLOAD_BUFFER_SIZE = 16*1024;

    private Lesson mLesson;
    private String mUrl;
    private String mDownloadType;
    private Handler activityHandler;
    private Activity mActivity;
    private LessonListAdapter mAdapter;

    private int totalRead = 0;
    private int lengthOfFile = 0;

    public DownloaderThread(Handler handler, Lesson lesson, String url, String downloadType, Activity activity, LessonListAdapter adapter) {
        fileCache = new FileCache(MainActivity.mainCtx);
        downloading = true;
        mLesson = lesson;
        mUrl = url;
        mDownloadType = downloadType;
        activityHandler = handler;
        mActivity = activity;
        mAdapter = adapter;
    }

    @Override
    public void run() {
        DownloadService.incrementCount();
        System.out.println("Increment - Count downloads: " + DownloadService.countDownloads);
        System.out.println("Thread started: " + currentThread().getName());

        String urlPath = mUrl;
        String idLesson = mLesson.getIdProperty();

        File outputTemp = null;
        File output = null;
        try {
            DBHandleLessons.updateLessonDownloadStatus(idLesson, 2, mDownloadType);
            outputTemp = fileCache.getFileTempFolder(urlPath);
            if (outputTemp.exists()) {
                outputTemp.delete();
                outputTemp = fileCache.getFileTempFolder(urlPath);
            }
            URL url = new URL(urlPath);
            System.out.println("url = " + url);
            long startTime = System.currentTimeMillis();
            Log.d("DownloadManager", "downloading url:" + url);

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(30000);
            lengthOfFile = conn.getContentLength();

            // start download
            InputStream inStream = new BufferedInputStream(conn.getInputStream());
            //outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
            FileOutputStream fileStream = new FileOutputStream(outputTemp);
            BufferedOutputStream outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            int bytesRead = 0;

            handler.postDelayed(runnable, 100);
            while(!isInterrupted() && (bytesRead = inStream.read(data, 0, data.length)) >= 0)
            {
                outStream.write(data, 0, bytesRead);

                // update progress bar
                totalRead += bytesRead;
//                int totalReadInKB = totalRead / 1024;
//                System.out.println("Progress " + currentThread().getName() + " : " + (int)((totalRead*100)/lengthOfFile) + " - int totalReadInKB: " + totalReadInKB);

//                Bundle msgBundle = new Bundle();
//                msgBundle.putString("idLesson", idLesson);
//                msgBundle.putInt("downloadProgress", (int)((totalRead*100)/lengthOfFile));
//                msg = new Message();
//                msg.setData(msgBundle);
//                msg.what = LessonListAdapter.MESSAGE_UPDATE_PROGRESS_BAR;
//                activityHandler.sendMessage(msg);

                downloadProgress = (int)((totalRead*100)/lengthOfFile);

//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println("mActivity.runOnUiThread");
//                        mLesson.setDownloadProgress(downloadProgress);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });

            }

            outStream.close();
            fileStream.close();
            inStream.close();

            result = Activity.RESULT_OK;
            //copy downloaded file from temp to audio folder
            output = fileCache.getFileAudioFolder(urlPath);
            fileCache.copyFile(outputTemp, output);
            outputTemp.delete();

            if(isInterrupted())
            {
                // the download was canceled, so let's delete the partially downloaded file
                output.delete();
                outputTemp.delete();
                System.out.println("Thread was interrupted");
                handler.removeCallbacks(runnable);
                Bundle msgBundle = new Bundle();
                msgBundle.putString("idLesson", mLesson.getIdProperty());
                msgBundle.putInt("downloadProgress", 0);
                Message msg = new Message();
                msg.setData(msgBundle);
                msg.what = LessonListAdapter.MESSAGE_UPDATE_PROGRESS_BAR;
                activityHandler.sendMessage(msg);
                DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            }
            else
            {
                // notify completion
//                msg = Message.obtain(parentActivity.activityHandler,
//                        AndroidFileDownloader.MESSAGE_DOWNLOAD_COMPLETE, fileName);
//                parentActivity.activityHandler.sendMessage(msg);
                System.out.println("Download is completed");
                DBHandleLessons.updateLessonDownloadStatus(idLesson, 1, mDownloadType);
            }
        }
        catch(MalformedURLException e)
        {
            System.out.println("MalformedURLException");
            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            e.printStackTrace();
//            output.delete();
            outputTemp.delete();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException");
            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            outputTemp.delete();
        }
        catch(IOException e)
        {
            System.out.println("IOException");
            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            outputTemp.delete();
            e.printStackTrace();
        }
        catch(Exception e)
        {
            System.out.println("Exception");
            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            outputTemp.delete();
            e.printStackTrace();
        }finally {
            DownloadService.decrementCount();
            System.out.println("Decrement - Count downloads: " + DownloadService.countDownloads);
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Bundle msgBundle = new Bundle();
            msgBundle.putString("idLesson", mLesson.getIdProperty());
            msgBundle.putInt("downloadProgress", downloadProgress);
            Message msg = new Message();
            msg.setData(msgBundle);
            msg.what = LessonListAdapter.MESSAGE_UPDATE_PROGRESS_BAR;
//            activityHandler.sendMessage(msg);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("mActivity.runOnUiThread");
                    mLesson.setDownloadProgress(downloadProgress);
                    mAdapter.notifyDataSetChanged();
                }
            });

            handler.postDelayed(this, 1000);
        }
    };

    private void publishResults(String idLesson, String fileName, String urlPath) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        intent.putExtra(FILENAME, fileName);
        intent.putExtra(URL, urlPath);
        intent.putExtra("idLesson", idLesson);
//        sendBroadcast(intent);
    }

}
