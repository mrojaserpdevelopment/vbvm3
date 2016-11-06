package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.BitmapManager2;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VbvmStaffAdapter extends BaseAdapter {

	private Activity activity;
	private List<Study> studies;
	private int selectedPos = -1;	// init value for not-selected
	private int cat;

	public VbvmStaffAdapter(Activity a, List<Study> study, String category) {
		activity = a;
		studies = study;
		if ( category.equals("staff") )
			cat = 0;
		else 
			cat = 1;			
	}
	
	@Override
	public int getCount() {
		return studies.size();
	}

	@Override
	public Object getItem(int position) {
		return studies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
		// Get the data item for this position
	       Study study = (Study) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
	       }
	       // Lookup view for data population
	       ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
	       
	       if ( cat == 0 ) {
		       switch (position) {
		       		case 0: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_bmp_kathryn);
						break;
					case 1: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_melissa);
						break;
					case 2: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_bmp_federico_2);
						break;
					case 3: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_bmp_tonya_2);
						break;
					case 4: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_bmp_brady_2);
						break;				
					default:
						break;
		       }
	       } else {
	    	   switch (position) {
		       		case 0: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_stephen_scaled);
						break;
					case 1: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_brian_scaled);
						break;
					case 2: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_board_joe);
						break;
					case 3: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_board_ron);
						break;
					case 4: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_board_jerry);
						break;
					case 5: BitmapManager2.setImageBitmap(activity, img, R.drawable.photo_board_anonymous);
						break;
					default:
						break;
	    	   }	    	   
	       }
		   
	       TextView tvTitle= (TextView) convertView.findViewById(R.id.tv_title_bible_study);
	       tvTitle.setText(study.getTitle());
	       
	       TextView tvSubTitle = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
	       tvSubTitle.setText(study.getStudiesDescription());
		   
	       if(selectedPos == position)
	    	   convertView.setBackgroundColor(Color.CYAN);
	       else	    	   
	    	   convertView.setBackgroundColor(Color.WHITE);
	       
		   return convertView;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public void setStudyListItems(List<Study> newList) {
	    studies = newList;
	    notifyDataSetChanged();
	}

}
