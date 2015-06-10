package com.harmazing.aixiumama.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.harmazing.aixiumama.fragment.UserArrayFragment;

/**
 * Created by Administrator on 2014/11/6.
 */
public class UserTabPageAdapter  extends FragmentStatePagerAdapter {
    int NUM_ITEMS = 3;
    int userId;
    public UserTabPageAdapter(FragmentManager fm,int id) {
        super(fm);
        userId = id;
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
            return UserArrayFragment.newInstance(position, userId);
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
