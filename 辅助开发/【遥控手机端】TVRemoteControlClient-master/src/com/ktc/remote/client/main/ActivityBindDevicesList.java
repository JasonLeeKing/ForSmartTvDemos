package com.ktc.remote.client.main;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.adapter.SetDeviceListAdapter;
import com.ktc.remote.client.bean.DeviceInfo;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.utils.CustomUI;
import com.ktc.remote.client.utils.WAPI;
import com.ktc.remote.client.views.BindDevicePopupMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityBindDevicesList extends Activity implements OnClickListener {

	public static final int GET_BIND_DEVICE_SUCCESSED = 1001;
	public static final int GET_BIND_DEVICE_FAILED = 1002;
	
	public static final int UNBIND_DEVICE_SUCCESSED = 1003;
	public static final int UNBIND_DEVICE_FAILED = 1004;

	private ArrayList<DeviceInfo> devicelists;
	private SetDeviceListAdapter adapter = null;
	private ListView listView = null;
	private BindDevicePopupMenu menuWindow;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.binddeviceslist);

		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBack();
			}
		});

		devicelists = new ArrayList<DeviceInfo>();
		adapter = new SetDeviceListAdapter(this, devicelists);
		listView = (ListView) findViewById(R.id.listView_BindDevices);
		listView.setAdapter(adapter);
		
		try {
			CustomUI.showtips(this, R.string.market_tips_get_info);
		} catch (Exception e) {
		}
		
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = GetBindDeviceInfo();					
				handler.sendMessage(msg);
			}
		}.start();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				menuWindow = new BindDevicePopupMenu(ActivityBindDevicesList.this);

				menuWindow.showAtLocation(parent, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				final int i = position;
				// popwindow.setFocusable(true);
				menuWindow.get_pop_unbundling().setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dismissPopupWindow();								
								obUnbunding(i);
							}
						});

				menuWindow.get_pop_cancel().setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dismissPopupWindow();
							}

						});
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				dismissPopupWindow();
			}
		});
	}

	public void obUnbunding(final int itemIndex) {	
		try {
			CustomUI.showtips(this, R.string.market_tips_get_info);
		} catch (Exception e) {
		}
		
		new Thread() {
			@Override
			public void run() {
				doUnbindWork(itemIndex);
			}
		}.start();
	}
	public void doUnbindWork(int itemIndex) {
		if (itemIndex == -1)
			return;

		String urlString = WAPI.GetUnBindURL(GlobalParams.userid,devicelists.get(itemIndex).getDeviceid());
		String content = WAPI.get_content_from_remote_url(urlString);

		if (content == null)
			return;
		ArrayList<String> fieldList = new ArrayList<String>();
		int ret = WAPI.parseUnBindParam(content, fieldList);
		if (ret == 0) {
			devicelists.remove(itemIndex);
			Message msg = new Message();
			msg.what = UNBIND_DEVICE_SUCCESSED;
			handler.sendMessage(msg);
		} else {
			Message msg = new Message();
			msg.what = UNBIND_DEVICE_FAILED;
			handler.sendMessage(msg);
		}
		return;
	}

	private void dismissPopupWindow() {
		if (menuWindow != null && menuWindow.isShowing()) {
			menuWindow.dismiss();
			menuWindow = null;
		}
	}

	private int GetBindDeviceInfo() {
		String urlString = WAPI.addUseridParams(GlobalParams.userid);
		String content = WAPI.get_content_from_remote_url(urlString);

		if (content == null)
			return GET_BIND_DEVICE_FAILED;
		devicelists.clear();
		int ret = WAPI.parseDeviceList(content, devicelists);
		if (ret == 0) {
			return GET_BIND_DEVICE_SUCCESSED;
		}

		return GET_BIND_DEVICE_FAILED;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBack();
			return false;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
	}

	public void onBack() {
		Intent intent = new Intent(ActivityBindDevicesList.this, SetFragment.class);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.push_no_move, R.anim.push_right_out);
	}

	public void onGetInfoFinished(int msg) {
		if(CustomUI.tipdialog!=null)
			CustomUI.tipdialog.dismiss();
		if (msg == GET_BIND_DEVICE_SUCCESSED) {
			// Toast.makeText(this, "获取数据完成", Toast.LENGTH_LONG).show();
			adapter.notifyDataSetChanged();
		} else if (msg == GET_BIND_DEVICE_FAILED) {
			Toast.makeText(this, getResources().getString(R.string.http_data_error), Toast.LENGTH_LONG).show();
		} else if (msg == UNBIND_DEVICE_SUCCESSED) {
			Toast.makeText(this, getResources().getString(R.string.setting_tips_successed), Toast.LENGTH_LONG).show();
			adapter.notifyDataSetChanged();
		} else if (msg == UNBIND_DEVICE_FAILED) {
			Toast.makeText(this, getResources().getString(R.string.setting_tips_failed), Toast.LENGTH_LONG).show();
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onGetInfoFinished(msg.what);
		}
	};
	
	public void onResume() {
		Log.w("BindDevicesList", "Activity1.OnResume()");
		super.onResume();

		StatService.onResume(this);
	}

	public void onPause() {
		Log.w("BindDevicesList", "Activity1.onPause()");
		super.onPause();

		StatService.onPause(this);
	}

	public static int BindDevice(Activity activity, ArrayList<String> fieldList) {
		String urlString = WAPI.GetBindDeviceURL();
		String content = WAPI.get_content_from_remote_url(urlString);

		if (content == null)
			return -1;
		int ret = WAPI.parseBindDeviceInfo(content, fieldList);
		return ret;
	}
}