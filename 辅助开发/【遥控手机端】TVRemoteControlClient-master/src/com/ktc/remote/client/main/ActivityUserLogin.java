package com.ktc.remote.client.main;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.utils.CustomUI;
import com.ktc.remote.client.utils.MyUtils;
import com.ktc.remote.client.utils.WAPI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityUserLogin extends Activity implements OnClickListener {

	public static final int LOGIN_SUCCESSED = 1001;
	public static final int LOGIN_FAILED = 1002;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlogin);

		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBack();
			}
		});

		Button btn_lgoin = (Button) findViewById(R.id.btn_login);
		btn_lgoin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBtnLogin();
			}
		});
	}
	
	public void onBtnLogin() {
		try {
			CustomUI.showtips(this, R.string.market_tips_get_info);
		} catch (Exception e) {
		}
		
		EditText editusername = (EditText) findViewById(R.id.edit_user_name);
		final String user_name = editusername.getText().toString();
		EditText edituserpwd = (EditText) findViewById(R.id.edit_user_pwd);
		final String user_pwd = edituserpwd.getText().toString();
		
//		if (user_name.length()<4) {
//			Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_input_name), Toast.LENGTH_LONG).show();
//			return;
//		}
//		if (user_pwd.length()<4) {
//			Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_input_pwd), Toast.LENGTH_LONG).show();		
//			return;
//		}
		
		new Thread() {
			@Override
			public void run() {
				doLoginWork(getApplicationContext(), user_name, user_pwd);
			}
		}.start();
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
	
	public void onResume() {
		Log.w("UserLogin", "Activity1.OnResume()");
		super.onResume();

		StatService.onResume(this);
	}

	public void onPause() {
		Log.w("UserLogin", "Activity1.onPause()");
		super.onPause();

		StatService.onPause(this);
	}

	public void onBack() {
		Intent intent = new Intent(ActivityUserLogin.this, SetFragment.class);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.push_no_move, R.anim.push_down_out);
	}

	public static boolean onLogin(Context context) {
		ArrayList<String> retList = new ArrayList<String>();
		String user_name = MyUtils.GetUserNameFromLocalFile(context);
		String user_pwd = MyUtils.GetUserPwdFromLocalFile(context);
		return onLogin(context,user_name,user_pwd,retList);
	}
	
	public static boolean onLogin(Context context, String user_name,
			String user_pwd, ArrayList<String> retList) {
		if (user_name == null || user_pwd == null || user_name == ""
				|| user_pwd == "") {
			return false;
		}

		String urlString = WAPI.addLoginParams(user_name,user_pwd);
		String content = WAPI.get_content_from_remote_url(urlString);
		if (content == null)
		{
			retList.add(context.getResources().getString(R.string.login_login_failed));
			return false;
		}

		ArrayList<String> fieldList = new ArrayList<String>();
		int iret = WAPI.parseUserLoginInfoResponse(context, content, fieldList);
		retList.add(fieldList.get(0));

		if (iret == 0 && fieldList.size() == 3) {
			GlobalParams.isLogin = true;
			GlobalParams.userid = fieldList.get(1);
			GlobalParams.username = fieldList.get(2);
			GlobalParams.userpwd = user_pwd;
			MyUtils.WriteUserInfoToLocalFile(context, user_name, user_pwd);
			return true;
		} else {
			GlobalParams.isLogin = false;
			GlobalParams.userid = "";
			GlobalParams.userid = "";
			GlobalParams.userpwd = "";
			MyUtils.WriteUserInfoToLocalFile(context, "", "");
			return false;
		}
	}
	public void doLoginWork(Context context, String user_name,
			String user_pwd) {
		ArrayList<String> retList = new ArrayList<String>();
		Message msg = new Message();
		if (!onLogin(context, user_name, user_pwd, retList)) {
			msg.what = LOGIN_FAILED;
		}
		else {
			msg.what = LOGIN_SUCCESSED;
		}
		msg.obj = retList.get(0);
		handler.sendMessage(msg);
	}
	
	public void LoginFailed(String failedMsg) {
		Toast.makeText(getApplicationContext(), failedMsg, Toast.LENGTH_LONG).show();
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if(CustomUI.tipdialog!=null)
				CustomUI.tipdialog.dismiss();

			if (msg.what == LOGIN_SUCCESSED) {
				onBack();
			} else if (msg.what == LOGIN_FAILED) {
				LoginFailed((String) msg.obj);
			}
		}
	};
}