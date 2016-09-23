package com.erpdevelopment.vbvm.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.view.SlidingDownPanelLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class LessonsFragment extends Fragment {

    private View rootView;

    public LessonsFragment() {
        // Required empty public constructor
    }

    public static LessonsFragment newInstance(int index) {
        LessonsFragment f = new LessonsFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();

        SlidingDownPanelLayout layout = (SlidingDownPanelLayout) rootView.findViewById(R.id.sliding_layout);
        layout.setSliderFadeColor(Color.argb(128, 0, 0, 0));
        layout.setParallaxDistance(100);

        layout.setPanelSlideListener(new SlidingDownPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelOpened(View panel) {
            }

            @Override
            public void onPanelClosed(View panel) {
            }
        });
    }
}
