package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Author;
import com.erpdevelopment.vbvm.utils.BitmapManager2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListAdapterAuthor extends BaseAdapter {

	private Activity activity;
	private List<Author> authors;
	
	public HorizontalListAdapterAuthor(Activity a, List<Author> author) {
		activity = a;
		authors = author;
	}
	
	@Override
	public int getCount() {
		return authors.size();
	}

	@Override
	public Object getItem(int position) {
		return authors.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
	   // Get the data item for this position
       Author author = (Author) getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	  LayoutInflater inflater = activity.getLayoutInflater();
          convertView = inflater.inflate(R.layout.item_hlistview_study, null);
       }
       // Lookup view for data population
       ImageView img = (ImageView) convertView.findViewById(R.id.img);       
       switch (position) {
			case 0: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_stephen_scaled);
				break;
			case 1: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_melissa);
				break;
			case 2: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_brian_scaled);
				break;
			case 3: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_ivette_scaled);
				break;
			case 4: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_brady);
				break;				
			default:
				break;
       }   
       
       TextView txt = (TextView) convertView.findViewById(R.id.txt);
       txt.setText(author.getAuthorName());
       	   
	   return convertView;
	}
}
