package com.ktc.remote.server.main;

import java.util.List;

import com.ktc.remote.server.bean.MainData;
import com.ktc.remote.server.utils.MyUtils;
import com.ktc.tvremote.client.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private TextView tv_textView;
	private TextView tv_deviceName;
	private TextView tv_version;
	private ImageView img_status;
	private Button btn_button;
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);

		mIntent = new Intent(this, TvRemoteServerService.class);

		img_status = (ImageView) this.findViewById(R.id.imageViewStatus);

		tv_textView = (TextView) this.findViewById(R.id.textView1);
		tv_deviceName = (TextView) this.findViewById(R.id.textViewDeviceName);
		tv_version = (TextView) this.findViewById(R.id.textViewVersion);

		String s = String.format(
				this.getResources().getString(R.string.version_info),
				MyUtils.getVersionName(this));
		tv_version.setText(s);

		String deviceName = MainData.get_profile_string_value(this,
				MainData.PROFILE_SERVER_NAME, MainData.DEFAULT_SERVER_NAME);
		s = String.format(this.getResources().getString(R.string.device_name),
				deviceName);
		tv_deviceName.setText(s);

		btn_button = (Button) this.findViewById(R.id.button1);
		btn_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int flag = ((Integer) v.getTag()).intValue();

				if (flag == 1) {// ֹͣ
					stopService(mIntent);
				} else {// ����
					startService(mIntent);
				}
				updateServiceStatus();
			}

		});

		updateServiceStatus();

	}

	public void updateServiceStatus() {
		boolean b = isServiceRunning(this,"com.soniq.tvremotecontrolserver.MainService");

		if (b) {
			img_status.setImageResource(R.drawable.service_running);
			tv_textView.setText(this.getResources().getString(R.string.status_running));// "������...");
			// _button.setText(this.getResources().getString(R.string.btn_stop_service));
			btn_button.setTag(1);
			btn_button.setBackgroundResource(R.drawable.btn_service_stop_selector);
		} else {
			img_status.setImageResource(R.drawable.service_stopped);
			tv_textView.setText(this.getResources().getString(R.string.status_stopped));
			// _button.setText(this.getResources().getString(R.string.btn_start_service));
			btn_button.setTag(0);
			btn_button.setBackgroundResource(R.drawable.btn_service_start_selector);
		}
	}

	public boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
