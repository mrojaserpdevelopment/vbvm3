package com.erpdevelopment.vbvm.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class BibleStudiesAdapter extends BaseAdapter implements Filterable {

	private Activity activity;
	private List<Study> originalListStudies;
	private List<Study> filteredListStudies;
    private ItemFilter mFilter = new ItemFilter();
	
    public ImageLoader imageLoader; 
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public BibleStudiesAdapter(Activity a, List<Study> studies) {
		activity = a;
		originalListStudies = studies;
		filteredListStudies = studies;
		imageLoader = new ImageLoader(activity);
	}
	
	@Override
	public int getCount() {
		return filteredListStudies.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredListStudies.get(position);
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

	       imageLoader.DisplayImage(study.getThumbnailSource(), img);
		   
		   TextView txt = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
	       txt.setText(study.getType());
		   
	       TextView txt2 = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
	       txt2.setText(study.getTitle());
		   
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
		originalListStudies = newList;
	    filteredListStudies = newList;
	    selectedPos = -1;
	    notifyDataSetChanged();
	}
	
	private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase(Locale.ENGLISH);

            final List<Study> list = originalListStudies;

            int count = list.size();
            final ArrayList<Study> nlist = new ArrayList<Study>(count);
            String filterableStringTitle;

            for (int i = 0; i < count; i++) {
                filterableStringTitle = list.get(i).getTitle();                
                if ( filterableStringTitle.toLowerCase(Locale.ENGLISH).contains(filterString) ) {
                    nlist.add(list.get(i));
                }
            }
            
            FilterResults results = new FilterResults();
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.values instanceof ArrayList)
            	filteredListStudies = (ArrayList<Study>) results.values;
            notifyDataSetChanged();
        }

    }

	@Override
	public Filter getFilter() {
		return mFilter;
	}


}
