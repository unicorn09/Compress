package com.sacnitp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sacnitp.Fragments.Blog;
import com.sacnitp.Fragments.MyBlogs;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new MyBlogs();
        } return new Blog();
    }

    @Override
    public int getCount() {
        return 2;
    }
}