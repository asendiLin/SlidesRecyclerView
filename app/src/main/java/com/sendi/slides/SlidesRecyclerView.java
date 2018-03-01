package com.sendi.slides;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Created by Administrator on 2018/2/4.
 */

public class SlidesRecyclerView extends RecyclerView {
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private View mItemView;
    private ImageView deleteView;
    private SlidesAdapter.SlidesViewHolder mViewHolder;
    private STATES states = STATES.CLOSE;//默认是关闭的
    private int deleteViewWidth;//功能View的宽度
    private int mLastX;
    private int mLastY;

    enum STATES {
        CLOSE,
        OPEN,
        TO_OPEN,
        TO_CLOSE
    }

    public SlidesRecyclerView(Context context) {
        this(context, null);
    }

    public SlidesRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidesRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mVelocityTracker = VelocityTracker.obtain();

        mScroller = new Scroller(context);
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItemView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else {
            if (states == STATES.TO_OPEN) {
                states = STATES.OPEN;
                deleteView.setVisibility(VISIBLE);
                //显示时添加动画，可自己定义
                Animation animator = AnimationUtils.loadAnimation(getContext(),
                        R.anim.fun_anim);
                deleteView.startAnimation(animator);

            } else if (states == STATES.TO_CLOSE) {
                states = STATES.CLOSE;
                deleteView.setVisibility(INVISIBLE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mVelocityTracker.addMovement(e);
        int startX = (int) e.getX();
        int startY = (int) e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (states == STATES.CLOSE) {//处于关闭状态
                    View view = this.findChildViewUnder(startX, startY);
                    mViewHolder = (SlidesAdapter.SlidesViewHolder) this.findContainingViewHolder(view);

                    mItemView = mViewHolder.ContentView;
                    deleteView = mViewHolder.deleteView;
                    deleteViewWidth = deleteView.getWidth();//获取侧滑出的功能图片的宽

                } else if (states == STATES.OPEN) {//处于打开状态
                    mScroller.startScroll(mItemView.getScrollX(), 0, -deleteViewWidth, 0);//滑动到关闭
                    deleteView.setVisibility(INVISIBLE);
                    invalidate();
                    states = STATES.CLOSE;
                    return false;
                } else {//其他状态
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaX = mLastX - startX;//<0:向右滑动、>0:向左滑动
                int deltaY = mLastY - startY;
                int scrollX = mItemView.getScrollX();//当前条目内容View的mScroller

                if (Math.abs(deltaX) < Math.abs(deltaY)) {//处于向上下滑动时，交给RecyclerView处理
                    break;
                }

                if (deltaX > 0) {//向左滑动
                    if (deltaX + scrollX >= deleteViewWidth) {//防止向左滑动出界
                        mItemView.scrollTo(deleteViewWidth, 0);
                        return true;
                    }
                } else {//向右滑动
                    if (scrollX < 0) {
                        mItemView.scrollTo(0, 0);
                        return true;
                    }
                }

                mItemView.scrollBy(deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                int upScrollX = mItemView.getScrollX();
                mVelocityTracker.computeCurrentVelocity(100);
                int xVleocity = (int) mVelocityTracker.getXVelocity();
                int deltaScrollX = 0;

//                滑动速度大于50
                if (Math.abs(xVleocity) >= 50) {
                    //判断手势
                    if (xVleocity >= 50) {//从左向右
                        deltaScrollX = -upScrollX;
                        states = STATES.TO_CLOSE;//关闭趋势

                    } else if (xVleocity <= -50) {//从右向左
                        deltaScrollX = deleteViewWidth - upScrollX;
                        states = STATES.TO_OPEN;//打开趋势
                    }
                } else {
                    if (upScrollX >= deleteViewWidth / 2) {
                        //当向左滑动超过删除View的宽度的1/2时，说明要打开
                        deltaScrollX = deleteViewWidth - upScrollX;
                        states = STATES.TO_OPEN;
                    } else {
                        //当向左滑动小于删除View的宽度的1/2时，说明要打开
                        deltaScrollX = -upScrollX;
                        states = STATES.TO_CLOSE;
                    }
                }

                mScroller.startScroll(upScrollX, 0, deltaScrollX, 0, 500);

                invalidate();
                mVelocityTracker.clear();

                startX = 0;//防止下次无法对View的滑动

                break;

        }
        mLastX = startX;
        mLastY = startY;
        return super.onTouchEvent(e);
    }

}
