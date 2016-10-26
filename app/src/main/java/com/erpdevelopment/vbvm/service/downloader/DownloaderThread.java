package com.erpdevelopment.vbvm.service.downloader;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.adapter.LessonsAdapter;
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

public class DownloaderThread extends Thread {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.erpdevelopment.vbvm2.service.receiver";
    public static final String NOTIFICATION2 = "com.erpdevelopment.vbvm2.service.receiver2";
    private FileCache fileCache;
//    public static boolean downloading = false;
    public int downloadProgress = 0;
    //private static final int DOWNLOAD_BUFFER_SIZE = 4096;
    private static final int DOWNLOAD_BUFFER_SIZE = 16*1024;

    private Lesson mLesson;
    private String mUrl;
    private String mDownloadType;
//    private Handler activityHandler;
    private Activity mActivity;
    private LessonsAdapter mAdapter;
    private int downloadStatus = 0;

    private int totalRead = 0;
    private int lengthOfFile = 0;

    public DownloaderThread(Handler handler, Lesson lesson, String url, String downloadType, Activity activity, LessonsAdapter adapter) {
        fileCache = new FileCache(MainActivity.mainCtx);
//        downloading = true;
        mLesson = lesson;
        mUrl = url;
        mDownloadType = downloadType;
//        activityHandler = handler;
        mActivity = activity;
        mAdapter = adapter;
    }

    @Override
    public void run() {
        DownloadService.incrementCount();
        System.out.println("Increment - Count downloads: " + DownloadService.countDownloads);
        String idLesson = mLesson.getIdProperty();
        Log.d("DownloadManager", "downloading url:" + mUrl);
        File outputTemp = null;
        File output = null;
        try {

            DBHandleLessons.updateLessonDownloadStatus(idLesson, 2, mDownloadType);
            downloadStatus = 2;
            outputTemp = fileCache.getFileTempFolder(mUrl);
            if (outputTemp.exists()) {
                outputTemp.delete();
                outputTemp = fileCache.getFileTempFolder(mUrl);
            }
            URL url = new URL(mUrl);
            long startTime = System.currentTimeMillis();
//            Log.d("DownloadManager", "downloading url:" + url);

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(20000);
            lengthOfFile = conn.getContentLength();

            // start download
            InputStream inStream = new BufferedInputStream(conn.getInputStream());
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
//                msg.what = LessonsAdapter.MESSAGE_UPDATE_PROGRESS_BAR;
//                activityHandler.sendMessage(msg);

                downloadProgress = (int)((totalRead*100)/lengthOfFile);

//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println("mActivity.runOnUiThread");
//                        mLesson.setDownloadProgressAudio(downloadProgress);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//                updateUiDownloadProgress();
            }
            System.out.println("right after interrupt...");
            downloadProgress = 0;
            handler.removeCallbacks(runnable);
            outStream.close();
            fileStream.close();
            inStream.close();

            if(isInterrupted())
            {
                System.out.println("Thread was interrupted");
                // the download was canceled, so let's delete the partially downloaded file
//                output.delete();
                outputTemp.delete();
//                handler.removeCallbacks(runnable);
//                Bundle msgBundle = new Bundle();
//                msgBundle.putString("idLesson", mLesson.getIdProperty());
//                msgBundle.putInt("downloadProgress", 0);
//                Message msg = new Message();
//                msg.setData(msgBundle);
//                msg.what = LessonsAdapter.MESSAGE_UPDATE_PROGRESS_BAR;
//                activityHandler.sendMessage(msg);

//                downloadProgress = 0;
//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        System.out.println("mActivity.runOnUiThread");
//                        mLesson.setDownloadProgressAudio(downloadProgress);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//                updateUiDownloadProgress();
//                DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
                downloadStatus = 0;
            }
            else
            {
                // notify completion
//                msg = Message.obtain(parentActivity.activityHandler,
//                        AndroidFileDownloader.MESSAGE_DOWNLOAD_COMPLETE, fileName);
//                parentActivity.activityHandler.sendMessage(msg);
                System.out.println("Download is completed");
//                downloadProgress = 0;
//                updateUiDownloadProgress();
//                DBHandleLessons.updateLessonDownloadStatus(idLesson, 1, mDownloadType);
                downloadStatus = 1;
                result = Activity.RESULT_OK;
                //copy downloaded file from temp to audio folder
                output = fileCache.getFileAudioFolder(mUrl);
                fileCache.copyFile(outputTemp, output);
                outputTemp.delete();
            }
//            updateUiDownloadProgress();



        }
        catch(MalformedURLException e)
        {
            System.out.println("MalformedURLException");
//            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            downloadStatus = 0;
            e.printStackTrace();
            outputTemp.delete();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException");
//            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            downloadStatus = 0;
            outputTemp.delete();
        }
        catch(IOException e)
        {
            System.out.println("IOException");
//            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            downloadStatus = 0;
            outputTemp.delete();
        }
        catch(Exception e)
        {
            System.out.println("Exception");
//            DBHandleLessons.updateLessonDownloadStatus(idLesson, 0, mDownloadType);
            downloadStatus = 0;
            outputTemp.delete();
        }finally {
//            setDownloadStatusByType();
            System.out.println("finally: " + downloadStatus);
            updateUiDownloadProgress();
            DBHandleLessons.updateLessonDownloadStatus(idLesson, downloadStatus, mDownloadType);
            DownloadService.decrementCount();
            System.out.println("Decrement - Count downloads: " + DownloadService.countDownloads);
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            Bundle msgBundle = new Bundle();
//            msgBundle.putString("idLesson", mLesson.getIdProperty());
//            msgBundle.putInt("downloadProgress", downloadProgress);
//            Message msg = new Message();
//            msg.setData(msgBundle);
//            msg.what = LessonsAdapter.MESSAGE_UPDATE_PROGRESS_BAR;
//            mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    System.out.println("mActivity.runOnUiThread");
//                    mLesson.setDownloadProgressAudio(downloadProgress);
//                    mAdapter.notifyDataSetChanged();
//                }
//            });
            updateUiDownloadProgress();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateUiDownloadProgress() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateLessonByDownloadType();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateLessonByDownloadType() {
        switch (mDownloadType) {
            case "audio":
                mLesson.setDownloadStatusAudio(downloadStatus);
                mLesson.setDownloadProgressAudio(downloadProgress);
                break;
            case "teacher":
                mLesson.setDownloadStatusTeacherAid(downloadStatus);
                mLesson.setDownloadProgressTeacher(downloadProgress);
                break;
            case "transcript":
                mLesson.setDownloadStatusTranscript(downloadStatus);
                mLesson.setDownloadProgressTranscript(downloadProgress);
                break;
        }
    }

}
