package com.ktc.remote.client.bean;

import com.ktc.remote.client.utils.SoftValueMap;

import android.graphics.Bitmap;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class GlobalParams {
	//将MainActivity中的TabHost赋值给TH，这样可以在其他地方方便地对它进行操作。
	public static TabHost TH=null;
	
	public static String DEVICENAME;
	
	public static String DEVICEID;
	
	public static String IP;
	
	public static SoftValueMap<Object, Bitmap> IMGCACHE = new SoftValueMap<Object, Bitmap>();

    public static String userid = "";

	public static String username = "";
	
	public static String userpwd = "";

	public static boolean isLogin = false;

	public static TextView tv_device;	
	
	public static boolean isget = true;

}
