package com.erpdevelopment.vbvm.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.VideoVbvmAdapter;
import com.erpdevelopment.vbvm.db.DBHandleVideos;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.VideoVbvm;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {

    private View rootView;
    private VideoVbvmAdapter adapterVideoVbvm;
    private ListView lvVideoVbvm;
    private Activity activity;
    private VideoChannel mVideoChannel;
    private ImageLoader2 imageLoader2;
    List<VideoVbvm> listVideos;

    public VideosFragment() {
    }

    public static VideosFragment newInstance(int index) {
        VideosFragment f = new VideosFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageLoader2 = new ImageLoader2(activity);
        listVideos = new ArrayList<>();
        adapterVideoVbvm = new VideoVbvmAdapter(activity, listVideos, imageLoader2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {// Inflate the layout for this fragment
        if (getArguments() != null)
            mVideoChannel = getArguments().getParcelable("videoChannel");
        return inflater.inflate(R.layout.fragment_video_channels, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();
        listVideos = DBHandleVideos.getVideosByChannel(mVideoChannel.getIdProperty());
        lvVideoVbvm = (ListView) rootView.findViewById(R.id.lvVideoChannels);
        lvVideoVbvm.setAdapter(adapterVideoVbvm);
        lvVideoVbvm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                adapterVideoVbvm.setSelectedPosition(position);
                VideoVbvm videoVbvm = (VideoVbvm) parent.getItemAtPosition(position);
//                Intent i = new Intent(getActivity(), VideosVbvmActivity.class);
//                i.putExtra("channel", channel);
//                startActivity(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoVbvm.getVideoSource()));
                startActivity(intent);
            }
        });
        adapterVideoVbvm.setVideoListItems(listVideos);
//        if ( (FilesManager.listVideoChannels == null) || (FilesManager.listVideoChannels.size() == 0) )
//            DownloadJsonData.getInstance().asyncJsonVideos(adapterVideos);
//        else
//            adapterVideos.setVideoListItems(FilesManager.listVideoChannels);
    }


}
