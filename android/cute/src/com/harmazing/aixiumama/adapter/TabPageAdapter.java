package com.harmazing.aixiumama.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.harmazing.aixiumama.utils.LogUtil;

import java.util.LinkedList;


/**
 * Created by Lyn on 2014/8/28.
 */
public  class TabPageAdapter extends FragmentStatePagerAdapter {
    int NUM_ITEMS = 2;
    LinkedList<Fragment> list = new LinkedList<Fragment>();

    public TabPageAdapter(FragmentManager fm,LinkedList<Fragment> fragmentList) {
        super(fm);
        list = fragmentList;

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
            return list.get(position);
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

    // 初始化每个页卡选项
    @Override
    public Object instantiateItem(ViewGroup arg0, int position) {
        LogUtil.v("instantiateItem", position);
        return super.instantiateItem(arg0, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LogUtil.v("destroyItem",position);
        super.destroyItem(container, position, object);
    }

}