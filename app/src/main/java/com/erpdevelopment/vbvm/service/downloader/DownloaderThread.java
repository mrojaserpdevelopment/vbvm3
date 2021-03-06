package com.erpdevelopment.vbvm.service.downloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.LessonsAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;

public class DownloaderThread extends Thread {

    private FileCache fileCache;
    public int downloadProgress = 0;
    private static final int DOWNLOAD_BUFFER_SIZE = 16*1024;
    private static final int CONNECTION_TIMEOUT = 10000;
    private boolean stopDownload = false;

    private Lesson mLesson;
    private String mUrl;
    private String mDownloadType;
    private Activity mActivity;
    private int downloadStatus = 0;
    private String messageError = "";

    private int totalRead = 0;
    private int lengthOfFile = 0;
    private Handler handler = new Handler();

    public DownloaderThread(Activity activity, Lesson lesson, String url,
                            String downloadType, List<Lesson> listLessons) {
        fileCache = new FileCache(MainActivity.mainCtx);
        mLesson = lesson;
        mUrl = url;
        mDownloadType = downloadType;
        mActivity = activity;
    }

    @Override
    public void run() {
        String idLesson = mLesson.getIdProperty();
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
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            lengthOfFile = conn.getContentLength();
            // start download
            InputStream inStream = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fileStream = new FileOutputStream(outputTemp);
            BufferedOutputStream outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            int bytesRead = 0;

            handler.postDelayed(runnable, 100);
            while(!stopDownload && (bytesRead = inStream.read(data, 0, data.length)) >= 0)
            {
                outStream.write(data, 0, bytesRead);
                totalRead += bytesRead;
                downloadProgress = (int)((totalRead*100)/lengthOfFile);
            }
            downloadProgress = 0;
            downloadStatus = 0;
            outStream.close();
            fileStream.close();
            inStream.close();

            if(stopDownload)
            {
                outputTemp.delete();
                downloadStatus = 0;
            } else {
                downloadStatus = 1;
                output = fileCache.getFileAudioFolder(mUrl);//copy downloaded file from temp to audio folder
                fileCache.copyFile(outputTemp, output);
                outputTemp.delete();
            }
        }
        catch(MalformedURLException e)
        {   messageError = mActivity.getResources().getString(R.string.error_message_connecting);
            downloadStatus = 0;
            downloadProgress = 0;
            outputTemp.delete();
            e.printStackTrace();
        }
        catch(UnknownHostException e)
        {
            messageError = mActivity.getResources().getString(R.string.error_message_connecting);
            downloadStatus = 0;
            downloadProgress = 0;
            outputTemp.delete();
            e.printStackTrace();
        }
        catch(Exception e)
        {
            messageError = mActivity.getResources().getString(R.string.error_message_connecting);
            downloadStatus = 0;
            downloadProgress = 0;
            outputTemp.delete();
            e.printStackTrace();
        } finally {
//            updateUiDownloadProgress();
            DBHandleLessons.updateLessonDownloadStatus(idLesson, downloadStatus, mDownloadType);
            DownloadService.decrementCount();
            messageError = "";
            handler.removeCallbacks(runnable);
            stopDownload = false;
            updateUiDownloadProgress();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateUiDownloadProgress();
            handler.postDelayed(this, 200);
        }
    };

    private void updateUiDownloadProgress() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                Intent intent = new Intent(DownloadService.NOTIFICATION_DOWNLOAD_PROGRESS);
                intent.putExtra("lesson",mLesson);
                intent.putExtra("downloadStatus", downloadStatus);
                intent.putExtra("downloadType", mDownloadType);
                intent.putExtra("messageError", messageError);
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
            }
        });
    }

    public void stopDownload() {
        stopDownload = true;
    }

}
