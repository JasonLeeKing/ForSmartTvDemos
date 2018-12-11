package com.ktc.remote.client.main;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GetDeviceList;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.bean.ServerData;

public class ControllerFragment extends Fragment implements OnClickListener,
		OnTouchListener {
	private Button controll_search;
	private View view;
	private String ip;
	private ImageView btnMenu;
	private ImageView btnPower;
	private ImageView btnBack;
	private ImageView btnExit;
	private ImageView btnHome;
	private ImageView btnMute;
	private ImageView keyRight;
	private ImageView keyLeft;
	private ImageView keyUp;
	private ImageView keyDown;
	private ImageView keyOk;
	private Bitmap bitmap_right, bitmap_up, bitmap_down, bitmap_left,
			bitmap_ok;
	private Button btnChannelUp;
	private Button btnChannelDown;
	private Button btnVolumeAdd;
	private Button btnVolumeDecrease;
	private boolean rightTransparent = false;
	private boolean upTransparent = false;
	private boolean downTransparent = false;
	private boolean leftTransparent = false;
	private boolean okTransparent = false;
	private TextView devicename;
	private GetDeviceList device;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.layout_controller, container, false);
		init();
		setListener();
		// 当该fragment创建过程中，搜索局域网内的设备，这样在打开应用后立刻将设备列表展现出来。
		device = new GetDeviceList(getActivity(), devicename);
		device.show();
		return view;
	}

	private void init() {
		controll_search = (Button) view.findViewById(R.id.controll_search);
		btnMenu = (ImageView) view.findViewById(R.id.menu);
		btnPower = (ImageView) view.findViewById(R.id.power);
		btnBack = (ImageView) view.findViewById(R.id.back);
		btnExit = (ImageView) view.findViewById(R.id.exit);
		btnHome = (ImageView) view.findViewById(R.id.home);
		btnMute = (ImageView) view.findViewById(R.id.mute);
		keyRight = (ImageView) view.findViewById(R.id.key_right);
		keyLeft = (ImageView) view.findViewById(R.id.key_left);
		keyUp = (ImageView) view.findViewById(R.id.key_up);
		keyDown = (ImageView) view.findViewById(R.id.key_down);
		keyOk = (ImageView) view.findViewById(R.id.key_ok);
		btnChannelUp = (Button) view.findViewById(R.id.channel_up);
		btnChannelDown = (Button) view.findViewById(R.id.channel_down);
		btnVolumeAdd = (Button) view.findViewById(R.id.volume_add);
		btnVolumeDecrease = (Button) view.findViewById(R.id.volume_decrease);
		bitmap_right = ((BitmapDrawable) (keyRight.getDrawable())).getBitmap();
		bitmap_up = ((BitmapDrawable) (keyUp.getDrawable())).getBitmap();
		bitmap_down = ((BitmapDrawable) (keyDown.getDrawable())).getBitmap();
		bitmap_left = ((BitmapDrawable) (keyLeft.getDrawable())).getBitmap();
		bitmap_ok = ((BitmapDrawable) (keyOk.getDrawable())).getBitmap();
		devicename = (TextView) view.findViewById(R.id.devicename);
	}

	private void setListener() {
		controll_search.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		btnPower.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		btnMute.setOnClickListener(this);
		btnChannelUp.setOnClickListener(this);
		btnChannelDown.setOnClickListener(this);
		btnVolumeAdd.setOnClickListener(this);
		btnVolumeDecrease.setOnClickListener(this);
		keyRight.setOnClickListener(this);
		keyLeft.setOnClickListener(this);
		keyUp.setOnClickListener(this);
		keyDown.setOnClickListener(this);
		keyOk.setOnClickListener(this);
		keyRight.setOnTouchListener(this);
		keyLeft.setOnTouchListener(this);
		keyUp.setOnTouchListener(this);
		keyDown.setOnTouchListener(this);
		keyOk.setOnTouchListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);// super方法必须被调用
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);// super方法必须被调用

	}

	public void onResume() {
		super.onResume();
		Log.d("ControlFragment", "Control Fragment resume");
		StatService.onResume(this);
	}

	public void onPause() {
		super.onPause();
		Log.d("ControlFragment", "Control Fragment pause");
		StatService.onPause(this);
	}

	// 该方法实现穿透点击效果，但有缺陷，需进一步优化。
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.controll_search:
			device.show();
			return;
		case R.id.menu:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_MENU);
			break;
		case R.id.power:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_POWER);
			break;
		case R.id.back:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_BACK);
			break;
		case R.id.exit:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_TV_INPUT);
			break;
		case R.id.home:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_HOME);
			break;
		case R.id.mute:
			ServerData.sendKeyCode(GlobalParams.IP,
					KeyEvent.KEYCODE_VOLUME_MUTE);
			break;
		case R.id.volume_add:
			ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_VOLUME_UP);
			break;
		case R.id.volume_decrease:
			ServerData.sendKeyCode(GlobalParams.IP,
					KeyEvent.KEYCODE_VOLUME_DOWN);
			break;
		case R.id.channel_up:
			ServerData
					.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_CHANNEL_UP);
			break;
		case R.id.channel_down:
			ServerData.sendKeyCode(GlobalParams.IP,
					KeyEvent.KEYCODE_CHANNEL_DOWN);
			break;
		case R.id.key_down:

			if (!downTransparent) {
				Log.i("test", "下边实体区域被点击");
				ServerData.sendKeyCode(GlobalParams.IP,
						KeyEvent.KEYCODE_DPAD_DOWN);
			}
			break;
		case R.id.key_left:
			if (!leftTransparent) {
				Log.i("test", "左边实体区域被点击");
				ServerData.sendKeyCode(GlobalParams.IP,
						KeyEvent.KEYCODE_DPAD_LEFT);
			}
			break;
		case R.id.key_right:
			if (!rightTransparent) {
				Log.i("test", "右边实体区域被点击");
				ServerData.sendKeyCode(GlobalParams.IP,
						KeyEvent.KEYCODE_DPAD_RIGHT);
			}

			break;
		case R.id.key_up:
			if (!upTransparent) {
				Log.i("test", "上边实体区域被点击");
				ServerData.sendKeyCode(GlobalParams.IP,
						KeyEvent.KEYCODE_DPAD_UP);
			}
			break;
		case R.id.key_ok:
			if (!okTransparent) {
				Log.i("test", "ok键实体区域被点击");
				ServerData.sendKeyCode(GlobalParams.IP, KeyEvent.KEYCODE_ENTER);
			}
			break;
		default:
			return;
		}

		// 震动
		Vibrator vibrator = (Vibrator) this.getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		GlobalParams.isget = false;
		System.exit(0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.key_down:
			if (event.getX() < bitmap_down.getWidth()
					&& event.getY() < bitmap_down.getHeight()
					&& event.getY() >= 0 && event.getX() >= 0)
				if (bitmap_down.getPixel((int) (event.getX()),
						((int) event.getY())) == 0) {
					downTransparent = true; // 透明区域设置true
					// keyDown.setImageResource(R.drawable.control_down_off);
					keyUp.dispatchTouchEvent(event);
				} else {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						keyDown.setImageResource(R.drawable.control_down_on);
					if (event.getAction() == MotionEvent.ACTION_UP)
						keyDown.setImageResource(R.drawable.control_down_off);

					downTransparent = false;
				}
			break;
		case R.id.key_up:
			if (event.getX() < bitmap_up.getWidth()
					&& event.getY() < bitmap_up.getHeight()
					&& event.getY() >= 0 && event.getX() >= 0)
				if (bitmap_up.getPixel((int) (event.getX()),
						((int) event.getY())) == 0) {
					upTransparent = true; // 透明区域设置true
					// keyUp.setImageResource(R.drawable.control_up_off);
					keyRight.dispatchTouchEvent(event);
				} else {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						Log.i("testdrage", "down --");
						keyUp.setImageResource(R.drawable.control_up_on);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						Log.i("testdrage", "up and move --");
						keyUp.setImageResource(R.drawable.control_up_off);
					}
					upTransparent = false;
				}
			break;
		case R.id.key_right:
			if (event.getX() < bitmap_right.getWidth()
					&& event.getY() < bitmap_right.getHeight()
					&& event.getY() >= 0 && event.getX() >= 0)
				if (bitmap_right.getPixel((int) (event.getX()),
						((int) event.getY())) == 0) {
					rightTransparent = true; // 透明区域设置true
					// keyRight.setImageResource(R.drawable.control_right_off);
				} else {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						keyRight.setImageResource(R.drawable.control_right_on);
					if (event.getAction() == MotionEvent.ACTION_UP)
						keyRight.setImageResource(R.drawable.control_right_off);
					rightTransparent = false;
				}
			break;
		case R.id.key_left:
			if (event.getX() < bitmap_left.getWidth()
					&& event.getY() < bitmap_left.getHeight()
					&& event.getY() >= 0 && event.getX() >= 0)
				if (bitmap_left.getPixel((int) (event.getX()),
						((int) event.getY())) == 0) {
					leftTransparent = true; // 透明区域设置true
					// keyLeft.setImageResource(R.drawable.control_left_off);
					keyDown.dispatchTouchEvent(event);
				} else {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						keyLeft.setImageResource(R.drawable.control_left_on);
					if (event.getAction() == MotionEvent.ACTION_UP)
						keyLeft.setImageResource(R.drawable.control_left_off);
					leftTransparent = false;
				}
			break;
		case R.id.key_ok:
			if (event.getAction() == MotionEvent.ACTION_DOWN)
				keyOk.setImageResource(R.drawable.control_key_ok);
			okTransparent = false;
			break;
		}

		return false;
	}

}
