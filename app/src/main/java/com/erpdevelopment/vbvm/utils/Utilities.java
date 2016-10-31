package com.erpdevelopment.vbvm.utils;


import com.erpdevelopment.vbvm.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class Utilities {
	
	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		String minutesString = "";
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = "0" + hours + ":";
		   }
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		// Prepending 0 to minutes if it is one digit
		   if(minutes < 10){ 
			   minutesString = "0" + minutes;
		   }else{
			   minutesString = "" + minutes;}
		   
		   finalTimerString = finalTimerString + minutesString + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	public static void setBackImageEventHandler(ImageView button, final Activity activity){
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		});
	}
	
	public static void setActionBar(Activity activity, String title) {
        ActionBar actionBar = activity.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        if ( title==null )
        	actionBar.setTitle("Back");
        else
        	actionBar.setTitle(title);
        actionBar.setDisplayUseLogoEnabled(false);
	}

	public static Drawable getTextViewAsDrawable(Activity activity, String text) {
		TextView upTextView = (TextView) activity.getLayoutInflater().inflate(
				R.layout.action_home_up_text, null);
		upTextView.setText(text);
		upTextView.measure(0, 0);
		upTextView.layout(0, 0, upTextView.getMeasuredWidth(),
				upTextView.getMeasuredHeight());
		Bitmap bitmap = Bitmap.createBitmap(upTextView.getMeasuredWidth(),
				upTextView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		upTextView.draw(canvas);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(activity.getResources(), bitmap);
		return bitmapDrawable;
	}

}