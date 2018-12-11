package com.ktc.remote.client.main;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.utils.CustomUI;
import com.ktc.remote.client.utils.MyUtils;
import com.ktc.remote.client.utils.WAPI;

import android.R.integer;
import android.R.string;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityUserRegister extends Activity implements OnClickListener{
	public static final int REGISTER_SUCCESSED = 1001;
	public static final int REGISTER_FAILED = 1002;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userregister);
		
		Button btn_back = (Button)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onBack();
			}
		});

		Button btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onRegister();
			}
		});
	}
	public void onRegister() {
		
		try {
			CustomUI.showtips(this, R.string.market_tips_get_info);			
		} catch (Exception e) {
			// TODO: handle exception
		}

		new Thread() {
			@Override
			public void run() {
				DoRegisterWork();
			}
		}.start();
	}

	public void DoRegisterWork() {
        EditText editusername =(EditText)findViewById(R.id.edit_user_name);  
        String user_name=editusername.getText().toString();  
        EditText edituserpwd =(EditText)findViewById(R.id.edit_user_pwd);  
        String user_pwd=edituserpwd.getText().toString();  
		//MyUtils.WriteUserInfoToLocalFile(getApplicationContext(), user_name, user_pwd);

		if (user_name.length()<4) {
			Message msg = new Message();
			msg.what = REGISTER_FAILED;
			msg.obj = getResources().getString(R.string.login_input_name);
			handler.sendMessage(msg);
			return;
		}
		if (user_pwd.length()<4) {
			Message msg = new Message();
			msg.what = REGISTER_FAILED;
			msg.obj = getResources().getString(R.string.login_input_pwd);
			handler.sendMessage(msg);
			return;
		}
		String urlString = WAPI.addRegisterParams(user_name, user_pwd);

		String content = WAPI.get_content_from_remote_url(urlString);
		if( content == null )
		{
			Message msg = new Message();
			msg.what = REGISTER_FAILED;
			msg.obj = getResources().getString(R.string.http_data_error);
			handler.sendMessage(msg);
			return;
		}

		ArrayList<String> fieldList = new ArrayList<String>();
		int iret = WAPI.parseUserLoginInfoResponse(getApplicationContext(), content, fieldList);
		
		if (iret == 0) {
			Message msg = new Message();
			msg.what = REGISTER_SUCCESSED;
			msg.obj = getResources().getString(R.string.setting_tips_successed);
			handler.sendMessage(msg);
		} else {
			Message msg = new Message();
			msg.what = REGISTER_FAILED;
			msg.obj = fieldList.get(0);
			handler.sendMessage(msg);
		}
		
		if (iret == 0 && fieldList.size() == 3) {
			GlobalParams.isLogin = true;
			GlobalParams.userid = fieldList.get(1);
			GlobalParams.username = fieldList.get(2);
			GlobalParams.userpwd = user_pwd;
			MyUtils.WriteUserInfoToLocalFile(getApplicationContext(),
					user_name, user_pwd);
		} else {
			GlobalParams.isLogin = false;
			GlobalParams.userid = "";
			GlobalParams.userid = "";
			GlobalParams.userpwd = "";
			MyUtils.WriteUserInfoToLocalFile(getApplicationContext(),"", "");
		}
	}
	public void onResume() {
		Log.w("UserRegister", "Activity1.OnResume()");
		super.onResume();

		StatService.onResume(this);
	}

	public void onPause() {
		Log.w("UserRegister", "Activity1.onPause()");
		super.onPause();

		StatService.onPause(this);
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
		Intent intent = new Intent(ActivityUserRegister.this, SetFragment.class);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.push_no_move, R.anim.push_down_out);		
	}

	public void RegisterFinised(String successedMsg,boolean bSuccessed) {
		if(CustomUI.tipdialog!=null)
			CustomUI.tipdialog.dismiss();
		
		Toast.makeText(getApplicationContext(), successedMsg, Toast.LENGTH_LONG).show();
		if (bSuccessed) {
			onBack();			
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == REGISTER_SUCCESSED) {
				RegisterFinised((String) msg.obj,true);
			} else if (msg.what == REGISTER_FAILED) {
				RegisterFinised((String) msg.obj,false);
			}
		}
	};
}