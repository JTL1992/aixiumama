package com.harmazing.aixiumama.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.harmazing.aixiumama.fragment.CutedArrayFragment;

/**
 * Created by Administrator on 2014/12/8.
 */
public class CutedTabPageAdapter extends FragmentStatePagerAdapter {
    int NUM_ITEMS = 2;
    public CutedTabPageAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    //得到每个item
    @Override
    public Fragment getItem(int position)
    {
        try {
            return CutedArrayFragment.newInstance(position);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


}
