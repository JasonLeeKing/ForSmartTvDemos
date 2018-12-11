package com.ktc.remote.client.main;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GlobalParams;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private AppmanageFragment appfragment;
	private EmarketFragment emarketfragment;
	private int mBackKeyPressedTimes = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		final TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
		//给全局tabHost赋值，作用是在点击MouseFragment中的返回按钮时，可以让tabHost中的第一个内容选中，并将其显示出来。
		GlobalParams.TH=tabHost;
		tabHost.setup();
		
		ImageButton iv1 = new ImageButton(HomeActivity.this);
		iv1.setBackgroundResource(R.drawable.btn_remotecontrol);
		ImageButton iv2 = new ImageButton(HomeActivity.this);
		iv2.setBackgroundResource(R.drawable.btn_mouse);
		ImageButton iv3 = new ImageButton(HomeActivity.this);
		iv3.setBackgroundResource(R.drawable.btn_appmanage);
		ImageButton iv4 = new ImageButton(HomeActivity.this);
		iv4.setBackgroundResource(R.drawable.btn_emarket);
		ImageButton iv5 = new ImageButton(HomeActivity.this);
		iv5.setBackgroundResource(R.drawable.btn_set);
		
		FragmentManager fm = getFragmentManager();
		appfragment = (AppmanageFragment) fm.findFragmentById(R.id.appmanage_tab);
		emarketfragment = (EmarketFragment) fm.findFragmentById(R.id.emarket_tab);
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(iv1).setContent(R.id.controller_tab));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(iv2).setContent(R.id.mouse_tab));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(iv3).setContent(R.id.appmanage_tab));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator(iv4).setContent(R.id.emarket_tab));
		tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator(iv5).setContent(R.id.set_tab));
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if("tab2".equals(tabId)){
					//当选中第二个tab时让底部的tabHost隐藏，这样就可以全屏显示MouseFragment了
					tabHost.getTabWidget().setVisibility(View.GONE);
				}else if("tab3".equals(tabId)){
					if(appfragment.applists.size()==0)
						//当选中应用管理模块时，如果应用列表为空并且没有正在加载，就自动开始加载，设置状态为true，这样能够避免重复加载
						if(!appfragment.isloading)
						{
						appfragment.isloading = true;
						appfragment.loadData();
						}
					
				}else if("tab4".equals(tabId)){
					//效果与加载应用管理模块一样
					if(emarketfragment.applists.size()==0)
						if(!emarketfragment.isloading)
						{
						emarketfragment.isloading = true;
					    emarketfragment.loadData();
						}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	//下面重写的两个生命周期方法用于百度统计
	public void onResume() {
		Log.w("MainActivity", "Activity1.OnResume()");
		super.onResume();

		StatService.onResume(this);
	}

	public void onPause() {
		Log.w("MainActivity", "Activity1.onPause()");
		super.onPause();

		StatService.onPause(this);
	}
	
	@Override  
    public void onDestroy() {
        super.onDestroy();
        
	}
	
	@Override
    public void onBackPressed() {
		ExitApp();
    }
	
	private long exitTime = 0;
	//按两次返回键退出程序
    public void ExitApp()
    {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                    Toast.makeText(this, getResources().getString(R.string.exit_apllication), Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
            } else
            {
                    this.finish();
            }

    }

}
