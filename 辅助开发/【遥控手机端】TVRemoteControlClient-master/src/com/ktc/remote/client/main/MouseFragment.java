package com.ktc.remote.client.main;


import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.bean.ServerData;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MouseFragment extends Fragment implements OnClickListener{

	private Button btn_return;
	private Button btn_confirm;
	private Button btn_menu;
	private Button btn_back;
	private Button iv_mousemove;
	private float _lastX;
	private float _lastY;
	private float _downX;
	private float _downY;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.layout_mouse, container, false);
		
		iv_mousemove = (Button) view.findViewById(R.id.mousemove);
		
		btn_return = (Button) view.findViewById(R.id.mouse_return);
		btn_confirm = (Button) view.findViewById(R.id.mouse_confirm);
		btn_menu = (Button) view.findViewById(R.id.mouse_menu);
		btn_back = (Button) view.findViewById(R.id.mouse_back);
		
		btn_back.setOnClickListener(this);
		btn_return.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
		btn_menu.setOnClickListener(this);
		iv_mousemove.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_ENTER);
			}
			
		});
		iv_mousemove.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch( event.getAction() )
				{
				case MotionEvent.ACTION_DOWN:
					_lastX = event.getX();
					_lastY = event.getY();
					_downX = _lastX;
					_downY = _lastY;
					break;
				case MotionEvent.ACTION_MOVE:
				{
					float x = event.getX();
					float y = event.getY();
					
					float offsetX = x - _lastX;
					float offsetY = y - _lastY;
					
					
					_lastX = x;
					_lastY = y;
					String s = String.format("%f,%f", offsetX, offsetY);
					
					Log.v("alex", s);
					ServerData.sendCommand(GlobalParams.IP, ServerData.CMD_CLIENT_MOUSEMOVE, s);
				}
					break;
				case MotionEvent.ACTION_UP:
				{
					float x = event.getX();
					float y = event.getY();
					
					if( Math.abs(x - _downX) <= 10 && Math.abs(y - _downY) <= 10 )
					{
						String s = String.format("%f,%f", x, y);
						
						Log.v("alex", "click:" + s);
						ServerData.sendMessage(GlobalParams.IP, ServerData.CMD_CLIENT_MOUSECLICK, s);
//						ServerData.sendCommand(ControlActivity.ip, ServerData.CMD_MOUSE_CLICK, s);
						
					}
				}
					break;
				}
				return true;
			}
        	
        });
		return view;
	}
	
	public void onResume() {
		super.onResume();
		Log.d("MouseFragment", "Control Fragment resume");
		StatService.onResume(this);
	}

	public void onPause() {
		super.onPause();
		Log.d("MouseFragment", "Control Fragment pause");
		StatService.onPause(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.mouse_back:
			//点击返回键时，将MainActivity中的tabhost选中项改为第一个。
			GlobalParams.TH.setCurrentTab(0);
			GlobalParams.TH.getTabWidget().setVisibility(View.VISIBLE);
			break;
		case R.id.mouse_confirm:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_ENTER);
			break;
		case R.id.mouse_menu:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_MENU);
			break;
		case R.id.mouse_return:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_BACK);
			break;
		default:
			break;
		}
	}
	
}
