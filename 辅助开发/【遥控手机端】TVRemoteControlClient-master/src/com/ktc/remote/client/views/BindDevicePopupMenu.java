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

public class BindDevicePopupMenu extends PopupWindow {

	
	private TextView pop_unbundling,pop_cancel;  
	    private View mMenuView; 
	    
	  
	    public BindDevicePopupMenu(Activity context) { 
	        super(context);  
	        LayoutInflater inflater = (LayoutInflater) context  
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	        mMenuView = inflater.inflate(R.layout.binddevice_popup_item, null);  
	        pop_unbundling = (TextView) mMenuView.findViewById(R.id.pop_unbundling);  
	        pop_cancel = (TextView) mMenuView.findViewById(R.id.pop_cancel);
	
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

		public TextView get_pop_unbundling() {
			return pop_unbundling;
		}

		public TextView get_pop_cancel() {
			return pop_cancel;
		}
}
