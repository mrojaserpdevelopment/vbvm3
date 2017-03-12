package com.erpdevelopment.vbvm.utils;


import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.helper.LeadingMarginSpanHelper;
import com.erpdevelopment.vbvm.model.Study;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


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
		
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage;
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		percentage =(((double)currentSeconds)/totalSeconds)*100;
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
		return currentDuration * 1000;
	}

	public static void setActionBar(AppCompatActivity activity, String title, boolean homeUp) {
        ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(homeUp);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
		Drawable textHomeUp = Utilities.getTextViewAsDrawable(activity, title);
		actionBar.setLogo(textHomeUp);
		actionBar.show();
	}

	public static Drawable getTextViewAsDrawable(Activity activity, String text) {
		TextView upTextView = (TextView) activity.getLayoutInflater().inflate(
				R.layout.actionbar_home_up_text, null);
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

	public static String getSimpleDateFormat(String dateString, String format) {
		long timeMills = Long.parseLong(dateString);
		Date d = new Date(timeMills);
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(d);
	}

	public static String capitalizeFirst(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}

	public static SpannableString getFloatingText(Activity activity, String description, int iconRefSize) {
		Drawable dIcon = ResourcesCompat.getDrawable(activity.getResources(), iconRefSize, null);
		int leftMargin = (dIcon != null ? dIcon.getIntrinsicWidth() : 0) + 15;
		SpannableString ss = new SpannableString(description);
		ss.setSpan(new LeadingMarginSpanHelper(leftMargin, 5), 0, ss.length(), 0);
		return ss;
	}

	public static void sortListStudies(List list){
		Collections.sort(list, new Comparator<Study>() {
			@Override
			public int compare(Study o1, Study o2) {
				int bibleIndex1 = Integer.parseInt(o1.getBibleIndex());
				int bibleIndex2 = Integer.parseInt(o2.getBibleIndex());
				return bibleIndex1 < bibleIndex2 ? -1
						: bibleIndex1 > bibleIndex2 ? 1
						: 0;

			}
		});
	}

	public static String getFormatString(Resources res, int stringId, String text) {
		return String.format(res.getString(stringId), text);
	}

	public static void setSupportActionBar(ActionBar actionBar, String title) {
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
		actionBar.show();
	}

	public static void loadStudyImages(Activity activity, String path, int size, ImageView imageView) {
		Picasso.with(activity)
				.load(path)
				.resize(size,size)
				.centerCrop()
				.into(imageView);
	}

	public static String getLocale(Activity a) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return a.getResources().getConfiguration().getLocales().get(0).getDisplayName();
		} else {
			return a.getResources().getConfiguration().locale.getDisplayName();
		}
	}

//	public static String getScreenDensity() {
//		Resources resources = getResources();
//		DisplayMetrics metrics = resources.getDisplayMetrics();
//		float dp = 800 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//	}

}