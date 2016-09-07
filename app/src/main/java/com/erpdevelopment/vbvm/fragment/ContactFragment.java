package com.erpdevelopment.vbvm.fragment;

import java.io.IOException;
import java.io.InputStream;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.utils.ConstantsVbvm;
import com.erpdevelopment.vbvm.utils.Utilities;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactFragment extends Fragment {
	
	private View rootView;
	private TextView tvContent;
	private String fileName = "";
	private LinearLayout llVbvmDetail;
	private ImageView btnBack;
	
	public ContactFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.activity_vbvm_contact, container, false);
         
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
//		btnBack = (ImageView) rootView.findViewById(R.id.btn_go_back);		
//		Utilities.setBackImageEventHandler(btnBack, getActivity());
		
//		RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.top_control_bar);
//		layout.setVisibility(View.GONE);
		
		Utilities.setActionBar(getActivity(), null);
		
		tvContent = (TextView) rootView.findViewById(R.id.tv_vbvm_contact);
		llVbvmDetail = (LinearLayout) rootView.findViewById(R.id.ll_vbvm_detail_header);
		llVbvmDetail.setVisibility(View.GONE);
		
		try {
			InputStream is = getActivity().getAssets().open(ConstantsVbvm.FILENAME_CONTACT);
            
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
}
