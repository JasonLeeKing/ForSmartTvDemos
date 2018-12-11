package com.ktc.tvremote.client.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.bean.GetDeviceList;
import com.ktc.tvremote.client.bean.GlobalParams;
import com.ktc.tvremote.client.bean.ServerData;

/**
 * @TODO 遥控器按键控制
 * @author Arvin
 * @date 2018.6.20
 */
public class FragmentController extends BasePageFragment implements OnClickListener{
	
	private GetDeviceList device;
	private TextView devicename;
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
		return R.layout.layout_remote_home;
	}
	
	@Override
	protected void initView (View view) {
		devicename = (TextView) view.findViewById(R.id.txt_dev_name);
		
		device = new GetDeviceList(getActivity(), devicename);
		device.show();
		device.show();
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
		case R.id.key_shutdown:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_POWER);
			break;
		case R.id.key_mute:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_VOLUME_MUTE);
			break;
		case R.id.key_input:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_TV_INPUT);
			break;
		case R.id.key_dpad_left:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_DPAD_LEFT);
			break;
		case R.id.key_dpad_up:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_DPAD_UP);
			break;
		case R.id.key_dpad_center:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_ENTER);
			break;
		case R.id.key_dpad_right:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_DPAD_RIGHT);
			break;
		case R.id.key_dpad_down:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_DPAD_DOWN);
			break;
		case R.id.key_vol_up:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_VOLUME_UP);
			break;
		case R.id.key_vol_down:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_VOLUME_DOWN);
			break;
		case R.id.key_home:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_HOME);
			break;
		case R.id.key_speach:
			
			break;
		case R.id.key_ch_up:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_CHANNEL_UP);
			break;
		case R.id.key_ch_down:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_CHANNEL_DOWN);
			break;
		case R.id.key_back:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_BACK);
			break;
		case R.id.key_menu:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_MENU);
			break;

		default:
			break;
		}
		
		Vibrator vibrator = (Vibrator) this.getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);
	}
	
}
