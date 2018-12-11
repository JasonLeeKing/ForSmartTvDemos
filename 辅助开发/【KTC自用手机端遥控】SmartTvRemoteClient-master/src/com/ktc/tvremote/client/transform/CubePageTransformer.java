package com.ktc.tvremote.client.transform;

import android.view.View;

/**
 * @author Arvin
 * @version v1.0
 * @since 2017.5.4
 */
public class CubePageTransformer extends BGAPageTransformer {
    private float mMaxRotation = 90.0f;
    private float mMinScale = 0.0f;

    public CubePageTransformer() {
    }

    public CubePageTransformer(float maxRotation) {
        setMaxRotation(maxRotation);
    }

    @Override
    public void handleInvisiblePage(View view, float position) {
        ViewHelper.setPivotX(view, view.getMeasuredWidth());
        ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
        ViewHelper.setRotationY(view, 0);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        ViewHelper.setPivotX(view, view.getMeasuredWidth());
        ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
        ViewHelper.setRotationY(view, mMaxRotation * position);
        ViewHelper.setAlpha(view, mMinScale + (1 - mMinScale) * (1 + position));
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewHelper.setPivotX(view, 0);
        ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
        ViewHelper.setRotationY(view, mMaxRotation * position);
        ViewHelper.setAlpha(view, mMinScale + (1 - mMinScale) * (1 - position));
    }

    public void setMaxRotation(float maxRotation) {
        if (maxRotation >= 0.0f && maxRotation <= 90.0f) {
            mMaxRotation = maxRotation;
        }
    }

}
