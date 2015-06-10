package com.harmazing.aixiumama.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.harmazing.aixiumama.fragment.StickersActivityArrayFragment;

/**
 * Created by Lyn on 2014/11/25.
 */
public class StickersActivityTabPageAdapter extends FragmentStatePagerAdapter {
    int NUM_ITEMS = 2;
    String stickerID;
    public StickersActivityTabPageAdapter(FragmentManager fm,String id) {
        super(fm);
        stickerID = id;
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
            return StickersActivityArrayFragment.newInstance(position, stickerID);
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
