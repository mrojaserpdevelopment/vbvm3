package com.erpdevelopment.vbvm.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.VideosVbvmActivity;
import com.erpdevelopment.vbvm.adapter.ChannelVbvmAdapter;
import com.erpdevelopment.vbvm.model.ChannelVbvm;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

import java.io.File;

public class VideosFragment extends Fragment implements TextWatcher {

    private View rootView;
    private ChannelVbvmAdapter adapterVideos;
    private ListView lvVideos;

    public VideosFragment() {
        // Required empty public constructor
    }

    public static VideosFragment newInstance(int index) {
        VideosFragment f = new VideosFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();
        adapterVideos = new ChannelVbvmAdapter(getActivity(), FilesManager.listChannels);
        lvVideos = (ListView) rootView.findViewById(R.id.lvVideos);
        lvVideos.setAdapter(adapterVideos);
        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                adapterVideos.setSelectedPosition(position);
                ChannelVbvm channel = (ChannelVbvm) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), VideosVbvmActivity.class);
                i.putExtra("channel", channel);
                startActivity(i);
            }
        });
        if ( (FilesManager.listChannels == null) || (FilesManager.listChannels.size() == 0) )
            DownloadJsonData.getInstance().asyncJsonVideos(adapterVideos);
        else
            adapterVideos.setVideoListItems(FilesManager.listChannels);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterVideos.getFilter().filter(s.toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
