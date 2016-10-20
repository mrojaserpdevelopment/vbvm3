package com.erpdevelopment.vbvm.activity;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.VbvmStaffAdapter;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class StaffBoardActivity extends Activity {

	private ListView lv;
//	private TextView tvTitle;
	private VbvmStaffAdapter adapter;
	private List<Study> studies = new ArrayList<Study>();
	private String category = "";
	private String titleActionBar;
//	private LinearLayout llButtonHome;
//	private ImageView btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_listview);
		
//		Utilities.setActionBar(this, "Staff");
		
		category = getIntent().getExtras().getString("category");

		if (category.equals("staff")) {
			
			titleActionBar = "Staff";
		
			Study study = new Study();
			study.setTitle("Kathryn Bashaw");
			study.setStudiesDescription("Communications Director");
			studies.add(study);	
			
			study = new Study();
			study.setTitle("Melissa Church");
			study.setStudiesDescription("Contributing Author");
			studies.add(study);		
	
			study = new Study();
			study.setTitle("Federico Acuna");
			study.setStudiesDescription("Director de Centro America");
			studies.add(study);		
	
			study = new Study();
			study.setTitle("Tonya Penfield");
			study.setStudiesDescription("Instructor");
			studies.add(study);		
	
			study = new Study();
			study.setTitle("Brady Stephenson");
			study.setStudiesDescription("Contributing Author");
			studies.add(study);		
			
		}
		
		if (category.equals("board")) {
			
//			tvTitle.setText("The Board");
			titleActionBar = "The Board";
			
			Study study = new Study();
			study.setTitle("Stephen Armstrong");
			study.setStudiesDescription("Director / President");
			studies.add(study);	
			
			study = new Study();
			study.setTitle("Brian Smith");
			study.setStudiesDescription("Director/ Treasurer");
			studies.add(study);		
	
			study = new Study();
			study.setTitle("Jow Baumgartner");
			study.setStudiesDescription("Director / Secretary");
			studies.add(study);		
	
			study = new Study();
			study.setTitle("Ron Sweet");
			study.setStudiesDescription("Director");
			studies.add(study);		
	
			study = new Study();
			study.setTitle("Jerry Dyke");
			study.setStudiesDescription("Director");
			studies.add(study);
			
			study = new Study();
			study.setTitle("Anonymous");
			study.setStudiesDescription("Director");
			studies.add(study);				
		}		

		Utilities.setActionBar(this, titleActionBar);
		
		adapter = new VbvmStaffAdapter(this, studies, category);		
		lv = (ListView) findViewById(R.id.lv_bible_studies);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {				
				// user clicked a list item, make it "selected"
				adapter.setSelectedPosition(position);
				Study study = (Study) parent.getItemAtPosition(position);				
				Intent i = new Intent(StaffBoardActivity.this, StaffBoardDetailsActivity.class);
				i.putExtra("posSelected", position);
				i.putExtra("title", study.getTitle());
				i.putExtra("subTitle", study.getStudiesDescription());
				i.putExtra("category", category);
				startActivity(i);
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fragment_studies, menu);

        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		    	Intent parentIntent = NavUtils.getParentActivityIntent(this);
		    	if ( parentIntent != null ) {
		            parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		            startActivity(parentIntent);
		    	}
	            finish();
	            return true;
		        
	    }
		return super.onOptionsItemSelected(item);
	}

}
