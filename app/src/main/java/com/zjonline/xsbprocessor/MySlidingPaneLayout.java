package com.zjonline.xsbprocessor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;


/**
 * 互动返回使用的
 */
public class MySlidingPaneLayout extends SlidingPaneLayout {
    public float touchLeftOffset;//左边的控制区域
    boolean isIntercept;
    private float tempX, tempY, downX;

    public MySlidingPaneLayout(Context context) {
        super(context);
        touchLeftOffset = dip2px(context, 50);
    }

    /**
     * 根据手机的分辨率dp 的单转成px(像素)
     */
    private   int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public MySlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 设置左边在什么位置可以滑动 finish
     *
     * @param touchLeftOffset_DP 左边可以滑动的距离
     */
    public void setTouchLeftOffset(float touchLeftOffset_DP) {
        touchLeftOffset = dip2px(getContext(), touchLeftOffset_DP);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept && isInterceptChild && super.onInterceptTouchEvent(ev);
    }

    private boolean isInterceptChild = true;

    /**
     * 设置是否需要拦击
     *
     * @param isInterceptChild true 拦截  false不拦截
     */
    public void setIntercept(boolean isInterceptChild) {
        this.isInterceptChild = isInterceptChild;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = ev.getX();
                tempY = ev.getY();
                isIntercept = downX < touchLeftOffset;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - tempX;
                float dy = y - tempY;
                tempX = x;
                tempY = y;
                isIntercept = Math.abs(dx) - Math.abs(dy) > 5 && downX < touchLeftOffset;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public static MySlidingPaneLayout swipeBackView(final Activity context, int layoutId) {
        final MySlidingPaneLayout mSlidingPaneLayout = new MySlidingPaneLayout(context);
        try {
            //mOverhangSize属性，意思就是左菜单离右边屏幕边缘的距离
            // SlidingPaneLayout没有暴露这个方法 所以要用反射
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            //设置左菜单离右边屏幕边缘的距离为0，设置全屏
            f_overHang.set(mSlidingPaneLayout, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSlidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                context.getWindow().getDecorView().setAlpha(1f - slideOffset);
                mSlidingPaneLayout.getChildAt(0).setAlpha(1f - slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                context.finish();
            }

            @Override
            public void onPanelClosed(View panel) {
            }
        });

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //添加两个view
        View leftView = new View(context);
        leftView.setLayoutParams(params);
        leftView.setBackgroundColor(0xcc000000);
        mSlidingPaneLayout.addView(leftView, 0);


        LayoutInflater.from(context).inflate(layoutId, mSlidingPaneLayout, true);
        mSlidingPaneLayout.setTouchLeftOffset(context.getResources().getDisplayMetrics().widthPixels);

        View rightView = mSlidingPaneLayout.getChildAt(1);
        Drawable drawable = rightView.getBackground();
        if (drawable == null) rightView.setBackgroundColor(Color.parseColor("#f0f0f0"));

        mSlidingPaneLayout.setSliderFadeColor(0x00000000);
        return mSlidingPaneLayout;
    }
}
