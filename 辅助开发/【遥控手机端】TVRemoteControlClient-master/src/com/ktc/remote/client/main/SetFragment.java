package com.ktc.remote.client.main;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.adapter.SetListAdapter;
import com.ktc.remote.client.bean.ButtonInfo;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.utils.CustomUI;
import com.ktc.remote.client.utils.ImageCache;
import com.ktc.remote.client.utils.MyUtils;
import com.ktc.remote.client.views.ClientUpgrade;
import com.ktc.remote.client.views.ClientUpgrade.ClientUpgradeCallback;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SetFragment extends Fragment implements OnClickListener,
		ClientUpgradeCallback {

	public static final int BTN_USER_LOGIN = 200101;
	public static final int BTN_REGISTER = 200102;
	public static final int BTN_CLEAR_CACHE = 200103;
	public static final int BTN_VERSION = 200104;
	public static final int BTN_CHECK_UPDATA = 200105;
	public static final int BTN_ABOUT = 200106;
	public static final int BTN_BIND_DEVICE = 200107;
	public static final int BTN_ALRAADY_BINDED_DEVICDS = 200108;
	public static final int BTN_DOWNLIST_LIST = 200109;
	public static final int BTN_CHANGE_PWD = 200110;
	public static final int BTN_LOGOUT = 200111;
	public static final int REQUSET_LOGIN = 1001;
	public static final int REQUSET_REGISTER = 1002;
	public static final int LOGIN_SUCCESSED = 1003;
	public static final int DEVICE_BIND = 1004;

	private ArrayList<ButtonInfo> buttonlists;
	public SetListAdapter adapter = null;
	private ListView listView = null;
	private Intent intent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_set, container, false);

		new Thread() {
			@Override
			public void run() {
				if(ActivityUserLogin.onLogin(getActivity()))
				{
					Message msg = new Message();
					msg.what = LOGIN_SUCCESSED;
					handler.sendMessage(msg);
				}
			}
		}.start();

		onCheckUpdata(false);

		buttonlists = new ArrayList<ButtonInfo>();
		setData();
		adapter = new SetListAdapter(getActivity(),buttonlists);
		listView = (ListView) view.findViewById(R.id.listView_set);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int nID = buttonlists.get(position).getBtnId();
				if (nID == BTN_USER_LOGIN) {
					onLogin();
				} else if (nID == BTN_REGISTER) {
					onRegister();
				} else if (nID == BTN_CLEAR_CACHE) {
					onClearCache();
				} else if (nID == BTN_VERSION) {
					onVersion();
				} else if (nID == BTN_CHECK_UPDATA) {
					onCheckUpdata(true);
				} else if (nID == BTN_ABOUT) {
					OnAbout();
				} else if (nID == BTN_BIND_DEVICE) {
					onBindDevice();
				} else if (nID == BTN_ALRAADY_BINDED_DEVICDS) {
					GetAlreadyBindedDevices();
				} else if (nID == BTN_DOWNLIST_LIST) {
					GetDownloadList();
				} else if (nID == BTN_CHANGE_PWD) {
					onChangePwd();
				} else if (nID == BTN_LOGOUT) {
					onLogout();
				}
			}
		});

		return view;
	}
	
	public void onResume() {
		super.onResume();
		Log.d("SetFragment", "Control Fragment resume");
		StatService.onResume(this);
	}

	public void onPause() {
		super.onPause();
		Log.d("SetFragment", "Control Fragment pause");
		StatService.onPause(this);
	}

	public void setData() {
		buttonlists.clear();

		ButtonInfo btninfo = new ButtonInfo();
		btninfo.setBtnName(getResources().getString(R.string.setting_section_user));
		btninfo.setBtnType(ButtonInfo.BTN_TYPE_TITLE);
		btninfo.setBtnId(-1);
		buttonlists.add(btninfo);

		if (GlobalParams.isLogin) {
			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_section_user) + ": "  + GlobalParams.username);			
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_NORMAL);
			btninfo.setBtnId(-1);
			buttonlists.add(btninfo);

			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_menu_bind_device));
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
			btninfo.setBtnId(BTN_BIND_DEVICE);
			buttonlists.add(btninfo);

			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_menu_my_bind_list));
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
			btninfo.setBtnId(BTN_ALRAADY_BINDED_DEVICDS);
			buttonlists.add(btninfo);

			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_menu_my_task_list));
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
			btninfo.setBtnId(BTN_DOWNLIST_LIST);
			buttonlists.add(btninfo);

