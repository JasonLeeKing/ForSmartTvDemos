package com.ktc.remote.client.main;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.utils.CustomUI;
import com.ktc.remote.client.utils.MyUtils;
import com.ktc.remote.client.utils.WAPI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityChangePwd extends Activity implements OnClickListener {
	public static final int CHANGEPWD_SUCCESSED = 1001;
	public static final int CHANGEPWD_FAILED = 1002;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepwd);

		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBack();
			}
		});

		Button btn_ChangePWd = (Button) findViewById(R.id.btn_change);
		btn_ChangePWd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onChangePwd();
			}
		});
	}
	public void onChangePwd() {
		try {
			CustomUI.showtips(this, R.string.market_tips_get_info);
		} catch (Exception e) {
		}
		new Thread() {
			@Override
			public void run() {
				doChangePwdWork();
			}
		}.start();
	}
	public void doChangePwdWork() {
		EditText editold_pwd = (EditText) findViewById(R.id.edit_old_pwd);
		String old_pwd = editold_pwd.getText().toString();
		EditText editnew_pwd = (EditText) findViewById(R.id.edit_new_pwd);
		String new_pwd = editnew_pwd.getText().toString();

		String urlString = WAPI.addChangePwdParams(GlobalParams.userid, old_pwd, new_pwd);

		String content = WAPI.get_content_from_remote_url(urlString);
		if( content == null )
		{
			Message msg = new Message();
			msg.what = CHANGEPWD_FAILED;
			msg.obj = getResources().getString(R.string.http_data_error);
			handler.sendMessage(msg);
			return;
		}

		ArrayList<String> fieldList = new ArrayList<String>();
		int iret = WAPI.parseChangePwdResponse(getApplicationContext(), content, fieldList);

		if (fieldList.size() != 1) {
			Message msg = new Message();
			msg.what = CHANGEPWD_FAILED;
			msg.obj = getResources().getString(R.string.http_data_error);
			handler.sendMessage(msg);
			return;
		}
		
		if (iret == 0) {
			Message msg = new Message();
			msg.what = CHANGEPWD_SUCCESSED;
			msg.obj = getResources().getString(R.string.setting_tips_successed);
			handler.sendMessage(msg);
		} else {
			Message msg = new Message();
			msg.what = CHANGEPWD_FAILED;
			msg.obj = fieldList.get(0);
			handler.sendMessage(msg);
		}
		
		if (iret == 0) {
			GlobalParams.userpwd = new_pwd;
			MyUtils.WriteUserInfoToLocalFile(getApplicationContext(),GlobalParams.username, new_pwd);
			onBack();
		}						
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBack();
			return false;
		}
		return false;
	}

	public void onResume() {
		Log.w("ChangePwd", "Activity1.OnResume()");
		super.onResume();

		StatService.onResume(this);
	}

	public void onPause() {
		Log.w("ChangePwd", "Activity1.onPause()");
		super.onPause();

		StatService.onPause(this);
	}
	
	@Override
	public void onClick(View v) {
	}

	public void onBack() {
		Intent intent = new Intent(ActivityChangePwd.this, SetFragment.class);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.push_no_move, R.anim.push_right_out);
	}
	public void ChangePwdFinised(String successedMsg,boolean bSuccessed) {
		Toast.makeText(getApplicationContext(), successedMsg, Toast.LENGTH_LONG).show();
		if (bSuccessed) {
			onBack();			
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if(CustomUI.tipdialog!=null)
				CustomUI.tipdialog.dismiss();
			if (msg.what == CHANGEPWD_SUCCESSED) {
				ChangePwdFinised((String) msg.obj,true);
			} else if (msg.what == CHANGEPWD_FAILED) {
				ChangePwdFinised((String) msg.obj,false);
			}
		}
	};
}