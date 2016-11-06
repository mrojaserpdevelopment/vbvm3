package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.EventVbvm;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.BitmapManager2;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListAdapterFavorites extends BaseAdapter {

	private Activity activity;
	private List<ItemInfo> items;
    public ImageLoader2 imageLoader;
	
	public HorizontalListAdapterFavorites(Activity a, List<ItemInfo> item) {
		activity = a;
		items = item;
		imageLoader = new ImageLoader2(activity);
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
		// Get the data item for this position
	       ItemInfo itemInfo = (ItemInfo) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_hlistview_study, null);
	       }
	       
    	   TextView tvTitle = (TextView) convertView.findViewById(R.id.txt);
	       ImageView imgThumb = (ImageView) convertView.findViewById(R.id.img);

	       String title = "";
	       
	       if (itemInfo.getType().equals("lesson")){
	    	   Lesson lesson = (Lesson) itemInfo.getItem();
	    	   title = lesson.getTitle();
		       imageLoader.DisplayImage(lesson.getStudyThumbnailSource(), imgThumb);
	       }
	       
	       if (itemInfo.getType().equals("article")){
	    	   Article article = (Article) itemInfo.getItem();
	    	   title = article.getTitle();
	    	   for ( int i=0; i<WebServiceCall.authorNames.length; i++ ) {
		    	   if ( article.getAuthorName().equals(WebServiceCall.authorNames[i]) ) {
		    		   switch (i) {
				   			case 0: BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.photo_stephen_scaled);
				   				break;
				   			case 1: BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.photo_melissa);
				   				break;
				   			case 2: BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.photo_brian_scaled);
				   				break;
				   			case 3: BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.photo_ivette_scaled);
				   				break;
				   			case 4: BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.photo_brady);
				   				break;				
				   			default:
				   				break;
				       }  
		    	   }
	    	   }

	       }
	       
	       if (itemInfo.getType().equals("event")){
	    	   EventVbvm event = (EventVbvm) itemInfo.getItem();
	    	   title = event.getTitle();
	    	   BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.bottom_bar_icon_calendar);
	       }
	       
	       if (itemInfo.getType().equals("post")){
	    	   Answer post = (Answer) itemInfo.getItem();
	    	   title = post.getTitle();
	    	   BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.icon_qa_posts);
	       }
	       
	       if (itemInfo.getType().equals("video")){
	    	   VideoVbvm video = (VideoVbvm) itemInfo.getItem();
	    	   title = video.getTitle();
	    	   BitmapManager2.setImageBitmap(activity, imgThumb, R.drawable.icon_youtube_48);
	       }
	       
	       tvTitle.setText(title);
	       
		   return convertView;
	}

	public void setRecentListItems(List<ItemInfo> newList) {
	    items = newList;
	    notifyDataSetChanged();
	}
	
}
