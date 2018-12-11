package com.ktc.tvremote.client.fragments;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.baidu.mobstat.StatService;
import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.bean.GlobalParams;
import com.ktc.tvremote.client.bean.ServerData;

/**
 * @TODO 触摸鼠标控制
 * @author Arvin
 * @date 2018.6.20
 */
public class FragmentMouse extends BasePageFragment implements OnClickListener {
	
	private Button mMouseView;
	private float mLastX;
	private float mLastY;
	private float mDownX;
	private float mDownY;
	
	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onResume () {
		super.onResume();
		if (getUserVisibleHint()) {
			//加载数据
			StatService.onResume(this);
		}
	}
	
	@Override
	public void onPause () {
		StatService.onPause(this);
		super.onPause();
	}
	
	@Override
	public void onDestroyView () {
		super.onDestroyView();
	}
	
	@Override
	protected int loadFragmentLayoutRes () {
		return R.layout.layout_remote_mouse;
	}
	
	@Override
	protected void initView (View view) {
		//初始化view等
		mMouseView = (Button) view.findViewById(R.id.mousemove);
		mMouseView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch( event.getAction()){
				case MotionEvent.ACTION_DOWN:
					mLastX = event.getX();
					mLastY = event.getY();
					mDownX = mLastX;
					mDownY = mLastY;
					break;
				case MotionEvent.ACTION_MOVE:{
						float x = event.getX();
						float y = event.getY();
						
						float offsetX = x - mLastX;
						float offsetY = y - mLastY;
						
						
						mLastX = x;
						mLastY = y;
						String s = String.format("%f,%f", offsetX, offsetY);
						
						Log.v("alex", s);
						ServerData.sendCommand(GlobalParams.IP, ServerData.CMD_CLIENT_MOUSEMOVE, s);
					}
					break;
				case MotionEvent.ACTION_UP:{
						float x = event.getX();
						float y = event.getY();
						
						if( Math.abs(x - mDownX) <= 10 && Math.abs(y - mDownY) <= 10 ){
							String s = String.format("%f,%f", x, y);
							Log.v("alex", "click:" + s);
							ServerData.sendMessage(GlobalParams.IP, ServerData.CMD_CLIENT_MOUSECLICK, s);
							//ServerData.sendCommand(ControlActivity.ip, ServerData.CMD_MOUSE_CLICK, s);
							
						}
					}
					break;
				}
				return true;
			}
		});
	}
	
	@Override
	public void onPageSelectedChange (int tendency) {
		super.onPageSelectedChange(tendency);
	}
	
	@Override
	public void onPageStateIdle () {
		super.onPageStateIdle();
	}
	
	@Override
	public void onDetach () {
		super.onDetach();
	}
	
	@Override
	public void setUserVisibleHint (boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mouse_key_home:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_HOME);
			break;
		case R.id.mouse_key_menu:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_MENU);
			break;
		case R.id.mouse_key_back:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_BACK);
			break;

		default:
			break;
		}
	}

}
