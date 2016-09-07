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
import com.erpdevelopment.vbvm.utils.FormatDate;

import android.app.Activity;
import android.graphics.Color;
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
	
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public ArticlesAdapter(Activity a, List<Article> articleList) {
		activity = a;
		filteredListArticles = articleList;
		originalListArticles = articleList;
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
        
		// Get the data item for this position
	       Article article = (Article) getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
		      LayoutInflater inflater = activity.getLayoutInflater();
	          convertView = inflater.inflate(R.layout.item_listview_vbvm, null);
	       }
	       ImageView img = (ImageView) convertView.findViewById(R.id.img_bible_study);
	       img.setVisibility(View.GONE);
	       
	       LinearLayout llArticleDate = (LinearLayout) convertView.findViewById(R.id.ll_article_date);
	       llArticleDate.setVisibility(View.VISIBLE);
	       
	       TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title_bible_study);
	       tvTitle.setText(article.getTitle());

	       TextView tvAuthor = (TextView) convertView.findViewById(R.id.tv_type_bible_study);
	       tvAuthor.setText(article.getAuthorName());
	       
	       Date date = new Date(Long.parseLong(article.getPostedDate()));
	       String[] dateArray = FormatDate.getDateArray(date);
	       TextView tvDay = (TextView) convertView.findViewById(R.id.tv_article_day);
	       tvDay.setText(dateArray[0]);		       
	       TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_article_month);
	       tvMonth.setText(dateArray[1]);

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
