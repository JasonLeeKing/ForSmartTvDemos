package com.ktc.remote.client.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mytabhost.R;

public class CustomUI {

	public static Dialog tipdialog;	
	public static void showtips(Context context,int id){
		tipdialog = new Dialog(context, R.style.dialog);
		View tipview = LayoutInflater.from(context).inflate(R.layout.tips, null);
		TextView tv = (TextView) tipview.findViewById(R.id.tip_tv);
		tv.setText(id);
		int w = MyUtils.dip2px(context,180);
		int h = MyUtils.dip2px(context, 50);
		
		tipdialog.setContentView(tipview,new ViewGroup.LayoutParams(w, h));
		tipdialog.setCanceledOnTouchOutside(false);
	
		tipdialog.show();
	}
	
	public static void showBindTipDialog(Context context){
		AlertDialog.Builder builder = new Builder(context);
    	builder.setMessage(context.getResources().getString(R.string.market_tips_not_bind));
    	builder.setPositiveButton(context.getResources().getString(R.string.market_btn_bind),new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which)
    	    {
    	     dialog.dismiss();

    	   }
    	   });
    	builder.setNegativeButton(context.getResources().getString(R.string.market_btn_cancel),new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which)
    	    {
    	     dialog.dismiss();

    	   }
    	   });
    	AlertDialog  dialog = builder.create();
    	dialog.show();
	}
	
	public static void showtips(Context context,int id,long duration){
		tipdialog = new Dialog(context, R.style.dialog);
		View tipview = LayoutInflater.from(context).inflate(R.layout.tips, null);
		TextView tv = (TextView) tipview.findViewById(R.id.tip_tv);
		tv.setText(id);
		int w = MyUtils.dip2px(context,180);
		int h = MyUtils.dip2px(context, 50);
		
		tipdialog.setContentView(tipview,new ViewGroup.LayoutParams(w, h));
	
		tipdialog.show();
		Runnable runner = new Runnable() { 
            public void run() { 
            	tipdialog.dismiss(); 
            } 
        };
        
        Executors.newSingleThreadScheduledExecutor().schedule(runner, duration, TimeUnit.SECONDS);
	}
	
	
}
