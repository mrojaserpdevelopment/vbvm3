package com.erpdevelopment.vbvm.fragment;

import java.util.ArrayList;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AboutDetailsActivity;
import com.erpdevelopment.vbvm.activity.StaffBoardActivity;
import com.erpdevelopment.vbvm.adapter.VbvmAdapter;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AboutFragment extends Fragment {
	
	private ListView lv;
	private VbvmAdapter vbvmAdapter;
	private View rootView;
	
	private static String[] values = new String[] { 
		"Our History", 
		"Our Beliefs", 
		"How To Get Involved", 
		"Where Your Money Goes",
		"About The Founder", 
		"Our Services", 
		"Our Mission", 
		"Our Staff", 
		"Board of Directors"
         };
	
	public AboutFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.activity_vbvm_listview, container, false);
         
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Utilities.setActionBar(getActivity(), "About");
//		tvTitle.setText("About VBVMI");
		
	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }    
	    
		vbvmAdapter = new VbvmAdapter(getActivity(), list);		
		lv = (ListView) rootView.findViewById(R.id.lv_bible_studies);
        lv.setAdapter(vbvmAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {

					vbvmAdapter.setSelectedPosition(position);
	
					Intent i = new Intent(getActivity(), AboutDetailsActivity.class);
					i.putExtra("posSelected", position);
					
					switch (position) {
					
						case 0: startActivity(i);
							break;
						case 1:	startActivity(i);
							break;
						case 2:	startActivity(i);
							break;
						case 3:	startActivity(i);
							break;
						case 4:	startActivity(i);
							break;
						case 5:	startActivity(i);
							break;
						case 6:	startActivity(i);
							break;
						case 7:	Intent intentStaff = new Intent(getActivity(), StaffBoardActivity.class);
								intentStaff.putExtra("category", "staff");
								startActivity(intentStaff);	
							break;
						case 8: Intent intentBoard = new Intent(getActivity(), StaffBoardActivity.class);
								intentBoard.putExtra("category", "board");
								startActivity(intentBoard);
							break;
					}
			}
		});

	}
}
