package com.erpdevelopment.vbvm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.erpdevelopment.vbvm.fragment.AnswersFragment;
import com.erpdevelopment.vbvm.fragment.ArticlesFragment;
import com.erpdevelopment.vbvm.fragment.StudiesFragment;
import com.erpdevelopment.vbvm.fragment.VideosFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Studies Fragment
                return new StudiesFragment();
            case 1:
                //Articles Fragment
                return new ArticlesFragment();
            case 2:
                //Answers Fragment
                return new AnswersFragment();
            case 3:
                //Videos Fragment
                return new VideosFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}