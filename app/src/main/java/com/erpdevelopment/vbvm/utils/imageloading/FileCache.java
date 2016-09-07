package com.erpdevelopment.vbvm.utils.imageloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.erpdevelopment.vbvm.utils.BitmapManager;

import android.content.Context;
import android.os.Environment;
 
public class FileCache {
     
    private static File cacheDir;
    public static File cacheDirAudio;
    private static File cacheDirTemp;
    
    private static  String DIRECTORY_IMAGES;
    private static  String DIRECTORY_AUDIO;
    private static  String DIRECTORY_TEMP;
     
    public FileCache(Context context){
         
        //Find the directory at SDCARD to save cached images
//    	DIRECTORY_IMAGES = context.getPackageName() + "/images";
//    	DIRECTORY_AUDIO = context.getPackageName() + "/audio";
//    	DIRECTORY_TEMP = context.getPackageName() + "/temp";
    	
    	DIRECTORY_IMAGES = "VBVMI/images";
    	DIRECTORY_AUDIO = "VBVMI/audio";
    	DIRECTORY_TEMP = "VBVMI/temp";
    	
        if (android.os.Environment.getExternalStorageState().equals(
                                     android.os.Environment.MEDIA_MOUNTED))
        {
            //if SDCARD is mounted (SDCARD is present on device and mounted)
        	cacheDir = new File( Environment.getExternalStorageDirectory(), DIRECTORY_IMAGES );
        	cacheDirAudio = new File( Environment.getExternalStorageDirectory(), DIRECTORY_AUDIO );
        	cacheDirTemp = new File( Environment.getExternalStorageDirectory(), DIRECTORY_TEMP );
        }  
        else
        {
            // if checking on simulator the create cache directory in your application context
            cacheDir = context.getCacheDir();
            cacheDirAudio = context.getCacheDir();
            cacheDirTemp = context.getCacheDir();
        }
         
        if(!cacheDir.exists()){
            // create cache directory in your application context
            cacheDir.mkdirs();
        }        

        if(!cacheDirAudio.exists())
            cacheDirAudio.mkdirs();
        
        if(!cacheDirTemp.exists())
            cacheDirTemp.mkdirs();
    }
     
    public File getFile(String url){
        //Identify images by directory or encode by URLEncoder.encode.
//        String filename = String.valueOf(url.hashCode());
    	String filename = BitmapManager.getFileNameFromUrl(url);
         System.out.println("url is: " + url + " after hashcode is: " + filename);
        File f = new File(cacheDir, filename);
        return f;         
    }
    
    public File getFileAudioFolder(String url){
    	String filename = BitmapManager.getFileNameFromUrl(url);
        File f = new File(cacheDirAudio, filename);
        return f;         
    }
     
    public File getFileTempFolder(String url){
    	String filename = BitmapManager.getFileNameFromUrl(url);
        File f = new File(cacheDirTemp, filename);
        return f;         
    }

    
    public void clear(){
        // list all files inside cache directory
        File[] files = cacheDir.listFiles();
        if(files==null)
            return;
        //delete all cache directory files
        for(File f:files)
            f.delete();
    }
    
    public void clearTempFolder(){
        // list all files inside cache directory
        File[] files = cacheDirTemp.listFiles();
        if( files == null)
            return;
        //delete all cache directory files
        for(File f: files)
            f.delete();
    }
    
    public void copyFile(File source, File target) {
    	FileInputStream fis = null;
    	FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
	    	fos = new FileOutputStream(target);
	    	Utils.CopyStream(fis, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
	     	  if (fis != null) {
		        try {
		          fis.close();
		        } catch (IOException e) {
		          e.printStackTrace();
		        }
		      }
		      if (fos != null) {
		        try {
		          fos.close();
		        } catch (IOException e) {
		          e.printStackTrace();
		        }
		      }
		}
    }
 
}
