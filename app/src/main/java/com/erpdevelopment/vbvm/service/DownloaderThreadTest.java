package com.erpdevelopment.vbvm.service;

import android.os.Message;

import com.erpdevelopment.vbvm.adapter.LessonsAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by manuel on 10/10/2016.
 */
public class DownloaderThreadTest extends Thread {

    // constants
    private static final int DOWNLOAD_BUFFER_SIZE = 4096;

    // instance variables
    private LessonsAdapter parentActivity;
    private String downloadUrl;

    public DownloaderThreadTest(LessonsAdapter inParentActivity, String inUrl)
    {
        downloadUrl = "";
        if(inUrl != null)
        {
            downloadUrl = inUrl;
        }
        parentActivity = inParentActivity;
    }

    /**
     * Connects to the URL of the file, begins the download, and notifies the
     * AndroidFileDownloader activity of changes in state. Writes the file to
     * the root of the SD card.
     */
    @Override
    public void run()
    {
        URL url;
        URLConnection conn;
        int fileSize, lastSlash;
        String fileName;
        BufferedInputStream inStream;
        BufferedOutputStream outStream;
        File outFile;
        FileOutputStream fileStream;
        Message msg;

//        // we're going to connect now
//        msg = Message.obtain(parentActivity.activityHandler,
//                AndroidFileDownloader.MESSAGE_CONNECTING_STARTED,
//                0, 0, downloadUrl);
//        parentActivity.activityHandler.sendMessage(msg);
//
//        try
//        {
//            url = new URL(downloadUrl);
//            conn = url.openConnection();
//            conn.setUseCaches(false);
//            fileSize = conn.getContentLength();
//
//            // get the filename
//            lastSlash = url.toString().lastIndexOf('/');
//            fileName = "file.bin";
//            if(lastSlash >=0)
//            {
//                fileName = url.toString().substring(lastSlash + 1);
//            }
//            if(fileName.equals(""))
//            {
//                fileName = "file.bin";
//            }
//
//            // notify download start
//            int fileSizeInKB = fileSize / 1024;
//            msg = Message.obtain(parentActivity.activityHandler,
//                    AndroidFileDownloader.MESSAGE_DOWNLOAD_STARTED,
//                    fileSizeInKB, 0, fileName);
//            parentActivity.activityHandler.sendMessage(msg);
//
//            // start download
//            inStream = new BufferedInputStream(conn.getInputStream());
//            outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
//            fileStream = new FileOutputStream(outFile);
//            outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
//            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
//            int bytesRead = 0, totalRead = 0;
//            while(!isInterrupted() && (bytesRead = inStream.read(data, 0, data.length)) >= 0)
//            {
//                outStream.write(data, 0, bytesRead);
//
//                // update progress bar
//                totalRead += bytesRead;
//                int totalReadInKB = totalRead / 1024;
//                msg = Message.obtain(parentActivity.activityHandler,
//                        AndroidFileDownloader.MESSAGE_UPDATE_PROGRESS_BAR,
//                        totalReadInKB, 0);
//                parentActivity.activityHandler.sendMessage(msg);
//            }
//
//            outStream.close();
//            fileStream.close();
//            inStream.close();
//
//            if(isInterrupted())
//            {
//                // the download was canceled, so let's delete the partially downloaded file
//                outFile.delete();
//            }
//            else
//            {
//                // notify completion
//                msg = Message.obtain(parentActivity.activityHandler,
//                        AndroidFileDownloader.MESSAGE_DOWNLOAD_COMPLETE, fileName);
//                parentActivity.activityHandler.sendMessage(msg);
//            }
//        }
//        catch(MalformedURLException e)
//        {
//            String errMsg = parentActivity.getString(R.string.error_message_bad_url);
//            msg = Message.obtain(parentActivity.activityHandler,
//                    AndroidFileDownloader.MESSAGE_ENCOUNTERED_ERROR,
//                    0, 0, errMsg);
//            parentActivity.activityHandler.sendMessage(msg);
//        }
//        catch(FileNotFoundException e)
//        {
//            String errMsg = parentActivity.getString(R.string.error_message_file_not_found);
//            msg = Message.obtain(parentActivity.activityHandler,
//                    AndroidFileDownloader.MESSAGE_ENCOUNTERED_ERROR,
//                    0, 0, errMsg);
//            parentActivity.activityHandler.sendMessage(msg);
//        }
//        catch(Exception e)
//        {
//            String errMsg = parentActivity.getString(R.string.error_message_general);
//            msg = Message.obtain(parentActivity.activityHandler,
//                    AndroidFileDownloader.MESSAGE_ENCOUNTERED_ERROR,
//                    0, 0, errMsg);
//            parentActivity.activityHandler.sendMessage(msg);
//        }
    }

}
