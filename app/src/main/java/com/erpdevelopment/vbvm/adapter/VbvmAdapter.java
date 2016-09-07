package com.erpdevelopment.vbvm.adapter;

import java.util.List;

import com.erpdevelopment.vbvm.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VbvmAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> items;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	
	public VbvmAdapter(Activity a, List<String> eventsList) {
		activity = a;
		items = eventsList;
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
	    String item = (String) getItem(position);
	    // Check if an existing view is being reused, otherwise inflate the view
	    if (convertView == null) {
		   LayoutInflater inflater = activity.getLayoutInflater();
	       convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
	    }
	    TextView txt = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
	    txt.setText(item);
	
	    TextView txt2 = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
	    txt2.setVisibility(View.GONE);
	
	    ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
	    img.getLayoutParams().height = 52;
	    img.getLayoutParams().width = 52;
//	    if ( position == 0 ) {
//	    	BitmapManager.setImageBitmap(activity, img, R.drawable.present);
//	    	txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//	    } else if ( position == 1 ) {
//	    	BitmapManager.setImageBitmap(activity, img, R.drawable.contact);
//		    txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//	    } else {
	    	img.setVisibility(View.GONE);
//	    }
	    
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

	public void setEventsListItems(List<String> newList) {
	    items = newList;
	    notifyDataSetChanged();
	}

}
