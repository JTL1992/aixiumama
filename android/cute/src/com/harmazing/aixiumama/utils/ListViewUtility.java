package com.harmazing.aixiumama.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.harmazing.aixiumama.application.CuteApplication;

/**
 * Created by Lyn on 2014/11/8.
 */
public class ListViewUtility {
    /**
     * 重新计算listview高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0,0);

            totalHeight += CuteApplication.getHeight(listItem);
//            totalHeight += listItem.getHeight();
            LogUtil.v("LU", CuteApplication.getHeight(listItem));
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * ( 1));
        listView.setLayoutParams(params);
    }

    public static void setListViewByPx(ListView listView, int height){
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height;
        listView.setLayoutParams(params);
    }
    public static int getListViewHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return params.height;
    }

    public static int getListViewHeightByMySelf(ListView listView,int height) {
        ListAdapter listAdapter = listView.getAdapter();

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            totalHeight += height;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return params.height;
    }

}