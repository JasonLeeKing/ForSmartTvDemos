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

public class CustomPopupWindow extends PopupWindow {

	
	private TextView tv_start, tv_uninstall, tv_cancel;  
	    private View mMenuView; 
	    
	  
	    public CustomPopupWindow(Activity context) {  
	        super(context);  
	        LayoutInflater inflater = (LayoutInflater) context  
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	        mMenuView = inflater.inflate(R.layout.popup_item, null);  
	        tv_start = (TextView) mMenuView.findViewById(R.id.pop_title);  
	        tv_uninstall = (TextView) mMenuView.findViewById(R.id.pop_unbundling);
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

		public TextView getTv_uninstall() {
			return tv_uninstall;
		}

		public void setTv_uninstall(TextView tv_uninstall) {
			this.tv_uninstall = tv_uninstall;
		}

		public TextView getTv_cancel() {
			return tv_cancel;
		}

		public void setTv_cancel(TextView tv_cancel) {
			this.tv_cancel = tv_cancel;
		}

		public TextView getTv_start() {
			return tv_start;
		}

		public void setTv_start(TextView tv_start) {
			this.tv_start = tv_start;
		}

}
