package com.harmazing.aixiumama.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义gridview，解决ListView中嵌套gridview显示不正常的问题
 * Created by guoyongwei on 2014/12/29.
 */
public class BaseMyGridView extends GridView {
    public BaseMyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMyGridView(Context context) {
        super(context);
    }

    public BaseMyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
