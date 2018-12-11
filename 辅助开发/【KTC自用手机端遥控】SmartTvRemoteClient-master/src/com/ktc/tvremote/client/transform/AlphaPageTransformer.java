package com.ktc.tvremote.client.transform;

import android.view.View;


/**
 * @author Arvin
 * @version v1.0
 * @since 2017.5.4
 */
public class AlphaPageTransformer extends BGAPageTransformer {
    private float mMinScale = 0.2f;

    public AlphaPageTransformer() {
    }

    public AlphaPageTransformer(float minScale) {
        setMinScale(minScale);
    }

    @Override
    public void handleInvisiblePage(View view, float position) {
        ViewHelper.setAlpha(view, 0);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        ViewHelper.setAlpha(view, mMinScale + (1 - mMinScale) * (1 + position));
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewHelper.setAlpha(view, mMinScale + (1 - mMinScale) * (1 - position));
    }

    public void setMinScale(float minScale) {
        if (minScale >= 0.0f && minScale <= 1.0f) {
            mMinScale = minScale;
        }
    }
}