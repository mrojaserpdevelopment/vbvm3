package com.erpdevelopment.vbvm.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ArticlesAdapter extends BaseAdapter implements Filterable{

	private Activity activity;
	private List<Article> filteredListArticles;
	private List<Article> originalListArticles;
    private ItemFilter mFilter = new ItemFilter();
	private final LinearLayout.LayoutParams lparams;
	private LinearLayout llTopicFilter;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected
	private InputMethodManager imm;
	private String dateFormat;

	public ArticlesAdapter(Activity a, List<Article> articleList) {
		activity = a;
		filteredListArticles = articleList;
		originalListArticles = articleList;
		lparams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lparams.setMargins(0,0,10,0);
		llTopicFilter = (LinearLayout) activity.findViewById(R.id.ll_topic_filter);
		imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
		dateFormat = Locale.getDefault().getCountry().equals("US") ? "MM/dd/yy" : "dd/MM/yy";
	}
	
	@Override
	public int getCount() {
		return filteredListArticles.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredListArticles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		Article article = (Article) getItem(position);
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

		viewHolder.tvTitle.setText(article.getTitle());
		viewHolder.tvAuthor.setText(article.getAuthorName());
		viewHolder.tvDate.setText(Utilities.getSimpleDateFormat(article.getPostedDate(),dateFormat));

		viewHolder.llTopics.removeAllViews();
		int count = 0;
		for (String topic : article.getTopics()){
			TextView tvTopic = new TextView(activity);
			tvTopic.setLayoutParams(lparams);
			tvTopic.setBackgroundResource( R.drawable.bg_text_topics);
			tvTopic.setPadding(10,5,10,5);
			tvTopic.setText(topic);
			tvTopic.setTextColor(ContextCompat.getColor(activity, R.color.light_gray));
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
	
	public void setArticleListItems(List<Article> newList) {
	    filteredListArticles = newList;
	    notifyDataSetChanged();
	}
	
	private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase(Locale.ENGLISH);

            final List<Article> list = originalListArticles;

            int count = list.size();
            final ArrayList<Article> nlist = new ArrayList<Article>(count);
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
            filteredListArticles = (ArrayList<Article>) results.values;
            notifyDataSetChanged();
        }

    }

	@Override
	public Filter getFilter() {
		return mFilter;
	}

	private void filterByTopic(String topic){
		List<Article> tempList = new ArrayList<>();
		for (int i = 0; i < FilesManager.listArticles.size(); i++) {
			List<String> topics = FilesManager.listArticles.get(i).getTopics();
			for ( int j=0; j < topics.size(); j++) {
				if ( topics.get(j).equals(topic) ) {
					tempList.add(FilesManager.listArticles.get(i));
					break;
				}
			}
		}
		setArticleListItems(tempList);
//		if (llTopicFilter.getChildCount()>0)
			llTopicFilter.removeAllViews();
		TextView tvTopic = new TextView(activity);
		tvTopic.setLayoutParams(lparams);
		tvTopic.setBackgroundResource( R.drawable.bg_text_topics);
		tvTopic.setPadding(10,5,10,5);
		tvTopic.setText(topic);
		tvTopic.setTextColor(ContextCompat.getColor(activity, R.color.light_gray));
		tvTopic.setTextSize(12);
		tvTopic.setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.ic_action_cancel_16), null, null, null);
		llTopicFilter.addView(tvTopic);
		llTopicFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setArticleListItems(FilesManager.listArticles);
				tvTopic.setText("");
				llTopicFilter.removeAllViews();
			}
		});
//		imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}


}
