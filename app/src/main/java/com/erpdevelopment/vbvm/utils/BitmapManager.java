package com.erpdevelopment.vbvm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import com.erpdevelopment.vbvm.MainActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class BitmapManager {
	
	//decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=70;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
	public static Bitmap getBitmapFromUrl(String url) {
//		String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
		String fileName = getFileNameFromUrl(url);
        File f = new File(MainActivity.SD_CARD_PATH + "/" + MainActivity.DIRECTORY_IMAGES + "/" + fileName);
        System.out.println("path is: " + MainActivity.SD_CARD_PATH + "/" + MainActivity.DIRECTORY_IMAGES + "/" + fileName);
//            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
        Bitmap bmp = decodeFile(f);
        return bmp;
	}
	
	public static Bitmap getBitmapFromFile(File f) {
        Bitmap bmp = decodeFile(f);
        if ( bmp == null )
        	return null;
	    Bitmap bmpScaled = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        return bmpScaled;
	}

	public static Bitmap getBitmapFromFile(File f, int size) {
		Bitmap bmp = decodeFile(f);
		if ( bmp == null )
			return null;
		Bitmap bmpScaled = null;
		if ( size > 0 )
			bmpScaled = Bitmap.createScaledBitmap(bmp, size, size, true);
		else
			bmpScaled = Bitmap.createScaledBitmap(bmp, 100, 100, true);
		return bmpScaled;
	}

	public static String getFileNameFromUrl(String url) {
		// get the filename
        int lastSlash = url.toString().lastIndexOf('/');
        String fileName = "file.bin";
        if(lastSlash >=0)
        {
                fileName = url.toString().substring(lastSlash + 1);
        }
        return fileName;
	}
	
	public static Bitmap getBitmapFromURL(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setImageBitmap(Activity activity, ImageView img, int resourceId) {
		Bitmap tempBMP = BitmapFactory.decodeResource(activity.getResources(), resourceId);
//		Bitmap bmp = Bitmap.createScaledBitmap(tempBMP, 100, 100, true);
		WeakReference<Bitmap> bmp = new WeakReference<Bitmap> (Bitmap.createScaledBitmap(tempBMP, 100, 100, true));
//	    img.setImageBitmap(bmp);
	    img.setImageBitmap(bmp.get());
//	    img.setScaleType(ImageView.ScaleType.FIT_XY);
	}
	
}
