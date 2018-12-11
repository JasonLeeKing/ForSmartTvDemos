package com.ktc.tvremote.client.activity;

import android.app.Application;
import android.content.Context;


/**
 * @TODO 初始化Application
 * @author Arvin
 * @since 2018.6.20
 */
public class HomeApplication extends Application{
	
	private static Context context;
	private static HomeApplication instance;
	
	@Override
	public void onCreate () {
		super.onCreate();
		instance = this;
        context = getApplicationContext();
		CrashHandler.getInstance().init(context);  
	}
	
    /**
     * @TODO init HomeApplication instance
     * @return HomeApplication 
     */
    public static HomeApplication getInstance(){
	   if(null == instance){
		   instance = new HomeApplication();
	    }
	    return instance;
    }
}
