package com.erpdevelopment.vbvm.fragment;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.ArticleDetailsActivity;
import com.erpdevelopment.vbvm.activity.AudioControllerActivity;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.activity.FindByTopicActivity;
import com.erpdevelopment.vbvm.activity.QAndAPostDetailsActivity;
import com.erpdevelopment.vbvm.activity.VideosVbvmActivity;
import com.erpdevelopment.vbvm.adapter.ArticlesAdapter;
import com.erpdevelopment.vbvm.adapter.BibleStudiesAdapter;
import com.erpdevelopment.vbvm.adapter.VideoChannelsAdapter;
import com.erpdevelopment.vbvm.adapter.AnswersAdapter;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

public class LibraryFragment extends Fragment implements OnTabChangeListener, TextWatcher {
	
	private View rootView;
	private Resources res;
	private BibleStudiesAdapter adapterStudies;
	private AnswersAdapter adapterQAPosts;
	private ArticlesAdapter adapterArticles;
	private VideoChannelsAdapter adapterVideos;
	private ListView lvStudies;
	private ListView lvQAPosts;
	private ListView lvArticles;
	private ListView lvVideos;
	private boolean filterdListSelected = false;
	private List<Study> listStudiesWithFilter = new ArrayList<Study>();
	private List<Article> listArticlesWithFilter = new ArrayList<Article>();
	private EditText etSearchStudies;
	private EditText etSearchAnswers;
	private EditText etSearchDevos;
	private EditText etSearchVideos;
	private TabHost tabs;
	
	public LibraryFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { 
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);         
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		rootView = getView();	
		res = getResources();
		
		tabs = (TabHost) rootView.findViewById(android.R.id.tabhost);
		tabs.setup();
		tabs.setOnTabChangedListener(this);
		 
		TabHost.TabSpec spec = tabs.newTabSpec("Studies");
		spec.setContent(R.id.tabStudies);
		spec.setIndicator("Studies", res.getDrawable(R.drawable.ic_home));
		tabs.addTab(spec);
		 
		spec = tabs.newTabSpec("Answers");
		spec.setContent(R.id.tabAnswers);
		spec.setIndicator("Answers", res.getDrawable(R.drawable.ic_home));
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("Devos");
		spec.setContent(R.id.tabDevos);
		spec.setIndicator("Devos", res.getDrawable(R.drawable.ic_home));
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("Videos");
		spec.setContent(R.id.tabVideos);
		spec.setIndicator("Videos", res.getDrawable(R.drawable.ic_home));
		tabs.addTab(spec);		
		 
		tabs.setCurrentTab(0);		
		
		setAdapterListviewTabs();
		
		Utilities.setActionBar(getActivity(), "Browse Library");
		setHasOptionsMenu(true);
		
		etSearchStudies = (EditText) rootView.findViewById(R.id.et_search_studies);
		etSearchAnswers = (EditText) rootView.findViewById(R.id.et_search_answers);
		etSearchDevos = (EditText) rootView.findViewById(R.id.et_search_devo);
		etSearchVideos = (EditText) rootView.findViewById(R.id.et_search_video);
		
