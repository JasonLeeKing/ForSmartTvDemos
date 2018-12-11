package com.ktc.tvremote.client.views;


import com.ktc.tvremote.client.R;

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

public class DownloadTaskPopupMenu extends PopupWindow {

	
	private TextView pop_unDonwload,pop_cancel;  
	    private View mMenuView; 
	    
	  
	    public DownloadTaskPopupMenu(Activity context) { 
	        super(context);  
	        LayoutInflater inflater = (LayoutInflater) context  
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	        mMenuView = inflater.inflate(R.layout.download_popup_item, null);  
	        pop_unDonwload = (TextView) mMenuView.findViewById(R.id.pop_undownload);  
	        pop_cancel = (TextView) mMenuView.findViewById(R.id.pop_cancel);
	
	        setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT)); 
	        setContentView(mMenuView); 
	        
	        //����SelectPicPopupWindow��������Ŀ�  
	        setWidth(LayoutParams.FILL_PARENT);
	        
	        //����SelectPicPopupWindow��������ĸ�  
	        setHeight(LayoutParams.WRAP_CONTENT);  
	        
	        //����SelectPicPopupWindow��������ɵ��  
	        this.setFocusable(true);  
	        
	        Animation animation = AnimationUtils.loadAnimation(
					context, R.anim.tran_in);
	        mMenuView.startAnimation(animation);    
	        
	  	    }

		public TextView get_pop_unbundling() {
			return pop_unDonwload;
		}

		public TextView get_pop_cancel() {
			return pop_cancel;
		}
}
