package com.erpdevelopment.vbvm.service.downloader;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadService extends Service {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlPath";
    public static int countDownloads = 0;	  // number of current IS_SERVICE_RUNNING files
    public static boolean IS_SERVICE_RUNNING = false;
    public static DownloaderThread downloaderThread;
    public static final String NOTIFICATION_DOWNLOAD_PROGRESS = "notification_download_progress";
    public static final String NOTIFICATION_DOWNLOAD_COMPLETE = "notification_download_complete";
    private static final String LOG_TAG = "DownloadService";

    public static Map<String,DownloaderThread> threadMap = new HashMap<>();

    public static synchronized void incrementCount() {
        countDownloads++;
    }
    public static synchronized void decrementCount() {
        countDownloads--;
    }

    public DownloadService() {
        IS_SERVICE_RUNNING = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        downloaderThread.start();
//        return super.onStartCommand(intent, flags, startId);
//    }

    Handler handler = new Handler();
//    private Runnable r = new Runnable() {
//        public void run() {
//            System.out.println("checking download service count... " + countDownloads);
//            if (countDownloads==0) {
//                Log.i(LOG_TAG, "Received Stop Foreground Intent");
//                stopForeground(true);
//                stopSelf();
//                handler.removeCallbacks(this);
//            }
//            handler.postDelayed(this, 1000);
//        }
//    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            System.out.println("checking download service count... " + countDownloads);
            if (countDownloads==0) {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
//                stopSelf();
                return;
            }
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showNotification();
            Toast.makeText(this, "Downloading lesson", Toast.LENGTH_SHORT).show();
            downloaderThread.start();
            countDownloads++;
            handler.postDelayed(runnable, 1000);
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
//        downloaderThread.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("DownloadService.onDestroy");
        handler.removeCallbacks(runnable);
//        stopSelf();
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, DownloadService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, DownloadService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, DownloadService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

//            Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.app_logo_24);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Verse By verse Ministry")
                .setTicker("VBVMI downloading")
                .setContentText("Downloading lesson")
                .setSmallIcon(R.drawable.app_logo_24)
//                    .setLargeIcon(
//                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
//                    .addAction(android.R.drawable.ic_media_previous,
//                            "Previous", ppreviousIntent)
//                    .addAction(android.R.drawable.ic_media_play, "Play",
//                            pplayIntent)
//                    .addAction(android.R.drawable.ic_media_next, "Next",
//                            pnextIntent).build();
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }

//    public static int startDownload(Context ctx) {
//        Intent intent = new Intent(ctx, DownloadService.class);
//        ctx.startService(intent);
//        return 0;
//    }

    public static int startDownload(Activity ctx, Lesson l, String url,
                                    String downloadType, List<Lesson> lessons) {
        downloaderThread = new DownloaderThread(ctx, l, url, downloadType, lessons);
        Intent intent = new Intent(ctx, DownloadService.class);
        intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        ctx.startService(intent);
        return 0;
    }

}
