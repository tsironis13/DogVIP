package com.tsiro.dogvip.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tsiro.dogvip.interfaces.RecyclerViewClickListener;

/**
 * Created by giannis on 24/6/2017.
 */

public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {

    private static final String debguTag = RecyclerViewTouchListener.class.getSimpleName();
    private GestureDetector gestureDetector;
    private RecyclerViewClickListener recyclerViewClickListener;

    public RecyclerViewTouchListener(Context context, RecyclerView recyclerView, RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        // gestureDetector onTouchEvent method forward the appropriate touch events to corresponding
        // gesture detector listeners (onSingleTapUp(MotionEvent e))
        if (child != null && recyclerViewClickListener != null && gestureDetector.onTouchEvent(e)) recyclerViewClickListener.onClick(child, rv.getChildLayoutPosition(child));
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//        Log.e(debguTag, rv.toString());
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

}
