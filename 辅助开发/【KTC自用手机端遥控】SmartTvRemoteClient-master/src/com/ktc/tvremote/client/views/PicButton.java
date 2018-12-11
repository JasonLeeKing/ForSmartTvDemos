package com.ktc.tvremote.client.views;

import com.ktc.tvremote.client.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/*
 * 上图下文
*/
public class PicButton extends RelativeLayout {
    private int imgId ;
    private ImageView mImgView;
    private Context mContext;

    public PicButton(Context context) {
        super(context);
    }

    public PicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.picButton);
        imgId = typedArray.getResourceId(R.styleable.picButton_pt_img, R.drawable.ic_launcher);
        typedArray.recycle();
        mContext = context;
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_pic_button, this);
        mImgView = (ImageView) view.findViewById(R.id.pic_or_txt_img);
        
        mImgView.setBackgroundResource(imgId);
        mImgView.setVisibility(imgId == R.drawable.ic_launcher ? View.GONE : View.VISIBLE);
    }
    
    
    public void setImageNormal(int normalId) {
    	mImgView.setBackgroundResource(normalId);
    }

    public void setImageFocus(int focusId) {
    	mImgView.setBackgroundResource(focusId);
    }

}
