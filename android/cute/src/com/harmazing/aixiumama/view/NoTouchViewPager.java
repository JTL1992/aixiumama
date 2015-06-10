package com.harmazing.aixiumama.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Lyn on 2014/11/12.
 */
public class NoTouchViewPager extends HMViewPager {


    public NoTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
