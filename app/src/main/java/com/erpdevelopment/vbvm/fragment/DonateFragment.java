package com.erpdevelopment.vbvm.fragment;

import com.erpdevelopment.vbvm.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DonateFragment extends Fragment {
	
	public DonateFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
         
        return rootView;
    }
}
