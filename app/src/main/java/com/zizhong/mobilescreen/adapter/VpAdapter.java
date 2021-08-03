package com.zizhong.mobilescreen.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VpAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fm;

    public VpAdapter(FragmentManager fm, ArrayList<Fragment> fm1) {
        super(fm);
        this.fm = fm1;
    }

    @Override
    public Fragment getItem(int position) {
        return fm.get(position);
    }

    @Override
    public int getCount() {
        return fm.size();
    }
}
