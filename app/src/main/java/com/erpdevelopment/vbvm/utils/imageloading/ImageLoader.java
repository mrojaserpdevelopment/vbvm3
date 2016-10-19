package com.erpdevelopment.vbvm.utils.imageloading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
 

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.utils.BitmapManager;

import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;
 
public class ImageLoader {
     
    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();
     
    FileCache fileCache;
    Activity activity;
    //Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
                                           new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
     
    //handler to display images in UI thread
    Handler handler = new Handler();

    private int size = 0;

    public ImageLoader(Activity activity){
         
        fileCache = new FileCache(activity.getApplicationContext());
        this.activity = activity; 
        // Creates a thread pool that reuses a fixed number of 
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);
         
    }

    // default image show in list (Before online image download)
    final int stub_id = R.drawable.placeholder;

    public void DisplayImage(String url, ImageView imageView)    {

        //Store image and url in Map
        imageViews.put(imageView, url);
         
        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(url);

	    if(bitmap != null){
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            //queue Photo to download from url
            queuePhoto(url, imageView);
                         
            //Before downloading image show default image 
            //imageView.setImageResource(stub_id);
            BitmapManager.setImageBitmap(activity, imageView, R.drawable.image_placeholder);
        }
    }

    public void DisplayImage(String url, ImageView imageView,int size)
    {
        this.size = size;
        DisplayImage(url,imageView);
    }
         
    private void queuePhoto(String url, ImageView imageView)
    {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);
         
        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution  
         
        executorService.submit(new PhotosLoader(p));
    }
     
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url = u; 
            imageView = i;
        }
    }
     
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
         
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad = photoToLoad;
        }
         
        @Override
        public void run() {
            try{
                //Check if image already downloaded
                if(imageViewReused(photoToLoad))
                    return;
                // download image from web url
                Bitmap bmp = getBitmap(photoToLoad.url);
                
                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp);
                 
                if(imageViewReused(photoToLoad))
                    return;
                 
                // Get bitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                 
                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue. 
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);
                 
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
     
    private Bitmap getBitmap(String url) 
    {
        File f = fileCache.getFile(url);

        //Bitmap b = BitmapManager.getBitmapFromFile(f);
        Bitmap b = BitmapManager.getBitmapFromFile(f,size);
        if( b != null )
            return b;
         
        // Download image file from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
             
            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream os = new FileOutputStream(f);
             
            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            Utils.CopyStream(is, os);
             
            os.close();
            conn.disconnect();
             
            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
//            bitmap = decodeFile(f);
             
            bitmap = BitmapManager.getBitmapFromFile(f);
            
            return bitmap;
             
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
 
    boolean imageViewReused(PhotoToLoad photoToLoad){
         
        String tag=imageViews.get(photoToLoad.imageView);
        //Check url is already exist in imageViews MAP
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
     
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
        	bitmap = b;
        	photoToLoad = p;
        }
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
             
            // Show bitmap on UI
            if( bitmap != null )
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }
 
    public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }
 
}