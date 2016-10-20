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
import android.widget.ImageView;
import android.widget.TextView;

public class StaffBoardDetailsActivity extends Activity {

	private TextView tvTitle;
	private TextView tvSubtitle;
	private ImageView imgVbvmDetail;
	private TextView tvContent;
	private String fileName = "";
//	private ImageView btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vbvm_staff_details);

		Utilities.setActionBar(this, null);
		
		tvTitle = (TextView) findViewById(R.id.tv_vbvm_title);
		tvSubtitle = (TextView) findViewById(R.id.tv_vbvm_subtitle);
		imgVbvmDetail = (ImageView) findViewById(R.id.img_vbvm_detail);
		tvContent = (TextView) findViewById(R.id.tv_vbvm_contact);
		
		int posSelected = getIntent().getExtras().getInt("posSelected");
		String category = getIntent().getExtras().getString("category");
		
		if ( category.equals("staff") ) {
			switch (posSelected) {
				case 0:	fileName = ConstantsVbvm.FILENAME_STAFF_KATHRYN;
						imgVbvmDetail.setImageResource(R.drawable.photo_bmp_kathryn);
					break;
				case 1:	fileName = ConstantsVbvm.FILENAME_STAFF_MELISSA;
						imgVbvmDetail.setImageResource(R.drawable.photo_melissa);
					break;
				case 2:	fileName = ConstantsVbvm.FILENAME_STAFF_FEDERICO;
						imgVbvmDetail.setImageResource(R.drawable.photo_bmp_federico_2);
					break;
				case 3:	fileName = ConstantsVbvm.FILENAME_STAFF_TONYA;			
						imgVbvmDetail.setImageResource(R.drawable.photo_bmp_tonya_2);
					break;
				case 4:	fileName = ConstantsVbvm.FILENAME_STAFF_BRADY;		
						imgVbvmDetail.setImageResource(R.drawable.photo_bmp_brady_2);
					break;
				default:
					break;
			}
		}
		
		if ( category.equals("board") ) {
			
			switch (posSelected) {
				case 0:	fileName = ConstantsVbvm.FILENAME_BOARD_STEPHEN;	
						imgVbvmDetail.setImageResource(R.drawable.photo_stephen_scaled);
					break;
				case 1:	fileName = ConstantsVbvm.FILENAME_BOARD_BRIAN;				
						imgVbvmDetail.setImageResource(R.drawable.photo_brian_scaled);
					break;
				case 2:	fileName = ConstantsVbvm.FILENAME_BOARD_JOE;				
						imgVbvmDetail.setImageResource(R.drawable.photo_board_joe);
					break;
				case 3:	fileName = ConstantsVbvm.FILENAME_BOARD_RON;
						imgVbvmDetail.setImageResource(R.drawable.photo_board_ron);
					break;
				case 4:	fileName = ConstantsVbvm.FILENAME_BOARD_JERRY;			
						imgVbvmDetail.setImageResource(R.drawable.photo_board_jerry);
					break;
				case 5:	fileName = ConstantsVbvm.FILENAME_BOARD_ANONYMOUS;	
						imgVbvmDetail.setImageResource(R.drawable.photo_board_anonymous);
					break;
				default:
					break;
			}			
		}
		
		String title = getIntent().getExtras().getString("title");
		String subTitle = getIntent().getExtras().getString("subTitle");	
		tvTitle.setText(title);
		tvSubtitle.setText(subTitle);
			
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
			// TODO Auto-generated catch block
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
