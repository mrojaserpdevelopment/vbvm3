package com.erpdevelopment.vbvm.service.downloader;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlPath";
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
        downloading = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloaderThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public static int startDownload(Context ctx) {
        Intent intent = new Intent(ctx, DownloadService.class);
        ctx.startService(intent);
        return 0;
    }
}
