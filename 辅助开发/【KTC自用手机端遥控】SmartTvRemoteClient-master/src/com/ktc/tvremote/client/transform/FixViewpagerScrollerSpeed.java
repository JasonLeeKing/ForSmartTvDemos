package com.ktc.tvremote.client.transform;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;

/**
 * @author Arvin
 * @version v1.0
 * @since 2017.5.4
 */

public class FixViewpagerScrollerSpeed extends Scroller {

    private static int mDuration = 1000;//ms

    public FixViewpagerScrollerSpeed(Context context) {
        super(context);
    }

    public FixViewpagerScrollerSpeed(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public static void setViewPagerScrollSpeed(ViewPager viewPager , int duration){
        try {
        	mDuration = duration ;
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixViewpagerScrollerSpeed scroller = new FixViewpagerScrollerSpeed(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        	e.printStackTrace();
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
        } catch (IllegalAccessException e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
