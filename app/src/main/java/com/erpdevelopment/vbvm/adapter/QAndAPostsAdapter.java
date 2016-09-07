package com.erpdevelopment.vbvm.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.QandAPost;

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

public class QAndAPostsAdapter extends BaseAdapter implements Filterable {

	private Activity activity;
	private List<QandAPost> originalListPosts;
	private List<QandAPost> filteredListPosts;
    private ItemFilter mFilter = new ItemFilter();
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public QAndAPostsAdapter(Activity a, List<QandAPost> postsList) {
		activity = a;
		originalListPosts = postsList;
		filteredListPosts = postsList;
	}
	
	@Override
	public int getCount() {
		return filteredListPosts.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredListPosts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
		// Get the data item for this position
	       QandAPost post = (QandAPost) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
	       }
	       TextView txt = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
	       txt.setMaxLines(2);
	       txt.setText(post.getTitle());

	       TextView txt2 = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
	       txt2.setText(post.getPostedDate());

	       ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
	       img.setImageResource(R.drawable.icon_qa_posts);
	       
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
	
	public void setQAndAPostsListItems(List<QandAPost> newList) {
	    originalListPosts = newList;
	    filteredListPosts = newList;
	    selectedPos = -1;
	    notifyDataSetChanged();
	}
	
	private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase(Locale.ENGLISH);

            final List<QandAPost> list = originalListPosts;

            int count = list.size();
            final ArrayList<QandAPost> nlist = new ArrayList<QandAPost>(count);
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

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredListPosts = (ArrayList<QandAPost>) results.values;
            notifyDataSetChanged();
        }

    }

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	
	
}
