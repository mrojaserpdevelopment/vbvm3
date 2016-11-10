package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.AnswersAdapter;
import com.erpdevelopment.vbvm.db.DBHandleAnswers;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.CheckConnectivity;
import com.erpdevelopment.vbvm.utils.Constants;
import com.erpdevelopment.vbvm.utils.FavoritesLRU;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class QAndAPostsActivity extends Activity {

	private List<Answer> postsList = new ArrayList<Answer>();
	private AnswersAdapter postsAdapter;
	private ListView lv;
	private TextView tvTitle;
	private LinearLayout llButtonHome;
	private SharedPreferences prefs;
	public PopupWindow popupWindowDogs;
    public ImageView imgButtonSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_listview);
		llButtonHome = (LinearLayout) findViewById(R.id.ll_button_chat);
		llButtonHome.setBackgroundResource(R.drawable.bottom_menu_bar);
		
		imgButtonSearch = (ImageView) findViewById(R.id.btn_search);
		imgButtonSearch.setVisibility(View.VISIBLE);
		
		prefs = getSharedPreferences(Constants.VBVM_PREFS, Context.MODE_PRIVATE);
		
		tvTitle = (TextView) findViewById(R.id.tv_title_top_control_bar);
		tvTitle.setText("Bible Answers");
		
		postsList = new ArrayList<Answer>();
		postsAdapter = new AnswersAdapter(this, postsList);
		lv = (ListView) findViewById(R.id.lv_bible_studies);
		lv.setAdapter(postsAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				// user clicked a list item, make it "selected"
				postsAdapter.setSelectedPosition(position);

				//Storing Data using SharedPreferences						
				SharedPreferences.Editor edit = prefs.edit();
				edit.remove("posPost").commit();
	            edit.putInt("posPost", position).commit();
	            
	            // save index and top position
	            int index = lv.getFirstVisiblePosition();	            
	            edit.remove("indexPost").commit();
	            edit.putInt("indexPost", index).commit();
				
				Answer post = (Answer) parent.getItemAtPosition(position);
				
				// Add item to Recently Viewed List
				ItemInfo item = new ItemInfo();
            	item.setId(post.getIdProperty());
            	item.setType("post");
            	item.setPosition(String.valueOf(position));
            	item.setItem(post);		            	
            	FavoritesLRU.addLruItem(post.getIdProperty(), item);
            	
				Intent i = new Intent(QAndAPostsActivity.this, QAndAPostDetailsActivity.class);
				i.putExtra("post", post);
				startActivity(i);				
			}
		});
		
		imgButtonSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), FindByTopicActivity.class);
				startActivityForResult(i, 100);	
			}
		});
		
		AsyncGetQAndAPosts async = new AsyncGetQAndAPosts();
		async.execute();
	}
	
	/**
	 * Receiving selected topic from list 
	 * and filter by topic
	 * */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 String topic = data.getExtras().getString("selectedTopic");
         	 if (!topic.equals("done")) {
         		 if ( topic.equals("clear"))
         			postsAdapter.setAnswersListItems(postsList);
         		 else
             		filterByTopic(topic);
         	 }         		 
        } 
    }
	
	private void filterByTopic(String topic){
		if ( !CheckConnectivity.isOnline(QAndAPostsActivity.this)) {
			CheckConnectivity.showMessage(QAndAPostsActivity.this);
		} else {
	    	List<Answer> tempList = new ArrayList<Answer>();
	    	for ( int i=0; i < postsList.size(); i++) {
	    		if ( postsList != null ) {
	    			List<String> topics = postsList.get(i).getTopics();
		    		for ( int j=0; j < topics.size(); j++) {
		    			if ( topics.get(j).equals(topic) ) {
		    				tempList.add(postsList.get(i));
		    				break;
		    			}
		    		}    			
	    		}
	    	}
	    	postsAdapter.setAnswersListItems(tempList);
		}
    }
	
	private class AsyncGetQAndAPosts extends AsyncTask< String, String, List<Answer> > {
	   	 
    	ProgressDialog pDialog;
    	
    	protected void onPreExecute() {
        	//para el progress dialog
            pDialog = new ProgressDialog(QAndAPostsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
		protected List<Answer> doInBackground(String... params) {
			Log.d("onPostExecute=", "Cargando lista Posts");
			List<Answer> listPosts = new ArrayList<Answer>();
	    	WebServiceCall.postsInDB = MainActivity.settings.getBoolean("postsInDB", false);			
			if ( WebServiceCall.postsInDB )
				listPosts = DBHandleAnswers.getAllPosts(false);
			else {
	    		if ( !CheckConnectivity.isOnline(QAndAPostsActivity.this)) {
	    			CheckConnectivity.showMessage(QAndAPostsActivity.this);
	    		} else {
					listPosts = new WebServiceCall().getQandAPosts();
					//Save state flag for sync Webservice/DB
	    			Editor e = MainActivity.settings.edit();
	    			e.putBoolean("postsInDB", true);
	    			e.commit();
					Log.i("PostsActivity info", "Update DB with data from Webservice");
	    		}
			}		    
        	return listPosts;
		}
       
        protected void onPostExecute(List<Answer> result) {
        	postsAdapter.setAnswersListItems(result);
        	postsList = result;
        	//get last selected row
        	prefs = getSharedPreferences(Constants.VBVM_PREFS, Context.MODE_PRIVATE);
    		int position = prefs.getInt("posPost", -1);
    		int index = prefs.getInt("indexPost", -1);
    		if ( position != -1 ) {
    			postsAdapter.setSelectedPosition(position);
    			if ( index != -1 ) {
    				View vi = lv.getChildAt(0);
    	            int top = (vi == null) ? 0 : vi.getTop();            
    	            // restore index and position
    	            lv.setSelectionFromTop(index, top);
    			}
    		}        	
            pDialog.dismiss();
        }
		
    }	
	
	public void onClickControlBar(View v){
		switch(v.getId()){
	        case R.id.button_home:
	        	startActivity(new Intent(QAndAPostsActivity.this, MainActivity.class));
	        	break;
	        case R.id.button_library:
	        	startActivity(new Intent(QAndAPostsActivity.this, BibleStudiesActivity.class));
	        	break;
	        case R.id.button_calendar:
				startActivity(new Intent(QAndAPostsActivity.this, EventsActivity.class));	        	
				break;
	        case R.id.button_chat:
	        	
	        	break;
	        case R.id.button_vbvm:
	        	startActivity(new Intent(QAndAPostsActivity.this, AboutActivity.class));
	        	break;
		}
	}
	
}
