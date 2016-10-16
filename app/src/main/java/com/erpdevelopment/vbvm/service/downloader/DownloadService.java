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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
