package com.tsiro.dogvip.utilities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by giannis on 12/7/2017.
 */

public class CustomViewPager extends ViewPager {

    private boolean paging;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.paging && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return paging && super.onInterceptTouchEvent(ev);
    }

    public void enablePaging(boolean paging) {
        this.paging = paging;
    }

}
