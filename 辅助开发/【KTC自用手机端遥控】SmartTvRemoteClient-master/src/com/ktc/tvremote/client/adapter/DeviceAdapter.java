package com.ktc.tvremote.client.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.bean.DeviceInfo;
import com.ktc.tvremote.client.bean.GlobalParams;
import com.ktc.tvremote.client.bean.ServerData;
import com.ktc.tvremote.client.utils.MyUtils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceAdapter extends BaseAdapter {

	private Context context;
	private List<DeviceInfo> lists;
	private Dialog renameDialog;
	private NameListAdapter nameAdapter;
	private List<String> listname;
	private String ip;

	public DeviceAdapter(Context cxt, List<DeviceInfo> lists, String ip) {
		context = cxt;
		this.lists = lists;
		this.ip = ip;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.search_item, null);
			holder.username = (TextView) convertView
					.findViewById(R.id.search_username);
			holder.userip = (TextView) convertView.findViewById(R.id.search_ip);
			holder.rename = (Button) convertView
					.findViewById(R.id.search_rename_btn);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DeviceInfo deviceinfo = lists.get(position);
		final String devIp = deviceinfo.getDeviceip();
		holder.username.setText(deviceinfo.getDevicename());
		holder.userip.setText(deviceinfo.getDeviceip());
		holder.rename.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				renameDialog = new Dialog(context, R.style.dialog);
				View rename_view = LayoutInflater.from(context).inflate(
						R.layout.activity_rename, null);
				ListView rename_list = (ListView) rename_view
						.findViewById(R.id.rename_list);
				Button rename_cancel = (Button) rename_view
						.findViewById(R.id.rename_cancel);

				rename_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						renameDialog.dismiss();
					}

				});
				rename_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						final String name = listname.get(position);
						holder.username.setText(name);
						new Thread(new Runnable() {
							@Override
							public void run() {
								// getMessage();
								// Log.v("deviceadapter ip ", devIp);
								ServerData.sendMessage(devIp,
										ServerData.CMD_CLIENT_SETNAME, name);
							}
						}).start();

						renameDialog.dismiss();
					}

				});
				listname = getAllName();
				nameAdapter = new NameListAdapter(context, listname);
				rename_list.setAdapter(nameAdapter);

				int w = MyUtils.dip2px(context, 250);
				int h = MyUtils.dip2px(context, 330);

				renameDialog.setContentView(rename_view,
						new ViewGroup.LayoutParams(w, h));
				renameDialog.show();
			}

		});

		return convertView;
	}

	class ViewHolder {
		private TextView username;
		private TextView userip;
		private Button rename;
	}

	private List<String> getAllName() {

		listname = new ArrayList<String>();
		String[] strs = context.getResources()
				.getString(R.string.list_device_name).split(",");

		for (int i = 0; i < strs.length; i++) {
			listname.add(strs[i]);
		}
		return listname;
	}

}
