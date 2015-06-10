package com.harmazing.aixiumama.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.harmazing.aixiumama.fragment.FilterViewFragment;

/**
 * Created by Lyn on 2014/11/12.
 */
public class FilterViewAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_ITEMS = 2;

    public FilterViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    //得到每个item
    @Override
    public Fragment getItem(int position) {
        /*BaseFragment baseFragment = null;
            switch (position) {
                case 0:
                    baseFragment = new BeautifyFragment();
                    break;
                case 1:
                    baseFragment = new PagerFragment();
                    break;
            }
        return baseFragment;*/

        try {
            return FilterViewFragment.newInstance(position);
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
