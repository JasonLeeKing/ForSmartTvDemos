package com.ktc.remote.client.views;

import com.example.mytabhost.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

public class OpenPopupWindow extends PopupWindow {

	private TextView tv_open, tv_cancel;  
    private View mMenuView; 
  
    public TextView getTv_open() {
		return tv_open;
	}

	public void setTv_open(TextView tv_open) {
		this.tv_open = tv_open;
	}

	public OpenPopupWindow(Activity context) {  
        super(context);  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mMenuView = inflater.inflate(R.layout.open_popup_item, null);  
        tv_open = (TextView) mMenuView.findViewById(R.id.tv_open);  
        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);

        setBackgroundDrawable(new ColorDrawable(
				Color.TRANSPARENT)); 
        setContentView(mMenuView); 
        
        //设置SelectPicPopupWindow弹出窗体的宽  
        setWidth(LayoutParams.FILL_PARENT);
        
        //设置SelectPicPopupWindow弹出窗体的高  
        setHeight(LayoutParams.WRAP_CONTENT);  
        
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        
        Animation animation = AnimationUtils.loadAnimation(
				context, R.anim.tran_in);
        mMenuView.startAnimation(animation);
       
         
        
  	    }

	
	public TextView getTv_cancel() {
		return tv_cancel;
	}

	public void setTv_cancel(TextView tv_cancel) {
		this.tv_cancel = tv_cancel;
	}


}
