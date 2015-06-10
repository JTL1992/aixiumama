package com.harmazing.aixiumama.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2015/1/28.
 */
public class BaseMyListView extends ListView{
    public BaseMyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMyListView(Context context) {
        super(context);
    }

    public BaseMyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
