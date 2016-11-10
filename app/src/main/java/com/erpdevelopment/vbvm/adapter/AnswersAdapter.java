package com.erpdevelopment.vbvm.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnswersAdapter extends BaseAdapter implements Filterable {

	private Activity activity;
	private List<Answer> originalListPosts;
	private List<Answer> filteredListPosts;
    private ItemFilter mFilter = new ItemFilter();
	private final LinearLayout.LayoutParams lparams;
	private LinearLayout llTopicFilterA;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public AnswersAdapter(Activity a, List<Answer> postsList) {
		activity = a;
		originalListPosts = postsList;
		filteredListPosts = postsList;
		lparams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lparams.setMargins(0,0,10,0);
		llTopicFilterA = (LinearLayout) activity.findViewById(R.id.ll_topic_filter2);
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
        
	   ViewHolder viewHolder = null;
		Answer answer = (Answer) getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_listview_articles_answers, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_article_title);
			viewHolder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_article_author);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_article_date);
			viewHolder.llTopics = (LinearLayout) convertView.findViewById(R.id.ll_articles_topics);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvTitle.setText(answer.getTitle());
		viewHolder.tvAuthor.setText(answer.getAuthorName());
		viewHolder.tvDate.setText(Utilities.getSimpleDateFormat(answer.getPostedDate(),"dd/MM/yy"));
		viewHolder.llTopics.removeAllViews();
		int count = 0;
		for (String topic : answer.getTopics()){
			TextView tvTopic = new TextView(activity);
			tvTopic.setLayoutParams(lparams);
			tvTopic.setBackgroundResource( R.drawable.bg_text_topics);
			tvTopic.setPadding(10,5,10,5);
			tvTopic.setText(topic);
			tvTopic.setTextColor(ContextCompat.getColor(activity, R.color.light_gray2));
			tvTopic.setTextSize(12);
			tvTopic.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView tvTopicFilter = (TextView) v;
					filterByTopic(tvTopicFilter.getText().toString());
				}
			});
			viewHolder.llTopics.addView(tvTopic);
			count++;
			if (count==3)
				break;
		}
		if(selectedPos == position)
			convertView.setBackgroundColor(Color.CYAN);
		else
			convertView.setBackgroundColor(Color.WHITE);

		return convertView;
	}

	static class ViewHolder {
		TextView tvTitle;
		TextView tvAuthor;
		TextView tvDate;
		LinearLayout llTopics;
	}

	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}
	
	public void setAnswersListItems(List<Answer> newList) {
	    originalListPosts = newList;
	    filteredListPosts = newList;
	    selectedPos = -1;
	    notifyDataSetChanged();
	}
	
	private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase(Locale.ENGLISH);

            final List<Answer> list = originalListPosts;

            int count = list.size();
            final ArrayList<Answer> nlist = new ArrayList<Answer>(count);
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
            filteredListPosts = (ArrayList<Answer>) results.values;
            notifyDataSetChanged();
        }

    }

	@Override
	public Filter getFilter() {
		return mFilter;
	}

	private void filterByTopic(String topic){
		List<Answer> tempList = new ArrayList<>();
		for (int i = 0; i < FilesManager.listAnswers.size(); i++) {
			List<String> topics = FilesManager.listAnswers.get(i).getTopics();
			for ( int j=0; j < topics.size(); j++) {
				if ( topics.get(j).equals(topic) ) {
					tempList.add(FilesManager.listAnswers.get(i));
					break;
				}
			}
		}
		setAnswersListItems(tempList);
		llTopicFilterA.removeAllViews();
		TextView tvTopic = new TextView(activity);
		tvTopic.setLayoutParams(lparams);
		tvTopic.setBackgroundResource( R.drawable.bg_text_topics);
		tvTopic.setPadding(10,5,10,5);
		tvTopic.setText(topic);
		tvTopic.setTextColor(ContextCompat.getColor(activity, R.color.light_gray2));
		tvTopic.setTextSize(12);
		tvTopic.setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.ic_action_cancel_16), null, null, null);
		llTopicFilterA.addView(tvTopic);
		llTopicFilterA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setAnswersListItems(FilesManager.listAnswers);
				tvTopic.setText("");
				llTopicFilterA.removeAllViews();
			}
		});
	}
	
}