//			btninfo = new ButtonInfo();
//			btninfo.setBtnName("修改密码");
//			btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
//			btninfo.setBtnId(BTN_CHANGE_PWD);
//			buttonlists.add(btninfo);

			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_menu_logout));
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
			btninfo.setBtnId(BTN_LOGOUT);
			buttonlists.add(btninfo);
		} else {

			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_menu_user_not_login));
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_NORMAL);
			btninfo.setBtnId(BTN_USER_LOGIN);
			buttonlists.add(btninfo);

			btninfo = new ButtonInfo();
			btninfo = new ButtonInfo();
			btninfo.setBtnName(getResources().getString(R.string.setting_menu_register));
			btninfo.setBtnType(ButtonInfo.BTN_TYPE_NORMAL);
			btninfo.setBtnId(BTN_REGISTER);
			buttonlists.add(btninfo);
		}

		btninfo = new ButtonInfo();
		btninfo.setBtnName(getResources().getString(R.string.setting_section_general));
		btninfo.setBtnType(ButtonInfo.BTN_TYPE_TITLE);
		btninfo.setBtnId(-1);
		buttonlists.add(btninfo);

		btninfo = new ButtonInfo();
		btninfo.setBtnName(getResources().getString(R.string.setting_menu_clear_cache));
		btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
		btninfo.setBtnId(BTN_CLEAR_CACHE);
		buttonlists.add(btninfo);

		String versionName = MyUtils.getVersionName(getActivity());
		if(versionName == null || versionName=="")
		{
			versionName = "1.0";
		}
		btninfo = new ButtonInfo();
		btninfo.setBtnName(getResources().getString(R.string.setting_menu_version)+": "+versionName);
		btninfo.setBtnType(ButtonInfo.BTN_TYPE_NORMAL);
		btninfo.setBtnId(BTN_VERSION);
		buttonlists.add(btninfo);

		btninfo = new ButtonInfo();
		btninfo.setBtnName(getResources().getString(R.string.setting_menu_check_version));
		btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
		btninfo.setBtnId(BTN_CHECK_UPDATA);
		buttonlists.add(btninfo);

		btninfo = new ButtonInfo();
		btninfo.setBtnName(getResources().getString(R.string.setting_menu_about));
		btninfo.setBtnType(ButtonInfo.BTN_TYPE_AROOW);
		btninfo.setBtnId(BTN_ABOUT);
		buttonlists.add(btninfo);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);// super方法必须被调用
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);// super方法必须被调用
	}


	public void onLogin() {

		if (MyUtils.isNetworkConnect(this.getActivity())) {
			intent = new Intent(this.getActivity(), ActivityUserLogin.class);
			startActivityForResult(intent, REQUSET_LOGIN);
			this.getActivity().overridePendingTransition(R.anim.push_up_in,
					R.anim.push_no_move);
		} else {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_please_connect_network), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onRegister() {
		if (MyUtils.isNetworkConnect(this.getActivity())) {
			intent = new Intent(this.getActivity(), ActivityUserRegister.class);
			startActivityForResult(intent, REQUSET_REGISTER);
			this.getActivity().overridePendingTransition(R.anim.push_up_in,
					R.anim.push_no_move);
		} else {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_please_connect_network), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onClearCache() {
		Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_tips_clear_cache_ok), Toast.LENGTH_LONG)
		.show();
		
	}

	public void onVersion() {
	}
			
	public void onCheckUpdata(boolean bTips) {
		if (MyUtils.isNetworkConnect(this.getActivity())) {
			ClientUpgrade cu = new ClientUpgrade(this.getActivity(),bTips);
			cu.startCheckVersion(this);
		} else {
			if (bTips) {
				Toast.makeText(
						this.getActivity(),
						getResources().getString(
								R.string.setting_please_connect_network),
						Toast.LENGTH_LONG).show();				
			}
		}
	}

	public void OnAbout() {
		intent = new Intent(this.getActivity(), ActivitySetAbout.class);
		startActivityForResult(intent, REQUSET_REGISTER);
		this.getActivity().overridePendingTransition(R.anim.push_left_in,
				R.anim.push_no_move);
	}

	public void onBindDevice() {
		if (MyUtils.isNetworkConnect(this.getActivity())) {
			if (GlobalParams.DEVICEID==null || GlobalParams.DEVICEID=="") {
				Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_tips_not_connect), Toast.LENGTH_LONG)
						.show();
			}
			else {
				try {
					CustomUI.showtips(this.getActivity(), R.string.market_tips_get_info);
				} catch (Exception e) {
				}
				new Thread() {
					@Override
					public void run() {
						ArrayList<String> fieldList = new ArrayList<String>();
						int ret = ActivityBindDevicesList.BindDevice(getActivity(),fieldList);
						Message msg = new Message();
						msg.what = DEVICE_BIND;	
						msg.obj = ret;
						handler.sendMessage(msg);
					}
				}.start();
			}
		} else {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_please_connect_network), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void GetAlreadyBindedDevices() {
		if (MyUtils.isNetworkConnect(this.getActivity())) {
			intent = new Intent(this.getActivity(), ActivityBindDevicesList.class);
			startActivity(intent);
			this.getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_no_move);
		} else {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_please_connect_network), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void GetDownloadList() {
		if (MyUtils.isNetworkConnect(this.getActivity())) {
			intent = new Intent(this.getActivity(), ActivitySetDownloadList.class);
			startActivity(intent);
			this.getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_no_move);			
		} else {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_please_connect_network), Toast.LENGTH_LONG)
					.show();
		}

	}

	public void onChangePwd() {
		if (MyUtils.isNetworkConnect(this.getActivity())) {
			intent = new Intent(this.getActivity(), ActivityChangePwd.class);
			startActivity(intent);
			this.getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_no_move);

		} else {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_please_connect_network), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void LoginSuccessed() {
		//Toast.makeText(this.getActivity(), getResources().getString(R.string.login_login_success), Toast.LENGTH_LONG).show();
		this.setData();
		adapter.notifyDataSetChanged();
	}

	public void BindSuccessed(int nRet) {
		if (nRet == 0) {
			Toast.makeText(this.getActivity(),
					getResources().getString(R.string.setting_tips_successed),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this.getActivity(),
					getResources().getString(R.string.setting_tips_failed),
					Toast.LENGTH_LONG).show();
		}
	}
	public void onLogout() {
		GlobalParams.isLogin = false;
		GlobalParams.userid = "";
		GlobalParams.userid = "";
		GlobalParams.userpwd = "";
		MyUtils.WriteUserInfoToLocalFile(this.getActivity(), "", "");
		this.setData();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckFinished(int state) {
		if (state == ClientUpgrade.STATE_ALREADY_NEW_VERSION || state == ClientUpgrade.STATE_CHECK_ERROR) {
			Toast.makeText(this.getActivity(), getResources().getString(R.string.setting_tips_already_new_version), Toast.LENGTH_LONG)
					.show(); 
		} else if (state == ClientUpgrade.STATE_UPGRADE) {
			// Toast.makeText(this.getActivity(), "升级完成", Toast.LENGTH_LONG)
			// .show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == REQUSET_LOGIN || requestCode == REQUSET_REGISTER)
				&& resultCode == Activity.RESULT_OK) {
			this.setData();
			adapter.notifyDataSetChanged();
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if(CustomUI.tipdialog!=null)
				CustomUI.tipdialog.dismiss();
			if (msg.what == LOGIN_SUCCESSED) {
				LoginSuccessed();
			} else if (msg.what == DEVICE_BIND) {
				BindSuccessed((Integer) msg.obj);				
			}
		}
	};
}