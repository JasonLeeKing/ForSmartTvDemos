package com.ktc.tvremote.client.transform;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author Arvin
 * @version v1.0
 * @since 2017.5.4
 */
public abstract class BGAPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        if (position < -1.0f) {
            // [-Infinity,-1)
            // This page is way off-screen to the left.
            handleInvisiblePage(view, position);
        } else if (position <= 0.0f) {
            // [-1,0]
            // Use the default slide transition when moving to the left page
            handleLeftPage(view, position);
        } else if (position <= 1.0f) {
            // (0,1]
            handleRightPage(view, position);
        } else {
            // (1,+Infinity]
            // This page is way off-screen to the right.
            handleInvisiblePage(view, position);
        }
    }

    public abstract void handleInvisiblePage(View view, float position);

    public abstract void handleLeftPage(View view, float position);

    public abstract void handleRightPage(View view, float position);

    public static BGAPageTransformer getPageTransformer(TransitionEffect effect) {
        switch (effect) {
            case Default:
                return new DefaultPageTransformer();
            case Alpha:
                return new AlphaPageTransformer();
            case Cube:
                return new CubePageTransformer();
            case Depth:
                return new DepthPageTransformer();
            case Tablet:
                return new TabletPageTransformer();
            default:
                return new DefaultPageTransformer();
        }
    }
}