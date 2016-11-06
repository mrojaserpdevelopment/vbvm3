package com.erpdevelopment.vbvm.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.adapter.VideoChannelsAdapter;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

public class VideoChannelsFragment extends Fragment {

    private View rootView;
    private VideoChannelsAdapter adapterVideoChannels;
    private ListView lvVideoChannels;

    private OnVideoChannelSelectedListener mListener;

    public VideoChannelsFragment() {
    }

    public static VideoChannelsFragment newInstance(int index) {
        VideoChannelsFragment f = new VideoChannelsFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_channels, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();
        adapterVideoChannels = new VideoChannelsAdapter(getActivity(), FilesManager.listVideoChannels);
        System.out.println("FilesManager.listVideoChannels: " + FilesManager.listVideoChannels.size());
        lvVideoChannels = (ListView) rootView.findViewById(R.id.lvVideoChannels);
        lvVideoChannels.setAdapter(adapterVideoChannels);
        lvVideoChannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                VideoChannel videoChannel = (VideoChannel) parent.getItemAtPosition(position);
                mListener.onVideoChannelSelected(videoChannel);
            }
        });

        if ( (FilesManager.listVideoChannels == null) || (FilesManager.listVideoChannels.size() == 0) )
            DownloadJsonData.getInstance().asyncJsonVideos(adapterVideoChannels);
        else
            adapterVideoChannels.setVideoListItems(FilesManager.listVideoChannels);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVideoChannelSelectedListener) {
            mListener = (OnVideoChannelSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVideoChannelSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnVideoChannelSelectedListener {
        void onVideoChannelSelected(VideoChannel videoChannel);
    }
}
