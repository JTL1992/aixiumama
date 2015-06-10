package com.harmazing.aixiumama.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * Created by Lyn on 2014/11/23.
 * 重新计算GridView的高度
 */
public class GridViewUtility {

    public static void setGridViewHeightOnChildren(GridView gridView){
        ListAdapter adapter = gridView.getAdapter();

        if (adapter == null) {
            // pre-condition
            return;
        }
        int rowCount = 0;
        int numColimns  = gridView.getNumColumns();
        if(numColimns == -1){
            //  消息模块收藏页面的gridview特此设定，这是问题要改
            numColimns = 2;
        }

        float numLine = adapter.getCount() / numColimns;
        int numLineInt = adapter.getCount() /numColimns;

        if (numLine - (float)numLineInt < 1.0){
            rowCount = numLineInt + 1;
        }else if(numLine - (float)numLineInt == 0.0){
            //  整除
            rowCount = numLineInt;
        }

        int totalHeight = 0;
        for (int i = 0; i < rowCount; i++) {
            View listItem = adapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getVerticalSpacing() *(rowCount - 1));
        gridView.setLayoutParams(params);

    }

    /**
     * 不能正确获取item高度的时候就自己指定咯(在指定item控件高度之后)
     * @param gridView
     * @param itemHeight
     */
    public static void setGridViewHeightByMySelf(GridView gridView,int itemHeight){
        ListAdapter adapter = gridView.getAdapter();

        if (adapter == null) {
            // pre-condition
            return;
        }
        int rowCount = 0;
        int numColimns  = gridView.getNumColumns();
        if(numColimns == -1){
            //  消息模块收藏页面的gridview特此设定，这是问题要改
            numColimns = 2;
        }

        float numLine = adapter.getCount() / numColimns;
        int numLineInt = adapter.getCount() /numColimns;

        if (numLine - (float)numLineInt < 1.0){
            rowCount = numLineInt + 1;
        }else if(numLine - (float)numLineInt == 0.0){
            //  整除
            rowCount = numLineInt;
        }

        int totalHeight = 0;
        totalHeight = itemHeight * rowCount;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getVerticalSpacing() *(rowCount - 1));
        gridView.setLayoutParams(params);

    }

    public static int getGridViewHeight(GridView gridView,int itemHeight){
        ListAdapter adapter = gridView.getAdapter();

        int rowCount = 0;
        int numColimns  = gridView.getNumColumns();
        if(numColimns == -1){
            //  消息模块收藏页面的gridview特此设定，这是问题要改
            numColimns = 2;
        }

        float numLine = adapter.getCount() / numColimns;
        int numLineInt = adapter.getCount() /numColimns;

        if (numLine - (float)numLineInt < 1.0){
            rowCount = numLineInt + 1;
        }else if(numLine - (float)numLineInt == 0.0){
            //  整除
            rowCount = numLineInt;
        }

        int totalHeight = 0;
        totalHeight = itemHeight * rowCount;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getVerticalSpacing() *(rowCount - 1));
        return params.height;
    }
}
