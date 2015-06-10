package com.harmazing.aixiumama.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gyw on 2014/12/16.
 */
public class MyViewPager extends HMViewPager {

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 不拦截事件,让事件向下传递,
     * 这样就不会调用父类的onTouchEvent方法,
     * 所以就不会对viewpager的左右滑动做出响应
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
