package com.erpdevelopment.vbvm.activity;

import java.io.IOException;
import java.io.InputStream;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.utils.ConstantsVbvm;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutDetailsActivity extends Activity {

	private TextView tvContent;
	private String fileName = "";
	private LinearLayout llVbvmDetail;
//	private ImageView btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_contact);
		
		Utilities.setActionBar(this, "About");
		
		tvContent = (TextView) findViewById(R.id.tv_vbvm_contact);
		llVbvmDetail = (LinearLayout) findViewById(R.id.ll_vbvm_detail_header);
		llVbvmDetail.setVisibility(View.GONE);
		
		int posSelected = getIntent().getExtras().getInt("posSelected");
		
		switch (posSelected) {
			case 0:	fileName = ConstantsVbvm.FILENAME_HISTORY;				
				break;
			case 1:	fileName = ConstantsVbvm.FILENAME_BELIEFS;				
				break;
			case 2:	fileName = ConstantsVbvm.FILENAME_HOW;				
				break;
			case 3:	fileName = ConstantsVbvm.FILENAME_WHERE;				
				break;
			case 4:	fileName = ConstantsVbvm.FILENAME_ABOUT;				
				break;
			case 5:	fileName = ConstantsVbvm.FILENAME_SERVICES;				
				break;
			case 6:	fileName = ConstantsVbvm.FILENAME_MISSION;				
				break;
			default:
				break;
		}		
		
		try {
			InputStream is = getAssets().open(fileName);
            
            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();
            
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            
            // Convert the buffer into a string.
            String text = new String(buffer);
            
            // Finally stick the string into the text view.
            tvContent.setText(text);
            tvContent.setMovementMethod(new ScrollingMovementMethod());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
