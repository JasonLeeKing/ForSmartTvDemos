package com.ktc.tvremote.client.transform;

import android.view.View;

/**
 * @author Arvin
 * @version v1.0
 * @since 2017.5.4
 */

public class DepthPageTransformer extends BGAPageTransformer {

	private static final float MIN_SCALE = 0.75f;

	@Override
	public void handleInvisiblePage(View view, float position) {
		handleTransform(view, position);
	}

	@Override
	public void handleLeftPage(View view, float position) {
		handleTransform(view, position);
	}

	@Override
	public void handleRightPage(View view, float position) {
		handleTransform(view, position);
	}
	
	protected void handleTransform(View view, float position) {
		if (position <= 0f) {
            ViewHelper.setTranslationX(view,0f);
            ViewHelper.setScaleX(view,1f);
            ViewHelper.setScaleY(view,1f);
		} else if (position <= 1f) {
			final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            ViewHelper.setAlpha(view,1-position);
            ViewHelper.setPivotY(view,0.5f * view.getHeight());
            ViewHelper.setTranslationX(view,view.getWidth() * - position);
            ViewHelper.setScaleX(view,scaleFactor);
            ViewHelper.setScaleY(view,scaleFactor);
		}
	}

}
