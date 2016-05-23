package com.cxmax.floatview.widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 自定义悬浮View
 * Created by cxMax on 2016/5/23.
 */
public class FloatView extends RelativeLayout {
    private ViewDragHelper mDragger;

    private void moveToSide(View view) {
        float top = view.getTop();
        float bottom = getMeasuredHeight() - view.getBottom();
        float right = getMeasuredWidth() - view.getRight();
        float left = view.getLeft();
        //上下滑动
        if ((top < bottom ? top : bottom) / getMeasuredHeight() < (right < left ? right : left) / getMeasuredWidth()) {
            mDragger.settleCapturedViewAt(view.getLeft(), top < bottom ? 0 : getMeasuredHeight() - view.getMeasuredHeight());
        } else {
            //左右滑动
            mDragger.settleCapturedViewAt(left < right ? 0 : getMeasuredWidth() - view.getMeasuredWidth(), view.getTop());
        }
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - child.getWidth() - leftBound;
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - child.getHeight();
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                return newTop;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                moveToSide(releasedChild);
                invalidate();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setClickable(true);
        }
    }

}
