package com.custom.tvzoomview;

import com.arvin.tvzoomview.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TvZoomLayout extends RelativeLayout{
    
    private Rect mBound;
    private Drawable mDrawable;
    private Rect mRect;
    private AnimatorSet mAnimatorSetZoomOut;
    private AnimatorSet mAnimatorSetZoomIn;
    private int SELECT_PADDING = 10;
    
    public TvZoomLayout(Context context) {
        super(context);
        init();
    }
    
    public TvZoomLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public TvZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    protected void init() { 
        setWillNotDraw(false);
        mRect = new Rect();
        mBound = new Rect();
        mDrawable = getResources().getDrawable(R.drawable.focused); 
        setChildrenDrawingOrderEnabled(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if (hasFocus()) {
            super.getDrawingRect(mRect);
            mBound.set(mRect.left - SELECT_PADDING, mRect.top - SELECT_PADDING, mRect.right + SELECT_PADDING, mRect.bottom + SELECT_PADDING);
            mDrawable.setBounds(mBound);
            canvas.save();
            //mDrawable.draw(canvas);
            canvas.restore();
        }
        super.onDraw(canvas);
    }
    
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            bringToFront();
            zoomOut();
        }
        else {
            zoomIn();
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        
    }
    
    private void zoomIn() {
        if (mAnimatorSetZoomIn == null) {
            mAnimatorSetZoomIn = new AnimatorSet();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1.2f, 1.0f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1.2f, 1.0f);
            animatorX.setDuration(500);
            animatorY.setDuration(500);
            mAnimatorSetZoomIn.playTogether(animatorX, animatorY);
        }
        mAnimatorSetZoomIn.start();
    }
    
    private void zoomOut() {
        if (mAnimatorSetZoomOut == null) {
            mAnimatorSetZoomOut = new AnimatorSet();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.2f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.2f);
            animatorX.setDuration(500);
            animatorY.setDuration(500);
            mAnimatorSetZoomOut.playTogether(animatorX, animatorY);
        }
        mAnimatorSetZoomOut.start();
    }
}
