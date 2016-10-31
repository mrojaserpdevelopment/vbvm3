package com.erpdevelopment.vbvm.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.QandAPost;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.utils.FormatDate;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArticlesAdapter extends BaseAdapter implements Filterable{

	private Activity activity;
	private List<Article> filteredListArticles;
	private List<Article> originalListArticles;
    private ItemFilter mFilter = new ItemFilter();
	private final LinearLayout.LayoutParams lparams;
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public ArticlesAdapter(Activity a, List<Article> articleList) {
		activity = a;
		filteredListArticles = articleList;
		originalListArticles = articleList;
		lparams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lparams.setMargins(10,0,10,0);
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
		// Get the data item for this position
		Article article = (Article) getItem(position);
//		LinearLayout llTopics = null;
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_listview_articles, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_article_title);
			viewHolder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_article_author);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_article_date);
			viewHolder.llArticleTopics = (LinearLayout) convertView.findViewById(R.id.ll_articles_topics);
//			for (String topic : article.getTopics()){
//				TextView tvTopic = new TextView(activity);
//				tvTopic.setLayoutParams(lparams);
//				tvTopic.setBackgroundResource( R.drawable.bg_text_topics);
//				tvTopic.setPadding(5,5,5,5);
//				tvTopic.setText(topic);
////			tvTopic.setTextColor(activity.getResources().getColor(R.color.light_gray));
//				tvTopic.setTextColor(ContextCompat.getColor(activity, R.color.light_gray));
//				tvTopic.setTextSize(12);
//				viewHolder.llArticleTopics.addView(tvTopic);
//			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(article.getTitle());
		viewHolder.tvAuthor.setText(article.getAuthorName());
//		Date date = new Date(Long.parseLong(article.getPostedDate()));
//		String[] dateArray = FormatDate.getDateArray(new Date(Long.parseLong(article.getPostedDate())));

//		long timeMills = Long.parseLong(article.getPostedDate());
//		Date d = new Date(timeMills);
//		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
//		String date = df.format(d);
		viewHolder.tvDate.setText(Utilities.getSimpleDateFormat(article.getPostedDate(),"dd/MM/yy"));

		System.out.println("ArticlesAdapter.getView: " + article.getTitle() + " - " + article.getTopics().size());

//		llTopics = (LinearLayout) convertView.findViewById(R.id.ll_articles_topics);
//		TextView tvTopic = (TextView) (inflater != null ? inflater.inflate(R.layout.tv_topics, null) : null);

		viewHolder.llArticleTopics.removeAllViews();
		for (String topic : article.getTopics()){
//			TextView tvTopic = (TextView) (inflater != null ? inflater.inflate(R.layout.tv_topics, null) : null);
			TextView tvTopic = new TextView(activity);
			tvTopic.setLayoutParams(lparams);
			tvTopic.setBackgroundResource( R.drawable.bg_text_topics);
			tvTopic.setPadding(5,5,5,5);
			tvTopic.setText(topic);
//			tvTopic.setTextColor(activity.getResources().getColor(R.color.light_gray));
			tvTopic.setTextColor(ContextCompat.getColor(activity, R.color.light_gray));
			tvTopic.setTextSize(12);
			viewHolder.llArticleTopics.addView(tvTopic);
		}

//		LinearLayout llTopics = (LinearLayout) convertView.findViewById(R.id.ll_articles_topics);
//		LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		TextView tv=new TextView(activity);
//		tv.setLayoutParams(lparams);
//		tv.setText("test1");
//		llTopics.addView(tv);
//
//		tv=new TextView(activity);
//		tv.setLayoutParams(lparams);
//		tv.setText("test123333444");
//		llTopics.addView(tv);
//
//		tv=new TextView(activity);
//		tv.setLayoutParams(lparams);
//		tv.setText("testing");
//		llTopics.addView(tv);
//
//		tv=new TextView(activity);
//		tv.setLayoutParams(lparams);
//		tv.setText("test44444444444");
//		llTopics.addView(tv);

//		viewHolder.tvDate.setText(article.getPostedDate());



//		ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
//		img.setVisibility(View.GONE);
//
//		LinearLayout llArticleDate = (LinearLayout) convertView.findViewById(R.id.ll_article_date);
//		llArticleDate.setVisibility(View.VISIBLE);
//
//		   TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
//		   tvTitle.setText(article.getTitle());
//
//		   TextView tvAuthor = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
//		   tvAuthor.setText(article.getAuthorName());
//
//		   Date date = new Date(Long.parseLong(article.getPostedDate()));
//		   String[] dateArray = FormatDate.getDateArray(date);
//		   TextView tvDay = (TextView) convertView.findViewById(R.id.tv_article_day);
//		   tvDay.setText(dateArray[0]);
//		   TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_article_month);
//		   tvMonth.setText(dateArray[1]);

//	       TextView tvDay = (TextView) convertView.findViewById(R.id.tv_article_day);
//	       tvDay.setText(article.getPostedDate());
//
//	       TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_article_month);
//	       tvMonth.setText(article.getPostedDate().substring(0, 3));

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
		LinearLayout llArticleTopics;
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
		// TODO Auto-generated method stub
		return mFilter;
	}

}