		etSearchStudies.addTextChangedListener(this);
		etSearchAnswers.addTextChangedListener(this);
		etSearchDevos.addTextChangedListener(this);
		etSearchVideos.addTextChangedListener(this);
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		if (tabs.getCurrentTab() == 0)
			adapterStudies.getFilter().filter(s.toString().trim());
		if (tabs.getCurrentTab() == 1)
			adapterQAPosts.getFilter().filter(s.toString().trim());
		if (tabs.getCurrentTab() == 2)
			adapterArticles.getFilter().filter(s.toString().trim());
//		if (tabs.getCurrentTab() == 3)
//			adapterVideos.getFilter().filter(s.toString().trim());
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		
	}
	
	@Override
	public void onTabChanged(String tabId) {
		getActivity().invalidateOptionsMenu();
	}
	
	private void setAdapterListviewTabs() {
		
		adapterStudies = new BibleStudiesAdapter(getActivity(), FilesManager.listStudies);	
		lvStudies = (ListView) rootView.findViewById(R.id.lvStudies);
        lvStudies.setAdapter(adapterStudies);
        lvStudies.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				adapterStudies.setSelectedPosition(position);
				Study study = (Study) parent.getItemAtPosition(position);
				asyncPlayLesson async = new asyncPlayLesson();
				async.execute(study);
			}
		});
        
        adapterQAPosts = new AnswersAdapter(getActivity(), FilesManager.listAnswers);
		lvQAPosts = (ListView) rootView.findViewById(R.id.lvQAPosts);
		lvQAPosts.setAdapter(adapterQAPosts);
		lvQAPosts.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				adapterQAPosts.setSelectedPosition(position);
				Answer post = (Answer) parent.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), QAndAPostDetailsActivity.class);
				i.putExtra("post", post);
				startActivity(i);
			}
		});
		
		adapterArticles = new ArticlesAdapter(getActivity(), FilesManager.listArticles);	
		lvArticles = (ListView) rootView.findViewById(R.id.lvArticles);
		lvArticles.setAdapter(adapterArticles);
		lvArticles.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				adapterArticles.setSelectedPosition(position);
				Article article = (Article) parent.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), ArticleDetailsActivity.class);
				i.putExtra("article", article);
				startActivity(i);
			}
		});
		
		adapterVideos = new VideoChannelsAdapter(getActivity(), FilesManager.listVideoChannels);
		lvVideos = (ListView) rootView.findViewById(R.id.lvVideos);
		lvVideos.setAdapter(adapterVideos);
		lvVideos.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				adapterVideos.setSelectedPosition(position);
				VideoChannel channel = (VideoChannel) parent.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), VideosVbvmActivity.class);
				i.putExtra("channel", channel);
				startActivity(i);
			}
		});
		
	}
	
	// Play first lesson from Studies list
	private class asyncPlayLesson extends AsyncTask< Study, String, Lesson > {   	 
		
    	ProgressDialog pDialog;
    	Study study;
    	
    	protected void onPreExecute() {
    		pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.msg_progress_dialog));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
		protected Lesson doInBackground(Study... params) {
			study = params[0];
////			AudioPlayerService.listTempLesson2 = (ArrayList<Lesson>) DBHandleLessons.getLessons(study.getIdProperty());
////			Lesson lesson = AudioPlayerService.listTempLesson2.get(0);
//			return lesson;
			return null;
        }
       
        protected void onPostExecute(Lesson resultLesson) {        	
        	if ( !(resultLesson.getIdProperty().equals(FilesManager.lastLessonId)) ) {
				AudioPlayerService.created = false;
				DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack);
				FilesManager.lastLessonId = resultLesson.getIdProperty();
				AudioPlayerService.savedOldPositionInTrack = resultLesson.getCurrentPosition();
			} else {
				if (!AudioPlayerService.created) 
					AudioPlayerService.savedOldPositionInTrack = MainActivity.settings.getLong("currentPositionInTrack", 0);
			}
        	pDialog.dismiss();
        	Intent i = new Intent(getActivity(), AudioControllerActivity.class);
    		i.putExtra("position", resultLesson.getPositionInList());
    		i.putExtra("thumbnailSource", resultLesson.getStudyThumbnailSource());
    		i.putExtra("description", resultLesson.getLessonsDescription());
    		i.putExtra("title", resultLesson.getTitle());
    		i.putExtra("size", resultLesson.getStudyLessonsSize());
    		i.putExtra("readSource", resultLesson.getTranscript());
    		i.putExtra("presentSource", resultLesson.getStudentAid());
    		i.putExtra("handoutSource", resultLesson.getTeacherAid());
    		i.putExtra("study", study);
    		startActivity(i);
        }
	
	}	

	@Override
	public void onStart() {
		filterdListSelected = false;
//		restoreSelectedRow();
		super.onStart();
	}	
	
	private void restoreSelectedRow(){
//		prefs = getSharedPreferences(Constants.VBVM_PREFS, Context.MODE_PRIVATE);
//		int position = prefs.getInt("posStudy", -1);
		int position = MainActivity.settings.getInt("posStudy", -1);
		int index = MainActivity.settings.getInt("indexStudy", -1);		
		if ( position != -1 ) {
			adapterStudies.setSelectedPosition(position);
			if ( index != -1 ) {
				View vi = lvStudies.getChildAt(0);
	            int top = (vi == null) ? 0 : vi.getTop();            
	            // restore index and position
	            lvStudies.setSelectionFromTop(index, top);
			}
		}
	}
	
