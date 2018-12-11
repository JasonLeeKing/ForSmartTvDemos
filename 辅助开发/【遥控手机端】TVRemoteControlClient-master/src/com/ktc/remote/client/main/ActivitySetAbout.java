package com.ktc.remote.client.main;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.example.mytabhost.R;
import com.ktc.remote.client.bean.GlobalParams;
import com.ktc.remote.client.utils.MyUtils;
import com.ktc.remote.client.utils.WAPI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySetAbout extends Activity implements OnClickListener{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setabout);
		
		Button btn_back = (Button)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onBack();
			}
		});
		
		String versionName = MyUtils.getVersionName(getApplicationContext());
		if (versionName!=null || !versionName.equals("")) {
			TextView tv_versionname = (TextView)findViewById(R.id.version_name);
			tv_versionname.setText(getResources().getString(R.string.set_soft_version) + ":" + versionName);
		}
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
		Intent intent = new Intent(ActivitySetAbout.this, SetFragment.class);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.push_no_move, R.anim.push_right_out);		
	}
	
	public void onResume() {
		Log.w("SetAbout", "Activity1.OnResume()");
		super.onResume();

		StatService.onResume(this);
	}

	public void onPause() {
		Log.w("SetAbout", "Activity1.onPause()");
		super.onPause();

		StatService.onPause(this);
	}
}