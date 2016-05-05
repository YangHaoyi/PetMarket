package com.lbt.petmarket.customView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/4/9 0009.
 */
public class ScrollSwipeRefreshLayout extends SwipeRefreshLayout {

    private ViewGroup viewGroup ;
    private boolean mIsAllowRefresh;

    public ScrollSwipeRefreshLayout(Context context) {
        super(context);
    }
    public ScrollSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    public ViewGroup getViewGroup() {
        return viewGroup;
    }
    public void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    private int mTouchSlop;
    // 上一次触摸时的X坐标
    private float mPrevX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);
                // Log.d("refresh" ,"move----" + eventX + "   " + mPrevX + "   " + mTouchSlop);
                // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                if (xDiff > mTouchSlop + 60) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent arg0) {
//        if(null!=viewGroup){
//            if(viewGroup.getScrollY()> 1){
//                //直接截断时间传播
//                return false;
//            }else{
//                return super.onTouchEvent(arg0);
//            }
//        }
//        return super.onTouchEvent(arg0);
//    }

}