//	private void removeSelectedRow() {
//		//Storing Data using SharedPreferences						
//		SharedPreferences.Editor edit = MainActivity.settings.edit();
//		edit.remove("posStudy").commit();
//        edit.remove("indexStudy").commit();
//	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_library, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (tabs.getCurrentTab() == 0) {
			onClickFilterStudies();
		}
		if (tabs.getCurrentTab() == 1) {
			Intent i = new Intent(getActivity(), FindByTopicActivity.class);
			i.putExtra("filterCategory", "post");
			startActivityForResult(i, 100);			}
		if (tabs.getCurrentTab() == 2) {
			Intent i = new Intent(getActivity(), FindByTopicActivity.class);
			i.putExtra("filterCategory", "author");
			startActivityForResult(i, 200);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		if (tabs.getCurrentTab() == 0) {
			menu.getItem(0).setTitle("FILTER");
		}
		if (tabs.getCurrentTab() == 1) {
			menu.getItem(0).setTitle("Topics");
		}
		if (tabs.getCurrentTab() == 2) {
			menu.getItem(0).setTitle("Authors");
		}
		if (tabs.getCurrentTab() == 3) {
			menu.getItem(0).setTitle("");
		}
		super.onPrepareOptionsMenu(menu);
	}

	
	public void onClickFilterStudies() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());    	
		builder.setTitle(res.getString(R.string.title_dialog_find_bible_studies))
				.setItems(R.array.items_find_bible_studies, 
						new DialogInterface.OnClickListener() {									
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								switch (which) {
									case 0:	filterStudiesByType("Old");
											filterdListSelected = true;
//											removeSelectedRow();
											break;
									case 1: filterStudiesByType("New");
											filterdListSelected = true;
//											adapterStudies.setStudyListItems(listStudiesWithFilter);
//											removeSelectedRow();
											break;
									case 2: filterStudiesByType("Single");
											filterdListSelected = true;
//											adapterStudies.setStudyListItems(listStudiesWithFilter);
//											removeSelectedRow();
											break;
									case 3: filterStudiesByType("Topical Series");
											filterdListSelected = true;
//											adapterStudies.setStudyListItems(listStudiesWithFilter);
//											removeSelectedRow();
											break;		
									case 4: adapterStudies.setStudyListItems(FilesManager.listStudies);
											filterdListSelected = false;
//											restoreSelectedRow();
											break;		
									default:
										break;
								}
							}							
						})
		        .setCancelable(false)
		        .setPositiveButton(res.getString(R.string.dialog_button_cancel),
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                    	filterdListSelected = false;
		                    	dialog.cancel();
		                    }
		                });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void filterStudiesByType(String pattern) {
		List<Study> tempList = FilesManager.listStudies;
		listStudiesWithFilter.clear();
		for ( int i=0; i < tempList.size(); i++ ){
			if ( tempList.get(i).getType().startsWith(pattern) )
				listStudiesWithFilter.add(tempList.get(i));
		}
		adapterStudies.setStudyListItems(listStudiesWithFilter);
	}
	
	private void filterPostsByTopic(String topic){
    	List<Answer> tempList = new ArrayList<Answer>();
    	for (int i = 0; i < FilesManager.listAnswers.size(); i++) {
			List<String> topics = FilesManager.listAnswers.get(i).getTopics();
			if ( topics != null ) {
	    		for ( int j=0; j < topics.size(); j++) {
	    			if ( topics.get(j).equals(topic) ) {
	    				tempList.add(FilesManager.listAnswers.get(i));
	    				break;
	    			}
	    		}
			}
    	}
    	adapterQAPosts.setAnswersListItems(tempList);
    }
	
	private void filterArticlesByAuthor(String author) {
		List<Article> tempList = FilesManager.listArticles;
		listArticlesWithFilter.clear();
		for ( int i=0; i < tempList.size(); i++ ){
			if ( tempList.get(i).getAuthorName().equals(author) )
				listArticlesWithFilter.add(tempList.get(i));
		}
		adapterArticles.setArticleListItems(listArticlesWithFilter);
	}
	
	/**
	 * Receiving selected topic from list 
	 * and filter by topic
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 String topic = data.getExtras().getString("selectedItem");
         	 if (!topic.equals("cancel")) {
         		 if ( topic.equals("clear"))
         			adapterQAPosts.setAnswersListItems(FilesManager.listAnswers);
         		 else
         			filterPostsByTopic(topic);
         	 }         		 
        }
        if(resultCode == 200){
        	 String author = data.getExtras().getString("selectedItem");
        	 if (!author.equals("cancel")) {
        		 if ( author.equals("clear"))
        		 	adapterArticles.setArticleListItems(FilesManager.listArticles);
        		 else
        			filterArticlesByAuthor(author);
        	 }         		 
        }
    }
}
